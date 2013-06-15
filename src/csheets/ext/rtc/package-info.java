/**
 * <p>This extension lets the user share his/her workbook with other CleanSheets
 * users and real-time collaboration (many people can be working on the same
 * workbook at the same time).</p>
 * 
 * <p>Currently, it is able to share one spreadsheet (however the structure is
 * ready to support for any amount of spreadsheets) or a range of cells in one
 * spreadsheet.</p>
 * 
 * <p>Once disconnected, the user can use that shared spreadsheet or piece of it
 * separately from the source.</p>
 * 
 * @author gil_1110484
 */
/**
 @startuml
 title Communication

 boundary Client
 boundary Server
 boundary "Other Clients"

 activate "Other Clients"
 activate Server
 Client -> Server: connects
 activate Client
 Client -> Server: sends identity
 Client <- Server: sends list of all users
 Client <- Server: sends RemoteWorkbook
 Server -> "Other Clients": notifies new user joined
 Client -> Server: asks for a spreadsheet
 Client <- Server: sends RemoteSpreadsheet
 Client -> Server: asks for some cells
 Client <- Server: sends RemoteCell[]
 loop while connected many of these may happen
 Client -> Server: notifies a cell was edited
 Server -> "Other Clients": notifies a cell was edited
 Client -> Server: notifies a cell was selected
 Server -> "Other Clients": notifies a cell was selected
 Client -> Server: asks for some cells
 Client -> Server: asks for a spreadsheet
 end
 Client -> Server: disconnects
 deactivate Client
 Server -> "Other Clients": notifies user left 
 @enduml

 @startuml
 title Server Side

 entity UI
 participant RealTimeCollaboration
 participant RtcEventResponder
 participant Server
 participant Client
 boundary "A Client"

 UI -> RealTimeCollaboration: createServer()
 RealTimeCollaboration -> Server: instantiate
 activate Server
 RealTimeCollaboration -> Server: start()
 Server -> Server: listen for incoming connections
 activate Server
 Server <- "A Client": connects
 Server -> Client: instantiate
 activate Client
 Client -> "A Client": communication
 Client <- "A Client": communication
 ...
 Client <- "A Client": event
 Server <- Client: event
 RtcEventResponder <- Server: event
 UI <- RtcEventResponder: do something with event
 ...
 UI -> RealTimeCollaboration: event
 RealTimeCollaboration -> Server: do something with event
 Server -> Client: send event
 Client -> "A Client": event
 ...
 Client <- "A Client": disconnects
 Server <- Client: notifies user disconnected
 RtcEventResponder <- Server: event
 destroy Client
 UI <- RtcEventResponder: do something with event
 ...
 UI -> RealTimeCollaboration: disconnect()
 RealTimeCollaboration -> Server: onDisconnected()
 Server -> Server: stop listening
 deactivate Server
 Server -> : notify other clients
 RtcEventResponder <- Server: event
 UI <- RtcEventResponder: do something with event
 destroy Server 
 @enduml

 @startuml Searching_for_new_shares_sequence_diagram.png
 title Searching for new shares
 participant Server
 participant "MulticastServer"
 boundary  Network
 participant "MulticastClient"
 participant Client
 boundary "A Client"

 activate Network
 activate Server
 activate "MulticastServer"
 activate "MulticastClient"
 Server -> "MulticastServer": start
 "MulticastServer"	->	Network: "starts propagating the message"
 "MulticastClient"	<-	Network: "receives the message"
 "MulticastClient"	->	Client: start
 activate Client
 Server	->	Client : "start communicating"
 Server	<-	Client : "start communicating"
 ...
 "A Client"	-> Client : disconnect
 Server	->	Client : "stop communicating"
 Server	<-	Client : "stop communicating"
 destroy  Server
 destroy "MulticastServer"
 destroy "MulticastClient"
 destroy Client

 @enduml

 @startuml
 title Client Side

 entity UI
 participant RealTimeCollaboration
 participant RtcEventResponder
 participant Client
 boundary Server

 activate Server
 UI -> RealTimeCollaboration: createClient()
 RealTimeCollaboration -> Client: instantiate
 activate Client
 RealTimeCollaboration -> Client: start()
 Client -> Server: connect
 ...
 Client <- Server: event
 RtcEventResponder <- Client: event
 UI <- RtcEventResponder: do something with event
 ...
 UI -> RealTimeCollaboration: event
 RealTimeCollaboration -> Client: do something with event
 Client -> Server: send event
 ...
 UI -> RealTimeCollaboration: disconnect()
 RealTimeCollaboration -> Client: onDisconnected()
 Client -> Server: send event
 RtcEventResponder <- Client: event
 UI <- RtcEventResponder: do something with event
 destroy Client 
 @enduml

 @startuml 
 class Client
 class ClientInfo {
 + getAddress()
 + getColor()
 + getName()
 }
 class ClientInterface
 abstract class Communicator {
 #DataOutputStream out
 #DataInputStream in
 +Communicator()
 +Communicator(Socket socket)
 #{abstract} close()
 # getMessage()
 # getMessageOrFail(MessageTypes type)
 # sendMessage(RtcMessage message)
 # setSocket(Socket socket)
 -{static} bytesToObject(byte[] bytes)
 -{static} objectToBytes(Object object)
 }
 enum MessageTypes {
 disconnect
 info
 infoList
 eventCellSelected
 eventCellChanged
 getCells
 workbook
 getSpreadsheet
 spreadsheet
 cells
 }
 class RealTimeCollaboration
 interface RtcCommunicator {
 + setListener(RtcListener listener)
 + getConnectedUsers()
 + isConnected()
 + getSharingProperties()
 + start()
 }
 class RtcEventsResponder
 interface RtcInterface {
 + onConnected(ClientInfo client)
 + onCellSelected(ClientInfo source, Address address)
 + onCellChanged(ClientInfo source, RemoteCell cell)
 + onUserAction(ClientInfo source, Object action)
 + onDisconnected(ClientInfo client)
 }
 interface RtcListener {
 + onWorkbookReceived(ClientInfo source, Workbook workbook)
 + onCellsReceived(ClientInfo source, RemoteCell[] cells)
 + onConnectionFailed(Exception e)
 }
 class MulticastServer{
 -DatagramSocket socket                                    
 -DatagramPacket outPacket                                 
 -Runnable searcher                                       
 -String serverName                                        
 -int serverNrClients                                      
 +MulticastServer(int port, String username, int nrClients)
 +String setMsg()                                          
 +void serverNrClient(int nrClient)                        
 +void startSearching()                                    
 +void stop()                                              
 }
 class MulticastClient {
 -MulticastSocket socket
 -DatagramPacket inPacket
 -byte[] inBuf
 -int port
 -Runnable searcher
 +MulticastClient(OnShareFoundListener onShareFoundListener)
 +void startSearching()
 +{static}String[] splitMsg(String msg)
 +void setPort(int port)
 +void stop()
 +void createConnection()
 }
 class ConnectionDialog {
 -OnIPSelectListener listener
 -JPanel contentPanel
 -JTextField userNameTextField
 -JTextField portTextField
 -JTextField addressTextField
 -MulticastServerListAdapter testAdapter
 -String[] serverSelected
 -int selectedIndex
 -MulticastClient searcher
 -JList<MulticastServerListAdapter> serverList
 -Timer time
 -String shareName
 +ConnectionDialog()
 +void setOnIpSelectedListener(OnIPSelectListener listener)
 }

 interface OnIPSelectListener {
 void onIPSelected(String address, String shareName, String userName, int port)
 }
 class ConnectAction {
 -RealTimeCollaboration extension
 -UIController uiController
 -DataListener dataListener
 -ConnectionDialog ipDialog
 +ConnectAction(RealTimeCollaboration extension, UIController uiController)
 +void actionPerformed(ActionEvent e)
 #String getName()
 +void setListener(DataListener dataListener)
 }

 class RtcMessage
 class RtcSharingProperties
 class ServerInterface

 class RemoteCell
 class RemoteSpreadsheet
 class RemoteWorkbook

 RtcInterface		-->	ClientInfo
 RtcInterface		<|--	RtcCommunicator
 RtcInterface		<|--	RtcListener
 RtcListener		<|--	RtcEventsResponder

 RtcInterface		<|--	Client
 Communicator		<|--	Client
 Client			-->	ServerInterface

 RtcCommunicator		<|--	ClientInterface
 Communicator		<|--	ClientInterface
 ClientInterface		-->	RtcListener
 ClientInterface		-->	RtcSharingProperties

 Communicator		-->	RtcMessage

 RealTimeCollaboration	-->	RtcCommunicator
 RealTimeCollaboration	-->	RtcEventsResponder
 RealTimeCollaboration	-->	ClientInfo

 RtcEventsResponder	-->	RtcCommunicator
 RtcEventsResponder	-->	RtcSharingProperties 
 RtcEventsResponder	-->	RealTimeCollaboration

 RtcMessage		-->	MessageTypes

 RtcCommunicator	<|--	ServerInterface
 ServerInterface	-->	RtcListener
 ServerInterface	-->	RtcSharingProperties
 ServerInterface	-->	Client

 RtcMessage		..>	RemoteCell
 RtcMessage		..>	RemoteSpreadsheet
 RtcMessage		..>	RemoteWorkbook

 MulticastClient	<--	ConnectionDialog
 MulticastServer	<--	ServerInterface
 ConnectAction	<-- 	ConnectionDialog
 ConnectAction	<|--	OnIPSelectListener
 RealTimeCollaboration	<--	ConnectAction
 @enduml


 @startuml
 participant ExtensionManager as ExtM
 participant Class
 participant "aClass:Class" as aClass
 participant "extension : RealTimeCollaboration" as RTC
 ExtM -> Class : aClass = forName("csheets.ext.rtc.RealTimeCollaboration");
 activate Class
 create aClass
 Class -> aClass : new
 deactivate Class
 ExtM -> aClass : extension = (Extension)newInstance();
 activate aClass
 create RTC
 aClass -> RTC : new
 deactivate aClass
 ExtM -> RTC : name = getName();
 activate RTC
 deactivate RTC
 ExtM -> ExtM : extensionMap.put(name, extension)
 @enduml

 @startuml
 participant UIController as UIC
 participant ExtensionManager as ExtM
 participant "extension : RealTimeCollaboration" as RTC
 participant "uiExtension : RtcUI" as RTCUI
 UIC -> ExtM : extensions=getExtensions();
 loop for Extension ext : extensions
 UIC -> RTC : uiExtension=getUIExtension(this);
 activate RTC
 create RTCUI
 RTC -> RTCUI : new
 deactivate RTC
 UIC -> UIC : uiExtensions.add(uiExtension);
 end
 @enduml

 @startuml
 participant Frame
 participant UIController as UIC
 participant "uiExtension : RtcUI" as RTCUI
 participant "sidebar : RtcSideBar" as RTCSideBar
 Frame -> UIC : uiExtensions=getExtensions();
 loop for UIExtension uiExt : uiExtensions
 Frame -> RTCUI : extBar=getSideBar();
 create RTCSideBar
 RTCUI -> RTCSideBar : new
 deactivate RTCSideBar
 Frame -> Frame : sideBar.insertTab(extBar);
 end
 @enduml

 @startuml
 participant CellRenderer
 participant UIController as UIC
 participant "uiExtension : RtcUI" as RTCUI
 participant "cellDecorator : RtcCellRenderer" as RTCCellRenderer
 CellRenderer -> UIC : uiExtensions=getExtensions();
 loop for UIExtension uiExt : uiExtensions
 CellRenderer -> RTCUI : decorator=getCellDecorator();
 create RTCCellRenderer
 RTCUI -> RTCCellRenderer : new
 deactivate RTCCellRenderer
 CellRenderer -> CellRenderer : decorators.add(decorator);
 end
 @enduml

@startuml
title Password Authentication
== Share ==
Actor Owner
Owner -> CleanSheetsInterface: Click Share Button
CleanSheetsInterface -> ShareOptionsDialog : instantiate
ShareOptionsDialog -> ShareOptionsDialog : setVisible(true)
Owner -> ShareOptionsDialog : Choose Export Mode
Owner -> ShareOptionsDialog : Enter Share Name
Owner -> ShareOptionsDialog : Enter User Name
Owner -> ShareOptionsDialog : Enter Port
Owner -> ShareOptionsDialog : Enter Password
Owner -> ShareOptionsDialog : Click Share Button
ShareOptionsDialog -> ShareOptionsDialog : setVisible(false)
ShareOptionsDialog -> ShareAction : onChoosedExport(ExportMode,UserName,ShareName,Port,Pass)
== Connect ==
Actor User
User -> CleanSheetsInterface: Click Connect Button
CleanSheetsInterface -> ConnectionDialog : instantiate
ConnectionDialog -> ConnectionDialog : setVisible(true)
User -> ConnectionDialog : Enter User Name
User -> ConnectionDialog : Choose Share to Connect
ConnectionDialog -> ConnectionDialog : Sets the IP
ConnectionDialog -> ConnectionDialog : Sets the Port
User -> ConnectionDialog : Enter Password
User -> ConnectionDialog : Click Connect Button
ConnectionDialog -> ConnectionDialog : setVisible(false)
ConnectionDialog  -> ConnectAction : onIPSelected(IPAddress, ShareName, UserName, Port, Pass)
ConnectAction -> RealTimeCollaboration : createClient()
RealTimeCollaboration -> ServerInterface : new()
RealTimeCollaboration -> ServerInterface : start()
ServerInterface -> ServerInterface : onClientConnected()
ServerInterface -> Client : new()
ServerInterface -> Client : run()
Client <-- ClientInfo : passwordMatches()
alt successful match

    Client -> CleanSheetsInterface : <font color=green>Authentication Accepted

else password doesn't match

    Client -> CleanSheetsInterface  : <font color=red>Authentication Failed
end
@enduml

@startuml
title Sidebar - Share Options Menu
Actor Owner
Owner -> CleanSheetsInterface : Creates a Share
...
Owner -> RtcSidebar : Right-Click on a Share
RtcSidebar -> RtcSidebar : Displays Options Menu
group Options Menu
   Actor User
   alt Choose Disconnect

   else Choose Deactivate
      RtcSidebar -> ServerListAdapter : setActivated(ShareIndex,false)
      ServerListAdapter -> ServerInterface : setActivated(false)
      User -> CleanSheetsInterface : Click Connect Button
      ...
      CleanSheetsInterface -> ConnectionDialog : Instantiate
      ...
      User -> ConnectionDialog : Trys to connect to a share
      ...
      ServerInterface -> ServerInterface : onClientConnected()
      ServerInterface -> User : sendMessage(Connection Refused)
   else Choose Read-Only
      RtcSidebar -> ServerListAdapter : setWritable(ShareIndex,false)
      ServerListAdapter -> ServerInterface : getSharingProperties()
      ServerListAdapter -> RtcSharingProperties : setWritable(false)
      ...
      User -> CleanSheetsInterface : Connects to a Share
      ...
      ServerInterface -> Client : run()
      alt case eventCellChanged
         Client -> ServerInterface : onCellChanged()
         ServerInterface -> RtcEventsResponder : onCellChanged()
         RtcEventsResponder -> RtcSharingProperties : canEdit()
         RtcSharingProperties -> RtcEventsResponder : return false
         note over RtcEventsResponder : The cell is not changed on the server side!
      end
   end
end
@enduml
 */

package csheets.ext.rtc;
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
 Client <-> "A Client": communication
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

RtcEventsResponder	-->	RtcCommunicator
RtcEventsResponder	-->	RtcSharingProperties 
RtcEventsResponder	-->	RealTimeCollaboration

RtcMessage		-->	MessageTypes

RtcCommunicator		<|--	ServerInterface
ServerInterface		-->	RtcListener
ServerInterface		-->	RtcSharingProperties
ServerInterface		-->	Client

RtcMessage		..>	RemoteCell
RtcMessage		..>	RemoteSpreadsheet
RtcMessage		..>	RemoteWorkbook
 @enduml
 */

package csheets.ext.rtc;
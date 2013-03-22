/**
 * Provides an extension to support comments on cells.
 * An extension is a class that extends the class Extension.
 * CleanSheets dynamically loads all the extensions that it finds declared in the following property files: res/extensions.props and extensions.props.
 * For the simple extension to be loaded at startup a line with the fully qualified name of the simple extension class must be present in one of the property files.
 * The fully qualified name of the comments extension is: csheets.ext.comments.CommentsExtension<br/>
 * <br/>
 * The following sections present diagrams that illustrate some important aspects of the analysis and design for this extension.<br/>
 * First, a simple textual description of the use case (that this extension needs to support) is presented.<br/>
 * Then we present a diagram that we call "analysis" use case realization that should illustrate the first approach to the design of the use case realization.<br/>
 * We then illustrate some aspects of the design: the setup of the extension, the class diagram and the use case realization.<br/>  
 * <p> 
 * <b>Sequence Diagram</b> <i>Analysis</i> Use Case Realization
 * <p>
 * Use Case "Enter Comment on Cell": The user selects the cell where he wants to enter a comment. The system displays the current comment of that cell. The user enter the text of the comment (or alters the existing one). The system saves the comment of the cell.<br/>
 * <br/>
 * The following diagram depicts a proposal for the realization of the previously described use case. We call this diagram an "analysis" use case realization because it functions like a draft that we can do during analysis or early design in order to get a previous approach to the design. For that reason we mark the elements of the diagram with the stereotype "analysis" that states that the element is not a design element and, therefore, does not exists as such in the code of the application (at least at the moment that this diagram was created).<br/> 
 * <br/>
 * <img src="doc-files/comments_extension_uc_realization1.png"> 
 * <br/> 
 * <p>
 * <b>Sequence Diagrams  - OUT-OF-DATE</b> illustrating the setup of the extension
 * <p>
 * <p>
 * The following sequence diagram illustrates the creation of the simple extension. 
 * All the extensions are loaded dynamically by the ExtensionManager at application startup. <br/><br/>  
 * <img src="doc-files/simple_extension_image2.png">
 * <br/> 
 * <p>
 * The following sequence diagram illustrates the creation of the user interface extension. 
 * All the UI extensions are loaded by the UIController at application startup. <br/><br/>  
 * <img src="doc-files/simple_extension_image3.png">
 * <br/> 
 * 
 * <p>
 * The following sequence diagram illustrates the creation of the menu extension. 
 * All the menu extensions are loaded by the MenuBar at application startup. <br/><br/>  
 * <img src="doc-files/simple_extension_image4.png">
 * <br/> 
 * <b>
 * Class Diagram - OUT-OF-DATE</b>
 * <p>
 * <img src="doc-files/simple_extension_image1.png">
 * <p>
 * <p>
 * <b>Sequence Diagrams  - OUT-OF-DATE</b> illustrating use cases of the extension
 * <p>
 * The following sequence diagram illustrates the use case <b>"Set the contents of the A1 cell"</b>. <br/>
 * To be noticed that the operation actionPerformed does not originate directly from the User. There are several other classes involved that are not depicted for clarity purposes. 
 * <br/><br/> 
 * <img src="doc-files/simple_extension_image5.png">
 * 
 * @author Alexandre Braganca
 * 
 */
/*
 * 
  @startuml doc-files/comments_extension_uc_realization1.png
  actor User 
  participant "<<analysis>>\nCommentUI" as UI
  participant "<<analysis>>\nCommentCtrl" as ctrl
  participant "<<analysis>>\nSpreadsheet" as sheet
  participant "<<analysis>>\nCell" as cell
  User -> UI : selectCell()
  activate UI
  UI -> ctrl : getCellComment()
  activate ctrl
  ctrl -> sheet : getCell()
  ctrl -> cell : getComment()
  deactivate ctrl
  UI -> UI : displayComment()
  deactivate UI
  User -> UI : enterCommentText()
  activate UI
  UI -> ctrl : setCellComment()
  activate ctrl
  ctrl -> cell : setComment()
  deactivate ctrl
  deactivate UI
  @enduml
  
  @startuml doc-files/simple_extension_image1.png
  class ExampleAction {
   }
  class ExampleMenu
  class ExtensionExample {
    -String NAME;
  }
  class UIExtensionExample
  class JMenuItem
  ExtensionExample -> UIExtensionExample : getUIExtension(UIController)
  UIExtensionExample -> ExampleMenu : getMenu()
  ExampleMenu -> JMenuItem : 'items'
  JMenuItem o-> ExampleAction : action
  @enduml

  @startuml doc-files/simple_extension_image2.png
  participant ExtensionManager as ExtM
  participant Class
  participant "aClass:Class" as aClass
  participant "extension : ExtensionExample" as EExample
  ExtM -> Class : aClass = forName("csheets.ext.simple.ExtensionExample");
  activate Class
  create aClass
  Class -> aClass : new
  deactivate Class
  ExtM -> aClass : extension = (Extension)newInstance();
  activate aClass
  create EExample
  aClass -> EExample : new
  deactivate aClass
  ExtM -> EExample : name = getName();
  activate EExample
  deactivate EExample
  ExtM -> ExtM : extensionMap.put(name, extension)
  @enduml
  
  @startuml doc-files/simple_extension_image3.png
  participant UIController as UIC
  participant ExtensionManager as ExtM
  participant "extension : ExtensionExample" as EExample
  participant "uiExtension : UIExtensionExample" as UIExt
  UIC -> ExtM : extensions=getExtensions();
  loop for Extension ext : extensions
  	UIC -> EExample : uiExtension=getUIExtension(this);
  	activate EExample
  	create UIExt
  	EExample -> UIExt : new
  	deactivate EExample
  	UIC -> UIC : uiExtensions.add(uiExtension);
  end
  @enduml
  
  @startuml doc-files/simple_extension_image4.png
  participant MenuBar as MB
  participant "extensionsMenu : JMenu" as extensionsMenu
  participant UIController as UIC
  participant "extension : UIExtensionExample" as UIE
  participant "extensionMenu : ExampleMenu" as EM 
  MB -> MB : extensionsMenu = addMenu("Extensions", KeyEvent.VK_X);
  activate MB
    create extensionsMenu
    MB -> extensionsMenu : new
  deactivate MB
  MB -> UIC : extensions=getExtensions();
  loop for UIExtension extension : extensions
    MB -> UIE : extensionMenu=extension.getMenu();
    activate UIE
  	create EM
  	UIE -> EM : new
    deactivate UIE
    MB -> EM : icon = getIcon();
    MB -> extensionsMenu : add(extensionMenu); 
  end
  @enduml
  
  @startuml doc-files/simple_extension_image5.png
  actor User
  participant ExampleAction as EA
  participant JOptionPane as JOption
  participant "this.uiController : UIController" as UIC
  participant "temp1 : Spreadsheet" as ss
  participant "temp2 : Cell" as cell

  User -> EA : actionPerformed(ActionEvent event)
  EA -> JOption : result=showConfirmDialog(...)
  alt result==JOptionPane.YES_OPTION
    EA -> UIC : temp1=getActiveSpreadsheet();
	EA -> ss : temp2=getCell(0, 0)
	EA -> cell : setContent("Changed");
  else
  end
  @enduml
  
 */
package csheets.ext.comments;



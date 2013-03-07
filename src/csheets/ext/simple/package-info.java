/**
 * Provides an example of how to build a simple extension.
 * An extension is a class that extends the class Extension.
 * CleanSheets dynamically loads all the extensions that it finds declared in the following property files: res/extensions.props and extensions.props.
 * For the simple extension to be loaded at startup a line with the fully qualified name of the simple extension class must be present in one of the property files.
 * The fully qualified name of the simple extension is: csheets.ext.simple.ExtensionExample
 * 
 * <p>
 * <b>Class Diagram</b>
 * <p>
 * <img src="doc-files/simple_extension_image1.png">
 * <p>
 * <b>Sequence Diagrams</b> illustrating the setup of the extension
 * <p>
 * The following sequence diagram illustrates the creation of the simple extension. 
 * All the extensions are loaded dynamically by the ExtensionManager at application startup. <br/><br/>  
 * <img src="doc-files/simple_extension_image2.png">
 *
 * <p>
 * The following sequence diagram illustrates the creation of the user interface extension. 
 * All the UI extensions are loaded by the UIController at application startup. <br/><br/>  
 * <img src="doc-files/simple_extension_image3.png">
 * 
 * <p>
 * The following sequence diagram illustrates the creation of the menu extension. 
 * All the menu extensions are loaded by the MenuBar at application startup. <br/><br/>  
 * <img src="doc-files/simple_extension_image4.png">
 *
 * <p>
 * <b>Sequence Diagrams</b> illustrating use cases of the extension
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
package csheets.ext.simple;



/**
 @startuml
 participant ExtensionManager as ExtM
 participant Class
 participant "aClass:Class" as aClass
 participant "extension : VersioningExtension" as Ext
 ExtM -> Class : aClass = forName("csheets.ext.versioning.VersioningExtension");
 activate Class
 create aClass
 Class -> aClass : new
 deactivate Class
 ExtM -> aClass : extension = (Extension)newInstance();
 activate aClass
 create Ext
 aClass -> Ext : new
 deactivate aClass
 ExtM -> Ext : name = getName();
 activate Ext
 deactivate Ext
 ExtM -> ExtM : extensionMap.put(name, extension)
 @enduml

 @startuml
 participant UIController as UIC
 participant ExtensionManager as ExtM
 participant "extension : VersioningExtension" as Ext
 participant "uiExtension : VersioningUI" as ExtUI
 UIC -> ExtM : extensions=getExtensions();
 loop for Extension ext : extensions
 UIC -> Ext : uiExtension=getUIExtension(this);
 activate Ext
 create ExtUI
 Ext -> ExtUI : new
 deactivate Ext
 UIC -> UIC : uiExtensions.add(uiExtension);
 end
 @enduml

 @startuml
 participant Frame
 participant UIController as UIC
 participant "uiExtension : VersioningUI" as ExtUI
 participant "sidebar : VersioningSideBar" as ExtSideBar
 Frame -> UIC : uiExtensions=getExtensions();
 loop for UIExtension uiExt : uiExtensions
 Frame -> ExtUI : extBar=getSideBar();
 create ExtSideBar
 ExtUI -> ExtSideBar : new
 deactivate ExtSideBar
 Frame -> Frame : sideBar.insertTab(extBar);
 end
 @enduml
 */

package csheets.ext.versioning;
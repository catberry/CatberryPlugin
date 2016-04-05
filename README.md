# CatberryPlugin
Intellij platform plugin for Catberry JS framework

#### Main features:
* Create Catberry project.
* Create Catberry components.
* Create Catberry stores.
* Auto-completion catberry-tags in template and navigation to components.
* Navigation to cat-component template from tag declaration.
* Auto-completion cat-store attributes in template and navigation to store.

#### How to use:
* Install plugin from IDE:
    * Open IDE preferences and navigate to plugins section
    * Find Catberry Framework support plugin in jetbrains repository and install it
    * Restart IDE
* (Alternative) Manual installation:
    * Download plugin [from JetBrains repository](https://plugins.jetbrains.com/plugin/8283)
    * Add plugin from disk in your jetbrains ide (tested: Idea, WebStorm)
    *Restart IDE
* Create project (for new project):
	* StaticWeb -> Catberry JS (for Idea)
	* Catberry JS (for WebStorm)
* Open any catberry project (for existing projects):
	* Check plugin settings (template engine, components and stores dirs)
* If you use dust templates, associate \*.dust files with HTML FileType

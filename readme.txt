The 'scripts' folder contains executable files that can be used to perform important tasks of the project.

Attention: Only files with the extension .sh (Linux and Mac OS) are updated. You should update/create the
corresponding files for Windows (.bat)!!

One of the first tasks is to generate technical documentation and study it.
For that you should do:
$> cd scripts
$> ./makeuml.sh
$> ./makedoc.sh
Then open the file doc/api/index.html in a browser.

To build the csheets.jar you should do:
$> cd scripts
$> ./cc.sh
$> ./makejar.sh

To execute csheets (from csheets.jar):
$> cd scripts
$> ./run.sh

 



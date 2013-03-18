The 'scripts' folder contains executable files that can be used to perform important tasks of the project.

Scripts with the .sh extension are to be used in Linux and Mac OS. Scripts with the .bat extension are to be used in Windows.
Attention: You should always maintain the scripts up-to-date!

One of the first tasks is to generate technical documentation and study it.
For that you should do:
$> cd scripts
$> ./makeuml.sh
$> ./makedoc.sh
Then open the file doc/api/index.html in a browser.

Then you should study the scripts!

To build the csheets.jar you should do:
$> cd scripts
$> ./cc.sh
$> ./makejar.sh

To execute csheets (from csheets.jar):
$> cd scripts
$> ./run.sh

To build the tests
$> cd scripts
$> ./build_tests.sh

To execute the tests
$> cd scripts
$> ./run_tests.sh


 



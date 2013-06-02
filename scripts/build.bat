@echo off
rmdir \S \Q  ..\tmp-build 
mkdir ..\tmp-build
REM del \S \Q ..\tmp-build\*.class >nul
dir \B \S \O:N ..\src\*.java > c.lst
javac -cp ..\src;..\src\lib\antlr.jar;..\src\csheets\*.java;..\src\csheets\core\*.java;..\src\csheets\ext\*.java;..\src\csheets\io\*.java;..\src\csheets\ui\*.java;..\src\csheets\core\formula\*.java;..\src\csheets\core\formula\compiler\*.java;..\src\csheets\core\formula\lang\*.java;..\src\csheets\core\formula\util\*.java;..\src\csheets\ext\assertion\*.java;..\src\csheets\ext\deptree\*.java;..\src\csheets\ext\test\*.java;..\src\csheets\ext\assertion\ui\*.java;..\src\csheets\ext\test\ui\*.java;..\src\csheets\ext\style\*.java;..\src\csheets\ext\style\ui\*.java;..\src\csheets\ui\ctrl\*.java;..\src\csheets\ui\ext\*.java;..\src\csheets\ui\sheet\*.java;..\src\csheets\ext\simple\*.java;..\src-tests\csheets\core\*.java;..\*.java;..\src\lib\hsqldb-2.2.9.jar;..\lib\h2-1.3.172.jar -d ..\tmp-build @c.lst %1 %2 %3
del c.lst
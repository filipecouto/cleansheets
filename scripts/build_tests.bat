@echo off
rmdir /S /Q  ..\tmp-build-tests 
mkdir ..\tmp-build-tests
REM del /S /Q ..\tmp-build-tests\*.class >nul
dir /B /S /O:N ..\src-tests\*.java > c.lst
javac -cp ..\src-tests;..\lib\antlr.jar;..\dist\csheets.jar;..\lib\junit-4.10.jar -d ..\tmp-build-tests @c.lst %1 %2 %3
del c.lst
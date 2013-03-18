@echo off
rmdir /S /Q  ..\tmp-build 
mkdir ..\tmp-build
REM del /S /Q ..\tmp-build\*.class >nul
dir /B /S /O:N ..\src\*.java > c.lst
javac -cp ..\src;..\lib\antlr.jar -d ..\tmp-build @c.lst %1 %2 %3
del c.lst
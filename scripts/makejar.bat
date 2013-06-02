@echo off

echo Compiling...
call build

echo Copying temporary files...
call xcopy ..\src-resources\csheets ..\tmp-build\csheets /S /Q /Y > nul

echo Creating archive...
call jar cmf makejar.mf ..\dist\csheets.jar -C ..\tmp-build csheets

echo Copiar as dependencias
call copy /Y ..\lib\antlr.jar ..\dist copy /Y ..\lib\h2-1.3.172.jar ..\dist
 copy /Y ..\lib\hsqldb-2.2.9.jar ..\dist 

REM echo Removing temporary files...
REM call rmdir jar /Q /S

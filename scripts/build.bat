@echo off
echo remover
rmdir \S \Q  ..\tmp-build 
echo criar
mkdir ..\tmp-build
echo comentario
REM del \S \Q ..\tmp-build\*.class >nul
echo dir

dir /B /S /O:N ..\src\**.java > c.lst

echo javac
javac -cp ..\src\*;..\lib\* -d ..\tmp-build @c.lst %1 %2 %3
del c.lst
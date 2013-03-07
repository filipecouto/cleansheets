#!/bin/sh
echo Compiling...
./build.sh

echo Copying resource files...
cp -R ../src-resources/csheets ../tmp-build 

echo Creating archive...
jar cmf makejar.mf ../dist/csheets.jar -C ../tmp-build csheets

echo Copiar as dependencias
cp ../lib/antlr.jar ../dist/antlr.jar

# echo Removing temporary files...
# rm -R jar

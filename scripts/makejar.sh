#!/bin/sh
echo Compiling...
./build.sh

echo Copying resource files...
cp -R ../src-resources/csheets ../tmp-build 

echo Creating archive...
jar cmf makejar.mf ../dist/csheets.jar -C ../tmp-build csheets

echo Copiar as dependencias
find ../lib -name "*.jar" -print0 | xargs -I{} -0 cp -v {} ../dist

# echo Removing temporary files...
# rm -R jar

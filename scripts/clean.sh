#!/bin/sh

echo Removing temporary files...
find ../res -iname "*.class" -exec rm {} \;

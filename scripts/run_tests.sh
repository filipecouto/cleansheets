#!/bin/sh
java -cp ../res:../lib/junit-4.10.jar:../src-tests org.junit.runner.JUnitCore csheets.core.WorkbookTest csheets.core.SpreadsheetTest csheets.ext.comments.CommentableCellTest 
#java -cp ../res:../lib/junit-4.10.jar:../tests junit.textui.TestRunner MyClassTest


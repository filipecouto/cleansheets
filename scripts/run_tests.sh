#!/bin/sh
java -cp ../tmp-build-tests:../dist/csheets.jar:../lib/antlr.jar:../lib/h2-1.3.172.jar:../lib/hsqldb-2.2.9.jar:../lib/junit-4.10.jar org.junit.runner.JUnitCore csheets.core.WorkbookTest csheets.core.SpreadsheetTest csheets.ext.comments.CommentableCellTest csheets.ext.db.ApplicationLayerTests csheets.core.formula.lang.AttributionTest csheets.ext.oi.XMLLoadTests csheets.ext.oi.XMLSaveTests


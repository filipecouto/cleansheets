#!/bin/sh
# javac -cp ../src-tests:../dist/csheets.jar:../lib/antlr.jar:../lib/junit-4.10.jar ../src-tests/csheets/ext/comments/*.java ../src-tests/csheets/core/*.java 
mkdir ../tmp-build-tests/
cp ../src/hibernate.cfg.xml ../tmp-build-tests/hibernate.cfg.xml
find ../src-tests -name "*.java" | xargs javac -cp ../src-tests:../dist/csheets.jar:../lib/antlr.jar:../lib/h2-1.3.172.jar:../lib/hsqldb-2.2.9.jar:../lib/dbunit-2.4.9.jar:../lib/derby.jar:../lib/dom4j-1.6.1.jar:../lib/hibernate-commons-annotations-4.0.2.Final.jar:../lib/hibernate-core-4.2.2.Final.jar:../lib/hibernate.jar:../lib/javassist-3.15.0-GA.jar:../lib/jboss-logging-3.1.0.GA.jar:../lib/jboss-transaction-api_1.1_spec-1.0.1.Final.jar:../lib/jcl-over-slf4j-1.7.5.jar:../lib/jul-to-slf4j-1.7.5.jar:../lib/log4j-over-slf4j-1.7.5.jar:../lib/slf4j-api-1.7.5.jar:../lib/slf4j-ext-1.7.5.jar:../lib/slf4j-migrator-1.7.5.jar:../lib/slf4j-simple-1.7.5.jar:../lib/junit-4.10.jar -d ../tmp-build-tests 

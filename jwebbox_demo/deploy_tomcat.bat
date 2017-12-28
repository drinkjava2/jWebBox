call mvn clean -DskipTests package
@echo off

rd /s/q /q C:\tomcat7\logs
md C:\tomcat7\logs

rd /s/q /q C:\tomcat7\work
md C:\tomcat7\work

rd /s/q /q C:\tomcat7\webapps
md C:\tomcat7\webapps

@echo on 
cd target
del ROOT.war
ren *.war ROOT.war 
copy ROOT.war C:\tomcat7\webapps\ /y 

call C:\tomcat7\bin\startup.bat

start http://127.0.0.1 

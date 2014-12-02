@echo off

echo            ********************************************
echo            *      Web Crawler Execution Program       *  
echo            ********************************************


javac -classpath ".\libs\jsoup-1.8.1.jar" .\src\com\sewn\crawler\*.java .\src\Crawler.java

cd src
java -classpath "..\libs\jsoup-1.8.1.jar;%PATH%" Crawler
cd ..

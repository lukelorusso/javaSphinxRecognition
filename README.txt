=== INTELLECTUAL PROPERTIES ===

Copyright (C) 2014 Luca Lorusso, luca.lor17@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

CMUSphinx is an Open Source Toolkit for Speech Recognition, a project by Carnegie Mellon
University
http://cmusphinx.sourceforge.net/

The Java Speech API (JSAPI) is an application programming interface for cross-platform
support of command and control recognizers, dictation systems, and speech synthesizers. 
Copyright 1997-1998 Sun Microsystems
https://docs.oracle.com/cd/E17802_01/products/products/java-media/speech/forDevelopers/jsapi-doc/index.html

=== ECLIPSE NOTES ===

Follow those steps to correctly configure Eclipse (Luna or higher) environment:

0) IMPORTANT: MAKE SURE THAT AT LEAST JavaSE-1.8 JDK IS INSTALLED ON YOUR SYSTEM!

1) extract the content of .tar/.zip file into a subfolder of your default workspace
EXAMPLE: "C:\Users\USERNAME\workspace\mindeskInterface"
This will be your project folder

2) go to lib subfolder of the project and extract jsapi.jar library:
due to license restrictions jsapi.jar is not shipped directly with Sphinx4 but can be
easily created by running lib/jsapi.sh (or lib/jsapi.bat on windows) once

3) open Eclipse IDE, File > New > Java Project;
uncheck "Use default location" and click Browse;
select your project folder as location and click Next;
select Libraries form and make sure that jsapi.jar and sphinx4.jar are included, if not:
 -> Add JARs, expand project folder / lib and select both jar files, OK;
click Finish

4) Run > Run Configurations... > double click Java Application;
click Browse, select project folder, OK;
click Search, select "recognizer - speech", OK;
select Classpath form, select User Entries, click Advanced...
check Add Folders, OK, expand project folder and select res, OK;
select Arguments form and inside VM arguments write without quotes: "-mx256m"
inside Program arguments you can:
 - leave blank (english grammar will be used)
 - write one of the following languages: {eng, ita, esp}
 - write a language followed by an IP:port specification for UDP communication:
   EXAMPLE: "ita 127.0.0.1:5005"
 - write a language followed by IP:port and the autostart command:
   EXAMPLE: "ita 127.0.0.1:5005 start"

5) Click Apply. Now you can Run your configuration!

To produce JAR executable:

6) from Project Explorer expand project folder / src / speech and select recognizer.java
File > Export...
expand Java and select Runnable JAR file
inside Launch configuration select your configuration (check program arguments you need);
select Browse and go to project folder / res (JAR file will need models filder to work);
give a name to JAR file and save it;
check "Package required libraries into generated JAR" and then Finish

To run you JAR just double click on it or run from prompt / terminal:
"java -jar JARNAME.jar"
ATTENTION: your prompt / terminal path must be your JAR folder! So before run:
"cd path/to/your/jar"
That's all folks!

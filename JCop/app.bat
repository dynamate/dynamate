@echo off

REM JavaAppLauncher: Generic Java Application Launcher
REM Copyright (C) 2007  Santhosh Kumar T
REM 
REM This library is free software; you can redistribute it and/or
REM modify it under the terms of the GNU Lesser General Public
REM License as published by the Free Software Foundation; either
REM version 2.1 of the License, or (at your option) any later version.
REM 
REM This library is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
REM Lesser General Public License for more details.


SETLOCAL ENABLEEXTENSIONS
Set command=
Set tmp=
Set prefix=java -classpath "%JCOP_HOME%\aspectjweaver.jar";
Set vmOpts="-Djava.system.class.loader=org.aspectj.weaver.loadtime.WeavingURLClassLoader" 
REM "-Daj.class.path=%ASPECTPATH%;%CLASSPATH%" "-Daj.aspect.path=%ASPECTPATH%"

:processline
if "%1" == "-classpath" goto option
if "%1" == "<java.endorsed.dirs>" goto option
if "%1" == "<java.ext.dirs>" goto option
if "%1" == "<java.library.path>" goto option
if "%1" == "<java.system.props>" goto option
if "%1" == "<java.bootclasspath>" goto option
if "%1" == "<java.bootclasspath.append>" goto option
if "%1" == "<java.bootclasspath.prepend>" goto option
if "%1" == "<jvm.args>" goto option

rem ignore if line is comment
SET LINE=%1
SET FIRST_CHAR=%LINE:~1,1%
IF "%FIRST_CHAR%" == "#" goto end

rem join the line to result
if defined RESULT set RESULT=%RESULT%%SEPARATOR%
set RESULT=%RESULT%"%PREFIX%%~1"
goto end

:option
echo %SECTION%
rem append result to command
if DEFINED RESULT call :processresult

set OPTION=%1
set SECTION=%OPTION:~1%
goto %SECTION%

:classpath
set RESULT=
set SECTION_PREFIX=-classpath 
set PREFIX=
set SEPARATOR=;
goto end

:java.endorsed.dirs
set RESULT=
set SECTION_PREFIX=-Djava.endorsed.dirs=
set PREFIX=
set SEPARATOR=;
goto end

:java.ext.dirs
set RESULT=
set SECTION_PREFIX=-Djava.ext.dirs=
set PREFIX=
set SEPARATOR=;
goto end

:java.library.path
set RESULT=
set SECTION_PREFIX=-Djava.library.path=
set PREFIX=
set SEPARATOR=;
goto end

:java.system.props
set RESULT=
set SECTION_PREFIX=
set PREFIX=-D
set SEPARATOR= 
goto end

:java.bootclasspath
set RESULT=
set SECTION_PREFIX=-Xbootclasspath:
set PREFIX=
set SEPARATOR=;
goto end

:java.bootclasspath.prepend
set RESULT=
set SECTION_PREFIX=-Xbootclasspath/p:
set PREFIX=
set SEPARATOR=;
goto end

:java.bootclasspath.append
set RESULT=
set SECTION_PREFIX=-Xbootclasspath/a:
set PREFIX=
set SEPARATOR=;
goto end

:jvm.args
set CMD=%CMD% "-DSCRIPT_FILE=%APP%.bat"
set RESULT=
set SECTION_PREFIX=
set PREFIX=
set SEPARATOR= 
goto end

:processresult
if "%SECTION%" == "java.ext.dirs" set RESULT="%JAVA_HOME%\lib\ext";"%JAVA_HOME%\jre\lib\ext";%RESULT%
set CMD=%CMD% %SECTION_PREFIX%%RESULT%

:end

@echo off

:RUN
java -jar -ea "%JCOP_HOME%\jcop.jar" %*
 
SET clp=
SET compilation_unit=

:END

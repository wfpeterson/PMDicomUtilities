del .\target\classes\com\pixelmed\validate\*.class
del .\target\classes\com\pixelmed\validate\DicomIODDescriptionsCompiled.xsl
del .\target\classes\com\pixelmed\validate\CompileDicomIODDescriptionsIntoXSLT.xsl
del .\target\classes\com\pixelmed\validate\CommonDicomIODValidationRules.xsl

set MAVEN_REPO=C:\Documents and Settings\vhaiswpeterb\.m2\repository\pixelmed\pixelmed\2.0

set MAVEN_PIXELMED="C:\Documents and Settings\vhaiswpeterb\.m2\repository\pixelmed\pixelmed\2.0"

set CLASSPATH=c:\usr\eclipse\workspace\PMValidation\target\classes
set CLASSPATH=%CLASSPATH%;%MAVEN_REPO%\pixelmed-2.0.jar

javac -sourcepath . -d ./target/classes -encoding "UTF8" ./src/java/com/pixelmed/validate/DicomInstanceValidator.java

javac -sourcepath . -d ./target/classes -encoding "UTF8" ./src/java/com/pixelmed/validate/CompileXSLTIntoTranslet.java

java -Djava.endorsed.dirs=%MAVEN_PIXELMED% com.pixelmed.validate.CompileXSLTIntoTranslet .\src\java\com\pixelmed\validate\CompileDicomIODDescriptionsIntoXSLT.xsl

copy .\src\java\com\pixelmed\validate\CompileDicomIODDescriptionsIntoXSLT.class .\target\classes\com\pixelmed\validate
copy .\src\java\com\pixelmed\validate\CompileDicomIODDescriptionsIntoXSLT.xsl .\target\classes\com\pixelmed\validate

javac -sourcepath . -d ./target/classes -encoding "UTF8" ./src/java/com/pixelmed/validate/ExecuteTranslet.java

java -Djava.endorsed.dirs=%MAVEN_PIXELMED% com.pixelmed.validate.ExecuteTranslet .\target\classes\com\pixelmed\validate\CompileDicomIODDescriptionsIntoXSLT.xsl .\src\java\com\pixelmed\validate\DicomIODDescriptionsSource.xml .\target\classes\com\pixelmed\validate\DicomIODDescriptionsCompiled.xsl

copy  .\src\java\com\pixelmed\validate\CommonDicomIODValidationRules.xsl .\target\classes\com\pixelmed\validate

::Test CT image
java com.pixelmed.validate.DicomInstanceValidator c:\images\scrubbed\B1052853.dcm


@echo off
echo Descargando librerias de graficas para el simulador...

if not exist lib mkdir lib

echo Descargando JFreeChart...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.3/jfreechart-1.5.3.jar' -OutFile 'lib/jfreechart-1.5.3.jar'"

echo Descargando JCommon...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/jfree/jcommon/1.0.24/jcommon-1.0.24.jar' -OutFile 'lib/jcommon-1.0.24.jar'"

echo.
echo Librerias descargadas exitosamente!
echo Ahora puedes ejecutar el simulador con graficas.
echo.
pause

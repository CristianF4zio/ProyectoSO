@echo off
echo Empaquetando Simulador de Sistema Operativo...
mvn clean package
if %ERRORLEVEL% EQU 0 (
    echo Empaquetado exitoso! JAR generado en target/
) else (
    echo Error en el empaquetado!
)
pause

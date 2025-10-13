@echo off
echo Ejecutando Simulador de Sistema Operativo...
mvn exec:java -Dexec.mainClass="com.simulador.so.SimuladorSO"
pause

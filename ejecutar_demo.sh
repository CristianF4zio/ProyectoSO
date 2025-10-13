#!/bin/bash

# Script para ejecutar la demostración del simulador
echo "=== Simulador de Planificación - Demostración ==="
echo "Compilando y ejecutando la demostración..."
echo ""

# Crear directorio de logs si no existe
mkdir -p logs

# Compilar la demostración
echo "Compilando demostración..."
javac -d "target/classes" src/main/DemoSimulador.java

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa"
    echo ""
    
    # Ejecutar la demostración
    echo "Ejecutando demostración del simulador..."
    echo "Presiona Ctrl+C para detener"
    echo ""
    
    java -cp "target/classes" main.DemoSimulador
    
else
    echo "✗ Error en la compilación"
    exit 1
fi

#!/bin/bash

# Script para compilar y ejecutar el simulador de planificación
# Autor: Simulador de Planificación
# Fecha: $(date)

echo "=== Simulador de Planificación ==="
echo "Compilando y ejecutando el simulador..."
echo ""

# Crear directorio de logs si no existe
mkdir -p logs

# Compilar el proyecto
echo "Compilando proyecto..."
javac -cp "src/main" -d "target/classes" src/main/**/*.java src/main/simuladorPlanificacion/**/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa"
    echo ""
    
    # Ejecutar el simulador
    echo "Ejecutando simulador..."
    echo "Presiona Ctrl+C para detener"
    echo ""
    
    java -cp "target/classes" main.SimuladorMain
    
else
    echo "✗ Error en la compilación"
    exit 1
fi

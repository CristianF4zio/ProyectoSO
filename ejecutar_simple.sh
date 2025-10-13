#!/bin/bash

# Script para compilar y ejecutar el simulador simplificado
echo "=== Simulador de Planificación - Versión Simplificada ==="
echo "Compilando y ejecutando el simulador..."
echo ""

# Crear directorio de logs si no existe
mkdir -p logs

# Crear directorio de clases si no existe
mkdir -p target/classes

# Compilar solo los componentes necesarios
echo "Compilando componentes del simulador..."

# Compilar clases base
javac -cp "src/main" -d "target/classes" src/main/gestor/Reloj.java

# Compilar componentes del simulador
javac -cp "target/classes:src/main" -d "target/classes" src/main/simuladorPlanificacion/io/*.java
javac -cp "target/classes:src/main" -d "target/classes" src/main/simuladorPlanificacion/metricas/*.java
javac -cp "target/classes:src/main" -d "target/classes" src/main/simuladorPlanificacion/logging/*.java

# Compilar simulador principal
javac -cp "target/classes:src/main" -d "target/classes" src/main/SimuladorSimple.java

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa"
    echo ""
    
    # Ejecutar el simulador
    echo "Ejecutando simulador simplificado..."
    echo "Presiona Ctrl+C para detener"
    echo ""
    
    java -cp "target/classes" main.SimuladorSimple
    
else
    echo "✗ Error en la compilación"
    exit 1
fi

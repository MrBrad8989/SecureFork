#!/bin/bash
# Script para ejecutar el servidor y cliente SecureFork

echo "================================"
echo "  SecureFork - Cliente SSH GUI"
echo "================================"
echo ""
echo "Selecciona una opción:"
echo "1. Ejecutar Servidor SSL"
echo "2. Ejecutar Cliente GUI (JavaFX)"
echo "3. Ejecutar Cliente Consola"
echo "4. Compilar proyecto"
echo "5. Limpiar y compilar"
echo "6. Salir"
echo ""
read -p "Opción [1-6]: " opcion

case $opcion in
    1)
        echo "🚀 Iniciando servidor SSL en puerto 5555..."
        mvn exec:java -Dexec.mainClass="com.refork.server.ServidorSSL"
        ;;
    2)
        echo "🖥️  Abriendo cliente con interfaz gráfica..."
        echo "   (Usando JavaFX Maven Plugin)"
        mvn javafx:run
        ;;
    3)
        echo "💻 Ejecutando cliente en consola..."
        mvn exec:java -Dexec.mainClass="com.refork.client.ClienteSSL"
        ;;
    4)
        echo "🔧 Compilando proyecto..."
        mvn compile
        ;;
    5)
        echo "🧹 Limpiando y compilando proyecto completo..."
        mvn clean install
        ;;
    6)
        echo "👋 ¡Hasta luego!"
        exit 0
        ;;
    *)
        echo "❌ Opción inválida"
        exit 1
        ;;
esac


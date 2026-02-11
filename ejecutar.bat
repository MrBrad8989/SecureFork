@echo off
REM Script para ejecutar el servidor y cliente SecureFork en Windows

:menu
cls
echo ================================
echo   SecureFork - Cliente SSH GUI
echo ================================
echo.
echo Selecciona una opcion:
echo 1. Ejecutar Servidor SSL
echo 2. Ejecutar Cliente GUI (JavaFX)
echo 3. Ejecutar Cliente Consola
echo 4. Compilar proyecto
echo 5. Limpiar y compilar
echo 6. Salir
echo.
set /p opcion="Opcion [1-6]: "

if "%opcion%"=="1" goto servidor
if "%opcion%"=="2" goto cliente_gui
if "%opcion%"=="3" goto cliente_consola
if "%opcion%"=="4" goto compilar
if "%opcion%"=="5" goto limpiar
if "%opcion%"=="6" goto salir
echo Opcion invalida
pause
goto menu

:servidor
echo Iniciando servidor SSL en puerto 5555...
mvn exec:java -Dexec.mainClass="com.refork.server.ServidorSSL"
pause
goto menu

:cliente_gui
echo Abriendo cliente con interfaz grafica...
echo (Usando JavaFX Maven Plugin)
mvn javafx:run
pause
goto menu

:cliente_consola
echo Ejecutando cliente en consola...
mvn exec:java -Dexec.mainClass="com.refork.client.ClienteSSL"
pause
goto menu

:compilar
echo Compilando proyecto...
mvn compile
pause
goto menu

:limpiar
echo Limpiando y compilando proyecto completo...
mvn clean install
pause
goto menu

:salir
echo Hasta luego!
exit


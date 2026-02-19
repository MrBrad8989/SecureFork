@echo off
cls
echo ============================================
echo   Configurar OpenSSH Server en Windows
echo ============================================
echo.
echo IMPORTANTE: Este script debe ejecutarse como ADMINISTRADOR
echo.
pause

echo.
echo [1/4] Verificando permisos de administrador...
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Este script requiere permisos de administrador
    echo.
    echo Click derecho en el archivo y selecciona "Ejecutar como administrador"
    echo.
    pause
    exit /b 1
)
echo Permisos OK

echo.
echo [2/4] Instalando OpenSSH Server...
powershell -Command "Add-WindowsCapability -Online -Name OpenSSH.Server~~~~0.0.1.0"
if %errorlevel% neq 0 (
    echo.
    echo ERROR instalando OpenSSH Server
    echo Intenta instalarlo manualmente desde Configuracion de Windows
    pause
    exit /b 1
)
echo OpenSSH Server instalado

echo.
echo [3/4] Iniciando servicio SSH...
net start sshd
sc config sshd start=auto
echo Servicio SSH iniciado y configurado para inicio automatico

echo.
echo [4/4] Configurando firewall...
netsh advfirewall firewall add rule name="SSH Server" dir=in action=allow protocol=TCP localport=22 >nul 2>&1
echo Firewall configurado

echo.
echo ============================================
echo   Configuracion Completada
echo ============================================
echo.
echo Para conectarte desde SecureFork usa:
echo.
echo   Tipo: SFTP
echo   Host: localhost
echo   Puerto: 22
echo   Usuario: %USERNAME%
echo   Contrasena: [tu contrasena de Windows]
echo.
echo Verifica el servicio con:
echo   Get-Service sshd
echo.
pause


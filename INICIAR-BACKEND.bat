@echo off
cls
echo ============================================
echo   SecureFork - Iniciar Backend
echo ============================================
echo.
echo Deteniendo procesos Java antiguos...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak >nul
echo.
echo Iniciando servidor Spring Boot...
echo MANTENER ESTA VENTANA ABIERTA
echo.
cd /d "%~dp0"
set JAVA_HOME=C:\Program Files\Java\jdk-23
"%USERPROFILE%\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
pause


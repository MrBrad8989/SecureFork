@echo off
cls
echo ============================================
echo   SecureFork - Iniciar Frontend
echo ============================================
echo.
echo ASEGURATE DE QUE EL BACKEND ESTE CORRIENDO
echo.
echo Iniciando Electron...
echo.
cd /d "%~dp0\electron-client"
call npm run dev
pause


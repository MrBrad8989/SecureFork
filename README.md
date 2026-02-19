# 🔱 SecureFork

![Versión](https://img.shields.io/badge/Versi%C3%B3n-1.0--SNAPSHOT-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen)
![Electron](https://img.shields.io/badge/Electron-30.0-9cf)
![React](https://img.shields.io/badge/React-18.3-blue)

**SecureFork** es una herramienta multiplataforma y moderna que actúa como cliente SSH y SFTP. Está diseñada con una arquitectura dividida: un backend robusto basado en **Java (Spring Boot)** que gestiona las conexiones de forma segura, y una interfaz de usuario elegante y fluida construida con **Electron y React**.

---

## ✨ Características Principales

* 🖥️ **Terminal SSH Interactiva:** Emulación de terminal completa en tiempo real (gracias a *xterm.js*) soportando coloreado de sintaxis, redimensionamiento y comandos interactivos.
* 📁 **Navegador SFTP de Doble Panel:** Interfaz intuitiva tipo *Commander* que muestra tus archivos locales a la izquierda y los archivos remotos a la derecha.
* ⚡ **Transferencia de Archivos:** Sube y descarga archivos entre tu máquina local y el servidor remoto con solo un clic. Visualiza archivos de texto directamente en la app.
* 📑 **Múltiples Pestañas:** Gestiona múltiples conexiones (SSH y SFTP) de forma simultánea sin perder el contexto.
* 🔒 **Conexiones Seguras:** Integración con protocolos seguros nativos y soporte para certificados SSL/Truststores locales.
* 🚀 **Multiplataforma:** Funciona de manera nativa en Windows, Linux y macOS.

---

## 🛠️ Tecnologías Utilizadas

### Backend (API REST y Core SSH/SFTP)
* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3.2.2
* **Librerías clave:** Apache Mina SSHD (Gestión de conexiones seguras)

### Frontend (Desktop App)
* **Framework:** Electron + React 18
* **Build Tool:** Vite
* **Componentes:** xterm.js (Terminal), Axios (Cliente HTTP)
* **Estilos:** CSS3 Moderno (Tema Oscuro por defecto)

---

## 📂 Estructura del Proyecto

```text
SecureFork/
├── src/main/java/com/refork/
│   ├── api/          # Controladores REST y Servicios Spring Boot (SSH/SFTP)
│   ├── client/       # Implementación base de cliente SSL (Legacy/Consola)
│   ├── gui/          # Cliente GUI mínimo en JavaFX
│   └── server/       # Servidor SSL nativo multi-hilo
├── electron-client/  # 💻 Interfaz gráfica principal (Electron + React)
│   ├── electron/     # Scripts de inicialización de la ventana de Electron
│   ├── src/          # Código fuente React (Terminal, SftpBrowserDual, etc.)
│   └── package.json  # Dependencias de Node.js
├── INICIAR-BACKEND.bat      # Script rápido para Windows (Backend)
├── INICIAR-FRONTEND.bat     # Script rápido para Windows (Frontend)
└── CONFIGURAR-SSH-WINDOWS.bat # Script de ayuda para montar OpenSSH Server en Windows

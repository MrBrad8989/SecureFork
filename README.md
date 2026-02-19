<div align="center">

# 🔱 SecureFork

### Cliente SSH y SFTP Multiplataforma de Nueva Generación

![Versión](https://img.shields.io/badge/Versi%C3%B3n-1.0--SNAPSHOT-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen)
![Electron](https://img.shields.io/badge/Electron-30.0-9cf)
![React](https://img.shields.io/badge/React-18.3-blue)
![Maven](https://img.shields.io/badge/Maven-3.9+-red)
![Node.js](https://img.shields.io/badge/Node.js-18+-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

**SecureFork** es una herramienta multiplataforma y moderna que actúa como cliente SSH y SFTP profesional. Está diseñada con una arquitectura dividida: un backend robusto basado en **Java (Spring Boot)** que gestiona las conexiones de forma segura, y una interfaz de usuario elegante y fluida construida con **Electron y React**.

[🚀 Inicio Rápido](#-instalación-y-configuración) • [📖 Documentación](#-documentación) • [🎯 Características](#-características-principales) • [🛠️ Tecnologías](#%EF%B8%8F-tecnologías-utilizadas)

</div>

---

## ✨ Características Principales

<table>
<tr>
<td width="50%">

### 🖥️ Terminal SSH Interactiva
Emulación de terminal completa en tiempo real gracias a **xterm.js**:
- Coloreado de sintaxis avanzado
- Redimensionamiento dinámico
- Comandos interactivos
- Historial de comandos
- Soporte para sesiones múltiples

</td>
<td width="50%">

### 📁 Navegador SFTP de Doble Panel
Interfaz intuitiva tipo **Commander/Midnight Commander**:
- Panel local (izquierda) + Panel remoto (derecha)
- Navegación visual entre directorios
- Vista previa de archivos de texto
- Gestión completa de archivos
- Indicadores de progreso

</td>
</tr>
<tr>
<td width="50%">

### ⚡ Transferencia de Archivos
Operaciones de transferencia optimizadas:
- Upload y download con un clic
- Visualización de archivos de texto
- Gestión de permisos
- Progreso en tiempo real

</td>
<td width="50%">

### 📑 Gestión Multi-Sesión
Sistema avanzado de pestañas:
- Múltiples conexiones SSH simultáneas
- Múltiples sesiones SFTP independientes
- Cambio rápido entre sesiones
- Sin pérdida de contexto

</td>
</tr>
<tr>
<td width="50%">

### 🔒 Conexiones Seguras
Seguridad de nivel empresarial:
- Protocolos SSH/SFTP nativos
- Soporte para certificados SSL
- Truststores personalizables
- Autenticación por contraseña

</td>
<td width="50%">

### 🚀 Multiplataforma
Funciona nativamente en:
- ✅ Windows 10/11
- ✅ Linux (Ubuntu, Debian, Fedora, Arch)
- ✅ macOS (Intel y Apple Silicon)

</td>
</tr>
</table>

---

## 🛠️ Tecnologías Utilizadas

### 🔧 Backend (API REST y Core SSH/SFTP)
| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Java** | 21 | Lenguaje base del backend |
| **Spring Boot** | 3.2.2 | Framework REST y gestión de servicios |
| **Apache Mina SSHD** | 2.12.1 | Implementación SSH/SFTP |
| **Maven** | 3.9+ | Gestión de dependencias y build |

### 🎨 Frontend (Desktop App)
| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Electron** | 30.0 | Framework de aplicación desktop |
| **React** | 18.3 | Biblioteca UI |
| **Vite** | 5.4+ | Build tool moderno y rápido |
| **xterm.js** | 5.3.0 | Emulador de terminal |
| **Axios** | 1.13+ | Cliente HTTP |
| **CSS3 Moderno** | - | Tema oscuro profesional |

---

## 📂 Estructura del Proyecto

```text
SecureFork/
├── 📁 src/main/java/com/refork/
│   ├── api/                     # 🌐 Controladores REST y Servicios Spring Boot
│   │   ├── controller/          # Endpoints SSH y SFTP
│   │   ├── service/             # Lógica de negocio (SshService, SftpService)
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── config/              # Configuración CORS y seguridad
│   ├── client/                  # 💻 Implementación cliente SSL (Legacy/Consola)
│   ├── gui/                     # 🎨 Cliente GUI mínimo en JavaFX (demo)
│   └── server/                  # 🔐 Servidor SSL nativo multi-hilo
│
├── 📁 electron-client/          # 💎 Interfaz gráfica principal (Electron + React)
│   ├── electron/                # Scripts de inicialización de Electron
│   │   ├── main.cjs            # Proceso principal de Electron
│   │   └── preload.cjs         # Script de precarga (bridge seguro)
│   ├── src/                     # Código fuente React
│   │   ├── App.jsx             # Componente principal
│   │   ├── Terminal.jsx        # Componente de terminal SSH
│   │   ├── SftpBrowserDual.jsx # Navegador SFTP de doble panel
│   │   ├── api.js              # Cliente API para backend
│   │   └── styles.css          # Estilos personalizados
│   ├── package.json            # Dependencias Node.js
│   └── vite.config.js          # Configuración de Vite
│
├── 📄 pom.xml                   # Configuración Maven
├── 📄 .gitignore                # Archivos ignorados por Git
│
├── 🖥️ INICIAR-BACKEND.bat      # Script de inicio rápido (Windows - Backend)
├── 🖥️ INICIAR-FRONTEND.bat     # Script de inicio rápido (Windows - Frontend)
├── 🐧 iniciar-backend.sh        # Script de inicio rápido (Linux - Backend)
├── 🐧 iniciar-frontend.sh       # Script de inicio rápido (Linux - Frontend)
└── 🔧 CONFIGURAR-SSH-WINDOWS.bat # Configuración OpenSSH Server en Windows
```

---

## 🚀 Instalación y Configuración

### 📋 Requisitos Previos

#### **Backend (Java/Spring Boot)**
- ✅ **Java JDK 21** o superior ([Descargar](https://adoptium.net/))
- ✅ **Maven 3.9+** ([Descargar](https://maven.apache.org/download.cgi))
- ✅ **Puerto 8080** disponible (configurable en `application.properties`)

#### **Frontend (Electron/React)**
- ✅ **Node.js 18+** ([Descargar](https://nodejs.org/))
- ✅ **npm 9+** (incluido con Node.js)
- ✅ **Puerto 5173** disponible (Vite dev server)

#### **Verificar Instalaciones**
```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Node.js y npm
node -v
npm -v
```

---

## ⚙️ Configuración Inicial

### 1️⃣ **Clonar el Repositorio**

```bash
git clone https://github.com/MrBrad8989/SecureFork.git
cd SecureFork
```

### 2️⃣ **Configurar el Backend**

```bash
# Compilar el proyecto y descargar dependencias
mvn clean install

# (Opcional) Verificar que la compilación fue exitosa
mvn test
```

### 3️⃣ **Configurar el Frontend**

```bash
# Navegar al directorio del cliente Electron
cd electron-client

# Instalar dependencias de Node.js
npm install

# Volver al directorio raíz
cd ..
```

---

## 🎯 Ejecución del Proyecto

### 🐧 **En Linux (Recomendado para Exposición)**

#### **Opción 1: Scripts Automáticos**

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x iniciar-backend.sh iniciar-frontend.sh

# Terminal 1: Iniciar el backend
./iniciar-backend.sh

# Terminal 2: Iniciar el frontend (en otra terminal)
./iniciar-frontend.sh
```

#### **Opción 2: Ejecución Manual**

```bash
# Terminal 1: Backend
mvn spring-boot:run

# Terminal 2: Frontend (en otra terminal)
cd electron-client
npm run dev
```

---

### 🪟 **En Windows**

#### **Opción 1: Scripts Automáticos (Doble Clic)**

1. **Ejecutar `INICIAR-BACKEND.bat`** (mantener abierta la ventana)
2. **Ejecutar `INICIAR-FRONTEND.bat`** (en otra ventana)

#### **Opción 2: Ejecución Manual**

```cmd
REM Terminal 1: Backend
mvn spring-boot:run

REM Terminal 2: Frontend
cd electron-client
npm run dev
```

---

## 🔍 Verificar que Todo Funciona

### ✅ **Backend (API REST)**
- El backend debe estar corriendo en: **http://localhost:8080**
- Prueba el endpoint de salud:
  ```bash
  curl http://localhost:8080/api/health
  ```

### ✅ **Frontend (Electron App)**
- La aplicación Electron debe abrirse automáticamente
- Si no se abre, verifica que el proceso de Vite esté corriendo en el puerto **5173**

### ✅ **Prueba de Conexión SSH**
1. En la aplicación, completa el formulario de conexión:
   - **Host:** `localhost` (o servidor SSH de prueba)
   - **Puerto:** `22` (puerto SSH estándar)
   - **Usuario:** Tu usuario SSH
   - **Contraseña:** Tu contraseña SSH
2. Clic en **Conectar**
3. Deberías ver una terminal interactiva

### ✅ **Prueba de Conexión SFTP**
1. Selecciona **SFTP** en el tipo de conexión
2. Completa los mismos datos
3. Deberías ver el navegador de doble panel con tus archivos

---

## 🏗️ Compilación para Producción

### **Backend: Generar JAR Ejecutable**

```bash
# Compilar el proyecto y generar el JAR
mvn clean package

# El JAR estará en: target/ServidorSeguroFork-1.0-SNAPSHOT.jar

# Ejecutar el JAR
java -jar target/ServidorSeguroFork-1.0-SNAPSHOT.jar
```

### **Frontend: Generar Aplicación Desktop**

```bash
cd electron-client

# Build de producción
npm run build

# Ejecutar la versión de producción
npm run start
```

Para empaquetar como aplicación instalable (`.exe`, `.dmg`, `.AppImage`), considera usar [electron-builder](https://www.electron.build/):

```bash
npm install --save-dev electron-builder

# Configurar package.json y ejecutar
npm run dist
```

---

## 📖 Documentación

### 🌐 **Endpoints de la API REST**

#### **SSH**
- `POST /api/ssh/connect` - Conectar a servidor SSH
- `POST /api/ssh/command` - Ejecutar comando SSH
- `POST /api/ssh/disconnect` - Desconectar sesión SSH

#### **SFTP**
- `POST /api/sftp/connect` - Conectar a servidor SFTP
- `GET /api/sftp/list` - Listar archivos en directorio
- `POST /api/sftp/download` - Descargar archivo remoto
- `POST /api/sftp/upload` - Subir archivo local
- `GET /api/sftp/read` - Leer contenido de archivo remoto
- `POST /api/sftp/disconnect` - Desconectar sesión SFTP

### 🧩 **Arquitectura**

```
┌─────────────────────────────────────────────────────────┐
│             Electron App (Frontend)                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │  React Components (UI)                           │  │
│  │  - Terminal (xterm.js)                           │  │
│  │  - SftpBrowserDual                               │  │
│  │  - Connection Form                               │  │
│  └────────────────┬─────────────────────────────────┘  │
│                   │ HTTP/REST (Axios)                   │
└───────────────────┼─────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────┐
│         Spring Boot Backend (API REST)                   │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Controllers (REST Endpoints)                    │  │
│  │  - SshController                                 │  │
│  │  - SftpController                                │  │
│  └────────────────┬─────────────────────────────────┘  │
│                   │                                      │
│  ┌────────────────▼─────────────────────────────────┐  │
│  │  Services (Business Logic)                       │  │
│  │  - SshService (Apache Mina SSHD)                 │  │
│  │  - SftpService (Apache Mina SSHD)                │  │
│  │  - SessionManager                                │  │
│  └────────────────┬─────────────────────────────────┘  │
└───────────────────┼─────────────────────────────────────┘
                    │
                    ▼
        ┌───────────────────────────┐
        │   Servidor SSH/SFTP       │
        │   (OpenSSH, etc.)         │
        └───────────────────────────┘
```

---

## 🐛 Solución de Problemas

### ❌ **Error: Puerto 8080 ya en uso**
```bash
# Linux
sudo lsof -i :8080
sudo kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### ❌ **Error: Maven no encuentra Java**
```bash
# Linux/macOS
export JAVA_HOME=/ruta/a/tu/jdk-21
export PATH=$JAVA_HOME/bin:$PATH

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
```

### ❌ **Error: npm install falla**
```bash
# Limpiar caché de npm
npm cache clean --force

# Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install
```

### ❌ **Error: Electron no se abre**
- Verifica que el backend esté corriendo en `http://localhost:8080`
- Verifica que Vite esté corriendo en `http://localhost:5173`
- Mira los logs de la consola de Electron (se abre automáticamente en modo dev)

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📜 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

---

## 👨‍💻 Autor

**MrBrad8989**  
GitHub: [@MrBrad8989](https://github.com/MrBrad8989)

---

## ⭐ Agradecimientos

- [Spring Boot](https://spring.io/projects/spring-boot) - Framework backend
- [Apache Mina SSHD](https://mina.apache.org/sshd-project/) - Implementación SSH/SFTP
- [Electron](https://www.electronjs.org/) - Framework desktop multiplataforma
- [React](https://reactjs.org/) - Biblioteca UI
- [xterm.js](https://xtermjs.org/) - Emulador de terminal

---

<div align="center">

**⭐ Si te gusta este proyecto, dale una estrella en GitHub ⭐**

[🔝 Volver arriba](#-securefork)

</div>

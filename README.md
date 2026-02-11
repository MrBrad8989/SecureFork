# SecureFork - Cliente SSH Seguro con Interfaz Gráfica

## 🚀 Descripción

SecureFork es un cliente SSH seguro multiplataforma desarrollado en Java con JavaFX. Proporciona:
- **Conexión SSL/TLS segura** con autenticación
- **Interfaz gráfica moderna** tipo terminal
- **Transferencia de archivos** (comando `get`)
- **Ejecución remota de comandos**
- **Multiplataforma** (Windows, Linux, macOS)

## 📋 Requisitos

- **Java 21** o superior
- **Maven 3.6+**
- Certificado SSL (`servidor_keystore.p12`)

## 🔧 Instalación

1. **Clonar/descargar el proyecto**

2. **Compilar con Maven** (descarga automáticamente JavaFX):
```bash
mvn clean install
```

## 🎮 Uso

### Ejecutar el Servidor (en una terminal):
```bash
mvn exec:java -Dexec.mainClass="com.refork.server.ServidorSSL"
```

### Ejecutar el Cliente GUI (en otra terminal):
```bash
mvn javafx:run
```

⚠️ **IMPORTANTE**: Para ejecutar la GUI, usa `mvn javafx:run` NO `mvn exec:java`

### Ejecutar el Cliente en Consola (versión antigua):
```bash
mvn exec:java -Dexec.mainClass="com.refork.client.ClienteSSL"
```

### Usar Scripts de Ejecución:
```bash
# Linux/macOS
chmod +x ejecutar.sh
./ejecutar.sh

# Windows
ejecutar.bat
```

## 🖥️ Características de la Interfaz

### Ventana de Conexión
- **Host**: Dirección del servidor (default: localhost)
- **Puerto**: Puerto SSL (default: 5555)
- **Usuario**: Nombre de usuario (default: admin)
- **Contraseña**: Password (default: 1234)

### Terminal Emulada
- **Área de terminal** con salida en tiempo real
- **Comandos rápidos** en panel lateral
- **Campo de entrada** con historial
- **Descarga de archivos** visual

## 📝 Comandos Disponibles

| Comando | Descripción |
|---------|-------------|
| `ls` | Listar archivos del directorio actual |
| `cd [directorio]` | Cambiar de directorio |
| `cd..` | Subir al directorio padre |
| `pwd` | Mostrar directorio actual |
| `get [archivo]` | Descargar archivo del servidor |
| `whoami` | Mostrar usuario actual del sistema |
| `date` | Mostrar fecha y hora |
| `exit` | Cerrar sesión |

## 🎨 Personalización

Los estilos CSS están en: `src/main/resources/styles.css`

Puedes modificar:
- Colores del terminal
- Fuentes
- Tamaños
- Efectos visuales

## 🔐 Seguridad

- Conexión SSL/TLS con certificados
- Autenticación mediante objetos serializados
- Validación de credenciales en el servidor
- Pool de hilos para múltiples conexiones

## 📦 Estructura del Proyecto

```
src/main/java/com/refork/
├── gui/                          # Interfaz gráfica JavaFX
│   ├── ClienteGUIApp.java       # Aplicación principal
│   ├── ConexionSSL.java         # Gestión de conexión
│   └── TerminalView.java        # Vista del terminal
├── client/                       # Cliente consola
│   ├── ClienteSSL.java          # Cliente original
│   ├── GestorCliente.java       # Gestor de sesiones
│   └── PeticionLogin.java       # DTO de autenticación
└── server/                       # Servidor
    └── ServidorSSL.java         # Servidor SSL/TLS

src/main/resources/
└── styles.css                    # Estilos CSS de la GUI
```

## 🌍 Multiplataforma

JavaFX se encarga automáticamente de:
- **Windows**: Ejecutable nativo
- **Linux**: Integración con GTK/Qt
- **macOS**: Look and feel nativo

## 🐛 Solución de Problemas

### Error: "Cannot resolve symbol 'javafx'"
Ejecuta:
```bash
mvn clean install
```

### Error: "JavaFX runtime components are missing"
**Solución**: Usa el comando correcto:
```bash
mvn javafx:run  # ✅ CORRECTO
```
NO uses:
```bash
mvn exec:java -Dexec.mainClass="com.refork.gui.ClienteGUIApp"  # ❌ NO FUNCIONA
```
Ver `SOLUCION_JAVAFX.md` para más detalles.

### Error: "No se encuentra el keystore"
Asegúrate de que `servidor_keystore.p12` está en la raíz del proyecto.

### Error: "Connection refused"
1. Verifica que el servidor esté ejecutándose
2. Comprueba host y puerto correctos
3. Revisa firewall/antivirus

## 📄 Credenciales por Defecto

- **Usuario**: admin
- **Contraseña**: 1234

⚠️ **Importante**: Cambiar en producción (ver `GestorCliente.java`)

## 🚀 Crear Ejecutable (Opcional)

Para crear un JAR ejecutable con todas las dependencias:
```bash
mvn clean package shade:shade
```

Ejecutar el JAR:
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/ServidorSeguroFork-1.0-SNAPSHOT.jar
```

## 📚 Tecnologías Utilizadas

- **Java 21**
- **JavaFX 21.0.1** (interfaz gráfica)
- **SSLSocket** (conexión segura)
- **ExecutorService** (pool de hilos)
- **Maven** (gestión de dependencias)

## 👨‍💻 Autor

Proyecto desarrollado para la asignatura de Programación de Servicios y Procesos.

---

**¡Disfruta de tu cliente SSH seguro con interfaz gráfica!** 🎉


const { app, BrowserWindow } = require("electron");
const path = require("path");
const fs = require("fs");

// Mejoras de renderizado en Linux
if (process.platform === "linux") {
  app.commandLine.appendSwitch("enable-features", "WebContentsFontRendering");
  app.commandLine.appendSwitch("font-render-hinting", "medium");
}
// Evitar parpadeos y artefactos en todos los SO
app.commandLine.appendSwitch("disable-gpu-vsync");
app.commandLine.appendSwitch("disable-frame-rate-limit");

let mainWindow;

function resolvePreloadPath() {
  const devPath = path.join(__dirname, "preload.cjs");
  if (fs.existsSync(devPath)) {
    return devPath;
  }
  const appPath = path.join(app.getAppPath(), "electron", "preload.cjs");
  return appPath;
}

function createWindow() {
  const preloadPath = resolvePreloadPath();
  console.log("[Electron] preload:", preloadPath);

  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 960,
    minHeight: 640,
    backgroundColor: "#0f172a",
    autoHideMenuBar: true,
    icon: path.join(__dirname, "..", "public", "icon.png"),
    webPreferences: {
      preload: preloadPath,
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false
    }
  });

  const devServerUrl = process.env.VITE_DEV_SERVER_URL;
  if (devServerUrl) {
    mainWindow.loadURL(devServerUrl);
    mainWindow.webContents.openDevTools({ mode: "detach" });
  } else {
    mainWindow.loadFile(path.join(__dirname, "..", "dist", "index.html"));
  }
}

app.whenReady().then(() => {
  createWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});

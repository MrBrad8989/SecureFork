const { contextBridge } = require("electron");
const os = require("os");
const fs = require("fs");
const path = require("path");

console.log("[Electron] preload loaded");

contextBridge.exposeInMainWorld("securefork", {
  version: "0.1.0",
  isElectron: true
});

contextBridge.exposeInMainWorld("electronBridge", {
  fsAvailable: true,
  pathAvailable: true
});

contextBridge.exposeInMainWorld("electron", {
  userHome: os.homedir()
});

contextBridge.exposeInMainWorld("electronFs", {
  readdirSync: fs.readdirSync,
  statSync: fs.statSync,
  readFileSync: fs.readFileSync
});

contextBridge.exposeInMainWorld("electronPath", {
  join: path.join,
  dirname: path.dirname
});

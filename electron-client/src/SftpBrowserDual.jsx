import React, { useEffect, useState } from "react";
import { sftpApi } from "./api.js";

const DEFAULT_LOCAL_FALLBACK = "C:\\Users\\Public";

export default function SftpBrowserDual({ sessionId, onList }) {
  const [remotePath, setRemotePath] = useState(".");
  const [localPath, setLocalPath] = useState(getDefaultLocalPath());
  const [remoteItems, setRemoteItems] = useState([]);
  const [localItems, setLocalItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRemote, setSelectedRemote] = useState(null);
  const [selectedLocal, setSelectedLocal] = useState(null);
  const [viewingFile, setViewingFile] = useState(null);
  const [transferring, setTransferring] = useState(false);
  const [localAvailable, setLocalAvailable] = useState(true);

  useEffect(() => {
    loadRemoteDirectory(remotePath);
    loadLocalDirectory(localPath);
  }, []);

  function getDefaultLocalPath() {
    const userHome = window.electron?.userHome || DEFAULT_LOCAL_FALLBACK;
    return userHome;
  }

  async function loadRemoteDirectory(path) {
    setLoading(true);
    try {
      const result = await onList(path);
      setRemoteItems(result);
      setRemotePath(path);
    } catch (error) {
      console.error("Error listing remote:", error);
    } finally {
      setLoading(false);
    }
  }

  function loadLocalDirectory(path) {
    try {
      const fs = window.electronFs;
      const pathModule = window.electronPath;
      const bridge = window.electronBridge;
      if (!fs || !pathModule || !bridge?.fsAvailable) {
        setLocalAvailable(false);
        setLocalItems([]);
        return;
      }

      const items = fs.readdirSync(path).map((name) => {
        const fullPath = pathModule.join(path, name);
        const stats = fs.statSync(fullPath);
        return {
          name,
          isDirectory: stats.isDirectory(),
          size: stats.size,
          fullPath
        };
      });
      setLocalAvailable(true);
      setLocalItems(items);
      setLocalPath(path);
    } catch (error) {
      setLocalAvailable(false);
      console.error("Error listing local:", error);
    }
  }

  function handleRemoteClick(item) {
    if (item.isDirectory) {
      const newPath = remotePath === "." ? item.name : `${remotePath}/${item.name}`;
      loadRemoteDirectory(newPath);
      setSelectedRemote(null);
      return;
    }
    setSelectedRemote(item);
  }

  function handleLocalClick(item) {
    if (item.isDirectory) {
      loadLocalDirectory(item.fullPath);
      setSelectedLocal(null);
      return;
    }
    setSelectedLocal(item);
  }

  function goUpRemote() {
    if (remotePath === ".") return;
    const parts = remotePath.split("/");
    parts.pop();
    const newPath = parts.length > 0 ? parts.join("/") : ".";
    loadRemoteDirectory(newPath);
  }

  function goUpLocal() {
    const pathModule = window.electronPath;
    if (!pathModule) return;
    const parent = pathModule.dirname(localPath);
    if (parent !== localPath) {
      loadLocalDirectory(parent);
    }
  }

  async function downloadFile() {
    if (!selectedRemote || selectedRemote.isDirectory) return;
    const pathModule = window.electronPath;
    if (!pathModule) return;

    const localFile = pathModule.join(localPath, selectedRemote.name);
    const remoteFile = remotePath === "." ? selectedRemote.name : `${remotePath}/${selectedRemote.name}`;

    setTransferring(true);
    try {
      const result = await sftpApi.download(sessionId, remoteFile, localFile);
      if (result.success) {
        const destino = result.localPath || localFile;
        alert("✅ Descargado en:\n" + destino);
        loadLocalDirectory(localPath);
      } else {
        alert("❌ Error: " + result.message);
      }
      setSelectedRemote(null);
    } catch (error) {
      alert("❌ Error descargando: " + error.message);
    } finally {
      setTransferring(false);
    }
  }

  async function uploadFile() {
    if (!selectedLocal || selectedLocal.isDirectory) return;
    const remoteFile = remotePath === "." ? selectedLocal.name : `${remotePath}/${selectedLocal.name}`;

    setTransferring(true);
    try {
      const result = await sftpApi.upload(sessionId, selectedLocal.fullPath, remoteFile);
      if (result.success) {
        alert("✅ Archivo subido correctamente");
        await loadRemoteDirectory(remotePath);
      } else {
        alert("❌ Error: " + result.message);
      }
      setSelectedLocal(null);
    } catch (error) {
      alert("❌ Error subiendo: " + error.message);
    } finally {
      setTransferring(false);
    }
  }

  async function viewFile(item, isRemote) {
    if (item.isDirectory) return;

    try {
      let content;
      if (isRemote) {
        const remoteFile = remotePath === "." ? item.name : `${remotePath}/${item.name}`;
        const result = await sftpApi.read(sessionId, remoteFile);
        if (!result.success) {
          alert("❌ Error leyendo archivo: " + result.message);
          return;
        }
        content = result.content;
      } else {
        const fs = window.electronFs;
        if (!fs) return;
        content = fs.readFileSync(item.fullPath, "utf-8");
      }
      setViewingFile({ name: item.name, content });
    } catch (error) {
      alert("❌ Error leyendo archivo: " + error.message);
    }
  }

  return (
    <div className="sftp-dual">
      {viewingFile ? (
        <div className="file-viewer">
          <div className="viewer-header">
            <h3>📄 {viewingFile.name}</h3>
            <button onClick={() => setViewingFile(null)} className="btn-close-viewer">
              Cerrar
            </button>
          </div>
          <pre className="file-content">{viewingFile.content}</pre>
        </div>
      ) : (
        <>
          <div className="sftp-panel">
            <div className="panel-header">
              <h3>💻 Local</h3>
              <div style={{ fontSize: "0.8rem", color: "#cbd5e1" }}>{localPath}</div>
            </div>
            <div className="sftp-toolbar">
              <button onClick={goUpLocal} className="btn-nav" disabled={!localPath}>
                ⬆️ Arriba
              </button>
              <input className="sftp-path" type="text" value={localPath} readOnly />
              <button onClick={() => loadLocalDirectory(localPath)} className="btn-refresh">
                🔄
              </button>
            </div>
            <div className="sftp-list">
              {!localAvailable ? (
                <div className="sftp-loading">
                  Acceso local no disponible.
                  <div style={{ marginTop: "8px" }}>
                    Verifica que el preload cargue y que el FS local este disponible.
                  </div>
                  <button className="btn-refresh" onClick={() => loadLocalDirectory(localPath)}>
                    Reintentar
                  </button>
                </div>
              ) : (
                localItems.map((item, idx) => (
                  <div
                    key={idx}
                    className={`sftp-item ${selectedLocal?.name === item.name ? "selected" : ""}`}
                    onClick={() => handleLocalClick(item)}
                    onDoubleClick={() => (item.isDirectory ? null : viewFile(item, false))}
                    title={item.isDirectory ? "Click para entrar" : "Doble click para ver contenido"}
                  >
                    <span className="sftp-icon">{item.isDirectory ? "📁" : "📄"}</span>
                    <span className="sftp-name">{item.name}</span>
                    {!item.isDirectory && <span className="sftp-size">{formatSize(item.size)}</span>}
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="sftp-actions">
            <button
              onClick={uploadFile}
              disabled={!selectedLocal || selectedLocal.isDirectory || transferring}
              className="btn-transfer"
              title="Subir archivo seleccionado al servidor"
            >
              {transferring ? "⏳" : "➡️"}
              <br />
              {transferring ? "..." : "Subir"}
            </button>
            <button
              onClick={downloadFile}
              disabled={!selectedRemote || selectedRemote.isDirectory || transferring}
              className="btn-transfer"
              title="Descargar archivo seleccionado del servidor"
            >
              {transferring ? "⏳" : "⬅️"}
              <br />
              {transferring ? "..." : "Descargar"}
            </button>
          </div>

          <div className="sftp-panel">
            <div className="panel-header">
              <h3>🌐 Remoto</h3>
            </div>
            <div className="sftp-toolbar">
              <button onClick={goUpRemote} className="btn-nav" disabled={remotePath === "."}>
                ⬆️ Arriba
              </button>
              <input
                className="sftp-path"
                type="text"
                value={remotePath}
                onChange={(e) => setRemotePath(e.target.value)}
              />
              <button onClick={() => loadRemoteDirectory(remotePath)} className="btn-nav">
                🔍 Ir
              </button>
              <button onClick={() => loadRemoteDirectory(remotePath)} className="btn-refresh">
                🔄
              </button>
            </div>
            <div className="sftp-list">
              {loading ? (
                <div className="sftp-loading">Cargando...</div>
              ) : (
                remoteItems.map((item, idx) => (
                  <div
                    key={idx}
                    className={`sftp-item ${selectedRemote?.name === item.name ? "selected" : ""}`}
                    onClick={() => handleRemoteClick(item)}
                    onDoubleClick={() => (item.isDirectory ? null : viewFile(item, true))}
                    title={item.isDirectory ? "Click para entrar" : "Doble click para ver contenido"}
                  >
                    <span className="sftp-icon">{item.isDirectory ? "📁" : "📄"}</span>
                    <span className="sftp-name">{item.name}</span>
                    {!item.isDirectory && <span className="sftp-size">{formatSize(item.size)}</span>}
                  </div>
                ))
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
}

function formatSize(bytes) {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

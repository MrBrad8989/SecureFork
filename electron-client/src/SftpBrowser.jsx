import React, { useState, useEffect } from "react";

export default function SftpBrowser({ sessionId, onList }) {
  const [currentPath, setCurrentPath] = useState(".");
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadDirectory(currentPath);
  }, [currentPath]);

  const loadDirectory = async (path) => {
    setLoading(true);
    try {
      const result = await onList(path);
      setItems(result);
    } catch (error) {
      console.error("Error listing:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleItemClick = (item) => {
    if (item.isDirectory) {
      const newPath = currentPath === "." ? item.name : `${currentPath}/${item.name}`;
      setCurrentPath(newPath);
    }
  };

  return (
    <div className="sftp-browser">
      <div className="sftp-toolbar">
        <input
          type="text"
          value={currentPath}
          onChange={(e) => setCurrentPath(e.target.value)}
          className="sftp-path"
        />
        <button onClick={() => loadDirectory(currentPath)}>Refrescar</button>
      </div>

      {loading ? (
        <div className="sftp-loading">Cargando...</div>
      ) : (
        <div className="sftp-list">
          {items.map((item, idx) => (
            <div
              key={idx}
              className="sftp-item"
              onClick={() => handleItemClick(item)}
            >
              <span className="sftp-icon">{item.isDirectory ? "📁" : "📄"}</span>
              <span className="sftp-name">{item.name}</span>
              {!item.isDirectory && (
                <span className="sftp-size">{formatSize(item.size)}</span>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function formatSize(bytes) {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / (1024 * 1024)).toFixed(1) + " MB";
}


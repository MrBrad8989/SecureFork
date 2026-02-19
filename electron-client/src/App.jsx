import React, { useMemo, useState } from "react";
import { sshApi, sftpApi } from "./api.js";
import TerminalComponent from "./Terminal.jsx";
import SftpBrowserDual from "./SftpBrowserDual.jsx";

const defaultForm = {
  type: "SecureFork",
  host: "localhost",
  port: "8282",
  user: "admin",
  password: "",
  trust: "servidor_keystore.p12"
};

function formatTabTitle(tab) {
  const prefix = tab.type === "SFTP" ? "SFTP" : "SSH";
  return `${prefix} ${tab.host}`;
}

export default function App() {
  const [form, setForm] = useState(defaultForm);
  const [tabs, setTabs] = useState([]);
  const [activeId, setActiveId] = useState(null);
  const [status, setStatus] = useState("");
  const [connecting, setConnecting] = useState(false);
  const isElectron = window.securefork?.isElectron === true;
  const fsReady = typeof window.electronFs !== "undefined" && typeof window.electronPath !== "undefined";

  const activeTab = useMemo(
    () => tabs.find((t) => t.id === activeId) || null,
    [tabs, activeId]
  );

  function updateField(key, value) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  async function connect() {
    if (!form.host || !form.user) {
      setStatus("❌ Completa host y usuario.");
      return;
    }

    if (form.type !== "SFTP" && !form.password) {
      setStatus("❌ La contraseña es obligatoria para este tipo de conexión.");
      return;
    }

    setConnecting(true);
    setStatus("⏳ Conectando...");

    try {
      let result;
      if (form.type === "SFTP") {
        result = await sftpApi.connect(form.host, form.port, form.user, form.password);
      } else {
        result = await sshApi.connect(form.host, form.port, form.user, form.password);
      }

      if (result.success) {
        const tab = {
          id: result.sessionId,
          type: form.type,
          host: form.host,
          port: form.port,
          user: form.user,
          sessionId: result.sessionId
        };
        setTabs((prev) => [...prev, tab]);
        setActiveId(result.sessionId);
        setStatus("");
        setForm({ ...defaultForm });
      } else {
        setStatus("❌ " + result.message);
      }
    } catch (error) {
      setStatus("❌ Error: " + error.message);
    } finally {
      setConnecting(false);
    }
  }

  async function closeTab(id) {
    const tab = tabs.find((t) => t.id === id);
    if (tab) {
      try {
        if (tab.type === "SFTP") {
          await sftpApi.disconnect(tab.sessionId);
        } else {
          await sshApi.disconnect(tab.sessionId);
        }
      } catch (error) {
        console.error("Error disconnecting:", error);
      }
    }

    setTabs((prev) => prev.filter((t) => t.id !== id));
    if (activeId === id) {
      const remaining = tabs.filter((t) => t.id !== id);
      setActiveId(remaining.length ? remaining[0].id : null);
    }
  }

  const handleSshCommand = async (command, callback) => {
    if (!activeTab) return;
    try {
      const result = await sshApi.executeCommand(activeTab.sessionId, command);
      callback(result.output || result.error || "");
    } catch (error) {
      callback("Error: " + error.message);
    }
  };

  const handleSftpList = async (path) => {
    if (!activeTab) return [];
    try {
      return await sftpApi.list(activeTab.sessionId, path);
    } catch (error) {
      console.error("SFTP list error:", error);
      return [];
    }
  };

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <div className="logo">SF</div>
          <div>
            <h1>SecureFork</h1>
            <p>Cliente SSH/SFTP</p>
          </div>
        </div>

        <div className="card">
          <h2>Nueva conexión</h2>
          <label>
            Tipo
            <select
              value={form.type}
              onChange={(e) => updateField("type", e.target.value)}
              disabled={connecting}
            >
              <option>SecureFork</option>
              <option>SFTP</option>
            </select>
          </label>
          <label>
            Host
            <input
              value={form.host}
              onChange={(e) => updateField("host", e.target.value)}
              disabled={connecting}
            />
          </label>
          <label>
            Puerto
            <input
              value={form.port}
              onChange={(e) => updateField("port", e.target.value)}
              disabled={connecting}
            />
          </label>
          <label>
            Usuario
            <input
              value={form.user}
              onChange={(e) => updateField("user", e.target.value)}
              disabled={connecting}
            />
          </label>
          <label>
            Contraseña
            <input
              type="password"
              value={form.password}
              onChange={(e) => updateField("password", e.target.value)}
              disabled={connecting}
              placeholder={form.type === "SFTP" ? "Opcional" : "Obligatoria"}
            />
          </label>
          {form.type === "SecureFork" && (
            <label>
              Truststore
              <input
                value={form.trust}
                onChange={(e) => updateField("trust", e.target.value)}
                disabled={connecting}
              />
            </label>
          )}
          <button className="primary" onClick={connect} disabled={connecting}>
            {connecting ? "Conectando..." : "Conectar"}
          </button>
          {status && <p className="status">{status}</p>}
        </div>

        <div className="hint">
          Conecta a servidores SSH/SFTP externos. El backend corre en localhost:8080.
        </div>
        <div className={`env-badge ${isElectron ? "env-ok" : "env-warn"}`}>
          Entorno: {isElectron ? "Electron" : "Navegador (sin acceso local)"}
        </div>
        <div className={`env-badge ${fsReady ? "env-ok" : "env-warn"}`}>
          FS local: {fsReady ? "Disponible" : "No disponible"}
        </div>
      </aside>

      <main className="content">
        <div className="tabs">
          <button
            className={`tab ${activeId === null ? "active" : ""}`}
            onClick={() => setActiveId(null)}
          >
            Inicio
          </button>
          {tabs.map((tab) => (
            <div key={tab.id} className={`tab ${activeId === tab.id ? "active" : ""}`}>
              <span onClick={() => setActiveId(tab.id)}>{formatTabTitle(tab)}</span>
              <button className="close" onClick={() => closeTab(tab.id)}>
                ×
              </button>
            </div>
          ))}
        </div>

        {activeTab ? (
          <section className="panel">
            <header>
              <h2>{formatTabTitle(activeTab)}</h2>
              <span>
                {activeTab.user}@{activeTab.host}:{activeTab.port}
              </span>
            </header>
            <div className="terminal-container">
              {activeTab.type === "SFTP" ? (
                <SftpBrowserDual sessionId={activeTab.sessionId} onList={handleSftpList} />
              ) : (
                <TerminalComponent sessionId={activeTab.sessionId} onCommand={handleSshCommand} />
              )}
            </div>
            <button className="ghost" onClick={() => closeTab(activeTab.id)}>
              Desconectar
            </button>
          </section>
        ) : (
          <section className="panel">
            <h2>Bienvenido a SecureFork</h2>
            <p>Conecta a servidores SSH o SFTP desde el panel lateral.</p>
            <p>
              <strong>Características:</strong>
            </p>
            <ul>
              <li>Terminal SSH interactiva en tiempo real</li>
              <li>Navegador de archivos SFTP</li>
              <li>Múltiples conexiones simultáneas en pestañas</li>
              <li>Conexión segura mediante API REST</li>
            </ul>
          </section>
        )}
      </main>
    </div>
  );
}

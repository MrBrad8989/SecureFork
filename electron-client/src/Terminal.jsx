import React, { useEffect, useRef } from "react";
import { Terminal } from "xterm";
import { FitAddon } from "xterm-addon-fit";
import "xterm/css/xterm.css";

export default function TerminalComponent({ sessionId, onCommand }) {
  const terminalRef = useRef(null);
  const xtermRef = useRef(null);
  const fitAddonRef = useRef(null);

  useEffect(() => {
    const term = new Terminal({
      cursorBlink: true,
      fontSize: 14,
      fontFamily: 'Consolas, "Courier New", monospace',
      theme: {
        background: "#0d1117",
        foreground: "#58a6ff",
        cursor: "#58a6ff"
      }
    });

    const fitAddon = new FitAddon();
    term.loadAddon(fitAddon);
    term.open(terminalRef.current);
    fitAddon.fit();

    xtermRef.current = term;
    fitAddonRef.current = fitAddon;

    let currentLine = "";
    term.write("$ ");

    term.onData((data) => {
      if (data === "\r") {
        // Enter
        term.write("\r\n");
        if (currentLine.trim()) {
          onCommand(currentLine.trim(), (output) => {
            term.write(output + "\r\n");
            term.write("$ ");
          });
        } else {
          term.write("$ ");
        }
        currentLine = "";
      } else if (data === "\u007F") {
        // Backspace
        if (currentLine.length > 0) {
          currentLine = currentLine.slice(0, -1);
          term.write("\b \b");
        }
      } else {
        currentLine += data;
        term.write(data);
      }
    });

    return () => {
      term.dispose();
    };
  }, [sessionId, onCommand]);

  return <div ref={terminalRef} style={{ width: "100%", height: "100%" }} />;
}


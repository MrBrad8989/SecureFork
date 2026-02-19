import axios from "axios";

const API_URL = "http://localhost:8080/api";

export const sshApi = {
  connect: async (host, port, user, password) => {
    const response = await axios.post(`${API_URL}/ssh/connect`, {
      host,
      port: parseInt(port),
      user,
      password,
      type: "SSH"
    });
    return response.data;
  },

  executeCommand: async (sessionId, comando) => {
    const response = await axios.post(`${API_URL}/ssh/command`, {
      sessionId,
      comando
    });
    return response.data;
  },

  disconnect: async (sessionId) => {
    await axios.post(`${API_URL}/ssh/disconnect`, { sessionId });
  }
};

export const sftpApi = {
  connect: async (host, port, user, password) => {
    const response = await axios.post(`${API_URL}/sftp/connect`, {
      host,
      port: parseInt(port),
      user,
      password,
      type: "SFTP"
    });
    return response.data;
  },

  list: async (sessionId, path) => {
    const response = await axios.get(`${API_URL}/sftp/list`, {
      params: { sessionId, path }
    });
    return response.data;
  },

  download: async (sessionId, remotePath, localPath) => {
    const response = await axios.post(`${API_URL}/sftp/download`, null, {
      params: { sessionId, remotePath, localPath }
    });
    return response.data;
  },

  upload: async (sessionId, localPath, remotePath) => {
    const response = await axios.post(`${API_URL}/sftp/upload`, null, {
      params: { sessionId, localPath, remotePath }
    });
    return response.data;
  },

  read: async (sessionId, remotePath) => {
    const response = await axios.get(`${API_URL}/sftp/read`, {
      params: { sessionId, remotePath }
    });
    return response.data;
  },

  disconnect: async (sessionId) => {
    await axios.post(`${API_URL}/sftp/disconnect`, { sessionId });
  }
};

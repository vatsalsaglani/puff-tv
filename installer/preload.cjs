const {contextBridge,ipcRenderer}=require('electron');
contextBridge.exposeInMainWorld('puff',{action:(name,data)=>ipcRenderer.invoke('action',name,data),onProgress:callback=>ipcRenderer.on('progress',(_event,value)=>callback(value))});

const {app,BrowserWindow,ipcMain,shell}=require('electron');
const path=require('node:path');
const {Installer}=require('./core.cjs');
if(!app.requestSingleInstanceLock())app.quit();
let win,service,busy=false;
const allowed=new Set(['https://developer.android.com/studio/terms','https://github.com/vatsalsaglani/puff-tv','https://vatsalsaglani.github.io/puff-tv/#install']);
app.whenReady().then(()=>{
 win=new BrowserWindow({width:940,height:790,minWidth:760,minHeight:650,backgroundColor:'#000000',title:'Puff Installer',autoHideMenuBar:true,webPreferences:{preload:path.join(__dirname,'preload.cjs'),contextIsolation:true,nodeIntegration:false,sandbox:true}});
 service=new Installer({directory:app.getPath('userData'),event:(message,data)=>{if(!win.isDestroyed())win.webContents.send('progress',{message,data});}});
 win.webContents.setWindowOpenHandler(()=>({action:'deny'}));
 win.webContents.on('will-navigate',event=>event.preventDefault());
 win.webContents.session.setPermissionRequestHandler((_wc,_permission,callback)=>callback(false));
 ipcMain.handle('action',async(event,action,payload={})=>{
  if(event.sender!==win.webContents||event.senderFrame!==win.webContents.mainFrame)throw Error('Invalid request.');
  if(action==='cancel'){service.cancel();return {ok:true};}
  if(action==='link'){if(allowed.has(payload.url))await shell.openExternal(payload.url);return {ok:true};}
  if(busy)return {ok:false,error:'Please wait for the current step to finish.'};
  busy=true;
  try{let data;switch(action){case 'status':data={ready:await service.toolsReady()};break;case 'prepare':await service.prepare(payload.accepted);data={ready:true};break;case 'scan':data=await service.scan();break;case 'connect':data=await service.connect(payload.address);break;case 'pair':data=await service.pair(payload.address,payload.code);break;case 'install':data=await service.install();break;default:throw Error('Unknown action.');}return {ok:true,data};}catch(e){return {ok:false,error:e.message};}finally{busy=false;}
 });
 win.loadFile(path.join(__dirname,'ui/index.html'));
});
let quitting=false;app.on('before-quit',event=>{if(service&&!quitting){event.preventDefault();quitting=true;service.close().finally(()=>app.quit());}});
app.on('window-all-closed',()=>app.quit());
app.on('second-instance',()=>{if(win){if(win.isMinimized())win.restore();win.focus();}});

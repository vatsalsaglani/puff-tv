const $=id=>document.getElementById(id);let busy=false;
function status(message,error=false){$('status').textContent=message;$('status').classList.toggle('error',error);}
function lock(value){busy=value;document.querySelectorAll('button:not([data-link])').forEach(b=>b.disabled=value);$('prepare').disabled=value||!$('license').checked;}
async function call(name,data){lock(true);try{const result=await window.puff.action(name,data);if(!result.ok)throw Error(result.error);return result.data;}catch(e){status(e.message,true);return null;}finally{lock(false);}}
function step(name){['tools','discover','confirm','done'].forEach(id=>$(id).hidden=id!==name);['step1','step2','step3'].forEach((id,i)=>$(id).classList.toggle('active',i===({tools:0,discover:1,confirm:2,done:2}[name])));}
window.puff.onProgress(({message,data})=>{if(message==='found'){if([...$('devices').children].some(b=>b.dataset.address===data.address))return;const b=document.createElement('button');b.className='device';b.dataset.address=data.address;const text=document.createElement('span');text.textContent=data.name;const sub=document.createElement('small');sub.textContent=data.address;b.append(text,sub);b.disabled=busy;b.onclick=()=>{if(data.pairing){$('pairing').open=true;$('pair-address').value=data.address;$('pair-code').focus();}else connect(data.address)};$('devices').append(b);}else status(message);});
$('license').onchange=()=>{$('prepare').disabled=busy||!$('license').checked};
$('prepare').onclick=async()=>{if(await call('prepare',{accepted:$('license').checked})){step('discover');status('Android tools are ready. Turn on network debugging on the TV, then search.');}};
$('scan').onclick=async()=>{$('devices').replaceChildren();$('cancel').hidden=false;status('Looking for TVs on your local network…');const pending=call('scan');$('cancel').disabled=false;const found=await pending;$('cancel').hidden=true;if(found)status(found.length?'Select your TV below. We’ll verify it before installing.':'No TVs found. Check network debugging, try again, or enter the TV’s address.');};
$('cancel').onclick=()=>window.puff.action('cancel');
async function connect(address){status('Connecting. Look for an approval prompt on your TV…');const tv=await call('connect',{address});if(tv){$('model').textContent=tv.model||'Android TV';$('target').textContent=tv.address;step('confirm');status('Connected. Ready to install.');}}
$('connect').onclick=()=>connect($('address').value.trim());
$('pair').onclick=async()=>{status('Pairing with your TV…');if(await call('pair',{address:$('pair-address').value.trim(),code:$('pair-code').value.trim()})){ $('pair-code').value='';$('manual').open=true;status('Paired. Enter the connection address shown on the main Wireless debugging screen, or search again.');}};
$('install').onclick=async()=>{const result=await call('install');if(result){$('success').textContent=result.opened?'Puff is installed and open on your TV.':'Puff is installed. Open it from your TV’s apps list.';step('done');status('All done. You can close this installer.');}};
$('back').onclick=()=>{step('discover');status('Select another TV or enter its address.');};
document.querySelectorAll('[data-link]').forEach(b=>b.onclick=()=>window.puff.action('link',{url:b.dataset.link}));
call('status').then(result=>{if(result?.ready)step('discover');});

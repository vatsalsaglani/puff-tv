const esc=s=>s.replaceAll('&','&amp;').replaceAll('<','&lt;');
const ease=x=>{x=Math.max(0,Math.min(1,x));return x*x*(3-2*x)};
const text=(id,x,y,label,size=14,color='#eee',extra='')=>`<text id="${id}" x="${x}" y="${y}" font-size="${size}" fill="${color}" ${extra}>${esc(label)}</text>`;
const rect=(x,y,w,h,r=12,fill='#080809',stroke='#38383d')=>`<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${r}" fill="${fill}" stroke="${stroke}" stroke-width=".7"/>`;
function cloud(x,y,s=1){return `<g transform="translate(${x} ${y}) scale(${s})"><path d="M8 30C-4 29-2 13 8 12C7-5 30-5 32 9C47 5 52 29 38 30Z" fill="#eee"/><circle cx="18" cy="17" r="1.5" fill="#080809"/><circle cx="30" cy="17" r="1.5" fill="#080809"/><path d="M21 22q3 3 6-1" fill="none" stroke="#080809" stroke-width="1.4"/></g>`}
export function renderSVG(frame,c){
 const i=c.sceneIndex,t=c.sceneTime,d=c.timing.duration,p=t/d;
 const click=ease((p-.18)/.12), result=p>.63;
 let art='', labels=[];
 const tx=(...args)=>{labels.push(args[0]);return text(...args)};
 const btn=(label,y,active=false)=>rect(52,y,268,29,15,active?'#ededf0':'#111113',active?'#fff':'#55555c')+tx('button-'+y,186,y+19,label,13,active?'#090909':'#eee','text-anchor="middle"');
 art+=rect(36,112,300,197,14,'#070708','#57575b');
 art+=`<path d="M36 142h300" stroke="#28282d" stroke-width=".7"/>`;
 art+=cloud(51,122,.36)+tx('app-name',75,132,i===0?'puff downloads':'puff installer',12);
 art+=`<circle cx="320" cy="127" r="2" fill="#555"/>`;
 art+=rect(387,130,217,137,9,'#060608','#55555b')+`<path d="M469 268v15h52v-15M446 285h98" fill="none" stroke="#55555b" stroke-width="1"/>`;
 if(i===0){art+=tx('download-title',52,165,'Choose your computer.',16);art+=btn('Mac · Apple silicon',178,click>.7)+btn('Mac · Intel',213)+btn('Windows · x64',248);art+=cloud(475,178,1)+tx('tv-label',495,244,'Android TV',13,'#aaa','text-anchor="middle"');}
 if(i===1){art+=tx('license-title',52,166,'A small toolkit.',18)+tx('license-subtitle',52,188,'Read the Android SDK license.',13,'#aaa');art+=rect(52,204,15,15,3,'#111','#777');if(click>.7)art+='<path d="m55 211 3 4 6-8" stroke="#fff" fill="none"/>';art+=tx('license-agree',75,216,'I have read and agree.',13);art+=btn(result?'Android tools ready':'Download Android tools',237,p>.45);if(p>.48)art+=`<rect x="52" y="281" width="${268*ease((p-.48)/.15)}" height="2" fill="#ddd"/>`;art+=cloud(476,179,1);}
 if(i===2){art+=tx('network-title',52,168,'Same trusted network.',17)+tx('network-subtitle',52,192,'Computer + Android TV',14,'#aaa');art+=btn('Find my TV',253);art+=tx('settings-title',402,153,'Developer options',14)+tx('settings-row',402,186,'Network debugging',13);art+=rect(552,203,34,17,9,click>.7?'#eee':'#303034','#555');art+=`<circle cx="${561+17*click}" cy="211.5" r="6" fill="${click>.7?'#111':'#aaa'}"/>`;art+=tx('menus-vary',402,245,'TV menus vary',12,'#aaa');art+=`<path d="M336 229h51" stroke="#aaa" stroke-dasharray="2 5" stroke-width="1.5"/>`;}
 if(i===3){art+=tx('find-title',52,166,'Turn on your TV.',18)+btn(p>.35?'Searching your network…':'Find my TV',180,click>.7);if(p>.5){art+=rect(52,221,268,32,9,p>.76?'#29292d':'#151518','#888')+tx('found-device',64,242,'Living room TV',14)+tx('address-option',52,277,'Not found? Enter its address.',12,'#aaa');}art+=cloud(475,176,1)+tx('nearby-tv',495,244,'Living room',13,'#aaa','text-anchor="middle"');}
 if(i===4){art+=tx('connection-title',52,166,result?'Connected to your TV':'Approve on your TV.',17)+tx('device-check',52,193,result?'Android TV · Android 9+':'Waiting for permission…',14,'#aaa')+btn(result?'Install Puff on this TV':'Checking connection…',253,result);if(!result){art+=tx('prompt-title',402,154,'Allow debugging?',15)+tx('prompt-sub',402,179,'Trust this computer?',13,'#aaa');art+=rect(410,204,170,29,15,click>.7?'#eee':'#111','#888')+tx('allow-button',495,223,'Allow',14,click>.7?'#080809':'#eee','text-anchor="middle"');}else{art+=tx('authorized',495,201,'Computer authorized',14,'#ddd','text-anchor="middle"');}}
 if(i===5){const state=p<.36?'Install Puff on this TV':p<.58?'Checking Puff…':p<.84?'Installing Puff…':'Installation complete';art+=tx('model-title',52,166,'Living room TV',18)+tx('model-detail',52,191,'Confirm your TV model.',14,'#aaa')+btn(state,218,click>.7);art+=`<rect x="52" y="270" width="268" height="2" fill="#27272c"/><rect x="52" y="270" width="${268*ease((p-.36)/.49)}" height="2" fill="#eee"/>`;art+=cloud(475,178,1)+tx('tv-install-state',495,244,p>.84?'Puff installed':'Keep your TV on',13,'#aaa','text-anchor="middle"');}
 if(i===6){art+=cloud(60,157,.9)+tx('done-title',52,219,'A little more room.',22)+btn('Open setup guide',253);art+=cloud(475,153,.8)+tx('puff-on-tv',495,202,'puff',25,'#eee','text-anchor="middle"')+tx('cast-home',495,230,'Cast · Home · Cleanup',13,'#aaa','text-anchor="middle"');}
 if(i<6){let target=i===2?[569,212]:i===4?[515,219]:i===1?[60,211]:i===5?[249,233]:i===3?[262,196]:[248,192];if(i===1&&p>.35)target=[245,252];if(i===3&&p>.6)target=[262,239];let x=target[0]+35*(1-click),y=target[1]+24*(1-click);if(p<.5||(i===3&&p>.6&&p<.84))art+=`<g transform="translate(${x} ${y})"><path d="m0 0 0 18 5-4 4 8 4-2-4-7 7-1Z" fill="#fff" stroke="#000" stroke-width="1"/></g>`;}
 const title=c.scene.title;
 let stars='';for(let n=0;n<35;n++){let x=24+(n*137%590),y=18+(n*83%290);stars+=`<circle cx="${x}" cy="${y}" r="${n%3===0?.7:.35}" fill="#777" opacity=".35"/>`;}
 const head=tx('film-brand',26,33,'PUFF / INSTALLER',12,'#999')+tx('headline',24,80,title,32);
 let foot=tx('step-caption',24,341,`0${i+1} / 07`,12,'#aaa')+tx('illustration-label',616,341,'Illustrative walkthrough · Beta 0.1',12,'#888','text-anchor="end"');
 for(let n=0;n<7;n++)foot+=`<rect x="${125+n*20}" y="333" width="14" height="2" fill="${n<=i?'#ddd':'#333'}"/>`;
 const assertions=[{id:'headline',textEquals:title,visible:true},{id:'illustration-label',textIncludes:'Illustrative walkthrough',visible:true}];
 if(i===1)assertions.push({id:'button-237',textEquals:result?'Android tools ready':'Download Android tools',visible:true});
 if(i===3)assertions.push({id:'found-device',visible:p>.5});
 if(i===4){assertions.push({id:'authorized',visible:result},{id:'prompt-title',visible:!result},{id:'button-253',textEquals:result?'Install Puff on this TV':'Checking connection…',visible:true});}
 if(i===5)assertions.push({id:'tv-install-state',textEquals:p>.84?'Puff installed':'Keep your TV on',visible:true});
 return {svg:`<svg xmlns="http://www.w3.org/2000/svg" width="${c.viewport.width}" height="${c.viewport.height}" viewBox="0 0 640 360"><rect width="640" height="360" fill="#000"/>${stars}<g font-family="Arial, sans-serif"><g data-zone="title">${head}</g><g data-zone="content" data-content-root="true">${art}</g><g data-zone="footer">${foot}</g></g></svg>`,metadata:{causalState:{scene:i,phase:p,illustrative:true},semanticChecks:[{id:'ordered-installation-state',passed:true,assertions}]},audit:{texts:labels.map(id=>({id,essential:true})),objects:[]}};
}

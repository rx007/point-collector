var RTBcs={client:"chrome",clsid:"f18e345d-28f1-4a58-8020-60394612b46c",toolid:1,startPages:["http://websearch.rakuten.co.jp/static/chrome/install.html","http://websearch.rakuten.co.jp/static/chrome/welcome.html"],RakutenWebSearch:{host:"websearch.rakuten.co.jp",path:"/Web",queryKey:"qt"},searchProviders:[{host:"www.google.co.jp",queryKey:"q"},{host:"www.google.com",queryKey:"q"},{host:"search.yahoo.co.jp",queryKey:"p"},{host:"www.bing.com",queryKey:"q"},{host:"search.goo.ne.jp",queryKey:"MT"}],loginCheckers:["https://websearch.rakuten.co.jp/pointMailControl","https://grp03.id.rakuten.co.jp/rms/nid/logout?service_id=p45","https://member.id.rakuten.co.jp/rms/nid/"],simpleTop:["http://websearch.rakuten.co.jp/SimpleTop"],messageListUpdateCheckers:["http://msg.websearch.rakuten.co.jp/view/AllMsg"],init:function(){this.showStart();this.checkSearchProvider();this.checkLogin();this.openSetting();chrome.extension.sendRequest({method:"loaded"});this.checkListUpdate()},showStart:function(){var a=window.location.href;for(var b=0;b<this.startPages.length;b++){if(a==this.startPages[b]){chrome.extension.sendRequest({method:"deleteSpFlag"});this._setUuid();this._setTbuid()}}},_setUuid:function(){if(!localStorage.uuid){delete localStorage.tbuid;function b(){return(((1+Math.random())*65536)|0).toString(16).substring(1)}function a(){return(b()+b()+"-"+b()+"-"+b()+"-"+b()+"-"+b()+b()+b())}localStorage.uuid=a()}chrome.extension.sendRequest({method:"uuid",value:localStorage.uuid})},_setTbuid:function(a){if(!localStorage.tbuid){chrome.extension.sendRequest({method:"getTbuid",action:"newuser",client:this.client,m:localStorage.uuid,g:this.clsid,toolid:this.toolid},function(b){localStorage.tbuid=b.tbuid;if(a){a()}})}else{chrome.extension.sendRequest({method:"saveTbuid",value:localStorage.tbuid});if(a){a()}}},checkSearchProvider:function(){var b=window.location.href;var a=parseUri(b);if(a.host==this.RakutenWebSearch.host&&a.queryKey[this.RakutenWebSearch.queryKey]){if(document.referrer==""){chrome.extension.sendRequest({method:"default",value:true})}this._setUuid();this._setTbuid(function(){RTBcs._runTBI();RTBcs._getRtbSess()})}else{if(document.referrer==""){for(var d in this.searchProviders){if(this.searchProviders[d].host=="www.google.co.jp"){var c=b.replace("#","");a=parseUri(c)}if(a.host==this.searchProviders[d].host&&a.queryKey[this.searchProviders[d].queryKey]){chrome.extension.sendRequest({method:"default",value:false});break}}}}},_runTBI:function(){var a={UserID:localStorage.tbuid,toolid:1};var c=document.createEvent("Event");c.initEvent("bridgeEvent",true,true);function b(e){var d=document.getElementById("bridge");d.innerText=e;d.dispatchEvent(c)}b(JSON.stringify(a))},_getRtbSess:function(){chrome.extension.sendRequest({method:"getRtbSess",userid:localStorage.tbuid},function(a){})},_getDateString:function(){var a=new Date();yyyy=a.getFullYear();mm=a.getMonth()+1;dd=a.getDate();if(mm<10){mm="0"+mm}if(dd<10){dd="0"+dd}return yyyy+"/"+mm+"/"+dd},checkLogin:function(){var a=window.location.href;for(var b in this.loginCheckers){if(a==this.loginCheckers[b]){chrome.extension.sendRequest({method:"checkLogin"});return}}for(var b in this.simpleTop){if(a==this.simpleTop[b]){chrome.extension.sendRequest({method:"checkLoginOnly"});return}}chrome.extension.sendRequest({method:"checkLoginTimer"})},checkListUpdate:function(){var a=window.location.href;if(a==this.messageListUpdateCheckers){chrome.extension.sendRequest({method:"messageListUpdate"})}},openSetting:function(){var a=window.location.href;for(var b=0;b<this.startPages.length;b++){if(a==this.startPages[b]){$(".sebtn").bind("click",function(c){chrome.extension.sendRequest({method:"opensetting"})})}}}};chrome.extension.onMessage.addListener(function(d,c,a){var b=window.location.href;if(d.method=="closeTab"&&b=="http://websearch.rakuten.co.jp/"){a()}});RTBcs.init();
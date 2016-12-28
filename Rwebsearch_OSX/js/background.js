var PROJECT_ID = '342483847500'; // senderId or ProjectId

// Returns a new notification ID used in the notification.
function getNotificationId() {
    var id = Math.floor(Math.random() * 9007199254740992) + 1;
    return id.toString();
}
 
function messageReceived(message) {
    // A message is an object with a data property that
    // consists of key-value pairs.
 
    //console.log("Message received: " + JSON.stringify(message.data));
 
    // Pop up a notification to show the GCM message.
    // If you want use images from remote resource add it to manifest permissions
    // https://developer.chrome.com/apps/app_codelab8_webresources
    chrome.notifications.create(getNotificationId(), {
        title: message.data.header || message.data.body,
        iconUrl: message.data.i || 'img/icon128.png',
        type: 'basic',
        message: message.data.body
    }, function(notificationId) {
        if (chrome.runtime.lastError) {
            // When the registration fails, handle the error and retry the registration later.
            // See error codes here https://developer.chrome.com/extensions/cloudMessaging#error_reference
            //console.log("Fail to create the message: " + chrome.runtime.lastError.message);
            return;
        }
    });
 
    chrome.storage.local.set({
        messageHash: message.data.p,
        richPageOld: message.data.h,
        url: message.data.l
    });
}
 
function firstTimeRegistration() {
    //console.log('firstTimeRegistration');
    register();
}
 

function pushClickEvent() {
    pushwooshStatistics();
    chrome.storage.local.get(['url', 'richPageOld'], function(items)  {
        if (items.url) {
            window.open(items.url, '_newtab');
        }
        else if (items.richPageOld) {
            window.open('https://cp.pushwoosh.com/pages/' + items.richPageOld, '_newtab');
        }
        chrome.storage.local.remove(['url', 'richPageOld']);
    });
}

// Set up a listener for GCM message event.
chrome.gcm.onMessage.addListener(messageReceived);
// Set up listeners to trigger the first time registration.
chrome.runtime.onInstalled.addListener(firstTimeRegistration);
chrome.runtime.onStartup.addListener(firstTimeRegistration);
// Add listener for send push-open statistics to Pushwoosh
chrome.notifications.onClicked.addListener(pushClickEvent);
 
function createUUID() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 32; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    return s.join("");
}
 
function generateHwid() {
    var hwid = APPLICATION_CODE + '_' + createUUID();
    console.log("HWID generated " + hwid);
    return hwid;
}
 
function register() {
    chrome.storage.local.get("registered", function(result) {
        // If already registered, bail out.
        if (result["registered"]) {
            return;
        }
        chrome.gcm.register([PROJECT_ID], registerCallback);
    });
}
 
function unregister() {
    chrome.gcm.unregister(unregisterCallback);
}
 
function registerCallback(pushToken) {
    // Mark that the first-time registration is done and generate hwid.
    chrome.storage.local.get('hwid', function(items) {
       if (items.hwid) {
           pushwooshRegisterDevice(pushToken);
       } else {
           var hwid = generateHwid();
           chrome.storage.local.set({hwid: hwid}, function() {
               pushwooshRegisterDevice(pushToken);
           });
       }
    });
    chrome.storage.local.set({registered: true});
}
 
function unregisterCallback() {
    if (chrome.runtime.lastError) {
        return;
    }
    // Mark that the first-time registration is not done.
    chrome.storage.local.set({registered: false});
    pushwooshUnregisterDevice();
}
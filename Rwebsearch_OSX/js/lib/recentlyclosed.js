var recentlyClosed = {
	showCount: 8,  // 「最近閉じたタブ」に表示する数
	newTab: function(windowId, id, title, url){
		var tabs;
		try{
		    tabs = localStorage["tabs"] != undefined ? JSON.parse(localStorage["tabs"]) : new Array;
		}
		catch(e){
			tabs = new Array;
		}
	    tabs.unshift({
	        windowId : windowId, id : id, title : title, url : url
	    });
	    localStorage["tabs"] = JSON.stringify(tabs);
	},
	update: function(windowId, id, title, url){
		var tabs;
		try{
		    tabs = localStorage["tabs"] != undefined ? JSON.parse(localStorage["tabs"]) : new Array;
		}
		catch(e){
			tabs = new Array;
		}
	    for (var i = 0; i < tabs.length; i++) {
	        if (tabs[i].id == id && tabs[i].windowId == windowId) {
	        	tabs[i].url = url;
	        	tabs[i].title = title;
	            break
	        }
	    }
	    localStorage["tabs"] = JSON.stringify(tabs)
	},
	add: function(windowId, tabId){
		var tabs;
		try{
		    tabs = localStorage["tabs"] != undefined ? JSON.parse(localStorage["tabs"]) : new Array;
		}
		catch(e){
			tabs = new Array;
		}
	    var url;
	    var title;
	    for (var i = 0; i < tabs.length; i++) {
	        if (tabs[i].id == tabId && tabs[i].windowId == windowId) {
	            url = tabs[i].url;
	            title = tabs[i].title;
	            tabs.splice(i, 1);
	            break
	        }
	    }
	    localStorage["tabs"] = JSON.stringify(tabs);
	    if (url != undefined && url.indexOf("chrome://") ==- 1 && url.indexOf("chrome-extension://") ==- 1 && url.indexOf("chrome-devtools://") ==- 1)
	    {
	        var closedTabs = localStorage["closedTabs"] != undefined ? JSON.parse(localStorage["closedTabs"]) : new Array;
	        if (closedTabs.length >= this.showCount) {
	            closedTabs.pop();
	        }
	        closedTabs.unshift({
	            title : title, url : url
	        });
	        localStorage["closedTabs"] = JSON.stringify(closedTabs);
	    }
	},
	show: function(){
		chrome.history.search({text: ""}, function (histories){ // chrome.history.searchは起動時のアニメーションをなくすためのダミー
			var closedTabs;
			try{
			    closedTabs = localStorage["closedTabs"] != undefined ? JSON.parse(localStorage["closedTabs"]) : new Array;
			}
			catch(e){
			    closedTabs = new Array;
			}
		    if (closedTabs.length > 0)
		    {
		        for (var i = 0; i < closedTabs.length; i++)
		        {
					$("#rc_content").append(
						'<a class="rc clearfix" href="' + closedTabs[i].url + '" title="' + closedTabs[i].title + '">' +
						'<img width="16" height="16" class="favicon" src="chrome://favicon/' + closedTabs[i].url + '" />' +
						'<p>' + recentlyClosed.formatTitle(closedTabs[i].title, 18) + '</p></a>'
					);
		        }
	
				$('#rc_content').masonry({
					"isAnimated": true,
					animationOptions: {
					    duration: 500,
					    easing: 'linear',
					    queue: false
					},
					isFitWidth: true
				});
				
				$(".rc").hover(
					function(){
						$(this).css("border", "1px solid #ccc");
					},
					function(){
						$(this).css("border", "none");
					}
				);
		    }
			
			$("#recently_closed .title").toggle(
				function(){
					$(this).next().slideUp("fast");
					localStorage[$(this).parent().attr("id")] = "close";
					$(this).children("img").attr("src", "img/arrow_right.png");
				},
				function(){
					$(this).next().slideDown("fast");
					$('#rc_content').masonry('destroy');
					$('#rc_content').masonry({
						"isAnimated": true,
						animationOptions: {
						    duration: 500,
						    easing: 'linear',
						    queue: false
						},
						isFitWidth: true
					});
					localStorage[$(this).parent().attr("id")] = "";
					$(this).children("img").attr("src", "img/arrow_down.png");
				}
			);
		    
			if(localStorage["recently_closed"] == "close"){
				$("#rc_content").css("display", "none");
				$("#recently_closed .title img").attr("src", "img/arrow_right.png");
				$("#recently_closed .title").trigger("click");
			}
			else{
				$("#recently_closed .title img").attr("src", "img/arrow_down.png");
			}
		});
	},
	formatTitle: function (text, length)
	{
	    if (text == undefined) {
	        return "";
	    }
	    var bites = 0;
	    for (var i = 0; i < text.length; i ++){
	    	if (escape(text.charAt(i)).length < 4){
	    		bites ++;
	    	}
	    	else{
	    		bites += 2;
	    	}
	    	if (bites > length){
	    		return text.substring(0, i) + "...";
	    	}
	    }
	    return text;
	}
}
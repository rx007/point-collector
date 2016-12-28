var apps = {
	show: function(){
		$(".chromeWebStore").attr("href", "https://chrome.google.com/webstore/category/home");
		var callback = function (apps){
			var application = $("#app_content");
			application.append(
				'<div class="app">' +
				'<a class="chromeWebStore" href="https://chrome.google.com/webstore/category/home?hl=ja">' +
				'<img height="80px" src="chrome://extension-icon/ahfgeienlihckogmohjhadlkjgocpleb/128/0">' +
				'<span class="name">Chrome Web Store</span></a></div>'
			);
			for (var i = 0; i < apps.length; i++){
				if (apps[i].isApp && apps[i].enabled){
					application.append(
						'<div class="app">' +
						'<a href="' + apps[i].appLaunchUrl + '">' +
						'<img height="80px" src="' + apps[i].icons[apps[i].icons.length - 1].url + '"/>' + 
						'<span class="name">' + apps[i].name + "</span></a></div>"
					);
				}
			}

			$('#app_content').masonry({
				"isAnimated": true,
				animationOptions: {
				    duration: 500,
				    easing: 'linear',
				    queue: false
				},
				isFitWidth: true
			});
			
			$(".app").hover(
				function(){
					$(this).css("border", "1px solid #ccc");
				},
				function(){
					$(this).css("border", "none");
				}
			);
			
			$("#applications .title").toggle(
				function(){
					$(this).next().slideUp("fast");
					localStorage[$(this).parent().attr("id")] = "close";
					$(this).children("img").attr("src", "img/arrow_right.png");
				},
				function(){
					$(this).next().slideDown("fast");
					localStorage[$(this).parent().attr("id")] = "";
					$(this).children("img").attr("src", "img/arrow_down.png");
				}
			);
			
			if(localStorage["applications"] == "close"){
				$("#app_content").css("display", "none");
				$("#applications .title img").attr("src", "img/arrow_right.png");
				$("#applications .title").trigger("click");
			}
			else{
				$("#applications .title img").attr("src", "img/arrow_down.png");
			}
		};
		chrome.management.getAll(callback);
	}
};
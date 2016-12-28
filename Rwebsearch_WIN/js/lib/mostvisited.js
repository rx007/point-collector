var mostVisited = {
	showCount: 8,  // 「よくアクセスするページ」に表示する数
	show: function(){
		var search_time = 1e3 * 60 * 60 * 24 * 14;  //過去2週間分の履歴を取得
		var start_time = (new Date).getTime() - search_time;
		chrome.history.search({text: "", startTime: start_time}, function (histories){
			var labeled = {};
			for(i = 0; i < histories.length; i ++){
				if(labeled[histories[i].visitCount] == undefined){
					labeled[histories[i].visitCount] = new Array();
				};
				labeled[histories[i].visitCount].push({
					"url": histories[i].url
				});
			}
			var sorted = new Array();
			
			// 順番を逆にする
			for(var key in labeled){
				sorted.unshift({
					"count": key,
					"contents": labeled[key]
				});
			}
			
			// アクセス数が多い順にmostvisitedに入れる
			var mostvisited = new Array();
			for(var i = 0; i < sorted.length; i ++){
				if(mostvisited.length + sorted[i]["contents"].length < mostVisited.showCount + 1){
					mostvisited = mostvisited.concat(sorted[i]["contents"]);
				}
				else{
					for(var j = 0; j < mostVisited.showCount - mostvisited.length; j ++){
						mostvisited.push(sorted[i]["contents"][j]);
					}
				}
			}

			// DOM追加
			for(var i = 0; i < mostvisited.length; i ++){
				$("#mv_content").append(
					'<a class="mv clearfix" href="' + mostvisited[i].url + '" title="' + mostvisited[i].url + '">' +
					'<img width="16" height="16" class="favicon" src="chrome://favicon/' + mostvisited[i].url + '" />' +
					'<p>' + mostVisited.getDomain(mostvisited[i].url) + '</p></a>'
				);
			}

			$('#mv_content').masonry({
				"isAnimated": true,
				animationOptions: {
				    duration: 500,
				    easing: 'linear',
				    queue: false
				},
				isFitWidth: true
			});
			
			$(".mv").hover(
				function(){
					$(this).css("border", "1px solid #ccc");
				},
				function(){
					$(this).css("border", "none");
				}
			);
			
			$("#most_visited .title").toggle(
				function(){
					$(this).next().slideUp("fast");
					localStorage[$(this).parent().attr("id")] = "close";
					$(this).children("img").attr("src", "img/arrow_right.png");
				},
				function(){
					$(this).next().slideDown("fast");
					$('#mv_content').masonry('destroy');
					$('#mv_content').masonry({
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
			
			if(localStorage["most_visited"] == "close"){
				$("#mv_content").css("display", "none");
				$("#most_visited .title img").attr("src", "img/arrow_right.png");
				$("#most_visited .title").trigger("click");
			}
			else{
				$("#most_visited .title img").attr("src", "img/arrow_down.png");
			}
		});
	},
	getDomain: function (url)
	{
	    if (url == undefined || url == "") {
	        return "";
	    }
	    if (url.indexOf("http://") !=- 1) {
	        url = url.substring(7, url.length);
	    }
	    if (url.indexOf("https://") !=- 1) {
	        url = url.substring(8, url.length);
	    }
	    if (url.indexOf("/") !=- 1) {
	        url = url.substring(0, url.indexOf("/"));
	    }
	    if (url.indexOf("www.") !=- 1) {
	        url = url.substring(4, url.length);
	    }
	    if (url.length > 15){
	    	url = url.substring(0, 14) + "..";
	    }
	    return url.charAt(0).toUpperCase() + url.slice(1);
	}
}
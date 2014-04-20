/* stats-production.js
 */

function sendmsg(id, data)
{
	var request = new AjaxRequest("stats-production.json");
	request.onsuccess = function(text)
		{
			var result = json_parse(text);
			replaceContent(id, result.production);
			initStatsProduction();
	    initTimer();
		};
	request.post(data);
	return false;
}

function initStatsProduction()
{
	var rgx = /[a-z]+-(.*)-([0-9]*)/;
	var elems = document.getElementsByTagName("a");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].id.substring(0, 7) == "resume-")
		{
			var res = elems[i].id.match(rgx);
			var pos = res[1];
			var num = res[2];
			var id = "id-"+pos+"-"+num;
			if (num == 0)
				elems[i].onclick = new Function("return sendmsg('"+id+"', 'do=Colony.resumeBuilding&c="+pos+"');");
			else
				elems[i].onclick = new Function("return sendmsg('"+id+"', 'do=Colony.resumeUnit&c="+pos+"&q="+(num-1)+"');");
		}
		
		if (elems[i].id.substring(0, 6) == "abort-")
		{
			var res = elems[i].id.match(rgx);
			var pos = res[1];
			var num = res[2];
			var id = "id-"+pos+"-"+num;
			if (num == 0)
				elems[i].onclick = new Function("return sendmsg('"+id+"', 'do=Colony.abortBuilding&c="+pos+"');");
			else
				elems[i].onclick = new Function("return sendmsg('"+id+"', 'do=Colony.abortUnit&c="+pos+"&q="+(num-1)+"');");
		}
	}
}

addInit(initStatsProduction);

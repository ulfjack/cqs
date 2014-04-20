/* map.js
 */

var keepOld;

function jump()
{
	var pos = document.getElementById("jpos").value;
	var regex = /^[\d:]+$/;
	if (pos.match(regex))
		window.location = "map|"+pos;
}

function highlight(id)
{
	var temp = id.split(':');
	temp[0] = temp[0].substring(1);
	var s = parseInt(temp[0]);
	var elem = document.getElementById("s"+s);
	keepOld = elem.innerHTML;
	elem.innerHTML = "&times;";
//	alert(elem.style);
}

function dehighlight(id)
{
	var temp = id.split(':');
	temp[0] = temp[0].substring(1);
	var s = parseInt(temp[0]);
	var elem = document.getElementById("s"+s);
	elem.style.textDecoration = "";
	elem.innerHTML = keepOld;
}

var highlighted = -1;

function doit()
{
	var which = document.getElementById("hpos").value;
	if (highlighted != -1)
		dehighlight(highlighted);
	if (document.getElementById("s"+which))
	{
		highlighted = "s"+which+":x";
		highlight(highlighted);
	}
}

function initMap()
{
	var elems = document.getElementsByTagName("li");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].id != "")
		{
			elems[i].onmouseover = new Function("highlight('"+elems[i].id+"')");
			elems[i].onmouseout = new Function("dehighlight('"+elems[i].id+"')");
		}
	}
}

addInit(initMap);

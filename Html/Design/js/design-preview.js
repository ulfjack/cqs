/* design-preview.js
 */

var ids = new Array();

function format(s)
{
	var result = "";
	for (var i = 0; i < s.length; i++)
	{
		if (((s.length-i) % 3 == 0) && (i != 0)) result += ".";
		result += s[i];
	}
	return result;
}

function formatTime(s)
{
	var sec = Math.floor(s % 60); s /= 60;
	var min = Math.floor(s % 60); s /= 60;
	var hrs = Math.floor(s % 24); s /= 24;
	var days = Math.floor(s);

	if (sec < 10)
		sec = "0" + sec;
	if (min < 10)
		min = "0" + min;
	if (hrs < 10)
		hrs = "0" + hrs;
	
	if (days != 0)
		return days+"."+hrs+":"+min+":"+sec;
	else
	{
		if (hrs != 0)
			return hrs+":"+min+":"+sec;
		else
			return min+":"+sec;
	}
}

function checkCapacity()
{
	var space = 0;
	var elem = document.getElementById("space");
	space = parseInt(elem.value);
	
	var sum = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("module-"+ids[i]);
		var elem2 = document.getElementById("size-"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) sum += v1*v2;
		}
	}
	if (sum <= space)
		replaceContent("spaceleft", ""+(space-sum));
	else
		replaceContent("spaceleft", "<i class='a'>"+(space-sum)+"</i>");
	return sum <= space;
}

function updateCosts()
{
	var sums = new Array(7);
	for (var j = 0; j < 7; j++)
		sums[j] = basecost[j];
	var space = 49;
	for (var i = 0; i < ids.length; i++)
	{
		var amount = document.getElementById("module-"+ids[i]).value;
		for (var j = 0; j < 7; j++)
			sums[j] += amount*modulecost[i][j];
		space -= amount*modulespace[i];
	}
	for (var j = 0; j < 7; j++)
	{
		var text;
		if ((j >= 0) && (j <= 5)) text = format(""+sums[j]);
		else text = formatTime(sums[j]);
		replaceContent("sum-"+j, text);
	}
}

function updateCalculators()
{
	checkCapacity();
	updateCosts();
}

function checkCoordinates()
{ return true; }

function validate()
{
	var errormsg = "";
	if (!checkCoordinates())
		errormsg = errormsg+gettext("The target coordinates are not valid.")+"\n\n";
	
	if (errormsg == "")
		return true;
	else
		return confirm(errormsg+gettext("Do you want to proceed anyways?"));
}

function initDesignPreview()
{
	var elems = document.getElementsByTagName("input");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].id.substring(0, 7) == "module-")
		{
			id = elems[i].id.substr(7);
			ids[ids.length] = id;
			elems[i].onkeyup = updateCalculators;
		}
	}
	updateCalculators();
	var elem = document.getElementById("previewbutton");
	elem.onclick = validate;
}

addInit(initDesignPreview);

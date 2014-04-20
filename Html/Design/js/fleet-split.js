/* fleet-split.js
 */

var ids = new Array();

function format(s)
{
	if (s < 0)
		return "-"+format(-s);
	s = ""+s;
	var result = "";
	for (var i = 0; i < s.length; i++)
	{
		if (((s.length-i) % 3 == 0) && (i != 0)) result += ".";
		result += s.charAt(i);
	}
	return result;
}

function checkResCapacity(which)
{
	var space = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+which+ids[i]);
		var elem2 = document.getElementById("res"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) space += v1*v2;
		}
	}
	var sum = 0;
	for (var i = 0; i < 4; i++)
	{
		var elem = document.getElementById("res"+which+i);
		if (elem)
		{
			var value = parseInt(elem.value);
			if (!isNaN(value)) sum += value;
		}
	}
	document.getElementById("reskapa"+which).innerHTML = format(space);
	document.getElementById("resload"+which).innerHTML = format(sum);
	if (sum <= space)
		document.getElementById("ressum"+which).innerHTML = format(space-sum);
	else
		document.getElementById("ressum"+which).innerHTML = "<i class='a'>"+format(space-sum)+"</i>";
	return sum <= space;
}

function checkGroundUnitCapacity(which)
{
	var sum = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+which+ids[i]);
		var elem2 = document.getElementById("size"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) sum += v1*v2;
		}
	}
	var space = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+which+ids[i]);
		var elem2 = document.getElementById("space"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) space += v1*v2;
		}
	}
	document.getElementById("unitkapa"+which).innerHTML = format(space);
	document.getElementById("unitload"+which).innerHTML = format(sum);
	if (sum <= space)
		document.getElementById("unitsum"+which).innerHTML = format(space-sum);
	else
		document.getElementById("unitsum"+which).innerHTML = "<i class='a'>"+format(space-sum)+"</i>";
	return sum <= space;
}

function checkSpaceUnitCapacity(which)
{
	var sum = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+which+ids[i]);
		var elem2 = document.getElementById("spacesize"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) sum += v1*v2;
		}
	}
	var space = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+which+ids[i]);
		var elem2 = document.getElementById("carrier"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) space += v1*v2;
		}
	}
	document.getElementById("spacekapa"+which).innerHTML = format(space);
	document.getElementById("spaceload"+which).innerHTML = format(sum);
	if (sum <= space)
		document.getElementById("spacesum"+which).innerHTML = format(space-sum);
	else
		document.getElementById("spacesum"+which).innerHTML = "<i class='a'>"+format(space-sum)+"</i>";
	return sum <= space;
}

function updateCalculators()
{
	checkResCapacity("left");
	checkResCapacity("right");
	checkGroundUnitCapacity("left");
	checkGroundUnitCapacity("right");
	checkSpaceUnitCapacity("left");
	checkSpaceUnitCapacity("right");
}

function updateLeftUnits(id)
{
	var a = document.getElementById("unitleft"+id);
	var b = document.getElementById("unitright"+id);
	var amount = document.getElementById("amount"+id).value;
	a.value = amount-b.value;
	updateCalculators();
}

function updateRightUnits(id)
{
	var a = document.getElementById("unitright"+id);
	var b = document.getElementById("unitleft"+id);
	var amount = document.getElementById("amount"+id).value;
	a.value = amount-b.value;
	updateCalculators();
}

function unselectUnits(id)
{
	var a = document.getElementById("unitleft"+id);
	var b = document.getElementById("unitright"+id);
	var amount = document.getElementById("amount"+id).value;
	a.value = amount;
	b.value = 0;
	updateCalculators();
}

function selectUnits(id)
{
	var a = document.getElementById("unitleft"+id);
	var b = document.getElementById("unitright"+id);
	var amount = document.getElementById("amount"+id).value;
	a.value = 0;
	b.value = amount;
	updateCalculators();
}

function updateLeftRes(id)
{
	var a = document.getElementById("resleft"+id);
	var b = document.getElementById("resright"+id);
	var amount = document.getElementById("resamount"+id).value;
	a.value = amount-b.value;
	updateCalculators();
}

function updateRightRes(id)
{
	var a = document.getElementById("resright"+id);
	var b = document.getElementById("resleft"+id);
	var amount = document.getElementById("resamount"+id).value;
	a.value = amount-b.value;
	updateCalculators();
}

function unselectRes(id)
{
	var a = document.getElementById("resleft"+id);
	var b = document.getElementById("resright"+id);
	var amount = document.getElementById("resamount"+id).value;
	a.value = amount;
	b.value = 0;
	updateCalculators();
}

function selectRes(id)
{
	var a = document.getElementById("resleft"+id);
	var b = document.getElementById("resright"+id);
	var amount = document.getElementById("resamount"+id).value;
	a.value = 0;
	b.value = amount;
	updateCalculators();
}

function initFleetSplit()
{
	var elems = document.getElementsByTagName("input");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].id.substring(0, 8) == "unitleft")
		{
			id = elems[i].id.substr(8);
			ids[ids.length] = id;
			elems[i].onkeyup = new Function("updateRightUnits('"+id+"');");
		}
		if (elems[i].id.substring(0, 9) == "unitright")
		{
			id = elems[i].id.substr(9);
			elems[i].onkeyup = new Function("updateLeftUnits('"+id+"');");
		}
		
		if (elems[i].id.substring(0, 7) == "resleft")
		{
			id = elems[i].id.substr(7);
			elems[i].onkeyup = new Function("updateRightRes('"+id+"');");
		}
		if (elems[i].id.substring(0, 8) == "resright")
		{
			id = elems[i].id.substr(8);
			elems[i].onkeyup = new Function("updateLeftRes('"+id+"');");
		}
	}
	
	updateCalculators();
}

addInit(initFleetSplit);

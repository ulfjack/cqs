/* fleet-send.js
 */

var ids = new Array();

function selectUnits(id, amount)
{
	document.getElementById(id).value = amount;
	updateCalculators();
}

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

function checkUnitCount()
{
	for (var i = 0; i < ids.length; i++)
	{
		var elem = document.getElementById("unit"+ids[i]);
		if (parseInt(elem.value) > 0) return true;
	}
	return false;
}

function extractCoordinates(v)
{
	if (!v) return null;
//	if ((v == 0)) return null;
	if (v == "") return null;
	v = v.replace(/[.,;]/g,':');
	if (v.charCodeAt(0) == 58 || v.charCodeAt(v.length-1) == 58) return null;
	var temp = v.split(':');
	if (temp.length > 3) return null;
	for (var i = 0; i < temp.length; i++)
	{
		var p = parseInt(temp[i]);
		if (isNaN(p)) return null;
	}
	return temp;
}

function checkCoordinates()
{
	var elem = document.getElementById('targetposition');
	if (!elem) return false;
	var temp = extractCoordinates(elem.value);
	if (!temp) return false;
	return true;
}

function isPlanetFlight()
{
	var elem;
	elem = document.getElementById('sourceposition');
	if (!elem) return false;
	var temp0 = extractCoordinates(elem.value);
	if (!temp0) return false;
	elem = document.getElementById('targetposition');
	if (!elem) return false;
	var temp1 = extractCoordinates(elem.value);
	if (!temp1) return false;
	if ((temp0.length > 2) && (temp1.length > 2))
	{
		if ((temp0[0] == temp1[0]) && (temp0[1] == temp1[1]))
			return true;
	}
	return false;
}

function isInterplanetFlight()
{
	var elem;
	elem = document.getElementById('sourceposition');
	if (!elem) return false;
	var temp0 = extractCoordinates(elem.value);
	if (!temp0) return false;
	elem = document.getElementById('targetposition');
	if (!elem) return false;
	var temp1 = extractCoordinates(elem.value);
	if (!temp1) return false;
	if ((temp0.length > 1) && (temp1.length > 1))
	{
		if (temp0[0] == temp1[0])
			return true;
	}
	return false;
}

function checkRessCapacity()
{
	var space = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+ids[i]);
		var elem2 = document.getElementById("res"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) space += v1*v2;
		}
	}
	var sum = 0;
	for (var i = 0; i <= 3; i++)
	{
		var elem = document.getElementById("load"+i);
		var value = parseInt(elem.value);
		if (!isNaN(value)) sum += value;
	}
	document.getElementById("reskapa").innerHTML = format(space);
	document.getElementById("resload").innerHTML = format(sum);
	if (sum <= space)
	{
		replaceContent("ressum", format(space-sum));
		document.getElementById("restrue").style.display = "";
		document.getElementById("resfalse").style.display = "none";
	}
	else
	{
		replaceContent("ressum", "<i class='a'>"+format(space-sum)+"</i>");
		document.getElementById("restrue").style.display = "none";
		document.getElementById("resfalse").style.display = "";
	}
	return sum <= space;
}

function checkGroundUnitCapacity()
{
	var sum = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+ids[i]);
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
		var elem1 = document.getElementById("unit"+ids[i]);
		var elem2 = document.getElementById("space"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) space += v1*v2;
		}
	}
	document.getElementById("unitkapa").innerHTML = format(space);
	document.getElementById("unitload").innerHTML = format(sum);
	if (sum <= space)
	{
		replaceContent("unitsum", format(space-sum));
		document.getElementById("unittrue").style.display = "";
		document.getElementById("unitfalse").style.display = "none";
	}
	else
	{
		replaceContent("unitsum", "<i class='a'>"+format(space-sum)+"</i>");
		document.getElementById("unittrue").style.display = "none";
		document.getElementById("unitfalse").style.display = "";
	}
	return sum <= space;
}

function checkSpaceUnitCapacity(groundUnitsOk)
{
	var sum = 0;
	for (var i = 0; i < ids.length; i++)
	{
		var elem1 = document.getElementById("unit"+ids[i]);
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
		var elem1 = document.getElementById("unit"+ids[i]);
		var elem2 = document.getElementById("carrier"+ids[i]);
		if (elem2)
		{
			var v1 = parseInt(elem1.value);
			var v2 = parseInt(elem2.value);
			if (!isNaN(v1) && !isNaN(v2)) space += v1*v2;
		}
	}
	document.getElementById("spacekapa").innerHTML = format(space);
	document.getElementById("spaceload").innerHTML = format(sum);

	if (sum <= space)
	{
		replaceContent("spacesum", format(space-sum));
	}
	else
	{
		replaceContent("spacesum", "<i class='a'>"+format(space-sum)+"</i>");
	}
	if ((sum <= space) && groundUnitsOk)
	{
		document.getElementById("spacetrue").style.display = "";
		document.getElementById("spacefalse").style.display = "none";
	}
	else
	{
		document.getElementById("spacetrue").style.display = "none";
		document.getElementById("spacefalse").style.display = "";
	}
	return sum <= space;
}

function validate()
{
	var errormsg = "";
	if (!checkUnitCount())
		errormsg = errormsg+gettext("You did not select any units.")+"\n\n";
	
	if (!checkRessCapacity())
		errormsg = errormsg+gettext("You loaded too many resources.")+"\n\n";
	
	if (!checkCoordinates())
		errormsg = errormsg+gettext("The target coordinates are not valid.")+"\n\n";
	
	if (!checkGroundUnitCapacity() && !isPlanetFlight())
		errormsg = errormsg+gettext("You loaded too many units for the selected transporters.")+"\n\n";
	
	if (!checkSpaceUnitCapacity() && !isPlanetFlight() && !isInterplanetFlight())
		errormsg = errormsg+gettext("You loaded too many units for the selected carrier.")+"\n\n";

	if (errormsg == "")
		return true;
	else
		return confirm(errormsg+gettext("Do you want to send off the fleet anyways?"));
}

function newchanger()
{
	var val = document.getElementById('changereset').value;
	if (val != "") document.getElementById('targetposition').value = val;
	document.getElementById('changereset').selectedIndex = 0;
}

function newchanger2()
{
	var val = document.getElementById('changereset2').value;
	if (val != "") document.getElementById('targetpositionB').value = val;
	document.getElementById('changereset2').selectedIndex = 0;
}

function checkUnitCapacity()
{
	var groundCapacity = checkGroundUnitCapacity();
	checkSpaceUnitCapacity(groundCapacity);
}

function updateCalculators()
{
	checkRessCapacity();
	checkUnitCapacity();
}

function initFleetSend()
{
	var elems = document.getElementsByTagName("input");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].name.substring(0, 4) == "unit")
		{
			id = elems[i].name.substr(4);
			ids[ids.length] = id;
			elems[i].onkeyup = updateCalculators;
		}
		if (elems[i].id.substring(0, 4) == "load")
			elems[i].onkeyup = updateCalculators;
		if (elems[i].id == "targetposition")
			elems[i].onkeyup = updateCalculators;
	}
	
	var elem = document.getElementById("submitbutton");
	elem.onclick = validate;
	
	updateCalculators();
}

addInit(initFleetSend);

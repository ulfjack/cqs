/* colony.js
 */

var buildIsLoaded = true;
var unitIsLoaded = new Array(false, false, false);
var educationIsLoaded = false;
var financesIsLoaded = false;

var bqueue;
var uqueue = new Array(3);
var edu;

function hideArea(id, changeLink)
{
	/* toggleDisplay: if currently showing, hide again */
	elem = document.getElementById("colony-"+id+"-td");
	if (elem && elem.style.display == "")
	{
		elem.style.display = "none";
		replaceContent("indent-"+id, "");
		if (changeLink)
		{
			replaceContent("colony-"+id+"-link", gettext("show details"));
		}
		return true;
	}
	return false;
}

function showArea(id, changeLink, fraction)
{
	/* toggleDisplay: if currently showing, hide again */
	elem = document.getElementById("colony-"+id+"-td");
	if (elem && elem.style.display == "none")
	{
		elem.style.display = "";
		if (fraction == 2)
		{
			replaceContent("indent-"+id, "<div class='bgconnector-half'><br /></div>");
		}
		else if (fraction == 4)
		{
			replaceContent("indent-"+id, "<div class='bgconnector-quarter'><br /></div>");
		}
		else
		{
			replaceContent("indent-"+id, "<div class='bgconnector-half'><br /></div>");
		}
		if (changeLink)
		{
			replaceContent("colony-"+id+"-link", gettext("hide details"));
		}
		return true;
	}
	return false;
}

function showRename()
{
	if (!hideArea("rename", false))
	{
		showArea("rename", false, -1);
	}
	return false;
}

function showBuild(position)
{
	if (!hideArea("build", false))
	{
		/* if not previously loaded, request content */
		if (!buildIsLoaded)
		{
			var request = new AjaxRequest("colony-open-build.json");
			request.onsuccess = function(text)
				{
					var result = json_parse(text);
					replaceContent("colony-build", result.build);
					initColonyBuild();
				};
			request.post("c="+position);
			buildIsLoaded = true;
		}
		/* if previously loaded, only update view */
		else
		{
			bqueue.updateView();
		}
		showArea("build", false, 4);
		for (i = 0; i < 3; i++)
		{
			hideArea("unit-"+i, false);
		}

	}
	return false;
}

function showUnit(position, type)
{
	if (!hideArea("unit-"+type, false))
	{
		/* if not previously loaded, request content */
		if (!unitIsLoaded[type])
		{
			var request = new AjaxRequest("colony-open-unit-"+type+".json");
			request.onsuccess = function(text)
				{
					var result = json_parse(text);
					replaceContent("colony-unit-"+type, result.colony_unit);
					initColonyUnits(type);
				};
			request.post("c="+position);
			unitIsLoaded[type] = true;
		}
		/* if previously loaded, only update view */
		else
		{
			uqueue[type].updateView();
		}
		showArea("unit-"+type, false, 4);
		hideArea("build", false);		
		for (i = 0; i < 3; i++)
		{
			if (i != type)
			{
				hideArea("unit-"+i, false);
			}
		}
	}
	return false;
}

function showFinances(position)
{
	if (!hideArea("finances", true))
	{
		/* if not previously loaded, request content */
		if (!financesIsLoaded)
		{
			var request = new AjaxRequest("colony-open-finances.json");
			request.onsuccess = function(text)
				{
					var result = json_parse(text);
					replaceContent("colony-finances", result.colony_finances);
				};
			request.post("c="+position);
			financesIsLoaded = true;
		}
		showArea("finances", true, 2);
		hideArea("education", true);
	}
	return false;	
}

function showEducation(position)
{
	if (!hideArea("education", true))
	{
		/* if not previously loaded, request content */
		if (!educationIsLoaded)
		{
			var request = new AjaxRequest("colony-open-education.json");
			request.onsuccess = function(text)
				{
					var result = json_parse(text);
					replaceContent("colony-education", result.colony_education);
					initColonyEducation();
				};
			request.post("c="+position);
			educationIsLoaded = true;
		}
		showArea("education", true, 2);
		hideArea("finances", true);
	}
	return false;	
}


function initColony()
{
	var position = document.getElementById("x-cqs-colony").value;

	bqueue = new BuildingQueue(position);
	bqueue.initCurrentHooks();
	for (i = 0; i < 3; i++)
	{
		uqueue[i] = new UnitQueue(position, i);
		uqueue[i].initCurrentHooks();
	}
	edu = new Education(position);
	edu.initHooks();
	initColonyBuild();
}

addInit(initColony);

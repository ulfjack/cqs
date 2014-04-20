/* colony-build.js
 */

function BuildingQueue(pos)
{ this.position = pos; }

BuildingQueue.prototype.sendmsg = function(postData)
	{
		var me = this;
		var request = new AjaxRequest("colony-build.json");
		request.onsuccess = function(text)
			{
				var result = json_parse(text);
				request.handleMeta(result.buildingqueue);
				replaceContent("events", result.events);
				replaceContent("buildingqueue", result.buildingqueue);
				replaceContent("construction-building", result.construction_building);
				me.updateView();
			};
		request.post(postData);
		return false;
	};


BuildingQueue.prototype.updateView = function()
	{
		this.initCurrentHooks();
		this.initQueueHooks();
		initTimer();
	};


/* Adds a job to the building queue.
   Parameters: buildingId specifies the building type.
               isDemolition specifies whether the count value needs to be inverted or not.
               count specifies how many jobs the entry has; if none is given, user input is requested.
 */
BuildingQueue.prototype.addEntry = function(buildingId, isDemolition, count)
	{
		if (!count)
		{
			if (isDemolition)
				count = promptNumber(gettext("How many do you want to pull down?"), "");
			else
				count = promptNumber(gettext("How many do you want to build?"), bPreset);
		}
		if (isNaN(count)) return false;
		if (isDemolition) count = -count;
		this.sendmsg("do=Colony.addBuilding&c="+this.position+"&id="+buildingId+"&count="+count);
		return false;
	};

BuildingQueue.prototype.giveUpColony = function()
	{
		Check = confirm(gettext("Do you really want to give up the colony by removing all buildings?"));
		if (Check == false) return false;
		this.sendmsg("do=Colony.giveUp&c="+this.position);
		return false;
	};

/* Modifies a job in the building queue.
   Parameters: index specifies the position of the job entry.
               isDecrease specifies whether the count value needs to be inverted or not.
               count specifies by how many jobs the entry is modified; if none is given, user input is requested.
 */
BuildingQueue.prototype.modifyEntry = function(index, isDecrease, count)
	{
		if (!count)
		{
			if (isDecrease)
				count = promptNumber(gettext("How many do you want to remove?"), "");
			else
				count = promptNumber(gettext("How many do you want to add?"), "");
		}
		if (isNaN(count)) return false;
		if (isDecrease) count = -count;
		this.sendmsg("do=Colony.modifyBuilding&c="+this.position+"&index="+index+"&count="+count);
		return false;
	};

BuildingQueue.prototype.initListHooks = function()
	{
		var elems = document.getElementsByTagName("span");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substring(0, 6) == "giveUp")
				elems[i].onclick = new Function("return giveUp();");

			/* add entries to building queue */
			if (elems[i].id.substring(0, 7) == "b-btns-")
			{
				var num = elems[i].id.substring(7);
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
						{
							subelems[j].onclick = curry(this.addEntry, this, num, false, parseInt(count));
						}
						else if (count == "+N")
						{
							subelems[j].onclick = curry(this.addEntry, this, num, false, false);
						}
						else if (count == "-N")
						{
							subelems[j].onclick = curry(this.addEntry, this, num, true, false);
						}
						else
							alert("found "+count);
					}
				
					/* add entries to queue */
					if (subelems[j].nodeName.toLowerCase() == "input")
					{
						var count = subelems[j].alt;
						if (!isNaN(parseInt(count)))
						{
							subelems[j].onclick = curry(this.addEntry, this, num, false, parseInt(count));
						}
						else if (count == "+N")
							subelems[j].onclick = curry(this.addEntry, this, num, false, false);
						else if (count == "-N")
							subelems[j].onclick = curry(this.addEntry, this, num, true, false);
						else
							alert("found "+count);
					}
				}
			}
		}
	};

BuildingQueue.prototype.initCurrentHooks = function()
	{
		var elems = document.getElementsByTagName("a");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id == "b-abort-"+this.position)
			{
				elems[i].onclick = curry(this.sendmsg, this, "do=Colony.abortBuilding&c="+this.position);
			}
		
			if (elems[i].id == "b-resume-"+this.position)
			{
				elems[i].onclick = curry(this.sendmsg, this, "do=Colony.resumeBuilding&c="+this.position);
			}
		}
	};

BuildingQueue.prototype.initQueueHooks = function()
	{
		var elems = document.getElementsByTagName("span");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substring(0, 13) == "b-modifybtns-")
			{
				var num = elems[i].id.substring(13);
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
						{
							subelems[j].onclick = curry(this.modifyEntry, this, num, false, parseInt(count));
						}
						else if (count == "+N")
						{
							subelems[j].onclick = curry(this.modifyEntry, this, num, false, false);
						}
						else if (count == "-N")
						{
							subelems[j].onclick = curry(this.modifyEntry, this, num, true, false);
						}
						else if (count == "[X]")
						{
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.deleteBuilding&c="+this.position+"&index="+num);
						}
						else
							alert("found "+count);
					}
				}
			}
		
			if (elems[i].id.substring(0, 11) == "b-movebtns-")
			{
				var num = elems[i].id.substring(11);
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var mtype = subelems[j].firstChild.alt;
						var func = new Function("return false;");
						if (mtype == "1.")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.moveBuilding&c="+this.position+"&index="+num+"&count=0");
						else if (mtype == "^")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.moveBuilding&c="+this.position+"&index="+num+"&count=1");
						else if (mtype == "v")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.moveBuilding&c="+this.position+"&index="+num+"&count=-1");
						else
							alert("found "+mtype);
					}
				}
			}
		
			if (elems[i].id == "b-abort-btn")
			{
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
						subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.abortBuilding&c="+this.position);
				}
			}
		
			if (elems[i].id == "b-resume-btn")
			{
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
						subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.resumeBuilding&c="+this.position);
				}
			}
		}
	};

function initColonyBuild()
{
	debug("enter initColonyBuild()");
	bqueue.initListHooks();
	bqueue.initCurrentHooks();
	bqueue.initQueueHooks();
	debug("exit initColonyBuild()");
}


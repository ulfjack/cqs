/* colony-units.js
 */

function UnitQueue(pos, type)
{
	this.position = pos;
	this.type = type;
}

UnitQueue.prototype.sendmsg = function(postData)
	{
		var me = this;
		var request = new AjaxRequest("colony-units.json");
		request.onsuccess = function(text)
			{
				var result = json_parse(text);
				request.handleMeta(result.unitqueue);
				replaceContent("events", result.events);
				replaceContent("unitqueue-"+me.type, result.unitqueue);
				replaceContent("construction-unit-"+me.type, result.construction_unit);
				me.updateView();
			};
		request.post(postData);
		return false;
	};


UnitQueue.prototype.updateView = function()
	{
		this.initCurrentHooks();
		this.initQueueHooks();
		initTimer();
	};

UnitQueue.prototype.addEntry = function(unitId, count)
	{
		if (!count)
		{
			count = promptNumber(gettext("How many do you want to train?"), uPreset);
		}
		if (isNaN(count)) return false;
		this.sendmsg("do=Colony.addUnit&c="+this.position+"&q="+this.type+"&id="+unitId+"&count="+count);
		return false;
	};

UnitQueue.prototype.modifyEntry = function(index, isDecrease, count)
	{
		if (!count)
		{
			if (isDecrease)
				count = prompt(gettext("How many do you want to remove?"), "");
			else
				count = prompt(gettext("How many do you want to add?"), "");
		}
		if (isNaN(count)) return false;
		if (isDecrease) count = -count;
		this.sendmsg("do=Colony.modifyUnit&c="+this.position+"&q="+this.type+"&index="+index+"&count="+count);
		return false;
	};

UnitQueue.prototype.initListHooks = function()
	{
		var rgx = /[a-z]*-([0-9]*)-(.*)/;
		var elems = document.getElementsByTagName("span");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substring(0, 5) == "btns-")
			{
				var res = elems[i].id.match(rgx);
				var queue = res[1];
				var id = res[2];
				if (queue != this.type)
				{
					return;
				}
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
							subelems[j].onclick = curry(this.addEntry, this, id, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.addEntry, this, id, false);
						else
							alert("found "+count);
					}
				}
			}
		}
	};

UnitQueue.prototype.initCurrentHooks = function()
	{
		var elems = document.getElementsByTagName("a");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id == "u-abort-"+this.position+"-"+this.type)
				elems[i].onclick = curry(this.sendmsg, this, "do=Colony.abortUnit&c="+this.position+"&q="+this.type);
		
			if (elems[i].id == "u-resume-"+this.position+"-"+this.type)
				elems[i].onclick = curry(this.sendmsg, this, "do=Colony.resumeUnit&c="+this.position+"&q="+this.type);
		}
	};

UnitQueue.prototype.initQueueHooks = function()
	{
		var rgx = /[a-z]*-([0-9]*)-(.*)/; /* id-queue-index */
		var elems = document.getElementsByTagName("span");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substring(0, 13) == "u-modifybtns-")
			{
				var res = elems[i].id.match(rgx);
				var queue = res[1];
				var index = res[2];
				if (queue != this.type)
				{
					return;
				}

				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
							subelems[j].onclick = curry(this.modifyEntry, this, index, false, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.modifyEntry, this, index, false, false);
						else if (count == "-N")
							subelems[j].onclick = curry(this.modifyEntry, this, index, true, false);
						else if (count == "[X]")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.deleteUnit&c="+this.position+
								"&q="+this.type+"&index="+index);
						else
							alert("found "+count);
					}
				}
			}
		
			if (elems[i].id.substring(0, 11) == "u-movebtns-")
			{
				var res = elems[i].id.match(rgx);
				var queue = res[1];
				var index = res[2];
				if (queue != this.type)
				{
					return;
				}

				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var mtype = subelems[j].firstChild.alt;
						if (mtype == "1.")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.moveUnit&c="+this.position+
								"&q="+this.type+"&index="+index+"&count=0");
						else if (mtype == "^")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.moveUnit&c="+this.position+
								"&q="+this.type+"&index="+index+"&count=1");
						else if (mtype == "v")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Colony.moveUnit&c="+this.position+
								"&q="+this.type+"&index="+index+"&count=-1");
						else
							alert("found "+mtype);
					}
				}
			}
		}
	
		var elems = document.getElementsByTagName("a");
		for (var i = 0; i < elems.length; i++)
		{
			if ((elems[i].id.substring(0,8) == "u-abort-") && (elems[i].id.substring(8) == this.type))
				elems[i].onclick = curry(this.sendmsg, this, "do=Colony.abortUnit&c="+this.position+"&q="+this.type);
		
			if ((elems[i].id.substring(0,9) == "u-resume-") && (elems[i].id.substring(9) == this.type))
				elems[i].onclick = curry(this.sendmsg, this, "do=Colony.resumeUnit&c="+this.position+"&q="+this.type);
		}
	};

function initColonyUnits(type)
{
	debug("enter initColonyUnits(" + type + ")");
	uqueue[type].initListHooks();
	uqueue[type].initCurrentHooks();
	uqueue[type].initQueueHooks();
	initTimer();
	debug("exit initColonyUnits(" + type + ")");
}


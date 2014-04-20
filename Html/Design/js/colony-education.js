/* colony-education.js
 */

function Education(pos)
{ this.position = pos; }

Education.prototype.sendmsg = function(postData)
	{
		var me = this;
		var request = new AjaxRequest("colony-education.json");
		request.onsuccess = function(text)
			{
				var result = json_parse(text);
				replaceContent("events", result.events);
				replaceContent("colony-education", result.colony_education);
				replaceContent("colony-education-basic", result.colony_education_basic);
				me.updateView();
			};
		request.post(postData);
		return false;
	};


Education.prototype.updateView = function()
	{
		this.initHooks();
		initTimer();
	};

Education.prototype.modifyTrainers = function(isDecrease, count)
	{
		if (!count)
		{
			if (isDecrease)
				count = promptNumber(gettext("How many trainer positions do you want to remove?"), "");
			else
				count = promptNumber(gettext("How many trainer positions do you want to add?"), "");
		}
		if (isNaN(count)) return false;
		if (isDecrease) count = -count;
		this.sendmsg("do=Education.addProf&c="+this.position+"&count="+count);
		return false;
	};

Education.prototype.modifyResearchers = function(isDecrease, count)
	{
		if (!count)
		{
			if (isDecrease)
				count = promptNumber(gettext("How many research positions do you want to remove?"), "");
			else
				count = promptNumber(gettext("How many research positions do you want to add?"), "");
		}
		if (isNaN(count)) return false;
		if (isDecrease) count = -count;
		this.sendmsg("do=Research.addProf&c="+this.position+"&count="+count);
		return false;
	};

Education.prototype.modifyTopic = function(topic, isDecrease, count)
	{
		if (!count)
		{
			if (isDecrease)
				count = promptNumber(gettext("How many trainers do you want to fire?"), "");
			else
				count = promptNumber(gettext("How many trainers do you want to employ?"), "");
		}
		if (isNaN(count)) return false;
		if (isDecrease) count = -count;
		this.sendmsg("do=Education.modify&c="+this.position+"&education="+topic+"&count="+count);
		return false;
	};


Education.prototype.initHooks = function()
	{
		var elems = document.getElementsByTagName("span");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id == "edu-btns")
			{
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
							subelems[j].onclick = curry(this.modifyTrainers, this, false, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.modifyTrainers, this, false, false);
						else if (count == "-N")
							subelems[j].onclick = curry(this.modifyTrainers, this, true, false);
						else
							alert("found "+count);
					}
				}
			}

			if (elems[i].id == "research-btns")
			{
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
							subelems[j].onclick = curry(this.modifyResearchers, this, false, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.modifyResearchers, this, false, false);
						else if (count == "-N")
							subelems[j].onclick = curry(this.modifyResearchers, this, true, false);
						else
							alert("found "+count);
					}
				}
			}

			if (elems[i].id.substr(0, 9) == "edu-btns-")
			{
				var rgx = /[a-z]*-[a-z]*-([a-z]*)-(.*)/;
				var res = elems[i].id.match(rgx);
				var type = res[1];
				var topic = res[2];
				var isStop;
				if (type == "start")
				{
					isStop = false;
				}
				else if (type == "stop")
				{
					isStop = true;
				}
				else
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
							subelems[j].onclick = curry(this.modifyTopic, this, topic, false, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.modifyTopic, this, topic, isStop, false);
						else if (count == "-N")
							subelems[j].onclick = curry(this.modifyTopic, this, topic, isStop, false);
						else
							alert("found "+count);
					}
				}
			}

		}
	};

function initColonyEducation()
{
	edu.initHooks();
	initTimer();
}


/* research.js
 */

function ResearchQueue()
{ }

ResearchQueue.prototype.sendmsg = function(postData)
	{
		var me = this;
		var request = new AjaxRequest("research.json");
		request.onsuccess = function(text)
			{
				var result = json_parse(text);
				replaceContent("events", result.events);
				replaceContent("researchqueue", result.researchqueue);
				me.updateView();
			};
		request.post(postData);
		return false;
	};

ResearchQueue.prototype.updateView = function()
	{
		this.initQueueHooks();
		initTimer();
	};

/* Modifies a job in the research queue.
   Parameters: index specifies the position of the job entry.
               isDecrease specifies whether the count value needs to be inverted or not.
               count specifies by how many jobs the entry is modified; if none is given, user input is requested.
 */
ResearchQueue.prototype.modifyEntry = function(index, isDecrease, count)
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
		this.sendmsg("do=Player.modifyResearch&index="+index+"&count="+count);
		return false;
	};

/* Adds a job to the research queue.
   Parameters: topicId specifies the research topic.
               maxCount specifies the default value when prompting for count.
               count specifies how many jobs the entry has; if none is given, user input is requested.
 */
ResearchQueue.prototype.addEntry = function(topicId, maxCount, count)
	{
		if (!count)
		{
			count = promptNumber(gettext("How many times do you want to research?"), maxCount);
		}
		if (isNaN(count)) return false;
		this.sendmsg("do=Player.addResearch&id="+topicId+"&count="+count);
		return false;
	};

ResearchQueue.prototype.initListHooks = function()
	{
		var elems = document.getElementById("researchlist").getElementsByTagName("td");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substring(0, 5) == "btns-")
			{
				var num = elems[i].id.substring(5).toUpperCase();
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
							subelems[j].onclick = curry(this.addEntry, this, num, 0, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.addEntry, this, num, subelems[j].title, false);
						else
						{
							alert("found "+count);
						}
					}
				}
			}
		}
	};

ResearchQueue.prototype.initQueueHooks = function()
	{
		var elems = document.getElementsByTagName("span");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substring(0, 11) == "modifybtns-")
			{
				var num = elems[i].id.substring(11);
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var count = subelems[j].innerHTML;
						if (!isNaN(parseInt(count)))
							subelems[j].onclick = curry(this.modifyEntry, this, num, false, parseInt(count));
						else if (count == "+N")
							subelems[j].onclick = curry(this.modifyEntry, this, num, false, false);
						else if (count == "-N")
							subelems[j].onclick = curry(this.modifyEntry, this, num, true, false);
						else if (count == "[X]")
							subelems[j].onclick =  curry(this.sendmsg, this, "do=Player.deleteResearch&index="+num);
						else
							alert("found "+count);
					}
				}
			}
		
			if (elems[i].id.substring(0, 9) == "movebtns-")
			{
				var num = elems[i].id.substring(9);
				var subelems = elems[i].childNodes;
				for (var j = 0; j < subelems.length; j++)
				{
					if (subelems[j].nodeName.toLowerCase() == "a")
					{
						var mtype = subelems[j].firstChild.alt;
						if (mtype == "1.")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Player.moveResearch&index="+num+"&count=0");
						else if (mtype == "^")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Player.moveResearch&index="+num+"&count=1");
						else if (mtype == "v")
							subelems[j].onclick = curry(this.sendmsg, this, "do=Player.moveResearch&index="+num+"&count=-1");
						else
							alert("found "+mtype);
					}
				}
			}
		}
	
		var elems = document.getElementsByTagName("a");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id == "abort-btn")
				elems[i].onclick = curry(this.sendmsg, this, "do=Player.abortResearch");
		
			if (elems[i].id == "resume-btn")
				elems[i].onclick = curry(this.sendmsg, this, "do=Player.resumeResearch");
		}
	};

function initResearch()
{
	var rq = new ResearchQueue();
	rq.initListHooks();
	rq.initQueueHooks();
}

addInit(initResearch);

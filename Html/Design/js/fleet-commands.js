/* fleet-commands.js
 */

function FleetCommands(id)
{ this.id = id; }

FleetCommands.prototype.sendmsg = function(postData)
	{
		var me = this;
		var request = new AjaxRequest("fleet-commands.json");
		request.onsuccess = function(text)
			{
				var result = json_parse(text);
				replaceContent("events", result.events);
				replaceContent("orders", result.orders);
				me.updateView();
			};
		request.post(postData);
		return false;
	};


FleetCommands.prototype.updateView = function()
	{
		this.initModifyHooks();
		cmds.insertEditCommands();
		initTimer();
	};

FleetCommands.prototype.getAjaxFunction = function(id)
{
	var elems = document.getElementById(id).childNodes;
	return this.sendmsg(getParams(elems));
}

FleetCommands.prototype.initModifyHooks = function()
	{
		var rgx = /[a-z]+-([0-9]+)-(.*)/;
		/* move up, move down, and remove single commands */
		var elems = document.getElementById("modifyorders").getElementsByTagName("a");
		for (var i = 0; i < elems.length; i++)
		{
			if (elems[i].id.substr(0, 7) == "delcmd-")
			{
				var res = elems[i].id.match(rgx);
				if (res[1] != this.id)
				{
					continue;
				} 
				var index = res[2];
				elems[i].onclick = curry(this.sendmsg, this, "do=Command.remove&id="+this.id+"&index="+index);
			}
		
			if (elems[i].id.substr(0, 10) == "moveupcmd-")
			{
				var res = elems[i].id.match(rgx);
				if (res[1] != this.id)
				{
					continue;
				} 
				var index = res[2];
				elems[i].onclick = curry(this.sendmsg, this, "do=Command.moveUp&id="+this.id+"&index="+index);
			}

			if (elems[i].id.substr(0, 12) == "movedowncmd-")
			{
				var res = elems[i].id.match(rgx);
				if (res[1] != this.id)
				{
					continue;
				} 
				var index = res[2];
				elems[i].onclick = curry(this.sendmsg, this, "do=Command.moveDown&id="+this.id+"&index="+index);
			}

			if (elems[i].id.substr(0, 10) == "switchcmd-")
			{
				var res = elems[i].id.match(rgx);
				if (res[1] != this.id)
				{
					continue;
				} 
				var index = res[2];
				elems[i].onclick = curry(this.sendmsg, this, "do=Command.switch&id="+this.id+"&index="+index);
			}
		}

		/* remove multiple commands */
		var elems = document.getElementById("deleteorders").getElementsByTagName("form");
		for (var i = 0; i < elems.length; i++)
		{
			var subelems = elems[i].childNodes;
			for (var j = 0; j < subelems.length; j++)
			{					
				if ((subelems[j].nodeName.toLowerCase() == "button") && (subelems[j].type.toLowerCase() == "submit"))
					subelems[j].onclick = curry(this.getAjaxFunction, this, elems[i].getAttribute("id"));
			}
		}

		/* loop and unloop */
		var elem = document.getElementById("loopcommands");
		var subelems = elem.childNodes;
		for (var j = 0; j < subelems.length; j++)
		{					
			if ((subelems[j].nodeName.toLowerCase() == "input") && (subelems[j].type.toLowerCase() == "image"))
			{
				subelems[j].onclick = curry(this.getAjaxFunction, this, "loopcommands");
			}
		}
	
	};

FleetCommands.prototype.initAddHooks = function()
{
	var addcmds = document.getElementById("addcommands");
	var elems = addcmds.childNodes;
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].nodeName.toLowerCase() == "form")
		{
			var subelems = elems[i].childNodes;
			for (var j = 0; j < subelems.length; j++)
			{
				if ((subelems[j].nodeName.toLowerCase() == "button") && (subelems[j].type.toLowerCase() == "submit"))
					subelems[j].onclick = curry(this.getAjaxFunction, this, elems[i].getAttribute("id"));
			}
		}
	}
	
	var elem = document.getElementById("rename-fleet");
	var subelems = elem.childNodes;
	for (var j = 0; j < subelems.length; j++)
	{					
		if ((subelems[j].nodeName.toLowerCase() == "button") && (subelems[j].type.toLowerCase() == "submit"))
			subelems[j].onclick = curry(this.getAjaxFunction, this, "rename-fleet");
	}
}

FleetCommands.prototype.openEditor = function(id, index, cmdtype)
{
	var ctext = "";
	if ((cmdtype == "SETTLE") || (cmdtype == "SPY"))	
	{
		var name = document.getElementById("name-"+this.id+"-"+index).innerHTML;
		var values = name.match(/-?[\d.,]+/g);
		ctext =
		'\t<form id="cmd-editor-'+index+'" action="fleets-commands|'+this.id+'" method="post">\n'+
		'\t\t<input type="hidden" name="do" value="Fleet.Command.edit" />\n'+
		'\t\t<input type="hidden" name="id" value="'+this.id+'" />\n'+
		'\t\t<input type="hidden" name="num" value="'+index+'" />\n'+
		'\t\t'+gettext("units")+': <input type="text" size="2" value="'+values[0]+'" name="amount" />'+
		'\t</form>\n';
	}
	else if ((cmdtype == "LOAD") || (cmdtype == "UNLOAD") || (cmdtype == "ROB"))
	{
		var name = document.getElementById("name-"+this.id+"-"+index).innerHTML;
		var values = name.match(/-?[\d.,]+/g);
		if (values.length < 8)
		{
			values[4] = 0;
			values[5] = 0;
			values[6] = 0;
			values[7] = 0;
		}
		ctext = 
		'<form id="cmd-editor-'+index+'" action="fleets-commands|'+this.id+'" method="post">\n'+
		'\t<input type="hidden" name="do" value="Fleet.Command.edit" />\n'+
		'\t<input type="hidden" name="id" value="'+this.id+'" />\n'+
		'\t<input type="hidden" id="editor.num" name="num" value="'+index+'" />\n';
		var res = [gettext("steel"), gettext("oil"), gettext("silicium"), gettext("deuterium")];
		for (var i = 0; i < 4; i++)
		{
			ctext += '\t<img src="pack/design/res'+(i+1)+'.png" width="22" height="22" alt="'+res[i]+'" />';
			ctext += '\t<input type="text" value="'+values[i]+'" size="6" name="r'+i+'" />\n';
			if (cmdtype != "ROB")
			{
				ctext += gettext("but leave")+' <input type="text" value="'+values[i+4]+'" size="6" name="l'+i+'" />\n';
			}
			ctext += '\t<br />\n'; 
		}
		ctext += '</form>\n';
	}
	else return false; // not an editable command

	var editorid = "editor-"+index;
	var editor = document.getElementById(editorid);
	if (editor != null)
	{
		editor.parentNode.parentNode.removeChild(editor.parentNode);
	}
	else
	{
		newtr = document.createElement("tr");
		if (index%2 == 0)
		{
			newtr.className = "even";
		}
		else
		{
			newtr.className = "odd";
		}
 		newtd = document.createElement("td");
		newtd.id = editorid;
 		newtd.colSpan = 5;
		newtr.appendChild(newtd);
		document.getElementById(id).parentNode.parentNode.insertBefore(newtr,document.getElementById(id).parentNode.nextSibling);

		replaceContent(editorid,ctext);
		var elem = document.getElementById("cmd-editor-"+index);
		var submit = document.createElement("button");
		submit.type = "submit";
		submit.innerHTML = gettext("change");
		submit.onclick = curry(this.getAjaxFunction, this, 'cmd-editor-'+index);
		elem.appendChild(submit);

	}
	return false;
}

FleetCommands.prototype.insertEditCommands = function()
{
	rgx = /editcmd-([0-9]*)-([0-9]*)-([A-Z]*)/;
	var elems = document.getElementsByTagName("td");
	for (var i = 0; i < elems.length; i++)
	{
		var idparams = elems[i].id.match(rgx);
		if (idparams != null)
		{
			if ((idparams[1] == this.id) && (idparams[3] != "NONE"))
			{
				var newa = document.createElement("a");
				newa.href = "";
				newa.onclick = curry(this.openEditor, this, elems[i].id, idparams[2], idparams[3]);
				newa.innerHTML = gettext("edit");
				replaceContent(elems[i].id, "");
				elems[i].appendChild(newa);
			}
		}
	}
}

function initCommands()
{
	var id = document.getElementById("x-cqs-fleet").value;
	cmds = new FleetCommands(id);
	cmds.initAddHooks();
	cmds.initModifyHooks();
	cmds.insertEditCommands();
}

function getParams(elements)
{
	var params = "";
	for (var i = 0; i < elements.length; i++)
	{
		if (elements[i].nodeName.toLowerCase() == "input" || elements[i].nodeName.toLowerCase() == "select")
		{
			params += "&"+elements[i].name+"="+elements[i].value;
		}
	}
	return params;
}

function transferValue(id, resetid, value)
{
	document.getElementById(id).value = value;
	document.getElementById(resetid).selectedIndex = 0;
}

function validate()
{
	if (!(document.getElementById('targetposition').value.length > 0) &&
	     ((document.getElementById('move').checked) ||
	      (document.getElementById('gate').checked)))
	{
		return alert(gettext("Please enter the target coordinates:"));
	}
	document.getElementById('formular').submit();
}

addInit(initCommands);

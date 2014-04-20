/* common.js
 * Use addInit(method) to add an initializer method.
 */

// common values
var debugflag = document.getElementById("x-cqs-debug").content;
var stime = document.getElementById("x-cqs-time").content;
var speed = document.getElementById("x-cqs-speed").content;
var running = document.getElementById("x-cqs-running").content;


// initializer registration and calling
var initializers = [];

function addInit(init)
{ initializers[initializers.length] = init; }

function initAction()
{
	for (var i = 0; i < initializers.length; i++)
		initializers[i]();
}

onload = initAction;


// i18n functions
var i18ndata = [];

function gettext(text)
{
	if (i18ndata[text])
		return i18ndata[text];
	else
		return text;
}


// common ajax functions
function debug(msg)
{
	if (console) {
		console.log(msg);
        }
	try
	{
		if (debugflag == "true")
		{
			// debug output purposes with firefox/mozilla:
			netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
			var consoleService = Components.classes["@mozilla.org/consoleservice;1"]
				.getService(Components.interfaces.nsIConsoleService);
			consoleService.logStringMessage(msg);
		}
	}
	catch (error)
	{}
}

function replaceContent(id, content)
{
	debug("update webpage at "+id);
	var elem = document.getElementById(id);
	if (!elem) return;
	
	if (typeof window.Range != 'undefined' && typeof Range.prototype.createContextualFragment == 'function')
	{
		while (elem.hasChildNodes())
			elem.removeChild(elem.firstChild);
		var rng = document.createRange();
		rng.setStart(elem, 0);
		var frag = rng.createContextualFragment(content);
		elem.appendChild(frag);
	}
	else
	{
		elem.innerHTML = content;
	}
}

function promptNumber(msg, preset)
{
	var result = prompt(msg, preset);
	if (result == null) return NaN;
	return parseInt(result);
}

function setAllCheckboxes(checkAllId, checkboxContainer, regex)
{
	var checked = document.getElementById(checkAllId).checked;
	var checkboxContainer = document.getElementById(checkboxContainer);
	var checkboxes = checkboxContainer.getElementsByTagName('input');
	for (var i = 0; i < checkboxes.length; i++) 
	{
		if (checkboxes[i].name.match(regex))
	 		checkboxes[i].checked = checked;
	}
}


// Call with a function, a this argument, and a list of curried parameters.
// Returns a function that calls the given function on the given this argument,
// and the curried parameters concatenated with the actual parameters.
function curry(fn, thisArg)
{
	var thisArg = thisArg || window;
	var args = Array.prototype.slice.call(arguments, 2);
	return function()
	{
		return fn.apply(thisArg, args.concat(Array.prototype.slice.call(arguments, 0)));
	};
}

function AjaxRequest(url)
{ this.url = url; }

AjaxRequest.prototype.setFeedbackContainer = function(id)
	{ this.id = id; };

AjaxRequest.prototype.handleMeta = function(rtext)
	{
		var metargx =
			/^\s*<meta\s+name\=\"([^\"]+)\"\s+value\=\"([^\"]*)\"\s+\/\>\s*/;
		var res;
		while ((res = rtext.match(metargx)))
		{
			if (res[1] == "alert")
				alert(res[2]);
			else
				replaceContent(res[1], res[2]);
			rtext = rtext.replace(metargx, "");
		}
		return rtext;
	};

AjaxRequest.prototype._statechange = function(request)
	{
		if (request.readyState == 4)
		{
			if (request.status == 200)
			{
				if (this.onsuccess)
				{
					this.onsuccess(request.responseText);
				}
				else
				{
					var rtext = this.handleMeta(request.responseText);
					if (this.id)
						replaceContent(this.id, rtext);
					if (this.onload)
						this.onload();
				}
			}
			else
			{
				if (this.onerror)
					this.onerror(request.statusText);
				else
					alert(gettext("Es trat ein Fehler auf:")+"\n"+request.statusText);
			}
		}
	};

AjaxRequest.prototype.post = function(data)
	{
		var request;
		try
		{ request = new XMLHttpRequest(); }
		catch (error)
		{ request = new ActiveXObject("Microsoft.XMLHTTP"); }
		
		var _this = this;
		request.onreadystatechange = function(){_this._statechange(request);};
		request.open("POST", this.url);
		if (!data) data = "";
		request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setRequestHeader("Content-Encoding", "UTF-8");
		request.send(data);
	};

AjaxRequest.prototype.get = function(data)
	{
		var request;
		try
		{ request = new XMLHttpRequest(); }
		catch (error)
		{ request = new ActiveXObject("Microsoft.XMLHTTP"); }

		var _this = this;
		request.onreadystatechange = function(){_this._statechange(request);};
		request.open("GET", this.url+"?"+data);
		request.send(data);
	};

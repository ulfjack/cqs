/* common-preview.js
 */

function preview(id, text)
{
	var request = new AjaxRequest("preview.json");
	request.onsuccess = function(text)
		{
			var result = json_parse(text);
			replaceContent("wikipreview-"+id, result.preview);
		};
	request.get("text="+encodeURIComponent(text));
}

function ajaxPreviewClick(id)
{
	preview(id, document.getElementById("wikitext-"+id).value);
	document.getElementById("wikitext-"+id).style.display = "none";
	document.getElementById("wikipreview-"+id).style.display = "block";
	document.getElementById("wikinor-"+id).className = "normal";
	document.getElementById("wikipre-"+id).className = "previewon";
}

function ajaxNormalClick(id)
{
	document.getElementById("wikitext-"+id).style.display = "block";
	document.getElementById("wikipreview-"+id).style.display = "none";
	document.getElementById("wikinor-"+id).className = "normalon";
	document.getElementById("wikipre-"+id).className = "preview";
}

function initCommonPreview()
{
	var elems = document.getElementsByTagName("div");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].id.substring(0, 10) == "pvcontrol-")
		{
			var id = elems[i].id.substring(10);
			elems[i].innerHTML =
				"<a id='wikinor-"+id+"' href='javascript:ajaxNormalClick("+id+")'>"+elems[i].innerHTML+"</a> "+
				"<a id='wikipre-"+id+"' href='javascript:ajaxPreviewClick("+id+")'>"+gettext("preview")+"</a>";
				ajaxNormalClick(id);
		}
	}
}

addInit(initCommonPreview);

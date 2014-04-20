/* table-sort.js
 * Dynamically inserts HTML and JavaScript to allow the client to sort
 * a table by different criteria.
 * 
 * Usage:
 * You need to give out a couple of unique ids.
 * 1. the table itself, the id MUST start with "sortable", e.g.
 *    <table id="sortable0">
 * 
 *    You can have more than more than one table marked as sortable.
 * 
 * 2. the header row, the id MUST start with "head", e.g.
 *    <tr id="head0">
 * 
 * 3. the header columns, the ids MAY start with "key", the next two
 *    characters are ignored and the remainder determines the sorting
 *    type, e.g.
 *    <td id="key0-string">
 * 
 *    If the id does not start with "key" the column will not be sortable.
 * 
 *    Allowed sorting types are "string", "number", "time", and "coords".
 *    "string", "time", and "coords" remove links (<a ...> and </a>) from the entry.
 *    "number" removes all '.' and ',' characters before sorting.
 * 
 * 4. the data rows, the ids MUST start with "sort", e.g.
 *    <tr id="sort0">
 * 
 * 5. the data colums
 *    each data column in each row MUST have a unique id, freeform, e.g.
 *    <td id="0-x917s9fj1">
 * 
 *    All data columns MUST have an id, even if they are not sortable.
 * 
 * You can have several header rows, if you wish, for example, one above and
 * one below the data. You can have an arbitrary number of other rows.
 * 
 * A complete example follows:
 * <table id="sortable0">
 *   <tr><th colspan="3">Title</th></tr>
 *   <tr id="head0">
 *     <th id="key0-coords">Coordinates</th>
 *     <th id="key1-string">Name</th>
 *     <th id="key2-number">Amount</th>
 *     <th>No sorting on this column</th>
 *   </tr>
 *   <tr id="sortA">
 *     <td id="coordsA">1:5:10</td>
 *     <td id="nameA">  My Colony</td>
 *     <td id="numberA">1234</td>
 *     <td id="stuffA"> Some random stuff</td>
 *   </tr>
 *   <tr id="sortB">
 *     <td id="coordsB">2:4:9</td>
 *     <td id="nameB">  His Colony</td>
 *     <td id="numberB">1,235</td>
 *     <td id="stuffB"> Some more random stuff</td>
 *   </tr>
 *   <tr id="sortC">
 *     <td id="coordsC">7:0:33</td>
 *     <td id="nameC">  <a href="BLABLA">No Colony</a></td>
 *     <td id="numberC">1.231</td>
 *     <td id="stuffC"> Some less random stuff</td>
 *   </tr>
 * </table>
 * 
 * Remember that every id in an HTML file MUST be unique!
 * 
 * As of May 21st 2006, it will also automatically move the class names around
 */

var tables = new Array();

function TableData()
{
	this.ids = new Array();
	this.rows = new Array();
}

function TableRow()
{
	this.key = "KEY";
	this.classes = new Array();
	this.data = new Array();
}

function cleanLink(x)
{ return String(x).toLowerCase().replace(/\<a .*\>(.*)\<\/a\>/g, '$1'); }

function cleanNumber(x)
{ return parseInt(x.replace(/[\.,]/g, '')); }

function SortString(a,b)
{
	var x = cleanLink(a.key);
	var y = cleanLink(b.key);
	if (x < y) return 1;
	if (x > y) return -1;
	return 0;
}

function SortNumber(a,b)
{
	var x = cleanNumber(a.key);
	var y = cleanNumber(b.key);
	if (x < y) return 1;
	if (x > y) return -1;
	return 0;
}

function safeInt(x)
{
  if (!x) return 0;
  return parseInt(x);
}

function SortTime(a,b)
{
/* time has format: xd xx:xx:xx */
	var regex = /(?:(?:(?:0?([0-9]+)d )?0?([0-9]+):)?0?([0-9]+):)?0?([0-9]+)/;
	var xs = a.key.match(regex);
	var x = safeInt(xs[1])*24*60*60+safeInt(xs[2])*60*60+safeInt(xs[3])*60+safeInt(xs[4]);
	var ys = b.key.match(regex);
	var y = safeInt(ys[1])*24*60*60+safeInt(ys[2])*60*60+safeInt(ys[3])*60+safeInt(ys[4]);

	if (x < y) return -1;
	else if (x > y) return 1;
	else return 0;
}

function SortCoords(a, b)
{
	var x = cleanLink(a.key);
	var y = cleanLink(b.key);
	var xs = x.split(":");
	var ys = y.split(":");
	if (parseInt(xs[0]) < parseInt(ys[0])) return 1;
	if (parseInt(xs[0]) > parseInt(ys[0])) return -1;
	if (parseInt(xs[1]) < parseInt(ys[1])) return 1;
	if (parseInt(xs[1]) > parseInt(ys[1])) return -1;
	if (parseInt(xs[2]) < parseInt(ys[2])) return 1;
	if (parseInt(xs[2]) > parseInt(ys[2])) return -1;
	return 0;
}

function writeBack(table, result)
{
	var ids = table.ids;
	for (var i = 0; i < ids.length; i++)
		for (var j = 0; j < ids[i].length; j++)
		{
			var elem = document.getElementById(ids[i][j]);
			elem.innerHTML = result.rows[i].data[j];
			elem.setAttribute("class", result.rows[i].classes[j]);
		}
}

function copyTable(data)
{
	var result = new TableData();
	for (var i = 0; i < data.rows.length; i++)
		result.rows[result.rows.length] = data.rows[i];
	return result;
}

function sort(table, index, type)
{
	var result = copyTable(table);
	for (var i = 0; i < result.rows.length; i++)
		result.rows[i].key = result.rows[i].data[index];
	if (type == "string") result.rows.sort(SortString);
	if (type == "number") result.rows.sort(SortNumber);
	if (type == "time") result.rows.sort(SortTime);
	if (type == "coords") result.rows.sort(SortCoords);
	return result;
}

function up(tableid, index, type)
{
	var table = tables[tableid];
	var result = sort(table, index, type);
	result.rows.reverse();
	writeBack(table, result);
}

function down(tableid, index, type)
{
	var table = tables[tableid];
	var result = sort(table, index, type);
	writeBack(table, result);
}

function findData(td, row)
{
	var cols = row.getElementsByTagName("td");
	var nids = new Array();
	var nrow = new TableRow();
	for (var i = 0; i < cols.length; i++)
	{
		var elem = cols[i];
		nids[nids.length] = elem.id;
		nrow.classes[nrow.classes.length] = elem.getAttribute("class");
		nrow.data[nrow.data.length] = elem.innerHTML;
	}
	td.ids[td.ids.length] = nids;
	td.rows[td.rows.length] = nrow;
}

function enhanceHeader(tableid, row)
{
	var cols = row.getElementsByTagName("th");
	for (var i = 0; i < cols.length; i++)
	{
		var elem = cols[i];
		if (elem.id.substring(0, 3) == "key")
		{
			var type = elem.id.substring(5);
			
			var upimage = document.createElement("img");
			upimage.src = "pack/design/aup.gif";
			upimage.onclick = new Function("up("+tableid+","+i+",'"+type+"')");
			
			var downimage = document.createElement("img");
			downimage.src = "pack/design/adown.gif";
			downimage.onclick = new Function("down("+tableid+","+i+",'"+type+"')");
			
			elem.innerHTML = elem.innerHTML+"&#160;&#160;";
			elem.appendChild(upimage);
			elem.appendChild(downimage);
		}
	}
}

function enhanceTable(table)
{
	var tableid = tables.length;
	var td = new TableData();
	var rows = table.getElementsByTagName("tr");
	for (var i = 0; i < rows.length; i++)
	{
		var elem = rows[i];
		var typ = elem.id.substring(0, 4);
		if (typ == "sort")
			findData(td, elem);
		else if (typ == "head")
			enhanceHeader(tableid, elem);
	}
	tables[tableid] = td;
}

function tableSortInit()
{
	var elems = document.getElementsByTagName("table");
	for (var i = 0; i < elems.length; i++)
	{
		if (elems[i].id.substring(0, 8) == "sortable")
			enhanceTable(elems[i]);
	}
}

addInit(tableSortInit);


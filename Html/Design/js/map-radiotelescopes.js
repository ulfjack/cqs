/* map-radiotelescopes.js
 */

var data;

function drawPixel(x, y)
{
	var temp = document.createElement("div");
	temp.style.position = "absolute";
	temp.style.backgroundColor = "white";
	temp.style.left = x+"px";
	temp.style.top = y+"px";
	temp.style.width = "1px";
	temp.style.height = "1px";
	data.appendChild(temp);
}

function drawCircle(xpos, ypos, radius)
{
	data = document.createElement("div");
	drawPixel(xpos, ypos);
	var d = 3 - (2 * radius);
	var x = 0;
	var y = radius;
	while (x <= y)
	{
		drawPixel(xpos+x, ypos+y);
		drawPixel(xpos+x, ypos-y);
		drawPixel(xpos-x, ypos+y);
		drawPixel(xpos-x, ypos-y);
		
		drawPixel(xpos+y, ypos+x);
		drawPixel(xpos+y, ypos-x);
		drawPixel(xpos-y, ypos+x);
		drawPixel(xpos-y, ypos-x);
		if (d < 0)
			d += (4 * x) + 6;
		else
		{
			d += 4 * (x - y) + 10;
			y--;
		}
		x++;
	}
	document.getElementById("data").appendChild(data);
}

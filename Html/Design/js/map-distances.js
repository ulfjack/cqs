/* map-distances.js
 */

function lookup(x)
{
	for (var i = 0; i < sys.length; i++)
	{
		if (sys[i] == x)
			return i;
	}
	return -1;
}

function calcDistance()
{
	var xpos = document.getElementById("xpos").value;
	var ypos = document.getElementById("ypos").value;
	var regex = /^\d+$/;
	if (!xpos.match(regex) || !ypos.match(regex))
	{
		alert(gettext("You have to enter two system numbers."));
		return;
	}
	var xindex = lookup(xpos);
	var yindex = lookup(ypos);
	if ((xindex >= 0) && (yindex >= 0))
	{
		var dx = xs[xindex]-xs[yindex];
		var dy = ys[xindex]-ys[yindex];
		var dz = zs[xindex]-zs[yindex];
		var dist = (Math.sqrt(dx*dx+dy*dy+dz*dz)*systemFactor)/(galaxyFactor*travelFactor);
		var sec = Math.floor(dist % 60); dist /= 60;
		var min = Math.floor(dist % 60); dist /= 60;
		var hrs = Math.floor(dist % 24); dist /= 24;
		var days = Math.floor(dist);
		var s = "";
		if (days != 0)
			s = days+" "+gettext("days")+", "+hrs+" "+gettext("hours")+", "+min+" "+gettext("minutes")+" "+gettext("and")+" "+sec+" "+gettext("seconds");
		else
		{
			if (hrs != 0)
				s = hrs+" "+gettext("hours")+", "+min+" "+gettext("minutes")+" "+gettext("and")+" "+sec+" "+gettext("seconds");
			else
				s = min+" "+gettext("minutes")+" "+gettext("and")+" "+sec+" "+gettext("seconds");
		}
		alert(gettext("travel duration between systems")+" ("+xpos+" -> "+ypos+"):\n"+s);
	}
	else
		alert(gettext("One of the systems is not visible!"));
}

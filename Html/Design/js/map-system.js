/* map-system.js
 */

start = 10*stime;
v = new Date();

fix_x = 300;
fix_y = 300;

function setPosition(id, x, y)
{
//	debug(id+" = "+x+", "+y);
	var e = document.getElementById(id);
	e.style.left = Math.round(5*x/6 - 25)+"px";
	e.style.top = Math.round(5*y/6 - 25)+"px";
}

function doFleets(t)
{
	n = new Date();
	for (var i = 0; i < fleet_origin_speed.length; i++)
	{
		var f = (fleet_arrival_time[i] - t/10)/fleet_duration[i];
		if (f > 1)
		{
			var x = Math.cos(t/fleet_origin_speed[i] - fleet_origin_initial_angle[i])*
				fleet_origin_radius[i] + fix_x-fleet_image_size[i]/2-8;
			var y = Math.sin(t/fleet_origin_speed[i] - fleet_origin_initial_angle[i])*
				fleet_origin_radius[i] + fix_y-fleet_image_size[i]/2;
			setPosition("q"+i, x, y);
		}
		else if (f < 0) 
		{
			var x = Math.cos(t/fleet_goal_speed[i] - fleet_goal_initial_angle[i])*
				fleet_goal_radius[i] + fix_x-fleet_image_size[i]/2-8;
			var y = Math.sin(t/fleet_goal_speed[i] - fleet_goal_initial_angle[i])*
				fleet_goal_radius[i] + fix_y-fleet_image_size[i]/2;
			setPosition("q"+i, x, y);
		}
		else
		{
			if (f < 0.5) f = 2*f*f;
			else f = -1 + 4*f - 2*f*f;
			var angle = fleet_origin_angle[i]*f+fleet_goal_angle[i]*(1-f);
			var radius = fleet_origin_radius[i]*f+fleet_goal_radius[i]*(1-f);
			var x = Math.cos(angle)*radius+fix_x-fleet_image_size[i]/2-8;
			var y = Math.sin(angle)*radius+fix_y-fleet_image_size[i]/2;
			setPosition("q"+i, x, y);
		}
	}
}

function doPlanets(t)
{
	for (var i = 0; i < planet_speed.length; i++)
	{
		var phi = normalize(t/planet_speed[i]-planet_initial_angle[i]);
		var x = Math.cos(phi)*planet_radius[i]+fix_x-planet_image_size[i]/2;
		var y = Math.sin(phi)*planet_radius[i]+fix_y-planet_image_size[i]/2;
		setPosition("p"+i, x, y);
	}
}

function t()
{
	n = new Date();
	s = start + Math.round((speed*(n.getTime()-v.getTime()))/100.);
	doPlanets(s);
	doFleets(s);
	window.setTimeout(function(){t();},499);
}

function normalize(angle)
{ return (angle % (2*Math.PI) + (2*Math.PI)) % (2*Math.PI); }

function initMapSystem()
{
	planet_speed = new Array();
	planet_radius = new Array();
	planet_image_size = new Array();
	planet_initial_angle = new Array();
	
	var e = document.getElementById("planets");
	var elems = e.getElementsByTagName("area");
	for (var i = 0; i < elems.length; i++)
	{
		var info = elems[i].alt;
		var res = info.split(",");
		planet_speed.push(Number(res[0]));
		planet_radius.push(Number(res[1]));
		planet_image_size.push(Number(res[2]));
		planet_initial_angle.push(Number(res[3])/10000.);
	}
	
	fleet_image_size = new Array();
	fleet_arrival_time = new Array();
	fleet_duration = new Array();
	fleet_time_passed = new Array();
	fleet_origin_speed = new Array();
	fleet_origin_radius = new Array();
	fleet_origin_initial_angle = new Array();
	fleet_origin_angle = new Array();
	fleet_goal_speed = new Array();
	fleet_goal_radius = new Array();
	fleet_goal_initial_angle = new Array();
	fleet_goal_angle = new Array();
		
	var e = document.getElementById("fleets");
	if (e == null) return;
	var elems = e.getElementsByTagName("area");
	for (var i = 0; i < elems.length; i++)
	{
		var info = elems[i].alt;
		var res = info.split(",");
		fleet_image_size.push(Number(res[0]));
		var ftime = Number(res[1]);
		var duration = Number(res[2]);
		
		fleet_arrival_time.push(ftime);
		fleet_duration.push(duration);
		fleet_time_passed.push(stime-ftime+duration);
		
		fleet_origin_speed.push(Number(res[3]));
		fleet_origin_radius.push(Number(res[4]));
		fleet_origin_initial_angle.push(Number(res[5]))/10000.;
		
		var angle = 10*(ftime-duration)/Number(res[3])-Number(res[5])/10000.;
		fleet_origin_angle.push(normalize(angle));
		
		fleet_goal_speed.push(Number(res[6]));
		fleet_goal_radius.push(Number(res[7]));
		fleet_goal_initial_angle.push(Number(res[8])/10000.);
		
		var angle = 10*ftime/Number(res[6])-Number(res[8])/10000.;
		fleet_goal_angle.push(normalize(angle));
	}
}

addInit(initMapSystem);
addInit(t);

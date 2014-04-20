/* defaults.js
 * JavaScript functions that are loaded by default by the Rhino JavaScript console plugin.
 */

function findPlayer(name)
{
  var p = sim.findPlayerByName(name);
  return p;
}

function findEmail(email)
{
  var pi = sim.playerIterator();
  while (pi.hasNext())
  {
    var p = pi.next();
    if (p.email == email)
      return p;
  }
}

function giveSight(p)
{
  p.startTime = - 1000000;
  for (var i=0; i < sim.data.length; i++) p.galaxyView.addSystem(sim.data[i]);
}

function time()
{
  return sim.getRelativeTime();
}

function help()
{
	return "=== You called? ===\n"
		+"findPlayer(name)\n"
		+"findEmail(email)\n"
		+"time()\n";
}





function checkDatabase()
{
  var time = Packages.cqsCore.sim.getRelativeTime();
  var ci = sim.colonyIterator();
  while (ci.hasNext())
  {
    var c = ci.next();
    for (i = 1; i <= 4; i++)
      if (c.ressources.get(i) < 0)
      {
//        rhino.output("Colony "+c+" has negative ressource!");
//        c.ressources.set(i, 0);
      }
  }
  
  var ci = sim.colonyIterator();
  while (ci.hasNext())
  {
    var c = ci.next();
    
    if (c.battle != null)
      if (!c.battle.checkValidity())
        rhino.output("Battle at colony "+c+" was invalid!");
    
    if (c.planet.battle != null)
      if (!c.planet.battle.checkValidity())
        rhino.output("Battle at planet "+p+" ("+c+") was invalid!");
    
    if (c.battle != null)
      rhino.output("Battle at colony "+c);
  }
  
  var pi = sim.playerIterator();
  while (pi.hasNext())
  {
    var p = pi.next();
    
    if (p.colonies.length() == 0)
    {
      if (p.fleets.length() != 0)
      {
        rhino.output("Player "+p+" has no colonies. Dispatching fleets.");
        p.disbandAllFleets();
      }
    }
    
    var fi = p.fleets.iterator();
    while (fi.hasNext())
    {
      var f = fi.next();
      
    }
  }
}

function checkBattles()
{
  var ci = sim.colonyIterator();
  while (ci.hasNext())
  {
    var c = ci.next();
    
    if (c.battle != null)
    {
      fi = c.battle.sides[1].iterator();
      while (fi.hasNext())
      {
        f = fi.next();
        if (f.owner == null)
          f.owner = f.home.owner;
      }
    }
  }
}

function findFleet(position)
{
  var pi = sim.playerIterator();
  while (pi.hasNext())
  {
    var p = pi.next();
    var fi = p.fleets.iterator();
    while (fi.hasNext())
    {
      var f = fi.next();
      if (f.checkDestination(position))
        return true;
    }
  }
  return false;
}

function showFreeSystems()
{
  var time = Packages.cqsCore.sim.getRelativeTime();
  var si = sim.systemIterator();
  var i = 0;
  while (si.hasNext())
  {
    var s = si.next();
    if (!s.isSettled())
    {
      var hasFleets = s.fleets.length() != 0;
      var hasIncoming = findFleet(s.position);
      rhino.output("System "+s+" is not used ("+hasFleets+","+hasIncoming+") !");
      i++;
    }
  }
  rhino.output(i+" Systems found!");
}

function checkInvisibleSystems()
{
  var si = sim.systemIterator();
  while (si.hasNext())
  {
    var s = si.next();
    if (s.isSettled() && s.invisible) 
    {
     rhino.output("System "+s+" is settled&invisble. Setting visible.");
     s.invisible = false;
    }
  }
}

function countSettledSystems()
{
  var c = 0;
  var si = sim.systemIterator();
  while (si.hasNext())
  {
    var s = si.next();
    if (s.isSettled()) 
    {
	c++;
    }
  }
  rhino.output("Settled Systems: "+c);
}

function removeFreeSystems()
{
  var time = Packages.cqsCore.sim.getRelativeTime();
  var si = sim.systemIterator();
  var i = 0;
  while (si.hasNext())
  {
    var s = si.next();
    if (!s.isSettled())
    {
      var hasFleets = s.fleets.length() != 0;
      var hasIncoming = findFleet(s.position);
      rhino.output("System "+s+" is not used ("+hasFleets+","+hasIncoming+") !");
      i++;
      if (!hasFleets && !hasIncoming)
        s.invisible = true;
    }
  }
  rhino.output(i+" Systems found!");
}

function removeSystems()
{
  var time = Packages.cqsCore.sim.getRelativeTime();
  var si = sim.systemIterator();
  var i = 0;
	var r = java.util.Random();
	var c = 0;
  while (si.hasNext())
  {
    var s = si.next();
    if (!s.isSettled())
    {
      var hasFleets = s.fleets.length() != 0;
      var hasIncoming = findFleet(s.position);
      if (!hasFleets && !hasIncoming)
			{
			 if (r.nextDouble() > visibleProp) 
			 {
					s.invisible = true;
					i++;
			 } else { s.invisible = false; c++; }
			} else { s.invisible = false; c++; }
    } else { s.invisible = false; c++; }
	}
  rhino.output(i+" Systems made invisible, "+c+" left visible");
}




function resetBattles()
{
  var time = Packages.cqsCore.getRelativeTime();
  var ci = sim.colonyIterator();
  while (ci.hasNext())
  {
    var c = ci.next();
    if (c.battle != null)
    {
      c.battle.emergencyDispatchBattle();
      c.battle = null;
    }
    if (c.planet.battle != null)
    {
      c.planet.battle.emergencyDispatchBattle();
      c.planet.battle = null;
    }
  }
  var pi = sim.playerIterator();
  while (pi.hasNext())
  {
    var p = pi.next();
    p.battles.removeAll();
  }
}






function updateColonies()
{
  var time = Packages.cqsCore.getRelativeTime();
  var ci = sim.colonyIterator();
  while (ci.hasNext())
  {
    var c = ci.next();
    c.updateBeforeEvent(time);
    c.updateAfterEvent();
  }
}

function updatePlayers()
{
  var time = Packages.cqsCore.getRelativeTime();
  var pi = sim.playerIterator();
  while (pi.hasNext())
  {
    var p = pi.next();
    p.update(time);
  }
}

package net.cqs.engine;

import static org.junit.Assert.assertEquals;
import net.cqs.config.CheckResult;
import net.cqs.config.Resource;
import net.cqs.engine.base.Cost;

import org.junit.Before;
import org.junit.Test;

public class ResourceListTest
{

private ResourceList rl; 

@Before
public void setUp()
{
	rl = new ResourceList(0);
	rl.updateBeforeEvent(0);
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		rl.setLimit(i, 1000);
	rl.updateAfterEvent();
}

@Test
public void testCheck()
{
	assertEquals(0L, rl.getPaidResources());
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		rl.addResources(0, i, 500);
	
	Cost cost = new Cost(new int[] {1,2,3,4}, 0, 0, 0, 0);
	assertEquals(CheckResult.OK, rl.check(0, cost));
}

@Test
public void testRob()
{
	for (int i = 0; i < 4; i++)
		rl.addResources(0, i, 100);

	Cost rob = new Cost(new int[] {50,60,70,80}, 0, 0, 0, 0);
	Cost robbed = rl.rob(0, rob, 400);
	assertEquals(50, robbed.getResource(0));
	assertEquals(60, robbed.getResource(1));
	assertEquals(70, robbed.getResource(2));
	assertEquals(80, robbed.getResource(3));
}

@Test
public void testRobAll()
{
	rl.addResources(0, 0, 100);
	rl.addResources(0, 1, 200);
	rl.addResources(0, 2, 300);
	rl.addResources(0, 3, 400);
	Cost rob = new Cost(new int[] {-1,90,-1,-1}, 0, 0, 0, 0);
	Cost robbed = rl.rob(0, rob, 400);
	assertEquals(100, robbed.getResource(0));
	assertEquals(90, robbed.getResource(1));
	assertEquals(210, robbed.getResource(2));
	assertEquals(0, robbed.getResource(3));
}

}

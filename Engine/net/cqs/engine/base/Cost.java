package net.cqs.engine.base;

import java.io.Serializable;

import net.cqs.config.Resource;
import net.cqs.config.ResourceEnum;

public final class Cost implements Serializable
{

private static final long serialVersionUID = 1L;

public static Cost sum(Cost c0, Cost c1)
{
	int[] sumResources = new int[Resource.MAX+1];
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		sumResources[i] = c0.resources[i] + c1.resources[i];
	int sumPopulation = c0.population+c1.population;
	int sumMoney = c0.money+c1.money;
	int sumRegularJobs = c0.regularJobs+c1.regularJobs;
	int sumAdministrativeJobs = c0.administrativeJobs+c1.administrativeJobs;
	int sumConstructionJobs = c0.constructionJobs+c1.constructionJobs;
	long sumTime = c0.time+c1.time;
	Cost result = new Cost(sumResources, sumPopulation, sumMoney, 
			sumRegularJobs, sumAdministrativeJobs, sumConstructionJobs, sumTime);
	return result;
}


private final int[] resources;
private final int population;
private final int money;

private final int regularJobs;
private final int administrativeJobs;
private final int constructionJobs;

private final long time;

public Cost(int[] resources, int population, int money, int regularJobs, int administrativeJobs, int constructionJobs, long time)
{
	this.resources = new int[Resource.MAX+1];
	for (int i = 0; i < resources.length; i++)
		this.resources[i] = resources[i];
	
	this.population = population;
	this.money = money;
	this.regularJobs = regularJobs;
	this.administrativeJobs = administrativeJobs;
	this.constructionJobs = constructionJobs;
	this.time = time;
}

public Cost(int[] resources, int money, int crew, int constructionJobs, long time)
{ this(resources, crew, money, 0, 0, constructionJobs, time); }

public Cost(int[] resources)
{ this(resources, 0, 0, 0, 0, 0, 0L); }

public Cost(int constructionJobs, long time)
{ this(new int[0], 0, 0, 0, 0, constructionJobs, time); }

public Cost(long time)
{ this(new int[0], 0, 0, 0, 0, 0, time); }

public Cost(Cost other)
{
	this(other.resources, other.population, other.money, other.regularJobs,
		other.administrativeJobs, other.constructionJobs, other.time);
}

public Cost(Cost other, int[] resources, long time)
{
	this(resources, other.population, other.money, other.regularJobs,
		other.administrativeJobs, other.constructionJobs, time);
}

public Cost(Cost other, int[] resources)
{
	this(resources, other.population, other.money, other.regularJobs,
		other.administrativeJobs, other.constructionJobs, other.time);
}

public Cost(Cost other, long time)
{
	this(other.resources, other.population, other.money, other.regularJobs,
		other.administrativeJobs, other.constructionJobs, time);
}

public Cost()
{ this(new int[0], 0, 0, 0, 0, 0, 0L); }

public int getResource(ResourceEnum resource)
{
	int index = resource.index();
	if (index >= resources.length)
		return 0;
	return resources[index];
}

public int getResource(int resource)
{
	if (resource >= resources.length)
		return 0;
	return resources[resource];
}

public int getPopulation()
{ return population; }

public int getMoney()
{ return money; }

public int getRegularJobs()
{ return regularJobs; }

public int getAdministrativeJobs()
{ return administrativeJobs; }

public int getConstructionJobs()
{ return constructionJobs; }

public long getTime()
{ return time; }

@Override
public String toString()
{
	StringBuffer result = new StringBuffer();
	result.append("Res: (");
	for (int i = 0; i < resources.length-1; i++)
		result.append(resources[i]).append(", ");
	result.append(resources[resources.length-1]);
	result.append("); Pop: ").append(population);
	result.append("; Money: ").append(money);
	result.append("; RegJ: ").append(regularJobs);
	result.append("; AdmJ: ").append(administrativeJobs);
	result.append("; conJ: ").append(constructionJobs);
	result.append("; time: ").append(time);
	return result.toString();
}

}

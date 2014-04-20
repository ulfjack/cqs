package net.cqs.engine;

import net.cqs.config.CheckResult;
import net.cqs.config.Constants;
import net.cqs.config.Resource;
import net.cqs.config.ResourceEnum;
import net.cqs.engine.base.Cost;

import java.io.Serializable;
import java.util.logging.Level;

public final class ResourceList implements Serializable
{

private static final long serialVersionUID = 1L;

private double wasted = 0;
private double overloaded = 0;
private double robbed = 0;
private long paid = 0;

private long[] storages = new long[Resource.MONEY+1];
private double[] rates  = new double[Resource.MONEY+1];
private double[] values = new double[Resource.MONEY+1];

private long administrativeJobs = 10;
private long constructionJobs = 0;
private long regularJobs = 0;

private double population = 15000;
private long populationLimit = 1;

private double populationGrowthLambda = 1e-5;
private double populationGrowthTotalModifier = 100;

private long   populationGrowthApprox = 0;
private double efficiencyApprox = 0;

private long timePopulationReachesReq;
private long lastUpdateTime;

private boolean displayValid = false;
private long displayTime;
private long[] displayValues = new long[Resource.MONEY+1];
private long displayPopulation;

public ResourceList(long time)
{
	lastUpdateTime = time;
	
	for (ResourceEnum res : ResourceEnum.realResources())
		values[res.index()] = res.getStartAmount();
	values[ResourceEnum.MONEY.index()] = ResourceEnum.MONEY.getStartAmount();
}


public long getPaidResources()
{ return paid; }

public long getWastedResources()
{ return (long) wasted; }

public void setPopulationLimit(long limit)
{ populationLimit = limit; }

public void setPopulationModifier(double twm)
{
	populationGrowthTotalModifier = Math.round(twm);
	populationGrowthLambda = (Colony.LAMBDA_FACTOR * Constants.POPULATION_FACTOR * twm) / 1000;
}

public void setRegularJobs(long sum)
{ regularJobs = sum; }

public void setAdministrativeJobs(long sum)
{ administrativeJobs = sum; }

public void setConstructionJobs(long sum)
{ constructionJobs = sum; }

public void setRate(int typ, double rate)
{ rates[typ] = rate; }

public void setLimit(int typ, long limit)
{ storages[typ] = limit; }


public long getPopulationLimit()
{ return populationLimit; }

public long getRegularJobs()
{ return regularJobs; }

public long getAdministrativeJobs()
{ return administrativeJobs; }

public long getConstructionJobs()
{ return constructionJobs; }

public long getRate(int typ)
{ return (long) Math.floor(rates[typ]); }

public long getLimit(int typ)
{ return storages[typ]; }


public long getIrrevocableJobs()
{ return constructionJobs + administrativeJobs; }

public double getEfficiencyApprox()
{ return efficiencyApprox; }

public double getPopulationGrowthTotalModifier()
{ return populationGrowthTotalModifier; }

public double getPopulationGrowthApprox()
{ return populationGrowthApprox; }


private double getEfficiency(long t1)
{
	long t0 = lastUpdateTime;
	long maxTime = t1-t0;
	
	if (maxTime <= 0)
	{
		if (maxTime < 0)
		{
			Galaxy.logger.log(Level.SEVERE, "Exception caught",
				  new RuntimeException("Invalid Timing: "+this+" "+this+" t0="+t0+" t1="+t1));
		}
		return 0;
	}
	
	// zuviel Bevoelkerung?
	if (population > populationLimit)
		population = populationLimit;
	
	double irrevocableJobs = administrativeJobs + constructionJobs;
	double regularWorkers = population - irrevocableJobs;
	
	if (regularWorkers <= 0) return 0;
	
	if (regularWorkers >= regularJobs)
		return maxTime;
	else
	{
		double tsum = 0;
		
		if (t0 > timePopulationReachesReq)
		{
			// We have a problem here!
		}
		if (t1 >= timePopulationReachesReq)
		{	
			tsum += t1-timePopulationReachesReq;
			t1 = timePopulationReachesReq;
		}
		
		// Rate abhaengig vom Bevoelkerungswachstum
		if (populationGrowthLambda > 1e-6)
		{
			double factor = Math.exp( (t1-t0)*populationGrowthLambda );
			double fraction = population / populationLimit;
			
			double sum = (factor - 1) * fraction + 1;
			assert sum >= 1;
			
			double enumerator = populationLimit*Math.log(sum);
			double denominator = regularJobs*populationGrowthLambda;
			
			double a = enumerator / denominator;
			double b = (t1-t0) * irrevocableJobs / regularJobs;
			double c = a - b;
			
			if ((a < 0) || (c < 0))
			{
				Galaxy.logger.log(Level.SEVERE, "Exception caught",
				  new RuntimeException("SHOULD NOT HAPPEN (das Uebliche halt): "
				    +this+" "+this+" a="+a+" b="+b+" t0="+t0)
				  );
				c = 0;
			}
			
			tsum += c;
		} 
		else 
		{
			//Kein wachstum == effizienz konstant
			tsum += regularWorkers*(t1-t0) / regularJobs;
			assert tsum >= 0;
		}
		
		if (tsum < 0)
		{
			tsum = 0;
		}
		return tsum;
	}
}

private double getLevel(long t1, int typ)
{
	double rate = rates[typ];
	double currentRess = values[typ];
	double tsum = getEfficiency(t1);
	
	assert currentRess >= 0;
	assert rate >= 0;
	assert tsum >= 0;
	
	double total = currentRess + tsum*rate / 3600.0;
	return total;
}

private double getPopulation(long t1)
{
	long t0 = lastUpdateTime;
	if (t0 == t1) return population;
	
	double factor = Math.exp( - populationGrowthLambda*(t1-t0) );
	double enumerator = populationLimit * population;
	double denominator = population + (populationLimit - population)*factor;
	double quotient = enumerator / denominator;
	
	double newPopulation = quotient;
	
	if (newPopulation > populationLimit)
	{
		newPopulation = populationLimit;
		Galaxy.logger.log(Level.SEVERE, "INTERNAL ERROR: population_limit exceeded!");
	}
	
	updateApproximates(newPopulation);
	return newPopulation;
}

private void updateApproximates(double newPop)
{
	populationGrowthApprox = Math.round((3600*populationGrowthLambda*newPop*(populationLimit - newPop))/populationLimit);
	efficiencyApprox = Math.min(1, (newPop - administrativeJobs - constructionJobs) / regularJobs );
	if (efficiencyApprox < 0) efficiencyApprox = 0;
}

private void updatePopulationVicinity()
{
	long irrevocableJobs = constructionJobs + administrativeJobs;
	long populationRequired = irrevocableJobs + regularJobs;
	long populationAvailable = (long) Math.floor(population) - irrevocableJobs;
	long populationAvailableLimit = populationLimit - irrevocableJobs;
	
	if ((populationAvailable >= regularJobs) || (regularJobs >= populationAvailableLimit))
		timePopulationReachesReq = Long.MAX_VALUE;
	else
	{
		if (populationRequired < population)
		{
			Galaxy.logger.log(Level.SEVERE, "Exception caught",
					new RuntimeException("This MUST NOT HAPPEN! "+this+" von "));
		}

		double enumerator  = populationRequired * populationLimit - populationRequired * population;
		double denominator = population         * populationLimit - populationRequired * population;
		double result = (Math.log(enumerator)-Math.log(denominator)) / populationGrowthLambda;
		if (result > Long.MAX_VALUE-lastUpdateTime-10000)
			timePopulationReachesReq = Long.MAX_VALUE;
		else
			timePopulationReachesReq = lastUpdateTime+(long) Math.floor(result);
		
		if (timePopulationReachesReq < lastUpdateTime)
		{
			Galaxy.logger.log(Level.SEVERE, "Exception caught",
					new RuntimeException(
			   "ERROR: number overflow\n"
			 + this+" "
			 + "population_required = "+populationRequired+"\n"
			 + "population_limit    = "+populationLimit+"\n"
			 + "population          = "+population+"\n"
			 + "enumerator          = "+enumerator+"\n"
			 + "denominator         = "+denominator+"\n"
			 + "result              = "+result+"\n"
			 + "time_population_reaches_req = "+timePopulationReachesReq+"\n"));
			
			timePopulationReachesReq = Long.MAX_VALUE;
		}
	}
}

private void updateResources(long time)
{
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		long max = storages[i];
		double value = getLevel(time, i);
		if (value > max)
		{
			wasted += value-max;
			value = max;
		}
		values[i] = value;
	}
	
	values[Resource.MONEY] = getLevel(time, Resource.MONEY);
}

private void updatePopulation(long time)
{
	population = getPopulation(time);
}

private void checkedAdd(int typ, long amount)
{
	values[typ] += amount;
	if (values[typ] > storages[typ])
	{
		overloaded += values[typ]-storages[typ];
		values[typ] = storages[typ];
	}
}

public long getIncome(long time)
{
	if (lastUpdateTime != time)
		updateForEvent(time);
	long result = (long) Math.floor(values[Resource.MONEY]);
	values[Resource.MONEY] -= result;
	return result;
}

public long calculateIncome()
{
	return Math.round(efficiencyApprox*rates[Resource.MONEY]);
}

public void updateBeforeEvent(long time)
{
	updateResources(time);
	updatePopulation(time);
	lastUpdateTime = time;
	displayValid = false;
}

public void updateAfterEvent()
{
//	calculateRates();
//	calculateStorages();
//	calculatePoints();
	updatePopulationVicinity();
	updateApproximates(population);
}

public void updateForEvent(long time)
{
	updateBeforeEvent(time);
}

public int addPeople(long time, int amount)
{
	updateBeforeEvent(time);
	if (population < populationLimit)
	{
		population += amount;
		if (population > populationLimit)
		{
			amount -= populationLimit-population;
			population = populationLimit;
		}
	}
	else
		amount = 0;
	updateAfterEvent();
	return amount;
}

public long addResources(long time, int typ, long amount)
{
	updateBeforeEvent(time);
	checkedAdd(typ, amount);
	updateAfterEvent();
	return amount;
}

public long removeResources(long time, int typ, long amount, long leaverest)
{
	updateBeforeEvent(time);
	long max = (long) Math.floor(values[typ]);
	if (amount > max-leaverest) amount = max-leaverest;
	if (amount < 0) amount = 0;
	values[typ] -= amount;
	updateAfterEvent();
	return amount;
}

public CheckResult check(long time, Cost cost)
{
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		if (cost.getResource(i) > getDisplayResource(time, i)) // values[i]
			return CheckResult.forResource(i);
	}
	if (cost.getConstructionJobs()+cost.getAdministrativeJobs()+cost.getPopulation() > 
			getDisplayPopulation(time)-administrativeJobs-constructionJobs)
		return CheckResult.PEOPLE_MISSING;
	return CheckResult.OK;
}

public Cost rob(long time, Cost cost, long maxAmount)
{
	updateBeforeEvent(time);
	// check
	int[] robbedResources = new int[Resource.MAX+1];
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		robbedResources[i] = cost.getResource(i);
		// plunder as many resources as possible
		if ((robbedResources[i] < 0) || (robbedResources[i] > values[i]))
			robbedResources[i] = (int) Math.min(Math.floor(values[i]), maxAmount);
		maxAmount -= robbedResources[i];
	}
	
	// remove
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		robbed += robbedResources[i];
		values[i] -= robbedResources[i];
	}
	updateAfterEvent();
	return new Cost(robbedResources);
}

public CheckResult buy(long time, Cost cost)
{
	updateBeforeEvent(time);
	// check
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		if (cost.getResource(i) > values[i])
			return CheckResult.forResource(i);
	}
	if (cost.getConstructionJobs()+cost.getAdministrativeJobs()+cost.getPopulation() > 
			population-administrativeJobs-constructionJobs)
		return CheckResult.PEOPLE_MISSING;
	
	// remove
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		paid += cost.getResource(i);
		values[i] -= cost.getResource(i);
	}
	population -= cost.getPopulation();
	updateAfterEvent();
	return CheckResult.OK;
}

public boolean payback(long time, Cost cost)
{
	updateBeforeEvent(time);
	// remove
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		paid -= cost.getResource(i);
		checkedAdd(i, cost.getResource(i));
	}
	population += cost.getPopulation();
	// TODO: Handle too many people!
	if (population > populationLimit)
		population = populationLimit;
	updateAfterEvent();
	return true;
}

private void updateDisplay(long time)
{
	if (displayValid && (time == displayTime)) return;
	displayValid = false;
	displayTime = time;
	
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
	{
		long max = storages[i];
		displayValues[i] = (long) Math.floor(getLevel(time, i));
		if (displayValues[i] > max)
			displayValues[i] = max;
	}
	displayValues[Resource.MONEY] = (long) Math.floor(getLevel(time, Resource.MONEY));
	
	displayPopulation = (long) Math.floor(getPopulation(time));
	displayValid = true;
}

public long getDisplayResource(long time, int typ)
{
	updateDisplay(time);
	return displayValues[typ];
}

public long getDisplayPopulation(long time)
{
	updateDisplay(time);
	return displayPopulation;
}

}

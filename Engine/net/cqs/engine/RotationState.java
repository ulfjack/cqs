package net.cqs.engine;

public final class RotationState
{

private final long radius;
private final long speed;
private final long theta;

public RotationState(long radius, long speed, long theta)
{
	this.radius = radius;
	this.speed = speed;
	this.theta = theta;
}

public long getRadius()
{ return radius; }

public long getSpeed()
{ return speed; }

public long getTheta()
{ return theta; }

}

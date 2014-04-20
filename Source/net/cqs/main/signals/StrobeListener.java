package net.cqs.main.signals;

public interface StrobeListener
{

/**
 * Return the desired strobe interval in seconds.
 * 
 * @return the desired strobe interval in seconds
 */
long getTimeInterval();

/**
 * Is called every <code>getTimeInterval()</code> seconds.
 */
void strobe();

}

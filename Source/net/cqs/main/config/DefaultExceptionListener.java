package net.cqs.main.config;

import java.util.logging.Level;

import net.cqs.main.signals.ExceptionListener;

public final class DefaultExceptionListener implements ExceptionListener
{

@Override
public void exceptionRaised(Throwable e)
{ FrontEnd.logger.log(Level.SEVERE, "Exception caught", e); }

}

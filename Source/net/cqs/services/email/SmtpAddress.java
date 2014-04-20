package net.cqs.services.email;

import java.io.Serializable;

public final class SmtpAddress implements Serializable
{

private static final long serialVersionUID = 1L;

private final String address;

public SmtpAddress(String address)
{ this.address = address; }

@Override
public String toString()
{ return address; }

}

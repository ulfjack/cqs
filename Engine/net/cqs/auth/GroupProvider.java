package net.cqs.auth;

public interface GroupProvider
{

boolean isInGroup(Identity identity, String name);

}

package net.cqs.services;

import net.cqs.auth.Identity;

public interface AuthService extends Service
{

Identity authenticate(String name, String pw);
String getEmail(Identity identity);
boolean isInGroup(Identity identity, String name);
Identity mapOpenId(String openidurl);

boolean createIdentity(String name, String passwd, String email, String[] groups);
boolean changePassword(Identity identity, String password);
boolean changeEmail(Identity identity, String email);

}

package net.cqs.services;

import net.cqs.auth.Identity;

public interface RegistrationService extends Service
{

boolean registerPerEmail(String email);
Identity createIdentity(String token, String name, String password, String email, String[] groups);

}

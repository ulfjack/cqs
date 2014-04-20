package net.cqs.services;

import net.cqs.auth.Identity;

public interface AdminService extends Service
{

boolean mayAccessAdministration(Identity id);

}

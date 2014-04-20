package net.cqs.web.admin.auth;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;

public interface Rule
{

/**
 * Check whether the current rule allows the given identity to access some resource.
 * Return false if this rule does not apply to the given identity.
 * @returns true, if access is allowed
 * @throws AccessViolationException if access is denied
 */
boolean check(GroupProvider provider, Identity identity) throws AccessViolationException;

}
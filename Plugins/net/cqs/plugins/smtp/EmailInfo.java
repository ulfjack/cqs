package net.cqs.plugins.smtp;

import net.cqs.services.email.Email;

final class EmailInfo
{

Email email;
int tries = 0;

public EmailInfo(Email email)
{ this.email = email; }

}

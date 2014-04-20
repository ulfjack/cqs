package net.cqs.services;

import net.cqs.services.email.Email;

public interface SmtpService extends Service
{

void sendEmail(Email email);

}

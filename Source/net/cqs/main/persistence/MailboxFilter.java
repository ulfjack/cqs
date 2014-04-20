package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.engine.messages.MessageState;
import net.cqs.engine.messages.PlayerMessageType;

public interface MailboxFilter extends Serializable
{

int getBox(PlayerMessageType type, MessageState state);

}

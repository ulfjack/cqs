package net.cqs.main.persistence;

import net.cqs.engine.messages.MessageState;
import net.cqs.engine.messages.PlayerMessageType;

public final class GroupMailboxFilter implements MailboxFilter
{

private static final long serialVersionUID = 1L;

@Override
public int getBox(PlayerMessageType type, MessageState state)
{
	if (type == PlayerMessageType.FORUM) return 0;
	if (type == PlayerMessageType.APPLICATION) return 2;
	return 1;
}

}

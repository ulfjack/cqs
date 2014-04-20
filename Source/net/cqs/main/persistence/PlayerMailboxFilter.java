package net.cqs.main.persistence;

import net.cqs.engine.messages.MessageState;
import net.cqs.engine.messages.PlayerMessageType;

public final class PlayerMailboxFilter implements MailboxFilter
{

private static final long serialVersionUID = 1L;

@Override
public int getBox(PlayerMessageType type, MessageState state)
{
	if (state == MessageState.SENT) return 2;
	return state == MessageState.READ ? 1 : 0;
}

}

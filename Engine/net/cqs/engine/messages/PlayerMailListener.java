package net.cqs.engine.messages;

import net.cqs.engine.Player;

public interface PlayerMailListener
{

void notifyMail(Player recipient, Message message);

}

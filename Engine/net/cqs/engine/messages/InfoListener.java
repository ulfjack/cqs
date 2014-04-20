package net.cqs.engine.messages;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;

/**
 * Ermoeglicht den Empfang von {@link Information}-Nachrichten.
 * 
 * @see Galaxy#addInfoListener(InfoListener)
 * @see Information
 */
public interface InfoListener
{

void notifyInfo(Player recipient, Information m);

}

package net.cqs.engine.messages;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import net.cqs.util.TextConverter;

import org.junit.Test;

public class MessageTest
{

	static class NullTextConverter implements TextConverter
	{
		@Override
    public String convert(String text)
		{ return text; }
	}

private static final NullTextConverter CONVERTER = new NullTextConverter();

@Test
public void testCopyConstructor()
{
	Message m = new Message(PlayerMessageType.SYSTEM, 22, 23, 1234L, Locale.GERMANY, "Subject", "Text");
	
	Message n = new Message(m);
	assertEquals(m.getRealtime(), n.getRealtime());
	assertEquals(m.getSenderId(), n.getSenderId());
	assertEquals(m.getRecipientId(), n.getRecipientId());
	assertEquals(m.getSubject(CONVERTER), n.getSubject(CONVERTER));
	assertEquals(m.getText(CONVERTER), n.getText(CONVERTER));
	assertEquals(m.getType(), n.getType());
}

}

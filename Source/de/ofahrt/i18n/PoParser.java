package de.ofahrt.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PoParser
{

	private static enum State
	{
		START, COMMENT, CONTEXT, MSGID, TRANSLATION;
	}

private static final Pattern DATA_PATTERN = Pattern.compile("msg(ctxt|id|str) \"(.*)\"");
private static final Pattern CONTINUATION_PATTERN = Pattern.compile("\"(.*)\"");

private final BufferedReader in;
private int lineNo = 0;

public PoParser(Reader reader)
{
	this.in = new BufferedReader(reader);
}

private String extract(String data) throws IOException
{
	Matcher m = DATA_PATTERN.matcher(data);
	if (!m.matches())
		throw new IOException("Unrecognized data in line "+lineNo);
	return PoHelper.unescape(m.group(2));
}

private String extractContinuation(String data) throws IOException
{
	Matcher m = CONTINUATION_PATTERN.matcher(data);
	if (!m.matches())
		throw new IOException("Unrecognized data in line "+lineNo);
	return PoHelper.unescape(m.group(1));
}

public PoEntry readEntry() throws IOException
{
	State state = State.START;
	PoEntry.Builder builder = new PoEntry.Builder();
	String s;
	while ((s = in.readLine()) != null)
	{
		lineNo++;
		if (s.equals(""))
		{
			if (state == State.START)
				continue;
			else if (state == State.TRANSLATION)
				break;
			else
				throw new IOException("Unrecognized data in line "+lineNo);
		}
		else if (s.startsWith("#"))
		{
			if ((state != State.START) && (state != State.COMMENT))
				throw new IOException("Unrecognized data in line "+lineNo);
			state = State.COMMENT;
			if (s.startsWith("#. "))
				builder.appendExtractedComment(s.substring(3));
			else if (s.startsWith("#: "))
				builder.appendReference(s.substring(3));
			else if (s.startsWith("#, "))
				builder.appendFlags(s.substring(3));
			else
				builder.appendTranslatorComment(s.substring(1).trim());
		}
		else if (s.startsWith("msg"))
		{
			if (s.startsWith("msgctxt "))
			{
				if ((state == State.CONTEXT) || (state == State.MSGID) || (state == State.TRANSLATION))
					throw new IOException("Unrecognized data in line "+lineNo);
				state = State.CONTEXT;
				builder.setContext(extract(s));
			}
			else if (s.startsWith("msgid "))
			{
				if ((state == State.MSGID) || (state == State.TRANSLATION))
					throw new IOException("Unrecognized data in line "+lineNo);
				state = State.MSGID;
				builder.setMessage(extract(s));
			}
			else if (s.startsWith("msgstr "))
			{
				if (state != State.MSGID)
					throw new IOException("Unrecognized data in line "+lineNo);
				state = State.TRANSLATION;
				builder.setTranslation(extract(s));
			}
			else
				throw new IOException("Unrecognized data in line "+lineNo);
		}
		else if (s.startsWith("\""))
		{
			switch (state)
			{
				case CONTEXT :
					builder.appendContext(extractContinuation(s));
					break;
				case MSGID :
					builder.appendMessage(extractContinuation(s));
					break;
				case TRANSLATION :
					builder.appendTranslation(extractContinuation(s));
					break;
				default :
					throw new IOException("Unrecognized data in line "+lineNo);
			}
		}
		else
			throw new IOException("Unrecognized data in line "+lineNo);
	}
	if (state == State.START) return null;
	return builder.build();
}

}

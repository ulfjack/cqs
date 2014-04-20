package de.ofahrt.i18n;

import java.io.IOException;
import java.io.Serializable;

public final class PoEntry implements Cloneable, Serializable
{

private static final long serialVersionUID = 1L;

private static final String LINE_BREAK = "\n";

private final String translatorComment;
private final String extractedComment;
private final String reference;
private final String flags;
private final String context;
private final String message;
private final String translation;

private PoEntry(Builder builder)
{
	this.translatorComment = builder.translatorComment;
	this.extractedComment = builder.extractedComment;
	this.reference = builder.reference;
	this.flags = builder.flags;
	this.context = builder.context;
	this.message = builder.message;
	this.translation = builder.translation;
}

/**
 * Returns the optional context in which the message was found. This is usually
 * a class or package name. Can be null.
 */
public String getContext()
{ return context; }

/**
 * Returns the message to be translated. By convention this is the text in US
 * English.
 */
public String getMessage()
{ return message; }

/**
 * Returns the translation of the message. Returns null if untranslated.
 */
public String getTranslation()
{ return translation; }

/**
 * Returns the optional comment associated with the message. This comment
 * typically clarifies the context in which the message is found.
 */
public String getExtractedComment()
{ return extractedComment; }

/**
 * Returns the optional comment the translator added.
 */
public String getTranslatorComment()
{ return translatorComment; }

/**
 * Returns a comma-separated list of flags. Flags may include "fuzzy" or
 * "c-style".
 */
public String getFlags()
{ return flags; }

/**
 * Returns a space-separated list of references. A reference is either only a
 * filename or a tuple filename:line, which specifies occurrences of the
 * message.
 */
public String getReference()
{ return reference; }

static boolean equalsAllowingNull(String s1, String s2)
{
	if (s1 == null)
		return s2 == null;
	return s1.equals(s2);
}

@Override
public boolean equals(Object other)
{
	if (!(other instanceof PoEntry))
		return false;
	PoEntry entry = (PoEntry) other;
	return equalsAllowingNull(message, entry.message)
		&& equalsAllowingNull(context, entry.context)
		&& equalsAllowingNull(translation, entry.translation)
		&& equalsAllowingNull(reference, entry.reference)
		&& equalsAllowingNull(translatorComment, entry.translatorComment)
		&& equalsAllowingNull(extractedComment, entry.extractedComment)
		&& equalsAllowingNull(flags, entry.flags);
}

static int hashCodeIgnoringNull(String s)
{ return s == null ? 0 : s.hashCode(); }

@Override
public int hashCode()
{
	return hashCodeIgnoringNull(message)+hashCodeIgnoringNull(context)
		+hashCodeIgnoringNull(translation)+hashCodeIgnoringNull(reference)
		+hashCodeIgnoringNull(translatorComment)
		+hashCodeIgnoringNull(extractedComment)+hashCodeIgnoringNull(flags);
}

private void appendToSplitAtSpace(String s, Appendable out) throws IOException
{
	int last = 0;
	while (last < s.length())
	{
		int i = s.length();
		if (i > last+77) i = s.lastIndexOf(" ", last+77)+1;
		if (i <= last) i = s.indexOf(" ", last+77)+1;
		if (i <= 0) i = s.length();
		out.append("\"").append(s.substring(last, i)).append("\"").append(LINE_BREAK);
		last = i;
	}
}

/**
 * Splits the given string {@code s}, and appends it to the given writer.
 * Follows the msginit/msgmerge conventions, splitting at 79 characters if
 * possible. Always splits after the string "\n" (the escaped string cannot
 * contain newline characters).
 *
 * @throws IOException 
 */
private void appendToEscapedAndSplit(String name, String s, Appendable out) throws IOException
{
	s = PoHelper.escape(s);
	out.append(name+" ");
	if (s.contains("\\n"))
	{
		out.append("\"\"").append(LINE_BREAK);
		String[] lines = PoHelper.split(s, "\\n");
		for (int i = 0; i < lines.length-1; i++)
			appendToSplitAtSpace(lines[i]+"\\n", out);
		if (!"".equals(lines[lines.length-1]))
			appendToSplitAtSpace(lines[lines.length-1], out);
	}
	else if ((s.length() > 79-3-name.length()) && s.contains(" "))
	{
		out.append("\"\"").append(LINE_BREAK);
		appendToSplitAtSpace(s, out);
	}
	else
		out.append("\"").append(s).append("\"").append(LINE_BREAK);
}

public void print(Appendable out) throws IOException
{
	if (translatorComment != null)
	{
		String[] commentLines = translatorComment.split("\n");
		for (String s : commentLines)
			out.append("#  ").append(s).append(LINE_BREAK);
	}
	if (extractedComment != null)
	{
		String[] commentLines = extractedComment.split("\n");
		for (String s : commentLines)
			out.append("#. ").append(s).append(LINE_BREAK);
	}
	if (reference != null)
	{
		String[] referenceLines = reference.split(" ");
		for (int i = 0; i < referenceLines.length; )
		{
			StringBuilder s = new StringBuilder();
			s.append("#: ");
			s.append(referenceLines[i++]);
			while ((i < referenceLines.length) && (s.length()+referenceLines[i].length()+1 < 79))
				s.append(" ").append(referenceLines[i++]);
			out.append(s).append(LINE_BREAK);
		}
	}
	if (flags != null)
		out.append("#, ").append(flags).append(LINE_BREAK);
	if (context != null)
		out.append("msgctxt \"").append(context).append("\"").append(LINE_BREAK);
	if (message == null)
		out.append("msgid \"\"").append(LINE_BREAK);
	else
		appendToEscapedAndSplit("msgid", message, out);
	if (translation == null)
		out.append("msgstr \"\"").append(LINE_BREAK);
	else
		appendToEscapedAndSplit("msgstr", translation, out);
}

@Override
public String toString()
{
	StringBuffer result = new StringBuffer();
	try
	{
		print(result);
	}
	catch (IOException e)
	{
		// This should never happen, StringBuffer.append doesn't throw exceptions.
		throw new AssertionError(e);
	}
	return result.toString();
}

	public static class Builder
	{
		private String translatorComment;
		private String extractedComment;
		private String reference;
		private String flags;
		private String context;
		private String message;
		private String translation;
		
		public PoEntry build()
		{ return new PoEntry(this); }
		
		public Builder clear()
		{
			this.translatorComment = null;
			this.extractedComment = null;
			this.reference = null;
			this.flags = null;
			this.context = null;
			this.message = null;
			this.translation = null;
			return this;
		}
		
		public Builder copy(PoEntry other)
		{
			this.translatorComment = other.translatorComment;
			this.extractedComment = other.extractedComment;
			this.reference = other.reference;
			this.flags = other.flags;
			this.context = other.context;
			this.message = other.message;
			this.translation = other.translation;
			return this;
		}
		
		private String concatWith(String a, String b, String between)
		{
			if (a == null) return b;
			if (b == null) return a;
			return a+between+b;
		}
		
		public Builder setContext(String context)
		{
			this.context = context;
			return this;
		}
		
		public Builder appendContext(String moreText)
		{
			this.context = concatWith(this.context, moreText, "");
			return this;
		}
		
		public Builder setMessage(String message)
		{
			this.message = message;
			return this;
		}
		
		public Builder appendMessage(String moreText)
		{
			this.message = concatWith(this.message, moreText, "");
			return this;
		}
		
		public Builder setTranslation(String translation)
		{
			this.translation = translation;
			return this;
		}
		
		public Builder appendTranslation(String moreText)
		{
			this.translation = concatWith(this.translation, moreText, "");
			return this;
		}
		
		public Builder setReference(String reference)
		{
			this.reference = reference;
			return this;
		}
		
		public Builder appendReference(String moreText)
		{
			this.reference = concatWith(this.reference, moreText, " ");
			return this;
		}
		
		public Builder setExtractedComment(String extractedComment)
		{
			this.extractedComment = extractedComment;
			return this;
		}
		
		public Builder appendExtractedComment(String moreText)
		{
			this.extractedComment = concatWith(this.extractedComment, moreText, "\n");
			return this;
		}
		
		public Builder setTranslatorComment(String translatorComment)
		{
			this.translatorComment = translatorComment;
			return this;
		}
		
		public Builder appendTranslatorComment(String moreText)
		{
			this.translatorComment = concatWith(this.translatorComment, moreText, "\n");
			return this;
		}
		
		public Builder setFlags(String flags)
		{
			this.flags = flags;
			return this;
		}
		
		public Builder appendFlags(String moreText)
		{
			this.flags = concatWith(this.flags, moreText, ",");
			return this;
		}
	}
}

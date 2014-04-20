package net.cqs.plugins.smtp;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public final class SmtpWriter extends FilterWriter
{

private static final String CRLF = "\r\n";

protected SmtpWriter(Writer out)
{ super(out); }

public void print(char c) throws IOException
{ write(c); }

public void print(char[] s) throws IOException
{ write(s); }

public void print(String s) throws IOException
{ write(s); }

public void print(CharSequence s) throws IOException
{
	for (int i = 0; i < s.length(); i++)
		write(s.charAt(i));
}

public void println() throws IOException
{
	print(CRLF);
	flush();
}

public void println(char c) throws IOException
{
	print(c);
	println();
}

public void println(char[] s) throws IOException
{
	print(s);
	println();
}

public void println(String s) throws IOException
{
	print(s);
	println();
}

public void println(CharSequence s) throws IOException
{
	print(s);
	print(CRLF);
}

}

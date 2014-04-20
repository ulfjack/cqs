package net.cqs.main.config;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;

import de.ofahrt.io.HexTranscoder;

public final class Input
{

private static String gen =
	"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
private static Random rand = new Random();

private static String quote(String what)
{
	if (what.length() == 0)
		return what;
	else
		return Pattern.quote(what);
}

public static String convertStringToRegex(String what)
{
	if ((what.indexOf('?') < 0) && (what.indexOf('*') < 0))
		return Pattern.quote(what);
	StringBuilder result = new StringBuilder();
	int current = 0;
	while (current < what.length())
	{
		int nextQuestion = what.indexOf('?', current);
		int nextStar = what.indexOf('*', current);
		if ((nextQuestion != -1) && ((nextStar == -1) || (nextQuestion < nextStar)))
		{
			result.append(quote(what.substring(current, nextQuestion)));
			result.append('.');
			current = nextQuestion+1;
		}
		else if (nextStar != -1)
		{
			result.append(quote(what.substring(current, nextStar)));
			result.append(".*");
			current = nextStar+1;
		}
		else
		{
			result.append(quote(what.substring(current)));
			current = what.length();
		}
	}
	return result.toString();
}

public static int decode(String s, int def, int min, int max)
{
	try
	{
		int result = Integer.parseInt(s);
		if (result < min) return min;
		if (result > max) return max;
		return result;
	}
	catch (Exception e)
	{ return def; }
}

public static int decode(String s, int def)
{
	try
	{
		return Integer.parseInt(s);
	}
	catch (Exception e)
	{ return def; }
}

public static String randomString(int len)
{
	StringBuffer result = new StringBuffer();
	for (int i = 0; i < len; i++)
	{
		int j = Input.randomInt(0, gen.length()-1);
		result.append(gen.charAt(j));
	}
	return result.toString();
}

public static int randomInt(int min, int max)
{ return rand.nextInt(max-min+1)+min; }

public static byte[] md5sum(byte[] data)
{
	try
	{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(data);
		return digest.digest();
	}
	catch (NoSuchAlgorithmException e)
	{ throw new RuntimeException(e); }
}

public static byte[] md5sum(String s)
{ return md5sum(s.getBytes(Charset.forName("UTF-8"))); }

public static String md5sumAsHex(String s)
{ return new HexTranscoder().transcode(md5sum(s.getBytes(Charset.forName("UTF-8")))); }

private Input()
{/*OK*/}

}

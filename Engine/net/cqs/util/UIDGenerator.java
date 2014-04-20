package net.cqs.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import de.ofahrt.io.HexTranscoder;

public final class UIDGenerator
{

private long uniqueId = 1L;
private final Random RAND = new Random();

public UIDGenerator()
{/*OK*/}

private String md5sumAsHex(String s)
{
	try
	{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(s.getBytes(Charset.forName("UTF-8")));
		byte[] result = digest.digest();
		return new HexTranscoder().transcode(result);
	}
	catch (NoSuchAlgorithmException e)
	{ throw new RuntimeException(e); }
}

public synchronized String generateUniqueID()
{
	String temp = RAND.nextLong()+" "+RAND.nextLong()+" "+(uniqueId++)+" MAGIC";
	return md5sumAsHex(temp);
}

}

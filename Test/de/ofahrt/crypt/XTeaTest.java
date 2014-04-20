package de.ofahrt.crypt;

import static org.junit.Assert.assertEquals;

import java.security.InvalidKeyException;

import org.junit.Test;

import de.ofahrt.io.HexTranscoder;

public class XTeaTest
{

private static String encrypt(String data, String key)
{
	try
	{
		byte[] dbytes = data.getBytes();
		byte[] kbytes = key.getBytes();
		byte[] enc = new XTea(kbytes).encrypt(dbytes);
		return new HexTranscoder().transcode(enc);
	}
	catch (InvalidKeyException e)
	{ throw new RuntimeException(e); }
}

@Test
public void testSimple()
{
	String r = encrypt("0123456776543210", "0123456789abcdef");
	assertEquals("c39b6b11b49c7be20626fd77a32f0f13", r);
}

}

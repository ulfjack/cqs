package de.ofahrt.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base64Test
{

private String encode(byte[] data)
{ return new Base64Transcoder().transcode(data); }

@Test
public void simpleTest()
{ assertEquals("AAAA", encode(new byte[] {0, 0, 0})); }

}

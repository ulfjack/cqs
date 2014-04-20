package de.ofahrt.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HexTest
{

private String encode(byte[] data)
{ return new HexTranscoder().transcode(data); }

@Test
public void simpleTest()
{ assertEquals("000000", encode(new byte[] {0, 0, 0})); }

}

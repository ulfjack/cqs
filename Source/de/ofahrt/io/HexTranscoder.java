package de.ofahrt.io;

import java.io.ByteArrayOutputStream;

public class HexTranscoder
{

private static final String CODE = "0123456789abcdef";

public String transcode(byte[] data, int offset, int length)
{
	StringBuffer buffer = new StringBuffer();
	for (int i = offset; i < offset+length; i++)
	{
		byte b = data[i];
		buffer.append(CODE.charAt((b >> 4) & 0x0f));
		buffer.append(CODE.charAt(b & 0x0f));
	}
	return buffer.toString();
}

public String transcode(byte[] data)
{ return transcode(data, 0, data.length); }

public byte[] transcode(String data) throws TranscoderException
{
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	for (int i = 0; i < data.length() / 2; i++)
	{
		int a = CODE.indexOf(data.charAt(2*i));
		int b = CODE.indexOf(data.charAt(2*i+1));
		if ((a < 0) | (b < 0)) throw new TranscoderException("Invalid character!");
		out.write((a << 4) | b);
	}
	return out.toByteArray();
}

}

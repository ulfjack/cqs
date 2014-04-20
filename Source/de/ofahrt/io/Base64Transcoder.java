package de.ofahrt.io;

public class Base64Transcoder
{

static final char[] valueToChar = new char[64];
static final int[] charToValue = new int[256];

static final int ERROR  = -3;
static final int IGNORE = -2;
static final int PAD    = -1;

static
{
	String data =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	for (int i = 0; i < data.length(); i++ )
		valueToChar[i] = data.charAt(i);
	
	for (int i = 0; i < 256; i++)
		charToValue[i] = ERROR;
	
	charToValue[10] = IGNORE;
	charToValue[13] = IGNORE;
	charToValue['='] = PAD;
	
	for (int i = 0; i < 64; i++)
		charToValue[valueToChar[i]] = i;
}

private static int combine(byte a, byte b, byte c)
{ return ((a & 0xff) << 16) | ((b & 0xff) << 8) | (c & 0xff); }

public String transcode(byte[] data, int offset, int length)
{
	byte[] temp = new byte[length];
	System.arraycopy(data, offset, temp, 0, length);
	return transcode(temp);
}

private final void append(StringBuffer out, byte[] data)
{
	int len = (data.length / 3) * 3;
	int leftover = data.length - len;
	
	for (int i = 0; i < len; i += 3)
	{
		int combined = combine(data[i+0], data[i+1], data[i+2]);
		int c3 = combined & 0x3f; combined >>>= 6;
		int c2 = combined & 0x3f; combined >>>= 6;
		int c1 = combined & 0x3f; combined >>>= 6;
		int c0 = combined & 0x3f;
		out.append(valueToChar[c0]);
		out.append(valueToChar[c1]);
		out.append(valueToChar[c2]);
		out.append(valueToChar[c3]);
	}
	
	if (leftover == 0) return;
	
	if (leftover == 1)
	{
		int combined = combine(data[len], (byte) 0, (byte) 0);
		combined >>>= 12;
		int c1 = combined & 0x3f; combined >>>= 6;
		int c0 = combined & 0x3f;
		out.append(valueToChar[c0]);
		out.append(valueToChar[c1]);
		out.append("==");
		return;
	}
	
	if (leftover == 2)
	{
		int combined = combine(data[len], data[len+1], (byte) 0);
		combined >>>= 6;
		int c2 = combined & 0x3f; combined >>>= 6;
		int c1 = combined & 0x3f; combined >>>= 6;
		int c0 = combined & 0x3f;
		out.append(valueToChar[c0]);
		out.append(valueToChar[c1]);
		out.append(valueToChar[c2]);
		out.append('=');
		return;
	}
	
	throw new RuntimeException("Internal Error!");
}

public String transcode(byte[] data)
{
	StringBuffer sb = new StringBuffer(((data.length + 2) / 3) * 4);
	append(sb, data);
	return sb.toString();
}

public byte[] transcode(String data) throws TranscoderException
{
	// estimate worst case size of output array
	byte[] b = new byte[(data.length() / 4) * 3];
	
	int cycle = 0;
	int combined = 0;
	
	int j = 0;
	int len = data.length();
	int dummies = 0;
	for (int i = 0; i < len; i++ )
	{
		int c = data.charAt(i);
		int value  = (c <= 255) ? charToValue[c] : IGNORE;
		if (value == PAD)
		{
			value = 0;
			dummies++;
		}
		// case PAD : HANDLED BEFORE SWITCH!
		switch (value)
		{
			case ERROR :
				throw new TranscoderException("Invalid character!");
			
			case IGNORE :
				break;
			
			default :
				/* regular value character */
				switch ( cycle )
				{
					case 0 :
					case 1 :
					case 2 :
						combined = (combined << 6) | value;
						break;
					
					case 3 :
						combined = (combined << 6) | value;
						
						b[j+2] = (byte) combined; combined >>>= 8;
						b[j+1] = (byte) combined; combined >>>= 8;
						b[j+0] = (byte) combined;
						j += 3;
						
						combined = 0;
						break;
				}
				cycle = (cycle+1) % 4;
				break;
		}
	}
	
	if (cycle != 0)
		throw new TranscoderException("Input to decode not a multiple of 4!");
	
	j -= dummies;
	if (b.length != j)
	{
		byte[] b2 = new byte[j];
		System.arraycopy(b, 0, b2, 0, j);
		b = b2;
	}
	return b;
}


}


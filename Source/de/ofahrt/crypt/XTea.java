package de.ofahrt.crypt;

/*
 * Originally created by Thomas Dixon on 12/30/05.
 * The original version was incorrectly using >> instead of >>>.
 * 
 * Modified by Ulf Ochsenfahrt 24.04.2006
 * This version is using LITTLE ENDIAN encoding for both data and key.
 */

import java.security.InvalidKeyException;

public class XTea
{

private static final int
    ROUNDS      = 32,   // iteration count (cycles)
    BLOCK_SIZE  = 8,    // bytes in a data block (64 bits)
    KEY_SIZE    = 16,   // key size (16*8 = 128 bits)
    DELTA       = 0x9E3779B9,
    D_SUM       = 0xC6EF3720;

// Subkeys
private final byte[] key;
private final int[] S = new int[4];

public XTea(byte[] key) throws InvalidKeyException
{
	if (key == null) throw new InvalidKeyException("Null key");
	if (key.length != KEY_SIZE) throw new InvalidKeyException("Invalid key length (req. 16 bytes)");
	this.key = key;
	generateSubKeys();
}

// Subkey generator
private void generateSubKeys()
{
	for (int off=0, i=0; i<4; i++)
	{
		S[i] = ((key[off++]&0xFF)      ) |
			((key[off++]&0xFF) <<  8) |
			((key[off++]&0xFF) << 16) |
			((key[off++]&0xFF) << 24);
	}
}

// Encrypt one block of data with XTEA algorithm (= 8 Byte)
private void engineCrypt(byte[] in, byte[] out, int offset, boolean decrypt)
{
	int inOffset = offset;
	// Pack bytes into integers
	int v0 =
		((in[inOffset++] & 0xFF)      ) |
		((in[inOffset++] & 0xFF) <<  8) |
		((in[inOffset++] & 0xFF) << 16) |
		((in[inOffset++] & 0xFF) << 24);
	int v1 =
		((in[inOffset++] & 0xFF)      ) |
		((in[inOffset++] & 0xFF) <<  8) |
		((in[inOffset++] & 0xFF) << 16) |
		((in[inOffset++] & 0xFF) << 24);
	
	int n = ROUNDS, sum;
	
	if (!decrypt)
	{ // Encipher
		sum = 0;
		while (n-- > 0)
		{
			v0	+= ((v1 << 4 ^ v1 >>> 5) + v1) ^ (sum + S[sum & 3]);
			sum += DELTA;
			v1	+= ((v0 << 4 ^ v0 >>> 5) + v0) ^ (sum + S[sum >>> 11 & 3]);
		}
	}
	else
	{ // Decipher
		sum = D_SUM;
		while (n-- > 0)
		{
			v1	-= ((v0 << 4 ^ v0 >>> 5) + v0) ^ (sum + S[sum >>> 11 & 3]);
			sum -= DELTA;
			v0	-= ((v1 << 4 ^ v1 >>> 5) + v1) ^ (sum + S[sum & 3]);
		}
	}
	
	// Unpack and return
	int outOffset = offset;
	out[outOffset++] = (byte)(v0       );
	out[outOffset++] = (byte)(v0 >>>  8);
	out[outOffset++] = (byte)(v0 >>> 16);
	out[outOffset++] = (byte)(v0 >>> 24);
	
	out[outOffset++] = (byte)(v1       );
	out[outOffset++] = (byte)(v1 >>>  8);
	out[outOffset++] = (byte)(v1 >>> 16);
	out[outOffset++] = (byte)(v1 >>> 24);
}

public byte[] encrypt(byte[] in)
{
	byte[] result = new byte[in.length];
	for (int i = 0; i < in.length; i += BLOCK_SIZE)
		engineCrypt(in, result, i, false);
	return result;
}

public byte[] decrypt(byte[] in)
{
	byte[] result = new byte[in.length];
	for (int i = 0; i < in.length; i += BLOCK_SIZE)
		engineCrypt(in, result, 0, true);
	return result;
}

}
    
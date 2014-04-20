package net.cqs.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerializationHelper
{

// private code
public static byte[] serialize(Object obj)
{
	if (!(obj instanceof Serializable))
		throw new RuntimeException("Object is not serializable!");
	try
	{
		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baout);
		out.writeObject(obj);
		out.flush();
		return baout.toByteArray();
	}
	catch (IOException e)
	{ throw new RuntimeException(e); }
}

public static Object deserialize(byte[] data)
{
	try
	{
		ByteArrayInputStream bain = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bain);
		return in.readObject();
	}
	catch (IOException e)
	{ throw new RuntimeException(e); }
	catch (ClassNotFoundException e)
	{ throw new RuntimeException(e); }
}

}

package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;

public class AttributeTest
{

static final Attribute<Integer> TEST_ATTR1 = Attribute.of(Integer.class, "test1", new Integer(1234));
static final Attribute<Boolean> TEST_ATTR2 = Attribute.of(Boolean.class, "test2", new Boolean(true));
static final Attribute<Long>    TEST_ATTR3 = Attribute.of(Long.class,    "test3", new Long(4321L));

@Test
public void testEquals1()
{
	assertFalse(TEST_ATTR1.equals(TEST_ATTR2));
}

@Test
public void testEquals2()
{
	assertFalse(TEST_ATTR1.equals(new Object()));
}

@Test
public void testEquals3()
{
	assertFalse(TEST_ATTR1.equals(TEST_ATTR3));
}

@Test
public void testEquals4()
{
	assertTrue(TEST_ATTR1.equals(Attribute.of(Integer.class, "test1", new Integer(1234))));
}

@Test
public void testToString()
{
	assertEquals("test1", TEST_ATTR1.toString());
}

@Test
public void testSerialization() throws Exception
{
	Attribute<?> a = Attribute.of(Serializable.class, "serializationtest", null);
	ByteArrayOutputStream baout = new ByteArrayOutputStream();
	ObjectOutputStream out = new ObjectOutputStream(baout);
	out.writeObject(a);
	out.flush();
	byte[] data = baout.toByteArray();
	ByteArrayInputStream bain = new ByteArrayInputStream(data);
	ObjectInputStream in = new ObjectInputStream(bain);
	Attribute<?> b = (Attribute<?>) in.readObject();
	assertTrue(a.equals(b));
	assertTrue(b.equals(a));
	assertSame(a, b);
}

}

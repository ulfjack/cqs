package net.cqs.main.persistence;

import org.junit.Before;
import org.junit.Test;

public class AllianceDataTest
{

private AllianceData data;

@Before
public void setUp()
{
	data = new AllianceData();
}

@Test
public void testMail()
{ data.getMail(); }

}

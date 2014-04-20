package net.cqs.auth;

import org.junit.Test;
import static org.junit.Assert.*;

public class IdentityTest
{

@Test
public void simple()
{
	Identity id = new Identity("Test");
	assertEquals("Test", id.getName());
}

}

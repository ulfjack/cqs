package net.cqs.config;

import org.junit.Test;
import static org.junit.Assert.*;

public class InputTest
{

@Test
public void fleetName()
{ assertTrue(InputValidation.validFleetName("Eckwarden - Burhave")); }

}

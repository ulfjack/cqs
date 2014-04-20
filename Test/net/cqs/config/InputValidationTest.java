package net.cqs.config;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InputValidationTest
{

@Test
public void fleetNameBeginsWithNumber()
{
	assertTrue(InputValidation.validFleetName("36:1 - 36:15"));
}

}

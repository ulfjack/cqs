package net.cqs.engine.messages;

import static org.junit.Assert.assertTrue;

import java.util.Locale;

import net.cqs.I18nTestHelper;
import net.cqs.config.InfoEnum;
import net.cqs.config.PlanetEnum;
import net.cqs.engine.Position;

import org.junit.Test;

@SuppressWarnings("boxing")
public class InformationTest
{

static {
	I18nTestHelper.init();
}

@Test
public void simpleExploredTest()
{
	// Position, type, int(size), int(steel), int(oil), int(silicon), int(deuterium), long(quality of life) 
	Information info = Information.createInstance(Locale.GERMANY, 1000, InfoEnum.FLEET_EXPLORED,
			new Position(1, 2, 3), PlanetEnum.GASGIANT, 4,  5, 6, 7, 8,  9);
	assertTrue(info.getI18nMessage().contains("5%"));
}

}

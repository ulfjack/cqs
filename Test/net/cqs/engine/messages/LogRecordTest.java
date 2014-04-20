package net.cqs.engine.messages;

import java.util.Locale;

import net.cqs.config.BuildingEnum;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.actions.PlayerCreateAction;
import net.cqs.main.setup.GalaxyCreator;
import net.cqs.main.setup.GameConfiguration.GalaxyDescription;
import net.cqs.storage.memory.MemoryStorageManager;

import org.junit.Before;
import org.junit.Test;

public class LogRecordTest
{

private Galaxy galaxy;

@Before
public void setUp()
{
	GalaxyDescription desc = new GalaxyDescription(2, 0);
	galaxy = new GalaxyCreator().createGalaxy(new MemoryStorageManager(), desc);
}

@Test
public void testSimple()
{
	galaxy.schedex(new PlayerCreateAction(null, "test", Locale.GERMANY));
	Player p = galaxy.findPlayerByName("test");
	p.log(PlayerLogMessage.createInstance(p, ErrorCode.BUILDING_ALREADY_ABORTED, BuildingEnum.PROCESSING_PLANT));
}

}

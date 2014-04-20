package net.cqs.engine.base;

import java.io.Serializable;

import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;

/**
 * Allows selecting certain units from a {@link UnitMap}.
 * @see UnitMap#unitIterator(UnitSelector)
 */
public interface UnitSelector extends Serializable
{

	public static final class AllSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return true; }
	}
	
	public static final class SpaceSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return !id.isPlanetary(); }
	}
	
	public static final class GroundSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return id.isPlanetary(); }
	}

	public static final class EspionageSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return id.hasSpecial(UnitSpecial.ESPIONAGE); }
	}

	public static final class ExplorationSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return id.hasSpecial(UnitSpecial.EXPLORATION); }
	}

	public static final class InvasionSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return id.hasSpecial(UnitSpecial.INVASION); }
	}

	public static final class SettlementSelector implements UnitSelector
	{
		private static final long serialVersionUID = 1L;
		@Override
    public boolean isSelected(Unit id)
		{ return id.hasSpecial(UnitSpecial.SETTLEMENT); }
	}

public static final UnitSelector ALL = new AllSelector();
public static final UnitSelector SPACE_ONLY = new SpaceSelector();
public static final UnitSelector GROUND_ONLY = new GroundSelector();
public static final UnitSelector ESPIONAGE = new EspionageSelector();
public static final UnitSelector EXPLORATION = new ExplorationSelector();
public static final UnitSelector INVASION = new InvasionSelector();
public static final UnitSelector SETTLEMENT = new SettlementSelector();

boolean isSelected(Unit id);

}

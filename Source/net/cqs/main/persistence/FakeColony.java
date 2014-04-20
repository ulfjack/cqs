package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.engine.Position;

public final class FakeColony implements Serializable
{

private static final long serialVersionUID = 1L;

Position position;
String name;
String tag;
long numFleets = 0;
long numUnits = 0;

}

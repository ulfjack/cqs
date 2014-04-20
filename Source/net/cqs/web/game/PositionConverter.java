package net.cqs.web.game;

import net.cqs.engine.Position;
import net.cqs.web.action.Converter;

public class PositionConverter implements Converter<Position>
{

@Override
public Position convert(String value)
{ return Position.decode(value); }

}

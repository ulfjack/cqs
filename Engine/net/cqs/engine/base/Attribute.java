package net.cqs.engine.base;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public final class Attribute<T extends Serializable> implements Serializable
{

private static final ConcurrentHashMap<String,Attribute<?>> cache = new ConcurrentHashMap<String,Attribute<?>>();

public static <T extends Serializable> Attribute<T> of(Class<T> keyType, String id, T defaultValue)
{
	Attribute<?> temp = cache.get(id);
	if (temp != null) return temp.as(keyType);
	Attribute<T> key = new Attribute<T>(keyType, id, defaultValue);
	temp = cache.putIfAbsent(id, key);
	if (temp != null) key = temp.as(keyType);
	return key;
}

public static final Attribute<Integer> QUOTA         = Attribute.of(Integer.class, "quota",       Integer.valueOf(2048));
public static final Attribute<Integer> QUOTA_USED    = Attribute.of(Integer.class, "quota-used",  Integer.valueOf(0));
public static final Attribute<Integer> GP_QUOTA_USED = Attribute.of(Integer.class, "gp-quota-used",  Integer.valueOf(0));
public static final Attribute<Boolean> CHECK_QUOTA   = Attribute.of(Boolean.class, "check-quota", Boolean.FALSE);

public static final Attribute<Integer> ALLIANCE_SURVEY   = Attribute.of(Integer.class, "alliance-survey", Integer.valueOf(0));
public static final Attribute<Integer> SYSTEM_SURVEY     = Attribute.of(Integer.class, "system-survey",    Integer.valueOf(0));
public static final Attribute<Integer> KOMMISSION_SURVEY = Attribute.of(Integer.class, "kommission-survey",    Integer.valueOf(0));

public static final Attribute<Boolean> IS_MULTI      = Attribute.of(Boolean.class, "is-multi",      Boolean.FALSE);
public static final Attribute<Integer> MULTI_COUNTER = Attribute.of(Integer.class, "multi-counter", Integer.valueOf(0));
public static final Attribute<String>  OLD_ALLIANCE  = Attribute.of(String.class,  "old-alliance",  null);

public static final Attribute<String>  BUILDING_PRESET = Attribute.of(String.class, "building-preset", "");
public static final Attribute<String>  UNIT_PRESET     = Attribute.of(String.class, "unit-preset", "");

public static final Attribute<Boolean> LOGIN_ALLOWED = Attribute.of(Boolean.class, "login-allowed", Boolean.TRUE);
public static final Attribute<Boolean> SHOW_ADS      = Attribute.of(Boolean.class, "show-ads",      Boolean.TRUE);
public static final Attribute<Long>    LAST_LOGIN    = Attribute.of(Long.class,    "last-login",    Long.valueOf(0));

public static final Attribute<String>  GRAPHIC_PATH = Attribute.of(String.class, "graphic-path", "pack/");
public static final Attribute<Boolean> CSS_IN_GP    = Attribute.of(Boolean.class, "css-in-gp", Boolean.FALSE);
public static final Attribute<Boolean> ADVANCED_CSS = Attribute.of(Boolean.class, "advanced-css", Boolean.FALSE);

public static final Attribute<Boolean> FORWARD_EMAIL = Attribute.of(Boolean.class, "forward-email", Boolean.FALSE);

public static final Attribute<Boolean> AUTO_RETURN  = Attribute.of(Boolean.class, "auto-return",  Boolean.TRUE);
public static final Attribute<Boolean> AUTO_STATION = Attribute.of(Boolean.class, "auto-station", Boolean.TRUE);

public static final Attribute<String>  PLAYER_PLAINTEXT   = Attribute.of(String.class, "player-plaintext", "");
public static final Attribute<String>  PLAYER_LOGO        = Attribute.of(String.class, "player-logo", "");

public static final Attribute<String>  ALLIANCE_PLAINTEXT   = Attribute.of(String.class, "alliance-plaintext", "");
public static final Attribute<String>  ALLIANCE_LOGO        = Attribute.of(String.class, "alliance-logo", "");

private static final long serialVersionUID = 1L;

private final Class<T> keyType;
private final String id;
private final T defaultValue;

private Attribute(Class<T> keyType, String id, T defaultValue)
{
	this.keyType = keyType;
	this.id = id;
	this.defaultValue = defaultValue;
}

@SuppressWarnings("unchecked")
public <S extends Serializable> Attribute<S> as(Class<S> skeytype)
{
	if (keyType.equals(skeytype))
		return (Attribute<S>) this;
	throw new ClassCastException();
}

public Class<T> getKeyType()
{ return keyType; }

public T getDefaultValue()
{ return defaultValue; }

public T cast(Object o)
{ return keyType.cast(o); }

@Override
public int hashCode()
{ return id.hashCode()+13*keyType.hashCode(); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof Attribute<?>))
		return false;
	Attribute<?> a = (Attribute<?>) o;
	return a.id.equals(id) && a.keyType.equals(keyType);
}

@Override
public String toString()
{ return id; }

private Object readResolve()
{ return Attribute.of(keyType, id, defaultValue); }

}

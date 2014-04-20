package net.cqs.main.i18n;

import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;
import net.cqs.main.config.Input;

public enum TippsAndTricks
{

EXPAND("<b>Expanding</b><br />For founding a new colony you need a settler and "+
			"a transporter. Then create a fleet with these units, fly to the planet "+
			"of your choice and give the order to found a new colony.<br /> "+
			"Note that you can only colonize planets on which you do not have "+
			"a colony yet.");

// TODO: Translate to english and add enums.
//	add("<b>Terrain-Analyse</b><br />Bevor Du eine neue Kolonie gründest, solltest Du "+
//			"Dir einen Planeten suchen, der gute Ressourcenverteilungen besitzt. "+
//			"Dies kannst Du mit einem Pionier und dem Befehl Erkunden bewerkstelligen.");
//	add("<b>Forschungsschleife</b><br />"+
//			"Man kann auch Forschungen in die Forschungsschleife stellen, deren Voraussetzungen "+
//			"man eigentlich noch nicht erfüllt! So kann man zwar nicht die Voraussetzungen "+
//			"\"umgehen\", aber dieses Feature ist überaus nützlich, da man so keine Zeit beim "+
//			"Umstellen der Forschungen verliert, sollte man zum Abschluss einer "+
//			"\"Voraussetzungsforschung\" mal nicht online sein können! Man muss einfach nur im "+
//			"Forschungsmenü \"Alle Forschungen wählen\" und kann dort auch Forschungen auswählen "+
//			"die im \"normalen\" Forschungsmenü noch gar nicht angezeigt werden!");
//	add("<b>Positionsangaben</b><br />" +
//			"Man kann Positionen nicht nur mit ':' eingeben. Auch ';', '.' und ',' sind erlaubt.<br/>" +
//			"Beispiel: 1:2:3 und 1,2;3 und 1:2.3 und 1,2,3 geben alle dieselbe Kolonie an.<br/>" +
//			"Unter anderem kann man dadurch Positionen komplett auf dem Numpad eingeben.");

private final String text;

private TippsAndTricks(String text)
{ this.text = text; }

public static String bundleName()
{ return "net.cqs.main.i18n.TippsAndTricks"; }

public String getText()
{ return text; }

public String getText(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(text);
}

public static String getTipp(Locale locale)
{
	TippsAndTricks[] tricks = TippsAndTricks.values();
	int index = Input.randomInt(0, tricks.length-1);
	return tricks[index].getText(locale);
}

}

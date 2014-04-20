package net.cqs.engine.fleets;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.logging.Logger;

import net.cqs.engine.Fleet;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

/**
 * An object of this class represents a command to the {@link Fleet}.
 * <p>
 * Jede Befehlsbearbeitung wird durch ein <code>prepare()</code> begonnen.
 * Sofern <code>prepare()</code> keine Exception wirft, wird nach der
 * zurueckgegebenen Zeit <code>execute()</code> aufgerufen.
 * <p>
 * All FleetCommand objects must be {@link Serializable}!
 */
public abstract class FleetCommand implements Serializable
{

private static final long serialVersionUID = 1L;

final String EDIT_LOAD         = "LOAD";
final String EDIT_LOAD_UNITS   = "LOADUNITS";
final String EDIT_NONE         = "NONE";
final String EDIT_ROB          = "ROB";
final String EDIT_SETTLE       = "SETTLE";
final String EDIT_SPY          = "SPY";
final String EDIT_UNLOAD       = "UNLOAD";

protected static final Logger logger = Logger.getLogger("net.cqs.engine.fleets");

public FleetCommand()
{/*OK*/}

public static final String bundleName()
{ return "net.cqs.engine.fleets.FleetCommand"; }

protected final String lookupTranslation(String original, Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(original);
}

protected final String format(String original, Locale locale, Object... arguments)
{
	MessageFormat temp = new MessageFormat(lookupTranslation(original, locale), locale);
	return temp.format(arguments);
}

/**
 * Uses a ResourceBundle to create a localized description of this command.
 */
public abstract String getName(Locale locale);

/**
 * @return a copy of this command
 */
public abstract FleetCommand copy();

/**
 * Ueberprueft, ob der Befehl mit der Flotte ausgefuehrt werden kann.
 * -1 : Nein, auf keinen Fall
 *  0 : Vielleicht/Unbekannt/kein Check
 *  1 : Ja, wahrscheinlich schon
 * <p>
 * Diese Funktion kann jederzeit aufgerufen werden.
 */
public abstract int check(Fleet f);

/**
 * @return a string to identify whether and how the command is editable
 */
public abstract String getEditorType();

/**
 * Bereitet die Abarbeitung dieses Befehls vor.
 * Rueckgabewert ist die Abarbeitungszeit in Sekunden.
 * <p>
 * Falls eine Exception geworfen wird, wird der aktuelle Befehl abgebrochen.
 * <p>
 * Falls eine FleetAbortException geworfen wird, wird der Befehl ebenfalls
 * abgebrochen. Es wird mittels ({@link FleetAbortException#getErrorCode()})
 * eine Fehlermeldung gesetzt.
 * <p>
 * Die Flotte MUSS bei Beendung dieser Funktion (auch im Falle einer Exception)
 * in einem gueltigen Zustand sein!
 * @return Abarbeitungszeit in Sekunden
 */
public abstract long prepare(Fleet f) throws FleetException;

/**
 * Vollendet die Abarbeitung des Befehls.
 * <p>
 * Falls eine Exception geworfen wird, dann gilt dasselbe wie fuer prepare().
 * <p>
 * Die Flotte MUSS bei Beendung dieser Funktion (auch im Falle einer Exception)
 * in einem gueltigen Zustand sein!
 */
public abstract void execute(Fleet f) throws FleetException;

/**
 * Returns if this command supports abortion.
 * Can be called at any time.
 * 
 * @param f Fleet
 */
public boolean mayAbort(Fleet f)
{ return false; }

/**
 * Is called when the user wants to abort this command.
 * This method is only called if this command is currently in progress
 * (i.e. between a call to {@link #prepare(Fleet)} and a call to {@link #execute(Fleet)}).
 * 
 * @param f Fleet
 * @throws FleetContinueException thrown if the Fleet should continue operating this command
 */
public void abort(Fleet f) throws FleetContinueException
{
	throw new UnsupportedOperationException("Do not abort! ;)");
}

}

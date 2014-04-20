package net.cqs.engine.base;

import net.cqs.config.units.Unit;

/**
 * Interface, um ueber die UnitMap zu iterieren.
 */
public interface UnitIterator
{

/**
 * Gibt zurueck, ob es noch einen Eintrag gibt.
 */
boolean hasNext();

/**
 * Geht zum naechsten Typ. Aufruf muss am Anfang der Schleife erfolgen!
 */
void next();

/**
 * Einheitentyp
 */
Unit key();

/**
 * Einheitenanzahl
 */
int value();

/**
 * Setzt die Anzahl des aktuellen Typs.
 */
void setValue(int amount);

/**
 * Aequivalent zu setValue(value()-1).
 */
void decrease(int amount);

/**
 * Aequivalent zu setValue(value()+1).
 */
void increase(int amount);

/**
 * Loescht den aktuellen Typ. Aequivalent zu setValue(0).
 * Danach sollte next() aufgerufen werden.
 */
void remove(); //deletes current entry and jumps to previous

}

package net.cqs.engine.units;

import java.util.EnumSet;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.EducationModifier;
import net.cqs.config.units.FightSpec;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitClass;
import net.cqs.config.units.UnitModule;
import net.cqs.config.units.UnitSpecial;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;
import net.cqs.util.EnumIntMap;

public enum UnitEnum implements Unit, Formattable
{

TRUCK("truck",
		UnitClassEnum.VEHICLE,
		new Cost(new int[] {1000, 1500, 200, 100}, 5, 80, 1000, 714L),
		new FightSpec(400.0f, 0.0f, new float[] {40.0f, 20.0f, 10.0f}),
		ResearchEnum.VEHICLE, -1,
		new UnitSpecial[] { UnitSpecial.CIVIL },
		new EducationEnum[] { EducationEnum.RESSOURCENTRANSPORT },
		15000, 0, 0,
		"transporter"),

SETTLER("settler",
		UnitClassEnum.VEHICLE,
		new Cost(new int[] {20000, 20000, 5000, 1000}, 20, 35000, 5000, 1429L),
		new FightSpec(400.0f, 0, new float[] {40.0f, 20.0f, 10.0f}),
		ResearchEnum.COLONIZATION, -1,
		new UnitSpecial[] { UnitSpecial.SETTLEMENT, UnitSpecial.CIVIL },
		new EducationEnum[] {},
		"settler-chassis"),

PIONEER("pioneer",
		UnitClassEnum.VEHICLE,
		new Cost(new int[] {5000, 5000, 500, 500}, 5, 3000, 5000, 143L),
		new FightSpec(400.0f, 0, new float[] {40.0f, 20.0f, 10.0f}),
		ResearchEnum.TERRAIN_ANALYSIS, -1,
		new UnitSpecial[] { UnitSpecial.EXPLORATION, UnitSpecial.CIVIL },
		new EducationEnum[] {},
		"pionier-chassis"),

SPY("spy",
		UnitClassEnum.INFANTRY,
		new Cost(new int[] {5000, 5000, 5000, 5000}, 10, 15000, 5000, 1429L),
		new FightSpec(250.0f, 0, new float[] {5.0f, 2.5f, 10.0f}),
		ResearchEnum.ESPIONAGE, -1,
		new UnitSpecial[] { UnitSpecial.ESPIONAGE, UnitSpecial.CIVIL },
		new EducationEnum[] {},
		"spion-chassis"),

MILITIA("militia",
		UnitClassEnum.INFANTRY,
		new Cost(new int[] {1000000, 1000000, 1000000, 1000000}, 1000000, 20, 1000000, 142857L),
		new FightSpec(250.0f, 0.5f, new float[] {75.0f, 37.5f, 150.0f}),
		new UnitSpecial[] { UnitSpecial.MILITIA }, true,
		"empty"),

INFANTRY                 ("infantry",                    UnitClassEnum.INFANTRY, "infanterie-chassis,infanterie-chassis-arme"),
INFANTRY_SPEED           ("fast infantry",               UnitClassEnum.INFANTRY, InfantryModulesEnum.SPEED, "infanterie-chassis,infanterie-chassis-arme,infanterie-speed-1"),
INFANTRY_SPEED_SPEED     ("very fast infantry",          UnitClassEnum.INFANTRY, InfantryModulesEnum.SPEED, InfantryModulesEnum.SPEED, "infanterie-chassis,infanterie-chassis-arme,infanterie-speed-1,infanterie-speed-2"),
INFANTRY_SPEED_ATTACK    ("fast, aggressive infantry",   UnitClassEnum.INFANTRY, InfantryModulesEnum.SPEED, InfantryModulesEnum.ATTACK, "infanterie-chassis,infanterie-speed-1,infanterie-angriff-1"),
INFANTRY_SPEED_DEFENSE   ("fast, robust infantry",       UnitClassEnum.INFANTRY, InfantryModulesEnum.SPEED, InfantryModulesEnum.DEFENSE, "infanterie-chassis,infanterie-speed-1,infanterie-verteidigung-1"),
INFANTRY_ATTACK          ("aggressive infantry",         UnitClassEnum.INFANTRY, InfantryModulesEnum.ATTACK, "infanterie-chassis,infanterie-angriff-1"),
INFANTRY_ATTACK_ATTACK   ("very aggressive infantry",    UnitClassEnum.INFANTRY, InfantryModulesEnum.ATTACK, InfantryModulesEnum.ATTACK, "infanterie-chassis,infanterie-angriff-2"),
INFANTRY_ATTACK_DEFENSE  ("aggressive, robust infantry", UnitClassEnum.INFANTRY, InfantryModulesEnum.ATTACK, InfantryModulesEnum.DEFENSE, "infanterie-chassis,infanterie-angriff-1-verteidigung-1"),
INFANTRY_DEFENSE         ("robust infantry",             UnitClassEnum.INFANTRY, InfantryModulesEnum.DEFENSE, "infanterie-chassis,infanterie-verteidigung-1"),
INFANTRY_DEFENSE_DEFENSE ("very robust infantry",        UnitClassEnum.INFANTRY, InfantryModulesEnum.DEFENSE, InfantryModulesEnum.DEFENSE, "infanterie-chassis,infanterie-verteidigung-2"),

VEHICLE                  ("jeep",                  UnitClassEnum.VEHICLE, "jeep-chassis"),
VEHICLE_SPEED            ("fast jeep",             UnitClassEnum.VEHICLE, VehicleModulesEnum.SPEED, "jeep-chassis,jeep-speed-1"),
VEHICLE_SPEED_SPEED      ("very fast jeep",        UnitClassEnum.VEHICLE, VehicleModulesEnum.SPEED, VehicleModulesEnum.SPEED, "jeep-chassis,jeep-speed-1,jeep-speed-2"),
VEHICLE_SPEED_ATTACK     ("fast, aggressive jeep", UnitClassEnum.VEHICLE, VehicleModulesEnum.SPEED, VehicleModulesEnum.ATTACK, "jeep-chassis,jeep-speed-1,jeep-waffen-1"),
VEHICLE_SPEED_DEFENSE    ("fast tank",             UnitClassEnum.VEHICLE, VehicleModulesEnum.SPEED, VehicleModulesEnum.DEFENSE, "panzer-chassis,panzer-speed"),
VEHICLE_ATTACK           ("aggressive jeep",       UnitClassEnum.VEHICLE, VehicleModulesEnum.ATTACK, "jeep-chassis,jeep-waffen-1"),
VEHICLE_ATTACK_ATTACK    ("very aggressive jeep",  UnitClassEnum.VEHICLE, VehicleModulesEnum.ATTACK, VehicleModulesEnum.ATTACK, "jeep-chassis,jeep-waffen-1,jeep-waffen-2"),
VEHICLE_ATTACK_DEFENSE   ("aggressive tank",       UnitClassEnum.VEHICLE, VehicleModulesEnum.ATTACK, VehicleModulesEnum.DEFENSE, "panzer-chassis,panzer-waffen"),
VEHICLE_DEFENSE          ("tank",                  UnitClassEnum.VEHICLE, VehicleModulesEnum.DEFENSE, "panzer-chassis"),
VEHICLE_DEFENSE_DEFENSE  ("robust tank",           UnitClassEnum.VEHICLE, VehicleModulesEnum.DEFENSE, VehicleModulesEnum.DEFENSE, "panzer-chassis,panzer-panzerung"),

AIRCRAFT                 ("helicopter",                    UnitClassEnum.AIRCRAFT, "heli-chassis"),
AIRCRAFT_SPEED           ("airplane",                      UnitClassEnum.AIRCRAFT, AircraftModulesEnum.SPEED, "jet-chassis"),
AIRCRAFT_SPEED_SPEED     ("fast airplane",                 UnitClassEnum.AIRCRAFT, AircraftModulesEnum.SPEED, AircraftModulesEnum.SPEED, "jet-chassis,jet-speed"),
AIRCRAFT_SPEED_ATTACK    ("aggressive airplane",           UnitClassEnum.AIRCRAFT, AircraftModulesEnum.SPEED, AircraftModulesEnum.ATTACK, "jet-chassis,jet-waffen"),
AIRCRAFT_SPEED_DEFENSE   ("robust airplane",               UnitClassEnum.AIRCRAFT, AircraftModulesEnum.SPEED, AircraftModulesEnum.DEFENSE, "jet-chassis,jet-verteidigung"),
AIRCRAFT_ATTACK          ("aggressive helicopter",         UnitClassEnum.AIRCRAFT, AircraftModulesEnum.ATTACK, "heli-chassis,heli-waffen-1"),
AIRCRAFT_ATTACK_ATTACK   ("very aggressive helicopter",    UnitClassEnum.AIRCRAFT, AircraftModulesEnum.ATTACK, AircraftModulesEnum.ATTACK, "heli-chassis,heli-waffen-1,heli-waffen-2"),
AIRCRAFT_ATTACK_DEFENSE  ("aggressive, robust helicopter", UnitClassEnum.AIRCRAFT, AircraftModulesEnum.ATTACK, AircraftModulesEnum.DEFENSE, "heli-chassis,heli-waffen-1,heli-verteidigung-1"),
AIRCRAFT_DEFENSE         ("robust helicopter",             UnitClassEnum.AIRCRAFT, AircraftModulesEnum.DEFENSE, "heli-chassis,heli-verteidigung-1"),
AIRCRAFT_DEFENSE_DEFENSE ("very robust helicopter",        UnitClassEnum.AIRCRAFT, AircraftModulesEnum.DEFENSE, AircraftModulesEnum.DEFENSE, "heli-chassis,heli-verteidigung-1,heli-verteidigung-2"),

FIGHTER                  ("fighter",                    UnitClassEnum.FIGHTER, "jaeger-chassis"),
FIGHTER_SPEED            ("fast fighter",               UnitClassEnum.FIGHTER, FighterModulesEnum.SPEED, "jaeger-chassis,jaeger-speed-1"),
FIGHTER_SPEED_SPEED      ("very fast fighter",          UnitClassEnum.FIGHTER, FighterModulesEnum.SPEED, FighterModulesEnum.SPEED, "jaeger-chassis,jaeger-speed-1,jaeger-speed-2"),
FIGHTER_SPEED_ATTACK     ("fast, aggressive fighter",   UnitClassEnum.FIGHTER, FighterModulesEnum.SPEED, FighterModulesEnum.ATTACK, "jaeger-chassis,jaeger-speed-1,jaeger-angriff-1"),
FIGHTER_SPEED_DEFENSE    ("fast, robust fighter",       UnitClassEnum.FIGHTER, FighterModulesEnum.SPEED, FighterModulesEnum.DEFENSE, "jaeger-chassis,jaeger-speed-1,jaeger-verteidigung-1"),
FIGHTER_SPEED_WARP       ("fast warp-fighter",          UnitClassEnum.FIGHTER, FighterModulesEnum.SPEED, FighterModulesEnum.WARP, "jaeger-chassis,jaeger-speed-1,jaeger-warp-1"),
FIGHTER_ATTACK           ("aggressive fighter",         UnitClassEnum.FIGHTER, FighterModulesEnum.ATTACK, "jaeger-chassis,jaeger-angriff-1"),
FIGHTER_ATTACK_ATTACK    ("very aggressive fighter",    UnitClassEnum.FIGHTER, FighterModulesEnum.ATTACK, FighterModulesEnum.ATTACK, "jaeger-chassis,jaeger-angriff-1,jaeger-angriff-2"),
FIGHTER_ATTACK_DEFENSE   ("aggressive, robust fighter", UnitClassEnum.FIGHTER, FighterModulesEnum.ATTACK, FighterModulesEnum.DEFENSE, "jaeger-chassis,jaeger-angriff-1,jaeger-verteidigung-1"),
FIGHTER_ATTACK_WARP      ("aggressive warp-fighter",    UnitClassEnum.FIGHTER, FighterModulesEnum.ATTACK, FighterModulesEnum.WARP, "jaeger-chassis,jaeger-angriff-1,jaeger-warp-1"),
FIGHTER_DEFENSE          ("robust fighter",             UnitClassEnum.FIGHTER, FighterModulesEnum.DEFENSE, "jaeger-chassis,jaeger-verteidigung-1"),
FIGHTER_DEFENSE_DEFENSE  ("very robust fighter",        UnitClassEnum.FIGHTER, FighterModulesEnum.DEFENSE, FighterModulesEnum.DEFENSE, "jaeger-chassis,jaeger-verteidigung-1,jaeger-verteidigung-2"),
FIGHTER_DEFENSE_WARP     ("robust warp-fighter",        UnitClassEnum.FIGHTER, FighterModulesEnum.DEFENSE, FighterModulesEnum.WARP, "jaeger-chassis,jaeger-warp-1,jaeger-verteidigung-1"),
FIGHTER_WARP             ("warp-fighter",               UnitClassEnum.FIGHTER, FighterModulesEnum.WARP, "jaeger-chassis,jaeger-warp-1"),
FIGHTER_WARP_WARP        ("warp-fighter specialist",    UnitClassEnum.FIGHTER, FighterModulesEnum.WARP, FighterModulesEnum.WARP, "jaeger-chassis,jaeger-warp-1,jaeger-warp-2"),

CORVETTE                 ("corvette",                    UnitClassEnum.CORVETTE, "corvette-chassis"),
CORVETTE_SPEED           ("fast corvette",               UnitClassEnum.CORVETTE, CorvetteModulesEnum.SPEED, "corvette-chassis,corvette-speed-1"),
CORVETTE_SPEED_SPEED     ("very fast corvette",          UnitClassEnum.CORVETTE, CorvetteModulesEnum.SPEED, CorvetteModulesEnum.SPEED, "corvette-chassis,corvette-speed-1,corvette-speed-2"),
CORVETTE_SPEED_ATTACK    ("fast, aggressive corvette",   UnitClassEnum.CORVETTE, CorvetteModulesEnum.SPEED, CorvetteModulesEnum.ATTACK, "corvette-chassis,corvette-speed-1,corvette-angriff-1"),
CORVETTE_SPEED_DEFENSE   ("fast, robust corvette",       UnitClassEnum.CORVETTE, CorvetteModulesEnum.SPEED, CorvetteModulesEnum.DEFENSE, "corvette-chassis,corvette-speed-1,corvette-verteidigung-1"),
CORVETTE_SPEED_WARP      ("fast warp-corvette",          UnitClassEnum.CORVETTE, CorvetteModulesEnum.SPEED, CorvetteModulesEnum.WARP, "corvette-chassis,corvette-speed-1,corvette-warp-1"),
CORVETTE_ATTACK          ("aggressive corvette",         UnitClassEnum.CORVETTE, CorvetteModulesEnum.ATTACK, "corvette-chassis,corvette-angriff-1"),
CORVETTE_ATTACK_ATTACK   ("very aggressive corvette",    UnitClassEnum.CORVETTE, CorvetteModulesEnum.ATTACK, CorvetteModulesEnum.ATTACK, "corvette-chassis,corvette-angriff-1,corvette-angriff-2"),
CORVETTE_ATTACK_DEFENSE  ("aggressive, robust corvette", UnitClassEnum.CORVETTE, CorvetteModulesEnum.ATTACK, CorvetteModulesEnum.DEFENSE, "corvette-chassis,corvette-angriff-1,corvette-verteidigung-1"),
CORVETTE_ATTACK_WARP     ("aggressive warp-corvette",    UnitClassEnum.CORVETTE, CorvetteModulesEnum.ATTACK, CorvetteModulesEnum.WARP, "corvette-chassis,corvette-angriff-1,corvette-warp-1"),
CORVETTE_DEFENSE         ("robust corvette",             UnitClassEnum.CORVETTE, CorvetteModulesEnum.DEFENSE, "corvette-chassis,corvette-verteidigung-1"),
CORVETTE_DEFENSE_DEFENSE ("very robust corvette",        UnitClassEnum.CORVETTE, CorvetteModulesEnum.DEFENSE, CorvetteModulesEnum.DEFENSE, "corvette-chassis,corvette-verteidigung-1,corvette-verteidigung-2"),
CORVETTE_DEFENSE_WARP    ("robust warp-corvette",        UnitClassEnum.CORVETTE, CorvetteModulesEnum.DEFENSE, CorvetteModulesEnum.WARP, "corvette-chassis,corvette-warp-1,corvette-verteidigung-1"),
CORVETTE_WARP            ("warp-corvette",               UnitClassEnum.CORVETTE, CorvetteModulesEnum.WARP, "corvette-chassis,corvette-warp-1"),
CORVETTE_WARP_WARP       ("warp-corvette specialist",    UnitClassEnum.CORVETTE, CorvetteModulesEnum.WARP, CorvetteModulesEnum.WARP, "corvette-chassis,corvette-warp-1,corvette-warp-2"),

DESTROYER                ("destroyer",                    UnitClassEnum.DESTROYER, "zerstoerer-chassis"),
DESTROYER_SPEED          ("fast destroyer",               UnitClassEnum.DESTROYER, DestroyerModulesEnum.SPEED, "zerstoerer-chassis,zerstoerer-speed-1"),
DESTROYER_SPEED_SPEED    ("very fast destroyer",          UnitClassEnum.DESTROYER, DestroyerModulesEnum.SPEED, DestroyerModulesEnum.SPEED, "zerstoerer-chassis,zerstoerer-speed-1,zerstoerer-speed-2"),
DESTROYER_SPEED_ATTACK   ("fast, aggressive destroyer",   UnitClassEnum.DESTROYER, DestroyerModulesEnum.SPEED, DestroyerModulesEnum.ATTACK, "zerstoerer-chassis,zerstoerer-speed-1,zerstoerer-angriff-1"),
DESTROYER_SPEED_DEFENSE  ("fast, robust destroyer",       UnitClassEnum.DESTROYER, DestroyerModulesEnum.SPEED, DestroyerModulesEnum.DEFENSE, "zerstoerer-chassis,zerstoerer-speed-1,zerstoerer-verteidigung-1"),
DESTROYER_SPEED_WARP     ("fast warp-destroyer",          UnitClassEnum.DESTROYER, DestroyerModulesEnum.SPEED, DestroyerModulesEnum.WARP, "zerstoerer-chassis,zerstoerer-speed-1,zerstoerer-warp-1"),
DESTROYER_ATTACK         ("aggressive destroyer",         UnitClassEnum.DESTROYER, DestroyerModulesEnum.ATTACK, "zerstoerer-chassis,zerstoerer-angriff-1"),
DESTROYER_ATTACK_ATTACK  ("very aggressive destroyer",    UnitClassEnum.DESTROYER, DestroyerModulesEnum.ATTACK, DestroyerModulesEnum.ATTACK, "zerstoerer-chassis,zerstoerer-angriff-1,zerstoerer-angriff-2"),
DESTROYER_ATTACK_DEFENSE ("aggressive, robust destroyer", UnitClassEnum.DESTROYER, DestroyerModulesEnum.ATTACK, DestroyerModulesEnum.DEFENSE, "zerstoerer-chassis,zerstoerer-angriff-1"),
DESTROYER_ATTACK_WARP    ("aggressive warp-destroyer",    UnitClassEnum.DESTROYER, DestroyerModulesEnum.ATTACK, DestroyerModulesEnum.WARP, "zerstoerer-chassis,zerstoerer-angriff-1"),
DESTROYER_DEFENSE        ("robust destroyer",             UnitClassEnum.DESTROYER, DestroyerModulesEnum.DEFENSE, "zerstoerer-chassis,zerstoerer-verteidigung-1"),
DESTROYER_DEFENSE_DEFENSE("very robust destroyer",        UnitClassEnum.DESTROYER, DestroyerModulesEnum.DEFENSE, DestroyerModulesEnum.DEFENSE, "zerstoerer-chassis,zerstoerer-verteidigung-1,zerstoerer-verteidigung-2"),
DESTROYER_DEFENSE_WARP   ("robust warp-destroyer",        UnitClassEnum.DESTROYER, DestroyerModulesEnum.DEFENSE, DestroyerModulesEnum.WARP, "zerstoerer-chassis,zerstoerer-warp-1,zerstoerer-verteidigung-1"),
DESTROYER_WARP           ("warp-destroyer",               UnitClassEnum.DESTROYER, DestroyerModulesEnum.WARP, "zerstoerer-chassis,zerstoerer-warp-1"),
DESTROYER_WARP_WARP      ("warp-destroyer specialist",    UnitClassEnum.DESTROYER, DestroyerModulesEnum.WARP, DestroyerModulesEnum.WARP, "zerstoerer-chassis,zerstoerer-warp-1,zerstoerer-warp-2"),

FREIGHTER                ("freighter",                 UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, 1500000, 0, "cargo-chassis,cargo-ip-m2,cargo-rm3,cargo-rm2,cargo-rm1,cargo-ip-m1"),
FREIGHTER_SPEED          ("fast freighter",            UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.SPEED, 1125000, 0, "cargo-chassis,cargo-ip-m2,cargo-rm3,cargo-rm2,cargo-rm1,cargo-ip-m1"),
FREIGHTER_SPEED_SPEED    ("very fast freighter",       UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.SPEED, CivilianModulesEnum.SPEED, 750000, 0, "cargo-chassis,cargo-ip-m2,cargo-rm3,cargo-rm2,cargo-rm1,cargo-ip-m1"),
FREIGHTER_SPEED_DEFENSE  ("fast, robust freighter",    UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.SPEED, CivilianModulesEnum.DEFENSE, 750000, 0, "cargo-chassis,cargo-ip-m2,cargo-rm3,cargo-rm2,cargo-rm1,cargo-ip-m1"),
FREIGHTER_SPEED_WARP		 ("fast warp-freighter",       UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.SPEED, CivilianModulesEnum.WARP, 750000, 0, "cargo-chassis,cargo-rm3,cargo-rm2,cargo-rm1,cargo-is-m2,cargo-is-m1"),
FREIGHTER_DEFENSE        ("robust freighter",          UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.DEFENSE, 1125000, 0, "cargo-chassis,cargo-ip-m2,cargo-rm3,cargo-rm2,cargo-rm1,cargo-ip-m1"),
FREIGHTER_DEFENSE_DEFENSE("very robust freighter",     UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.DEFENSE, CivilianModulesEnum.DEFENSE, 750000, 0, "cargo-chassis,cargo-ip-m2,cargo-rm3,cargo-rm2,cargo-rm1,cargo-ip-m1"),
FREIGHTER_DEFENSE_WARP   ("robust warp-freighter",     UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.DEFENSE, CivilianModulesEnum.WARP, 750000, 0, "cargo-chassis,cargo-rm3,cargo-rm2,cargo-rm1,cargo-is-m2,cargo-is-m1"),
FREIGHTER_WARP           ("warp-freighter",            UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.WARP, 1125000, 0, "cargo-chassis,cargo-rm3,cargo-rm2,cargo-rm1,cargo-is-m2,cargo-is-m1"),
FREIGHTER_WARP_WARP      ("warp-freighter specialist", UnitClassEnum.CIVILIAN, CivilianModulesEnum.RES_TRANSPORT, CivilianModulesEnum.WARP, CivilianModulesEnum.WARP, 750000, 0, "cargo-chassis,cargo-rm3,cargo-rm2,cargo-rm1,cargo-is-m2,cargo-is-m1"),

TRANSPORTER                ("transport ship",              UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, 0, 750, "cargo-chassis,cargo-ip-m2,cargo-tm3,cargo-tm2,cargo-tm1,cargo-ip-m1"),
TRANSPORTER_SPEED          ("fast transporter",            UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.SPEED, 0, 560, "cargo-chassis,cargo-ip-m2,cargo-tm3,cargo-tm2,cargo-tm1,cargo-ip-m1"),
TRANSPORTER_SPEED_SPEED		 ("very fast transporter",       UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.SPEED, CivilianModulesEnum.SPEED, 0, 370, "cargo-chassis,cargo-ip-m2,cargo-tm3,cargo-tm2,cargo-tm1,cargo-ip-m1"),
TRANSPORTER_SPEED_DEFENSE	 ("fast, robust transporter",    UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.SPEED, CivilianModulesEnum.DEFENSE, 0, 370, "cargo-chassis,cargo-ip-m2,cargo-tm3,cargo-tm2,cargo-tm1,cargo-ip-m1"),
TRANSPORTER_SPEED_WARP		 ("fast warp-transporter",       UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.SPEED, CivilianModulesEnum.WARP, 0, 370, "cargo-chassis,cargo-tm3,cargo-tm2,cargo-tm1,cargo-is-m1"),
TRANSPORTER_DEFENSE        ("robust transporter",          UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.DEFENSE, 0, 560, "cargo-chassis,cargo-ip-m2,cargo-tm3,cargo-tm2,cargo-tm1,cargo-ip-m1"),
TRANSPORTER_DEFENSE_DEFENSE("very robust transporter",     UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.DEFENSE, CivilianModulesEnum.DEFENSE, 0, 370, "cargo-chassis,cargo-ip-m2,cargo-tm3,cargo-tm2,cargo-tm1,cargo-ip-m1"),
TRANSPORTER_DEFENSE_WARP   ("robust warp-transporter",     UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.DEFENSE, CivilianModulesEnum.WARP, 0, 370, "cargo-chassis,cargo-tm3,cargo-tm2,cargo-tm1,cargo-is-m1"),
TRANSPORTER_WARP           ("warp-transporter",            UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.WARP, 0, 560, "cargo-chassis,cargo-tm3,cargo-tm2,cargo-tm1,cargo-is-m1"),
TRANSPORTER_WARP_WARP      ("warp-transporter specialist", UnitClassEnum.CIVILIAN, CivilianModulesEnum.TROOP_TRANSPORT, CivilianModulesEnum.WARP, CivilianModulesEnum.WARP, 0, 370, "cargo-chassis,cargo-tm3,cargo-tm2,cargo-tm1,cargo-is-m1"),

CARRIER("carrier",
		UnitClassEnum.DESTROYER,
		new Cost(new int[] {220000, 160000, 110000, 220000}, 20, 13000, 240000, 14286L),
		new FightSpec(2500.0f, 0, new float[] {240.0f, 60.0f, 120.0f, 60.0f}),
		ResearchEnum.DESTROYER, -1,
		new UnitSpecial[] { UnitSpecial.WARP, UnitSpecial.CIVIL },
		new EducationEnum[] {},
		0, 0, 50, "warptraeger");

private final String englishTranslation;
private final boolean design;
private final boolean hidden; // only for standard units
private final UnitClassEnum uclass;
private final UnitModule[] umodules;
private final Cost cost;
private final FightSpec spec;

private final EnumSet<UnitSpecial> specials = EnumSet.noneOf(UnitSpecial.class);
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum[] education;
private final EducationModifier educationModifier;
private final int resTransport;
private final int groundTransport;
private final int spaceTransport;

private final String[] imageNames;

private UnitEnum(String englishTranslation, UnitClassEnum uclass, UnitModule[] umodules, int resTransport, int groundTransport, int spaceTransport, String imageNames)
{
	if (englishTranslation == null) throw new NullPointerException();
	this.design = true;
	this.hidden = false;
	this.englishTranslation = englishTranslation;
	this.uclass = uclass;
	this.umodules = umodules;
	this.education = new EducationEnum[0];
	
	Cost result = uclass.getCost();
	for (int i = 0; i < umodules.length; i++)
		result = Cost.sum(result, umodules[i].getCost());
	this.cost = result;
	
	FightSpec fresult = uclass.getFightSpec();
	// If both a defense and an attack module are contained, weaken the modules by 10%.
	// Modules add 25%, on mixed modules we want 1-1.5^0.5=22.47%. 22.47/25=89.898%.
	boolean hasAttack = false, hasDefense = false;
	for (int i = 0; i < umodules.length; i++) {
		if (umodules[i].getFightSpec().attack[0] > 0)
			hasAttack = true;
		if (umodules[i].getFightSpec().defense > 0)
			hasDefense = true;
	}
	for (int i = 0; i < umodules.length; i++) {
		if (hasAttack && hasDefense)
			fresult = FightSpec.sum(fresult, umodules[i].getFightSpec(), .89898f);
		else
			fresult = FightSpec.sum(fresult, umodules[i].getFightSpec());
	}
	this.spec = fresult;
	
	for (UnitSpecial special : uclass.specials())
		specials.add(special);
	
	for (int i = 0; i < umodules.length; i++)
		for (UnitSpecial special : umodules[i].specials())
			specials.add(special);
	
	if (!specials.contains(UnitSpecial.CIVIL)) {
		specials.add(UnitSpecial.AUTO_DEFENDER);
	}
	if (uclass == UnitClassEnum.INFANTRY)
		specials.add(UnitSpecial.INVASION);
	
	dependencies.merge(uclass.getResearchDependencies());
	for (int i = 0; i < umodules.length; i++)
		dependencies.merge(umodules[i].getResearchDependencies());
	UnitHelper.transitiveDependencies(this.dependencies);
	
	this.resTransport = resTransport;
	this.groundTransport = groundTransport;
	this.spaceTransport = spaceTransport;

	this.educationModifier = new EducationModifier();
	setEducationModifier();
	
	this.imageNames = imageNames.split(",");
}

private UnitEnum(String englishTranslation, UnitClassEnum uclass, String imageNames)
{ this(englishTranslation, uclass, new UnitModule[0], 0, 0, 0, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, UnitModule mod0, String imageNames)
{ this(englishTranslation, uclass, new UnitModule[] { mod0 }, 0, 0, 0, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, UnitModule mod0, UnitModule mod1, String imageNames)
{ this(englishTranslation, uclass, new UnitModule[] { mod0, mod1 }, 0, 0, 0, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, UnitModule mod0, int resTransport, int groundTransport, String imageNames)
{ this(englishTranslation, uclass, new UnitModule[] { mod0 }, resTransport, groundTransport, 0, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, UnitModule mod0, UnitModule mod1, int resTransport, int groundTransport, String imageNames)
{ this(englishTranslation, uclass, new UnitModule[] { mod0, mod1 }, resTransport, groundTransport, 0, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, UnitModule mod0, UnitModule mod1, UnitModule mod2, int resTransport, int groundTransport, String imageNames)
{ this(englishTranslation, uclass, new UnitModule[] { mod0, mod1, mod2 }, resTransport, groundTransport, 0, imageNames); }


// predesigned units
private UnitEnum(String englishTranslation,
		UnitClassEnum uclass, Cost cost, FightSpec spec, ResearchEnum dep, int depcount,
		UnitSpecial[] special, EducationEnum[] education,
		int resTransport, int groundTransport, int spaceTransport,
		boolean hidden, String imageNames)
{
	this.design = false;
	this.hidden = hidden;
	this.englishTranslation = englishTranslation;
	this.uclass = uclass;
	this.umodules = new UnitModule[0];
	this.education = education;
	
	this.cost = cost;
	this.spec = spec;

	if (depcount < 0) depcount = dep.getMax();
	if (depcount > dep.getMax()) depcount = dep.getMax();
	this.dependencies.set(dep, depcount);
	
	for (int i = 0; i < special.length; i++)
	{
		if (special[i] == UnitSpecial.WARP)
		{
			DependencyList<ResearchEnum> warpdep; 
			if (uclass == UnitClassEnum.FIGHTER)
				warpdep = FighterModulesEnum.WARP.getResearchDependencies();
			else if (uclass == UnitClassEnum.CORVETTE)
				warpdep = CorvetteModulesEnum.WARP.getResearchDependencies();
			else warpdep = DestroyerModulesEnum.WARP.getResearchDependencies();
			this.dependencies.merge(warpdep);	
		}
		this.specials.add(special[i]);
	}
	UnitHelper.transitiveDependencies(this.dependencies);
	
	this.resTransport = resTransport;
	this.groundTransport = groundTransport;
	this.spaceTransport = spaceTransport;

	this.educationModifier = new EducationModifier();
	setEducationModifier();
	
	this.imageNames = imageNames.split(",");
}

private UnitEnum(String englishTranslation, UnitClassEnum uclass, Cost cost, FightSpec spec,
		UnitSpecial[] special, boolean hidden, String imageNames)
{ this(englishTranslation, uclass, cost, spec, ResearchEnum.SCHOKOPUDDING, -1, special,
		new EducationEnum[0], 0, 0, 0, hidden, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, Cost cost, FightSpec spec,
		ResearchEnum dep, int depcount, UnitSpecial[] special, EducationEnum[] education,
		int resTransport, int groundTransport, int spaceTransport, String imageNames)
{ this(englishTranslation, uclass, cost, spec, dep, depcount, special, education,
		resTransport, groundTransport, spaceTransport, false, imageNames); }

private UnitEnum(String englishTranslation, UnitClassEnum uclass, Cost cost, FightSpec spec,
		ResearchEnum dep, int depcount, UnitSpecial[] special, EducationEnum[] education, String imageNames)
{ this(englishTranslation, uclass, cost, spec, dep, depcount, special, education,
		0, 0, 0, false, imageNames); }


private void setEducationModifier()
{
	if (!design) // special unit
	{
		for (int i = 0; i < education.length; i++)
		{
			float[] unitfactors = new float[4];
			for (int j = 0; j < unitfactors.length; j++)
				unitfactors[j] = education[i].getModifier(j);
			educationModifier.addEducation(education[i], unitfactors);
		}
	}
	else // module unit
	{
		float[] classfactors = new float[4];
		Cost ccost = uclass.getCost();
		EducationEnum classedu = uclass.getEducation();
		if (classedu == null)
			if (resTransport > 0)
				classedu = EducationEnum.RESSOURCENTRANSPORT;
			else if (groundTransport > 0)
				classedu = EducationEnum.BODENTRUPPENTRANSPORT;
		
		if (classedu != null)
		{
			for (int i = 0; i < classfactors.length; i++)
			{
				float factor = classedu.getModifier(i);
				classfactors[i] = (factor*ccost.getResource(i))/cost.getResource(i);
			}
			educationModifier.addEducation(classedu, classfactors);
		}
		
		for (int i = 0; i < umodules.length; i++)
		{
			float[] modulefactors = new float[4];
			Cost mcost = umodules[i].getCost();
			EducationEnum moduleedu = umodules[i].getEducation();
			for (int j = 0; j < modulefactors.length; j++)
			{
				float factor = moduleedu.getModifier(j);
				modulefactors[j] = (factor*mcost.getResource(j))/cost.getResource(j);
			}
			educationModifier.addEducation(moduleedu, modulefactors);
		}
	}	
}

@Override
public boolean isDesign()
{ return design; }

public boolean isHidden()
{ return hidden; }

@Override
public UnitSystem unitSystem()
{ return UnitSystemImpl.getImplementation(); }

@Override
public UnitClass unitClass()
{ return uclass; }

@Override
public UnitModule[] getUnitModules()
{ return umodules; }

@Override
public boolean isPlanetary()
{ return uclass.isPlanetary(); }

@Override
public boolean isInterplanetary()
{ return !isPlanetary() && !hasSpecial(UnitSpecial.WARP); }

@Override
public boolean isInterstellar()
{ return !isPlanetary() && hasSpecial(UnitSpecial.WARP); }

@Override
public boolean hasSpecial(UnitSpecial special)
{ return specials.contains(special); }

@Override
public boolean hasSpeedModule()
{
	for (int i = 0; i < umodules.length; i++)
	{
		UnitModule m = umodules[i];
		if ((m == FighterModulesEnum.SPEED)
			|| (m == CorvetteModulesEnum.SPEED)
			|| (m == DestroyerModulesEnum.SPEED)
			|| (m == CivilianModulesEnum.SPEED))
			return true;
	}
	return false;
}

@Override
public boolean hasDoubleWarpModule()
{
	for (int i = 0; i < umodules.length; i++)
	{
		UnitModule m = umodules[i];
		if ((m == FighterModulesEnum.WARP)
			|| (m == CorvetteModulesEnum.WARP)
			|| (m == DestroyerModulesEnum.WARP)
			|| (m == CivilianModulesEnum.WARP))
		for (int j = i+1; j < umodules.length; j++)
			if ((m == FighterModulesEnum.WARP)
					|| (m == CorvetteModulesEnum.WARP)
					|| (m == DestroyerModulesEnum.WARP)
					|| (m == CivilianModulesEnum.WARP))
				return true;
	}
	return false;
}

@Override
public boolean hasDoubleSpeedModule()
{
	for (int i = 0; i < umodules.length; i++)
	{
		UnitModule m = umodules[i];
		if ((m == FighterModulesEnum.SPEED)
			|| (m == CorvetteModulesEnum.SPEED)
			|| (m == DestroyerModulesEnum.SPEED)
			|| (m == CivilianModulesEnum.SPEED))
		for (int j = i+1; j < umodules.length; j++)
			if ((m == FighterModulesEnum.SPEED)
					|| (m == CorvetteModulesEnum.SPEED)
					|| (m == DestroyerModulesEnum.SPEED)
					|| (m == CivilianModulesEnum.SPEED))
				return true;
	}
	return false;
}


@Override
public boolean hasGroundSpeedModule()
{
	for (int i = 0; i < umodules.length; i++)
	{
		UnitModule m = umodules[i];
		if ((m == InfantryModulesEnum.SPEED)
			|| (m == VehicleModulesEnum.SPEED)
			|| (m == AircraftModulesEnum.SPEED))
			return true;
	}
	return false;
}

@Override
public boolean hasDoubleGroundSpeedModule()
{
	for (int i = 0; i < umodules.length; i++)
	{
		UnitModule m = umodules[i];
		if ((m == InfantryModulesEnum.SPEED)
			|| (m == VehicleModulesEnum.SPEED)
			|| (m == AircraftModulesEnum.SPEED))
		for (int j = i+1; j < umodules.length; j++)
			if ((m == InfantryModulesEnum.SPEED)
					|| (m == VehicleModulesEnum.SPEED)
					|| (m == AircraftModulesEnum.SPEED))
				return true;
	}
	return false;
}

@Override
public Cost getCost()
{ return cost; }

@Override
public EducationModifier getEducationModifier()
{ return educationModifier; }

@Override
public FightSpec getFightSpec()
{ return spec; }

@Override
public DependencyList<ResearchEnum> getResearchDependencies()
{ return dependencies; }

public EducationEnum[] getEducation()
{ return education; }

@Override
public boolean checkResearchDependencies(EnumIntMap<ResearchEnum> map)
{ return dependencies.check(map); }

@Override
public int getGroup()
{ return uclass.getGroup(); }

@Override
public int getSize()
{
	if (getSpaceUnitCapacity() > 0)
		return getSpaceUnitCapacity();
	switch (uclass)
	{
		case INFANTRY  :
		case FIGHTER   : return 1;
		case VEHICLE   : return 3;
		case AIRCRAFT  : return 4;
		case CORVETTE  : return 5;
		case DESTROYER : return 12;
		default: return getGroup()+1;
	}
}

// FIXME: getPower
@Override
public int getPower()
{ return Math.round(spec.averageAttack()/100); }

@Override
public int getUpkeep()
{ return cost.getMoney(); }

@Override
public int getScore()
{
	if (this.isDesign())
		return (int) Math.round(this.cost.getTime()/10.0);
	else
		return (int) Math.round(this.cost.getTime()/20.0);
}

@Override
public int getResourceCapacity()
{ return resTransport; }

@Override
public int getGroundUnitCapacity()
{ return groundTransport; }

@Override
public int getSpaceUnitCapacity()
{ return spaceTransport; }

@Override
public float getLandingAttack()
{ return spec.getLandingAttack(); }

@Override
public float getAttack(int group)
{ return spec.getAttack(group); }

@Override
public float getDefense()
{ return spec.getDefense(); }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.engine.units.UnitEnum"; }

@Override
public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

@Override
public String[] getImageNames()
{ return imageNames; }

}

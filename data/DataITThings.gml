ITThingsAll [
	Tech [
		Name Wheels
		TechId 1
		Type sr
		Prerequisit T78
		Partner "FRQ,XFG"
		ProductionCost "1xRubber,1xMechanicalParts"
		LeasePrice 54000
		Notes "Allows movement over WwP and p"
	]
	Tech [
		Name Track
		TechId 2
		Type sr
		Prerequisit T1
		ProductionCost "2xMechanicalParts"
		LeasePrice -1
		Notes "Allows movement over WwPp and b"
	]
	Tech [
		Name HexaPod
		TechId 3
		Type r
		Prerequisit T2
		ProductionCost "5xMechanicalParts"
		LeasePrice -1
		Notes "Allows movement over WwPpb and B"
	]
	Tech [
		Name BiPod
		TechId 4
		Type r
		Prerequisit T3
		ProductionCost "3xMechanicalParts,1xComputerParts"
		LeasePrice -1
		Notes "Allows movement over WwPpbB and m"
	]
	Tech [
		Name HoverFit
		TechId 5
		Type sr
		Prerequisit T78
		Partner TNT
		ProductionCost "1xRubber,2xMechanicalParts"
		LeasePrice -1
		Notes "Allows movement over WwPpb and B. Requires atmosphere"
	]
	Tech [
		Name Wings
		TechId 6
		Type sr
		Prerequisit T5
		ProductionCost "1xFabric,2xAluminum,1xPlastic"
		LeasePrice -1
		Notes "Allows movement over all terrain. Requires atmosphere"
	]
	Tech [
		Name PropulsionJets
		TechId 7
		Type sr
		Prerequisit T6
		ProductionCost "2xDuralloy,1xSteel,1xEngineParts"
		LeasePrice -1
		Notes "Allows movement over all terrain."
	]
	Tech [
		Name EVAJets
		TechId 8
		Type r
		Prerequisit T7
		ProductionCost "1xMechanicalParts,1xEngineParts"
		LeasePrice -1
		Notes "Allows movement in orbit and uncontrolled reentry"
	]
	Tech [
		Name BoringDrill
		TechId 9
		Type r
		Prerequisit T27
		ProductionCost "1xDuralloy,3xSteel,1xMechanicalParts"
		LeasePrice -1
		Notes "Allows movement below surface"
	]
	Tech [
		Name Parachute
		TechId 10
		Type r
		Prerequisit T79
		ProductionCost "2xFabric"
		LeasePrice -1
		Notes "Allows reentry from space.  Uncontrolled without atmosphere."
	]
	Tech [
		Name Airbag
		TechId 11
		Type r
		Prerequisit T10
		ProductionCost "4xFabric"
		LeasePrice -1
		Notes "Allows reentry."
	]
	Tech [
		Name Rocket1G
		TechId 12
		Type s
		Prerequisit T79
		Partner "XFG,FRQ"
		ProductionCost "5xEngineParts,2xDuralloy"
		LeasePrice 30000
		Notes "Allows ship to move intraplanetary, launch from surface and reentry."
	]
	Tech [
		Name Rocket2G
		TechId 13
		Type s
		Prerequisit T12
		Partner XFG
		ProductionCost "8xEngineParts,2xDuralloy"
		LeasePrice 70000
		Notes "Allows ship to move faster intraplanetary, launch from surface and reentry."
	]
	Tech [
		Name Rocket3G
		TechId 14
		Type s
		Prerequisit T13
		Partner "XFG,FRQ"
		ProductionCost "10xEngineParts,2xDuralloy"
		LeasePrice 120000
		Notes "Allows ship to move fastest intraplanetary, launch from surface and reentry."
	]
	Tech [
		Name IonDrive
		TechId 15
		Type s
		Prerequisit T14
		ProductionCost "2xEngineParts,2xElectronicParts,1xHybridMetals,1xDuralloy"
		LeasePrice -1
		Notes "Allows ship to move slowly intraplanetary and use very low endurance.  Allows uncontrolled reentry."
	]
	Tech [
		Name WeatherResistance
		TechId 16
		Type frs
		Prerequisit T82
		Partner TNT
		ProductionCost "2xPlastic"
		LeasePrice 55000
		Notes "Protection from minor weather damage rated over 1.  Defense level 1."
	]
	Tech [
		Name WaterProof
		TechId 17
		Type frs
		Prerequisit T16
		ProductionCost "3xPlastic"
		LeasePrice -1
		Notes "Protection from liquid surface damage  Defense level 1."
	]
	Tech [
		Name CorrosionProtection
		TechId 18
		Type frs
		Prerequisit T16
		ProductionCost "1xPlastic,1xRubber"
		LeasePrice -1
		Notes "Protection atmosphere damage from levels above 4.  Defense level 2."
	]
	Tech [
		Name VacuumProtection
		TechId 19
		Type f
		Prerequisit T82
		ProductionCost "8xPlastic"
		LeasePrice -1
		Notes "Protection from 0 atmosphere and deep space. Defense level 4."
	]
	Tech [
		Name LowTempProtection
		TechId 20
		Type fr
		Prerequisit T82
		Partner "FRQ,XFG"
		ProductionCost "1xPlastic,1xLubricants"
		LeasePrice 40000
		Notes "Protection cold damage temp <-80.   Defense level 1."
	]
	Tech [
		Name ExtremeLowTempProtection
		TechId 21
		Type fr
		Prerequisit T20
		Partner XFG
		ProductionCost "4xPlastic,1xWater,1xCopper"
		LeasePrice 95000
		Notes "Protection cold damage temp <-200.  Defense level 1."
	]
	Tech [
		Name HighTempProtection
		TechId 22
		Type frs
		Prerequisit T20
		ProductionCost "2xSteel"
		LeasePrice -1
		Notes "Protection heat damage temp >100.  Defense level 1."
	]
	Tech [
		Name ExtremeTempProtection
		TechId 23
		Type frs
		Prerequisit T22
		ProductionCost "4xDuralloy"
		LeasePrice -1
		Notes "Protection heat damage temp >200.  Defense level 1."
	]
	Tech [
		Name EarthquakeProtection
		TechId 24
		Type frs
		Prerequisit T41
		ProductionCost "2xDuralloy,1xPlastic,2xRubber"
		LeasePrice -1
		Notes "Protection from minor tectonics of 1 and over.  Defense level 3."
	]
	Tech [
		Name LowLevelRadHardening
		TechId 25
		Type frs
		Prerequisit T42
		ProductionCost "2xLead"
		LeasePrice -1
		Notes "Protection from radiation damage rated 1,2 or 3.  Defense level 1."
	]
	Tech [
		Name StrongRadHarding
		TechId 26
		Type frs
		Prerequisit T25
		ProductionCost "4xLead"
		LeasePrice -1
		Notes "Protection radation damage rated over 3.  Defense level 1."
	]
	Tech [
		Name PressureFit
		TechId 27
		Type f
		Prerequisit T24
		ProductionCost "10xSteel"
		LeasePrice -1
		Notes "Protection from underground pressure damage up to level 2.  Defense level 3."
	]
	Tech [
		Name ExtremePressureFit
		TechId 28
		Type f
		Prerequisit T27
		ProductionCost "8xSteel,8xDuralloy"
		LeasePrice -1
		Notes "Protection from underground pressure damage level 3 or greater.  Defense level 5."
	]
	Tech [
		Name OpenHull
		TechId 29
		Type sr
		Prerequisit T78
		ProductionCost "2xDuralloy"
		LeasePrice -1
		Notes "Defense level 1"
	]
	Tech [
		Name BoxHull
		TechId 30
		Type sr
		Prerequisit T29
		ProductionCost "3xDuralloy"
		LeasePrice -1
		Notes "Defense level 4"
	]
	Tech [
		Name CylinderHull
		TechId 31
		Type sr
		Prerequisit T30
		ProductionCost "4xDuralloy"
		LeasePrice -1
		Notes "Defense level 6"
	]
	Tech [
		Name SphericalHull
		TechId 32
		Type sr
		Prerequisit T79
		ProductionCost "6xDuralloy"
		LeasePrice -1
		Notes "Defense level 8"
	]
	Tech [
		Name ArrowHull
		TechId 33
		Type s
		Prerequisit T32
		ProductionCost "6xHybridMetals"
		LeasePrice -1
		Notes "Defense level 12"
	]
	Tech [
		Name WedgeHull
		TechId 34
		Type s
		Prerequisit T33
		ProductionCost "10xHybridMetals"
		LeasePrice -1
		Notes "Defense level 15"
	]
	Tech [
		Name LightWorkArm
		TechId 35
		Type sr
		Prerequisit T78
		Partner "FRQ,TNT,XFG"
		ProductionCost "1xMechanicalParts,1xElectronicParts"
		LeasePrice 40000
		Notes "Enables robots and ships to load and unload cargo at 1 ton per hour, and attack level 1."
	]
	Tech [
		Name HeavyWorkArm
		TechId 36
		Type r
		Prerequisit T35
		Partner "FRQ,TNT,XFG"
		ProductionCost "3xMechanicalParts,1xElectronicParts"
		LeasePrice 100000
		Notes "Enable robot to collect, refine, produce, construct, salvage, repair, attack level 2, load and unload at 3 tons per hour"
	]
	Tech [
		Name Radar
		TechId 37
		Type sr
		Prerequisit T81
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning for robots, ships, stock piles, facilities and prefabs."
	]
	Tech [
		Name LowLightVideo
		TechId 38
		Type sr
		Prerequisit T81
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning surface including sector scans."
	]
	Tech [
		Name ThermalImage
		TechId 39
		Type sr
		Prerequisit T38
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning the temperature including sector scans."
	]
	Tech [
		Name Spectroscope
		TechId 40
		Type sr
		Prerequisit T81
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning for resources including in sector scan."
	]
	Tech [
		Name Seismometer
		TechId 41
		Type sr
		Prerequisit T81
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning of tectonics in sector scan only."
	]
	Tech [
		Name Geigercounter
		TechId 42
		Type sr
		Prerequisit T81
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning of radiation in sector scan only."
	]
	Tech [
		Name Meteorology
		TechId 43
		Type sr
		Prerequisit T39
		ProductionCost "1xSensorParts"
		LeasePrice -1
		Notes "Enables scanning of weather and atmosphere in sector scan only."
	]
	Tech [
		Name Collector
		TechId 44
		Type f
		Prerequisit T80
		Partner PPP
		ProductionCost "7xFerroconcrete,2xMechanicalParts,2xNitroglycerin"
		Consumes "2xMegaWatts"
		LeasePrice 70000
		Notes "Enables units to collection of resources level 1-5 when inside."
	]
	Tech [
		Name Refine
		TechId 45
		Type f
		Prerequisit T44
		ProductionCost "2xDuralloy,3xFerroconcrete,4xMechanicalParts"
		Consumes "7xMegaWatts"
		LeasePrice -1
		Notes "Enables units to refine materials when inside."
	]
	Tech [
		Name Production
		TechId 46
		Type f
		Prerequisit T45
		ProductionCost "5xMechanicalParts,10xFerroconcrete"
		Consumes "4xMegaWatts"
		LeasePrice -1
		Notes "Enables units to produce material, prefabs, robots and ships when inside."
	]
	Tech [
		Name Repair
		TechId 47
		Type rf
		Prerequisit T80
		Partner XFG
		ProductionCost "1xMechanicalParts"
		Consumes "1xMegaWatts"
		LeasePrice 70000
		Notes "Facilities with ability enables units to repair units when inside.  Robots with ability can repair anywhere, but more slowly."
	]
	Tech [
		Name Salvage
		TechId 48
		Type rf
		Prerequisit T47
		ProductionCost "1xMechanicalParts,1xLubricants,1xPlastic"
		Consumes "1xMegaWatts"
		LeasePrice -1
		Notes "Facilities with ability enables units to salvage Junk into SpareParts when inside.  Robots with ability can salvage anywhere with ability but more slowly."
	]
	Tech [
		Name Recycler
		TechId 49
		Type f
		Prerequisit T48
		ProductionCost "5xMechanicalParts,6xPlastic,2xDuralloy"
		Consumes "2xMegaWatts"
		LeasePrice -1
		Notes "Enables units to produce Hydrocarbons from IndrustralWaste when inside."
	]
	Tech [
		Name Incinerator
		TechId 50
		Type f
		Prerequisit T49
		ProductionCost "7xFerroconcrete,6xDuralloy,2xMechanicalParts"
		Consumes "3xMegaWatts"
		LeasePrice -1
		Notes "Enables units to produce Hydrocarbons from any type of Waste except Industrial when inside."
	]
	Tech [
		Name Distilation
		TechId 51
		Type f
		Prerequisit T84
		ProductionCost "5xPlastic,2xFerroconcrete,2xMechanicalParts,2xPlastic"
		Consumes "1xMegaWatts"
		LeasePrice -1
		Notes "Enables units to collect liquid resources at level 7 or above.  Must be constructed on liquid surface."
	]
	Tech [
		Name Condenser
		TechId 52
		Type f
		Prerequisit T84
		ProductionCost "1xFabric,2xMechanicalParts,4xPlastic"
		Consumes "1xMegaWatts"
		LeasePrice -1
		Notes "Enables units to collect gaseous resources at level 6 or above.  Must be constructed on planet surface with atmosphere."
	]
	Tech [
		Name Laser
		TechId 53
		Type sr
		Prerequisit T78
		Partner FRQ
		ProductionCost "1xElectronicParts,1xGemStones"
		LeasePrice -1
		Notes "Attack level 3"
	]
	Tech [
		Name PlasmaGun
		TechId 54
		Type rs
		Prerequisit T53
		ProductionCost "4xElectronicParts,1xHybridMetals"
		LeasePrice -1
		Notes "Attack level 7"
	]
	Tech [
		Name XrayLaser
		TechId 55
		Type rs
		Prerequisit T54
		ProductionCost "4xElectronicParts"
		LeasePrice -1
		Notes "Attack level 9"
	]
	Tech [
		Name GammaRayLaser
		TechId 56
		Type rs
		Prerequisit T55
		ProductionCost "6xElectronicParts"
		LeasePrice -1
		Notes "Attack level 12"
	]
	Tech [
		Name Security
		TechId 57
		Type rsf
		Prerequisit T80
		Partner XXX
		ProductionCost "1xComputerParts,1xMechanicalParts"
		Consumes "1xMegaWatts"
		LeasePrice 50000
		Notes "Block entrance and exit to facility and actives by non-corp actives.  Only facilities need MegaWatts."
	]
	Tech [
		Name ConnectorTransport
		TechId 58
		Type f
		Prerequisit T80
		Partner FRQ
		ProductionCost "4xFerroconcrete"
		LeasePrice -1
		Notes "Enables robots and ships to move over any surface regardless of ability."
	]
	Tech [
		Name ConnectorTunnel
		TechId 59
		Type f
		Prerequisit T58
		ProductionCost "6xFerroconcrete"
		LeasePrice -1
		Notes "Enables robots and ships to move below surface without boring technology."
	]
	Tech [
		Name ConnectorSupply
		TechId 60
		Type f
		Prerequisit T58
		ProductionCost "3xCopper,1xPlastic"
		LeasePrice -1
		Notes "Enables facilities in adjacent location to share power and other provided resources."
	]
	Tech [
		Name PowerCombustion
		TechId 64
		Type f
		Prerequisit T80
		Partner TNT
		ProductionCost "5xFerroconcrete,2xSteel"
		Provides "10xMegaWatts,1xM70"
		Consumes "1xM21"
		LeasePrice 70000
		Notes "Can recharge robots and power facilities."
	]
	Tech [
		Name PowerSolar
		TechId 65
		Type f
		Prerequisit T64
		ProductionCost "5xElectronicParts,2xFerroconcrete"
		Provides "2xMegaWatts"
		LeasePrice -1
		Notes "Can recharge robots and power facilities.  Must be constructed on surface of planet or in orbit."
	]
	Tech [
		Name Research
		TechId 69
		Type f
		Prerequisit T70
		ProductionCost "2xPlastic,1xFabric,3xFerroconcrete"
		Consumes "1xHumanLabor,1xMegaWatts"
		LeasePrice -1
		Notes "Human research and ability to contract."
	]
	Tech [
		Name HumanShelter
		TechId 70
		Type f
		Prerequisit T80
		ProductionCost "2xFerroconcrete,1xPlastic,1xElectronicParts"
		Provides "2xHumanLabor,1xM69"
		Consumes "1xHydration,1xFood,1xOxygen"
		LeasePrice -1
		Notes "Source of human labor."
	]
	Tech [
		Name OxygenGenerator
		TechId 71
		Type f
		Prerequisit T70
		ProductionCost "1xPreciousMetals,3xFerroconcrete"
		Provides "2xOxygen"
		Consumes "2xMegaWatts"
		LeasePrice -1
		Notes "Source of o2 for humans."
	]
	Tech [
		Name OxygenRebreather
		TechId 72
		Type f
		Prerequisit T71
		ProductionCost "4xFabric,1xPlastic,1xFerroconcrete"
		Provides "3xOxygen"
		Consumes "1xMegaWatts"
		LeasePrice -1
		Notes "Source of o2 for humans."
	]
	Tech [
		Name HydrationPump
		TechId 73
		Type f
		Prerequisit T70
		ProductionCost "3xPlastic,2xFerroconcrete"
		Provides "3xHydration"
		Consumes "1xMegaWatts,1xM3"
		LeasePrice -1
		Notes "Source of water for humans."
	]
	Tech [
		Name BioFarm
		TechId 74
		Type f
		Prerequisit T70
		ProductionCost "5xFertilizer,2xFerroconcrete"
		Provides "2xFood,1xM70"
		Consumes "1xMegaWatts,1xM30"
		LeasePrice -1
		Notes "Source of food for human."
	]
	Tech [
		Name DefenseRocket
		TechId 75
		Type f
		Prerequisit T80
		ProductionCost "8xMunitions,3xFerroconcrete,1xSensorParts"
		LeasePrice -1
		Notes "Defense level 15."
	]
	Tech [
		Name DefenseLaser
		TechId 76
		Type f
		Prerequisit T75
		ProductionCost "5xElectronicParts,2xGemStones,1xFerroconcrete,1xSensorParts"
		LeasePrice -1
		Notes "Defense level 25."
	]
	Tech [
		Name LandingZone
		TechId 77
		Type f
		Prerequisit T80
		Partner "FRQ,TNT"
		ProductionCost "4xFerroconcrete,2xSensorParts,2xPlastic"
		Consumes "1xMegaWatts"
		LeasePrice -1
		Notes "Safe landings."
	]
	Tech [
		Name BasicRobot
		TechId 78
		Type r
		Prerequisit none
		Partner public
		ProductionCost "1xDuralloy"
		LeasePrice -1
		Notes "TYPE defines robot"
	]
	Tech [
		Name BasicShip
		TechId 79
		Type s
		Prerequisit none
		Partner public
		ProductionCost "2xDuralloy"
		LeasePrice -1
		Notes "TYPE defines ship"
	]
	Tech [
		Name BasicFacility
		TechId 80
		Type f
		Prerequisit none
		Partner public
		ProductionCost "4xDuralloy"
		LeasePrice -1
		Notes "TYPE defines facility"
	]
	Tech [
		Name BasicSensor
		TechId 81
		Type rs
		Prerequisit none
		Partner none
		ProductionCost ""
		LeasePrice 0
		Notes "TYPE defines Sensor"
	]
	Tech [
		Name BasicProtection
		TechId 82
		Type rsf
		Prerequisit none
		Partner none
		ProductionCost ""
		LeasePrice 0
		Notes "TYPE defines Protection"
	]
	Tech [
		Name Market
		TechId 83
		Type f
		Prerequisit none
		Partner none
		ProductionCost ""
		LeasePrice -1
		Notes "DONT KNOW.  Allows remote sale or remot buy of material.  10% fee, 5 for shipment, 5 for operating corporation.  NOT OPERATIONAL."
	]
	Tech [
		Name Office
		TechId 84
		Type f
		Prerequisit none
		Partner none
		ProductionCost ""
		LeasePrice -1
		Notes "DONT KNOW.  Default blocking out technology.  Industructable, non auctionable, fully powered."
	]
	Tech [
		Name PowerUtility
		TechId 85
		Type f
		Prerequisit none
		Partner none
		ProductionCost ""
		Provides "100xMegaWatts"
		LeasePrice -1
		Notes "DONT KNOW.  Earth based power.  Runs office.  Free supply."
	]
	Tech [
		Name PublicMarket
		TechId 86
		Type f
		Prerequisit none
		Partner none
		ProductionCost ""
		LeasePrice -1
		Notes "DONT KNOW.  Earth based market."
	]
	Tech [
		Name StarGate
		TechId 87
		Type f
		Prerequisit T14
		Partner XFG
		ProductionCost "2xDarkMatter,5xAlienRelics"
		LeasePrice 200000
		Notes "Entering this facility opens worm hole to new solar system and transports unit there instantly.  This is a one way trip."
	]
	Tech [
		Name Crane
		TechId 88
		Type sr
		Prerequisit T36
		Partner FRQ
		ProductionCost "4xMechanicalParts,2xElectronicParts"
		LeasePrice -1
		Notes "Enables loading at 6 tons per hour."
	]
	Tech [
		Name Regenerate
		TechId 89
		Type rsf
		Prerequisit T48
		ProductionCost "4xAlienLifeForm"
		LeasePrice -1
		Notes "Auto repair self 0-1% per day."
	]
	Tech [
		Name AdvancedComputer
		TechId 90
		Type rs
		Prerequisit T78
		Partner FRQ
		ProductionCost "1xDarkMatter,2xAlienRelics"
		LeasePrice -1
		Notes "Store 40 lines of programs and upload 2 subroutines."
	]
	Tech [
		Name DNAComputer
		TechId 91
		Type rs
		Prerequisit T90
		ProductionCost "5xAlienLifeForm"
		LeasePrice -1
		Notes "Store 60 lines of program and upload 3 subroutines."
	]
	Tech [
		Name QuantumComputer
		TechId 92
		Type rs
		Prerequisit T91
		ProductionCost "1xDarkMatter,3xAlienRelics"
		LeasePrice -1
		Notes "Store 1000 lines of program and upload 10 subroutines."
	]
	Tech [
		Name WareHousing
		TechId 93
		Type f
		Prerequisit T80
		Partner XXX
		ProductionCost "5xFerroconcrete"
		LeasePrice 75000
		Notes "Increase capacity to 1000 tons."
	]
	Tech [
		Name Cloak
		TechId 94
		Type rsf
		Prerequisit T90
		ProductionCost "5xDarkMatter,1xAlienRelics"
		Consumes "1xMegaWatts"
		LeasePrice -1
		Notes "Invisible to radar detection.  Only facilities need MegaWatts."
	]
	Tech [
		Name Telescope
		TechId 95
		Type sr
		Prerequisit T38
		ProductionCost "2xSensorParts"
		LeasePrice -1
		Notes "Can SCAN SYSTEM from orbit."
	]
	Tech [
		Name Reinforcements
		TechId 96
		Type f
		Prerequisit T27
		ProductionCost "5xFerroconcrete,2xDuralloy"
		LeasePrice -1
		Notes "Defense level 30."
	]
	Tech [
		Name CombatComputer
		TechId 97
		Type rs
		Prerequisit T84
		ProductionCost "2xComputerParts"
		LeasePrice -1
		Notes "ABILIIY. 50% increase on attack and 100% increase on defense. NOT OPERATIONAL."
	]
	Tech [
		Name HydrationRecycler
		TechId 98
		Type f
		Prerequisit T73
		ProductionCost "3xPlastic,2xFerroconcrete"
		Provides "1xHydration"
		Consumes "2xMegaWatts"
		LeasePrice -1
		Notes "Source of h2o for humans."
	]
	Tech [
		Name ThemePark
		TechId 99
		Type f
		Prerequisit T70
		ProductionCost "2xPlastic,1xSteel,2xFerroconcrete"
		Provides "7xM74"
		Consumes "1xMegaWatts,1xHumanLabor,7xM73"
		LeasePrice -1
		Notes "Converts travelers into tourists."
	]
	Tech [
		Name PowerGeoThermal
		TechId 100
		Type f
		Prerequisit T27
		ProductionCost "2xPlastic,2xFerroconcrete,1xDuralloy"
		Provides "15xMegaWatts"
		LeasePrice -1
		Notes "Can recharge robots and power facilities.  Must be constructed below ground on planet with tectonics at 1 or higher."
	]
	Tech [
		Name PowerTidal
		TechId 101
		Type f
		Prerequisit T17
		ProductionCost "2xPlastic,2xFerroconcrete,1xDuralloy"
		Provides "15xMegaWatts"
		LeasePrice -1
		Notes "Can recharge robots and power facilities.  Must be constructed on liquids surface."
	]
	Tech [
		Name PowerMagnetic
		TechId 102
		Type f
		Prerequisit T19
		ProductionCost "3xIron,2xCopper,5xFerroconcrete"
		Provides "10xMegaWatts"
		LeasePrice -1
		Notes "Can recharge robots and power facilities.  Must be constructed in orbit."
	]
	Tech [
		Name PowerNuclear
		TechId 103
		Type f
		Prerequisit "T64,T25"
		ProductionCost "5xLead,2xElectronicParts,5xFerroconcrete"
		Provides "30xMegaWatts,1xM68"
		Consumes "1xM61"
		LeasePrice -1
		Notes "Can recharge robots and power facilities."
	]
	ActiveDesign [
		Name AbleJack
		ActiveDesignId 1
		Partner XXX
		Function "T78,T36,T1,T37,T38,T40"
		ProductionCost "1xDuralloy,4xMechanicalParts,1xRubber,3xSensorParts"
		LeasePrice 10000
		CargoCapacity 30
		MaxEndurance 10
		Type r
	]
	FacilityDesign [
		Name CorporateHQ
		FacilityDesignId 1
		Partner XXX
		Function "T80,T85,T84,T77,T57,T69"
		ProductionCost "2xDuralloy,10xFerroconcrete,1xSensorParts,2xPlastic,4xComputerParts,2xMechanicalParts"
		LeasePrice -1
	]
	ActiveDesign [
		Name Apollo
		ActiveDesignId 2
		Partner XXX
		Function "T79,T12,T35,T37,T38,T39,T40"
		ProductionCost "2xDuralloy,5xEngineParts,2xMechanicalParts,1xElectronicParts,9xSensorParts"
		LeasePrice 20000
		CargoCapacity 140
		MaxEndurance 7
		Type s
	]
	FacilityDesign [
		Name Road
		FacilityDesignId 2
		Partner XXX
		Function "T80,T58"
		ProductionCost "5xDuralloy,5xFerroconcrete"
		LeasePrice 10000
	]
	ActiveDesign [
		Name JunkSlave1
		ActiveDesignId 3
		Partner FRQ
		Function "T36,T78"
		ProductionCost "2xMechanicalParts,1xDuralloy"
		LeasePrice 44000
		CargoCapacity 0
		MaxEndurance 12
		Type r
	]
	FacilityDesign [
		Name Mine
		FacilityDesignId 3
		Partner XXX
		Function "T80,T44"
		ProductionCost "5xDuralloy,4xFerroconcrete,1xMechanicalParts,2xNitroglycerin"
		LeasePrice 20000
	]
	FacilityDesign [
		Name LandingStrip
		FacilityDesignId 4
		Partner XXX
		Function "T80,T77"
		ProductionCost "5xDuralloy,4xFerroconcrete,1xSensorParts,2xPlastic"
		LeasePrice 20000
	]
	ActiveDesign [
		Name FreeTransport1
		ActiveDesignId 5
		Partner FRQ
		Function "T88,T12,T1,T79"
		ProductionCost "9xMechanicalParts,4xElectronicParts,2xEngineParts,2xDuralloy,2xRubber"
		LeasePrice -1
		CargoCapacity 176
		MaxEndurance 6
		Type s
	]
	FacilityDesign [
		Name Refinery
		FacilityDesignId 5
		Partner XXX
		Function "T80,T45"
		ProductionCost "5xDuralloy,1xFerroconcrete,3xMechanicalParts"
		LeasePrice 30000
	]
	ActiveDesign [
		Name FreeGeneral0
		ActiveDesignId 6
		Partner FRQ
		Function "T36,T1,T78"
		ProductionCost "5xMechanicalParts,1xElectronicParts,1xRubber"
		LeasePrice -1
		CargoCapacity 56
		MaxEndurance 9
		Type r
	]
	FacilityDesign [
		Name PowerPlantCombustion
		FacilityDesignId 6
		Partner XXX
		Function "T80,T64"
		ProductionCost "4xDuralloy,7xFerroconcrete,2xSteel"
		LeasePrice 20000
	]
	FacilityDesign [
		Name SolarPowerPlant
		FacilityDesignId 7
		Partner XXX
		Function "T65,T80"
		ProductionCost "4xElectronicParts,1xFerroconcrete,5xDuralloy"
		LeasePrice 30000
	]
	FacilityDesign [
		Name Factory
		FacilityDesignId 9
		Partner XXX
		Function "T80,T46"
		ProductionCost "2xDuralloy,2xMechanicalParts,5xFerroconcrete"
		LeasePrice 40000
	]
]


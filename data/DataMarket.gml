all [
	marketitem [
		name Hydrocarbons
		id 1
		resType I
		value 1018
		targetquantity 5000
		quantity 5660
		price 899.29
		liquidTemp -80
		gasTemp -30
		nativeElement 0.85
	]
	marketitem [
		name Acids
		id 2
		resType C
		value 1530
		targetquantity 5000
		quantity 5513
		price 1387.62
		liquidTemp -10
		gasTemp 30
		nativeElement 0.9
	]
	marketitem [
		name Water
		id 3
		resType I
		value 1805
		targetquantity 5000
		quantity 5510
		price 1637.92
		liquidTemp 0
		gasTemp 100
		nativeElement 0.4
	]
	marketitem [
		name Fluoride
		id 4
		resType C
		value 2060
		targetquantity 5000
		quantity 5644
		price 1824.94
		liquidTemp -60
		gasTemp -20
		nativeElement 0.9
	]
	marketitem [
		name Chloride
		id 5
		resType C
		value 2080
		targetquantity 5000
		quantity 5510
		price 1887.47
		liquidTemp -60
		gasTemp -20
		nativeElement 0.9
	]
	marketitem [
		name AluminumOre
		id 6
		resType M
		value 2209
		targetquantity 5000
		quantity 5673
		price 1946.93
		nativeElement 0.7
	]
	marketitem [
		name CopperOre
		id 7
		resType M
		value 2740
		targetquantity 5000
		quantity 5699
		price 2403.92
		nativeElement 0.7
	]
	marketitem [
		name LeadOre
		id 8
		resType H
		value 2909
		targetquantity 5000
		quantity 5633
		price 2582.1
		liquidTemp 250
		nativeElement 0.7
	]
	marketitem [
		name Silicates
		id 9
		resType I
		value 3201
		targetquantity 5000
		quantity 5569
		price 2873.94
		nativeElement 0.9
	]
	marketitem [
		name IronOre
		id 10
		resType M
		value 3811
		targetquantity 5000
		quantity 5526
		price 3448.24
		nativeElement 0.9
	]
	marketitem [
		name MagnesiumOre
		id 11
		resType C
		value 4431
		targetquantity 5000
		quantity 5698
		price 3888.2
		nativeElement 0.8
	]
	marketitem [
		name SilverOre
		id 12
		resType M
		value 4808
		targetquantity 5000
		quantity 5660
		price 4247.34
		liquidTemp 410
		nativeElement 0.65
	]
	marketitem [
		name LanthanumOre
		id 13
		resType E
		value 5590
		targetquantity 5000
		quantity 5682
		price 4919.04
		nativeElement 0.25
	]
	marketitem [
		name YtterbiumOre
		id 14
		resType E
		value 5902
		targetquantity 5000
		quantity 5672
		price 5202.74
		nativeElement 0.25
	]
	marketitem [
		name GoldOre
		id 15
		resType H
		value 6840
		targetquantity 5000
		quantity 5612
		price 6094.08
		liquidTemp 455
		nativeElement 0.5
	]
	marketitem [
		name TitaniumOre
		id 16
		resType M
		value 7190
		targetquantity 5000
		quantity 8915
		price 4033.82
		nativeElement 0.8
	]
	marketitem [
		name PlatinumOre
		id 17
		resType M
		value 8609
		targetquantity 5000
		quantity 5565
		price 7734.94
		nativeElement 0.5
	]
	marketitem [
		name UraniumOre
		id 18
		resType R
		value 9103
		targetquantity 5000
		quantity 5628
		price 8087.23
		nativeElement 0.45
	]
	marketitem [
		name GemStones
		id 19
		resType X
		value 6241.67
		targetquantity 5000
		quantity 5738
		price 5438.88
		baseMult 1.9
		nativeElement 0.25
		producedFrom "Carbon,Aluminum"
	]
	marketitem [
		name Carbon
		id 20
		resType A
		value 1200
		targetquantity 5000
		quantity 5556
		price 1079.91
		refined Hydrocarbons
	]
	marketitem [
		name Petroleum
		id 21
		resType A
		value 1440
		targetquantity 5000
		quantity 5228
		price 1377.19
		baseMult 1.2
		refined Hydrocarbons
	]
	marketitem [
		name Lubricants
		id 22
		resType A
		value 1851.49
		targetquantity 5000
		quantity 5332
		price 1736.2
		baseMult 1.4
		producedFrom Hydrocarbons
	]
	marketitem [
		name Fabric
		id 23
		resType A
		value 2982.74
		targetquantity 5000
		quantity 5626
		price 2650.85
		baseMult 1.2
		producedFrom "Lubricants,Carbon"
	]
	marketitem [
		name RocketFuel
		id 24
		resType A
		value 3427.91
		targetquantity 5000
		quantity 4887
		price 3506.71
		baseMult 1.8
		producedFrom Petroleum
	]
	marketitem [
		name Plastic
		id 25
		resType A
		value 4162.63
		targetquantity 5000
		quantity 5562
		price 3742.02
		baseMult 1.7
		producedFrom Lubricants
	]
	marketitem [
		name Rubber
		id 26
		resType A
		value 3428.05
		targetquantity 5000
		quantity 5538
		price 3095.02
		baseMult 1.4
		producedFrom Lubricants
	]
	marketitem [
		name Nitrates
		id 27
		resType A
		value 1870
		targetquantity 5000
		quantity 5502
		price 1699.38
		refined Acids
	]
	marketitem [
		name Phosphates
		id 28
		resType A
		value 1890
		targetquantity 5000
		quantity 5536
		price 1707
		refined Acids
	]
	marketitem [
		name Nitroglycerin
		id 29
		resType A
		value 3285.08
		targetquantity 5000
		quantity 5520
		price 2975.61
		baseMult 1.2
		producedFrom "Phosphates,Nitrates"
	]
	marketitem [
		name Fertilizer
		id 30
		resType A
		value 3285.08
		targetquantity 5000
		quantity 5581
		price 2943.09
		baseMult 1.2
		producedFrom "Phosphates,Nitrates"
	]
	marketitem [
		name Chlorine
		id 31
		resType A
		value 2500
		targetquantity 5000
		quantity 5646
		price 2213.95
		refined Chloride
	]
	marketitem [
		name Fluorine
		id 32
		resType A
		value 2480
		targetquantity 5000
		quantity 5536
		price 2239.88
		refined Fluoride
	]
	marketitem [
		name Aluminum
		id 33
		resType A
		value 2640
		targetquantity 5000
		quantity 5445
		price 2424.23
		refined AluminumOre
	]
	marketitem [
		name Copper
		id 34
		resType A
		value 3240
		targetquantity 5000
		quantity 5354
		price 3025.77
		refined CopperOre
	]
	marketitem [
		name Silver
		id 35
		resType A
		value 5760
		targetquantity 5000
		quantity 5440
		price 5294.11
		refined SilverOre
	]
	marketitem [
		name Gold
		id 36
		resType A
		value 8208
		targetquantity 5000
		quantity 5630
		price 7289.51
		refined GoldOre
	]
	marketitem [
		name Platinum
		id 37
		resType A
		value 10320
		targetquantity 5000
		quantity 5480
		price 9416.05
		refined PlatinumOre
	]
	marketitem [
		name PreciousMetals
		id 38
		resType A
		value 13619.16
		targetquantity 5000
		quantity 5528
		price 12318.34
		baseMult 0.9
		producedFrom "Gold,Silver,Platinum"
	]
	marketitem [
		name Silicon
		id 39
		resType A
		value 3849
		targetquantity 5000
		quantity 5540
		price 3473.82
		refined Silicates
	]
	marketitem [
		name ElectronicParts
		id 40
		resType A
		value 15297.41
		targetquantity 5000
		quantity 5408
		price 14143.31
		producedFrom "Plastic,Carbon,Copper,PreciousMetals,Silicon"
	]
	marketitem [
		name Munitions
		id 41
		resType A
		value 5647.89
		targetquantity 5000
		quantity 5577
		price 5063.55
		baseMult 1.3
		producedFrom Nitroglycerin
	]
	marketitem [
		name Halogen
		id 42
		resType A
		value 4745.12
		targetquantity 5000
		quantity 5424
		price 4374.18
		baseMult 1.3
		producedFrom "Chlorine,Fluorine"
	]
	marketitem [
		name Lead
		id 43
		resType A
		value 3481
		targetquantity 5000
		quantity 5601
		price 3107.47
		refined LeadOre
	]
	marketitem [
		name Iron
		id 44
		resType A
		value 4560
		targetquantity 5000
		quantity 5315
		price 4289.74
		refined IronOre
	]
	marketitem [
		name MechanicalParts
		id 45
		resType A
		value 6214
		targetquantity 5000
		quantity 5073
		price 6124.57
		producedFrom "Lubricants,Iron,Copper"
	]
	marketitem [
		name Steel
		id 46
		resType A
		value 5231.8
		targetquantity 5000
		quantity 5430
		price 4817.49
		producedFrom "Carbon,Iron"
	]
	marketitem [
		name PicocomputerParts
		id 47
		resType A
		value 6601.92
		targetquantity 5000
		quantity 5610
		price 5884.06
		baseMult 1.3
		producedFrom Silicon
	]
	marketitem [
		name ComputerParts
		id 48
		resType A
		value 7109.75
		targetquantity 5000
		quantity 5449
		price 6523.9
		baseMult 1.4
		producedFrom Silicon
	]
	marketitem [
		name Magnesium
		id 49
		resType A
		value 5280
		targetquantity 5000
		quantity 5506
		price 4794.76
		refined MagnesiumOre
	]
	marketitem [
		name FerroConcrete
		id 50
		resType A
		value 6245.72
		targetquantity 5000
		quantity 5199
		price 6006.65
		producedFrom "Iron,Silicates"
	]
	marketitem [
		name Duralloy
		id 51
		resType A
		value 8030.21
		targetquantity 5000
		quantity 5493
		price 7309.49
		baseMult 1.2
		producedFrom "Magnesium,Aluminum"
	]
	marketitem [
		name Lanthanum
		id 52
		resType A
		value 6601
		targetquantity 5000
		quantity 5471
		price 6032.71
		refined LanthanumOre
	]
	marketitem [
		name Ytterbium
		id 53
		resType A
		value 7080
		targetquantity 5000
		quantity 5462
		price 6481.13
		refined YtterbiumOre
	]
	marketitem [
		name RareEarths
		id 54
		resType A
		value 13681.79
		targetquantity 5000
		quantity 5546
		price 12334.82
		baseMult 1.3
		producedFrom "Lanthanum,Ytterbium"
	]
	marketitem [
		name EngineParts
		id 55
		resType A
		value 22137.9
		targetquantity 5000
		quantity 5677
		price 19497.88
		producedFrom "RareEarths,Lubricants,HybridMetals"
	]
	marketitem [
		name SensorParts
		id 56
		resType A
		value 16003.74
		targetquantity 5000
		quantity 5625
		price 14225.54
		producedFrom "Halogen,PreciousMetals,Copper,Iron"
	]
	marketitem [
		name SpareParts
		id 57
		resType A
		value 34272.12
		targetquantity 5000
		quantity 6322
		price 27105.43
		producedFrom "ElectronicParts,MechanicalParts,EngineParts,SensorParts,ComputerParts"
	]
	marketitem [
		name Titanium
		id 58
		resType A
		value 8402
		targetquantity 5000
		quantity 5575
		price 7535.42
		refined TitaniumOre
	]
	marketitem [
		name HybridMetals
		id 59
		resType A
		value 17548.18
		targetquantity 5000
		quantity 5619
		price 15615.03
		baseMult 0.9
		producedFrom "Steel,Aluminum,Titanium,Copper,Iron,PreciousMetals"
	]
	marketitem [
		name Uranium
		id 60
		resType A
		value 10920
		targetquantity 5000
		quantity 5632
		price 9694.59
		refined UraniumOre
	]
	marketitem [
		name Plutonium
		id 61
		resType A
		value 12997.52
		targetquantity 5000
		quantity 5502
		price 11811.63
		baseMult 0.9
		producedFrom Uranium
	]
	marketitem [
		name Junk
		id 62
		resType A
		value 4073
		targetquantity 5000
		quantity 5679
		price 3586.01
		baseMult 1.13
	]
	marketitem [
		name AlienLifeForm
		id 63
		resType X
		value 9320
		targetquantity 3000
		quantity 0
		price 27959.99
		nativeElement 0.1
		producedFrom "M67,M66"
	]
	marketitem [
		name DarkMatter
		id 64
		resType X
		value 11507
		targetquantity 3000
		quantity 0
		price 34520.99
		nativeElement 0.05
	]
	marketitem [
		name AlienRelics
		id 65
		resType A
		value 14704
		targetquantity 3000
		quantity 0
		price 44112
		producedFrom DarkMatter
	]
	marketitem [
		name Fossils
		id 66
		resType X
		value 4803
		targetquantity 3000
		quantity 0
		price 14409
		nativeElement 0.05
	]
	marketitem [
		name AminoAcids
		id 67
		resType X
		value 3016
		targetquantity 3000
		quantity 0
		price 9047.99
		nativeElement 0.1
	]
	marketitem [
		name ToxicWaste
		id 68
		resType A
		value 108
		targetquantity 1000
		quantity 0
		price 323.99
	]
	marketitem [
		name HumanWaste
		id 69
		resType A
		value 205
		targetquantity 1000
		quantity 0
		price 614.99
	]
	marketitem [
		name IndustrialWaste
		id 70
		resType A
		value 411
		targetquantity 1000
		quantity 44
		price 1232.99
	]
	marketitem [
		name Tourists
		id 73
		resType A
		value 1073
		targetquantity 5000
		quantity 5679
		price 944.7
		baseMult 1.13
	]
	marketitem [
		name Travelers
		id 74
		resType A
		value 3073
		targetquantity 5000
		quantity 5700
		price 2695.61
		baseMult 1.13
	]
]


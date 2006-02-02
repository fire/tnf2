graph [
	node [
		gesture ">"
		id 200
		x 379
		y 132
		notes "not_a_node"
		name Stab
		discription "*STAB* (gesture *>*)\n\nThis is not a spell but an attack which can be directed at any individual monster or wizard. Unless protected in that turn by a Shield spell or another spell with the same effect, the being stabbed suffers 1 point of damage. The wizard only has one knife so can only stab with one hand in any turn, although which hand doesn't matter. The stab cannot be reflected by a Magic Mirror or stopped by Dispel Magic (although its Shield effect *could* stop the stab). Clumsy wizards are allowed to stab themselves. Knives cannot be thrown.\n\ndiffuse - Shield,Dispel_Magic,Counter_Spell,Protection\n"
	]
	node [
		gesture P
		id 2
		x 379
		y 132
		name Shield
		priority 32
		discription "*SHIELD* (gesture *P* (standard/classic))\n\n\n\nThis spell protects the subject from all attacks from monsters (that is, creatures created by a summoning spell), from missile spells, and from stabs by wizards. The shield will block any number of such attacks but lasts for only one round. The shield protects the subject on the turn in which it is cast.\n\ndiffuse - Dispel_Magic\n"
	]
	node [
		gesture p
		id 3
		x 141
		y 316
		name Surrender
		notes "not_a_node"
		discription "*SURRENDER* (gestures *p* (standard/classic))\n\nThis is not a spell and consequently cannot be cast at anyone. The wizard making these gestures, irrespective of whether they terminate spells or not, surrenders and the contest is over. If the gestures do complete spells, those spells will be cast as usual - the surrender does not happen until the end of the round. The surrendering wizard is deemed to have lost unless his gestures completed spells which killed all remaining opponents, in which case he loses no points, but does not get any points either.\n\nTwo or more simultaneous surrenders count as a draw, and the victory points are divided between the surviving mages ... rounded down.\n\nIt is a skill for wizards to work their spells so that they never accidentally perform 2 *P* gestures simultaneously. It is too late to surrender on the turn you are killed - if you are killed as you make the surrender gesture, you will still die. However, if you live to the end of the turn, the referee will cure any diseases, poisons etc. in time for your next battle.\n\n"
	]
	node [
		gesture cDPW
		id 4
		x 93
		y 13
		name "Dispel_Magic"
		priority 1
		discription "*DISPEL MAGIC* (gestures *c D P W* (standard/classic))\n\n This spell acts as a combination of Counter Spell and Remove Enchantment, but its effects are universal rather than limited to the subject of the spell. It will stop any spell cast in the same turn from working (apart from another Dispel Magic spell which combines with it for the same result), and will remove all enchantments from all beings before they have effect. In addition, all monsters are destroyed although they can attack that turn. Counter Spells and Magic Mirrors have no effect. The spell will not work on stabs or surrenders. As with a Counter Spell it also acts as a Shield for its subject.\n\n"
	]
	node [
		gesture PSDF
		id 5
		x 299
		y 132
		name "Charm_Person"
		priority 21
		discription "*CHARM PERSON* (gestures *P S D F* (standard/classic))\n\nExcept for cancellation with other enchantments, this spell only affects humans. The subject of the spell submits orders as normal, but the caster of the spell also submits an order for one of the subject's hands. (This is done using the 'DIRECT' order).\n\nThe server uses the order submitted by the caster for the subject's affected hand next turn. If the subject is only so because of a reflection from a Magic Mirror the subject of the mirror assumes the role of caster and decides his opponent's gesture. If the caster does not submit a 'DIRECT' command the following turn, the target will do his or her gestures as normal. If the subject is also the subject of any of Amnesia, Confusion, Charm Monster, Fear, or another Charm Person, then none of the spells work.\n\ndiffuse - Amnesia,Confusion,Charm_Monster,Fear,or another Charm_Person\n"
	]
	node [
		gesture cSWPP
		id 6
		x 115
		y 124
		name "Summon_Fire_Elemental"
		notes "classic_only"
		priority 8
		discription "*SUMMON FIRE ELEMENTAL* (gestures *c W S S W* (standard) / *c S W P P (classic))\n\n This spell creates a fire elemental. Casting this spell will cause a elemental to be created, even if no target is specified.\n\n The elemental will, for that turn and until destroyed, attack everyon who is not resistant to its type (heat or cold), causing three points o damage per turn. The elemental takes three points of damage to be kille but may be destroyed by spells of the opposite type (e.g. Fire Storm Resist Cold or Fireball will kill an ice elemental), and will als neutralize the cancelling spell. Elementals will not attack on the tur they are destroyed by such a spell. An elemental will also be engulfe and destroyed by a storm of its own type but, in such an event, th storm is not neutralized although the elemental still does not attack i that turn.\n\n Two elementals of the opposite type will also destroy each other befor attacking, and two of the same type will join together to form a singl elemental of normal strength. Note that only wizards or monster resistant to the type of elemental, or who are casting a spell which ha the effect of a Shield do not get attacked by the elemental. Casting  Fireball upon yourself when being attacked by an ice elemental is no defence! (Cast it at the elemental...)\n\n"
	]
	node [
		gesture PSFW
		id 7
		x 288
		y 300
		name "Summon_Ogre"
		priority 5
		discription "*SUMMON OGRE* (gestures *P S F W* (standard/classic))\n\n This spell is the same as Summon Goblin but the ogre created inflicts and is destroyed by two points of damage rather than one. The summoning spell cannot be cast at an elemental, and if cast at something which doesn't exist, the spell has no effect.\n\ndiffuse - Charm_Monster,Blindness,Invisibility\ndelay - Confusion,Amnisia,Paralysis,Fear\ncancel - Remove_Enchantment,Dispel_Magic\n"

	]
	node [
		gesture cSWWS
		id 8
		x 84
		y 135
		name "Summon_Ice_Elemental"
		priority 9
		discription "*SUMMON ICE ELEMENTAL* (gestures *c S W W S* (standard/classic))\n\n This spell creates an ice elemental. Casting this spell will cause an elemental to be created, even if no target is specified.\n\n The elemental will, for that turn and until destroyed, attack everyone who is not resistant to its type (heat or cold), causing three points of damage per turn. The elemental takes three points of damage to be killed but may be destroyed by spells of the opposite type (e.g. Fire Storm, Resist Cold or Fireball will kill an ice elemental), and will also neutralize the cancelling spell. Elementals will not attack on the turn they are destroyed by such a spell. An elemental will also be engulfed and destroyed by a storm of its own type but, in such an event, the storm is not neutralized although the elemental still does not attack in that turn.\n\n  Two elementals of the opposite type will also destroy each other before attacking, and two of the same type will join together to form a single elemental of normal strength. Note that only wizards or monsters resistant to the type of elemental, or who are casting a spell which has the effect of a Shield do not get attacked by the elemental. Casting a Fireball upon yourself when being attacked by an ice elemental is no defence! (Cast it at the elemental...)\n\ndiffuse - Fireball,Resist_Cold,Fire_Storm,Fire_Elemental,Blindness,Invisibility\ndelay - Shield,Magic_Mirror,Protection,Counter_Spell,Amnisia,Paralysis\ncancel - Ice_Storm,Remove_Enchantment,Dispel_Magic\n"
	]
	node [
		gesture PWPFSSSD
		id 9
		x 378
		y 342
		name "Finger_Of_Death"
		priority 38
		discription "*FINGER OF DEATH* (gestures *P W P F S S S D* (standard/classic))\n\nKills the subject stone dead. This spell is so powerful that it is unaffected by a Counter Spell, although a Dispel Magic spell cast upon the final gesture will stop it. The usual way to prevent being harmed by this spell is to disrupt it during casting, using, for example, an Anti Spell. If the target is subject to both Raise Dead and Finger of Death, the two spells will cancel each other.\n\ndiffuse - Dispel_Magic,Raise_Dead \n"
	]
	node [
		gesture cWSSW
		id 10
		x 204
		y 138
		name "Summon_Fire_Elemental"
		notes "standard_only"
		priority 8
		discription "*SUMMON FIRE ELEMENTAL* (gestures *c W S S W* (standard) / *c S W P P*(classic))\n\nThis spell creates a fire elemental. Casting this spell will cause an elemental to be created, even if no target is specified.\nThe elemental will, for that turn and until destroyed, attack everyone who is not resistant to its type (heat or cold), causing three points of damage per turn. The elemental takes three points of damage to be killed but may be destroyed by spells of the opposite type (e.g. Fire Storm, Resist Cold or Fireball will kill an ice elemental), and will also neutralize the cancelling spell. Elementals will not attack on the turn they are destroyed by such a spell. An elemental will also be engulfed and destroyed by a storm of its own type but, in such an event, the storm is not neutralized although the elemental still does not attack in that turn.\nTwo elementals of the opposite type will also destroy each other before attacking, and two of the same type will join together to form a single elemental of normal strength. Note that only wizards or monsters resistant to the type of elemental, or who are casting a spell which has the effect of a Shield do not get attacked by the elemental. Casting a Fireball upon yourself when being attacked by an ice elemental is no defence! (Cast it at the elemental...)\n\n diffuse - Resist_Heat,Ice_Storm,Ice_Elemental,Blindness,Invisibility\ndelay - Shield,Magic_Mirror,Protection,Counter_Spell,Amnisia,Paralysis\ncanceled - Fire_Storm,Remove_Enchantment,Dispel_Magic\n"
	]
	node [
		gesture PWPWWc
		id 11
		x 68
		y 342
		name Haste
		priority 11
		discription "*HASTE* (gestures *P W P W W c* (standard/classic))\n\nFor the next 3 turns, the subject (but not his monsters if a wizard) makes an extra set of gestures due to being speeded up. This takes effect immediately, so that immediately after the turn in which the Haste is cast the Hastened wizard gets a 'free' set of orders. Non-hastened wizards and monsters can see everything the hastened individual is doing.\n\nNote that any spells cast by non-hastened wizards do not expire before the end of the extra hastened turn. This means that (for example) a Shield cast by a non-hastened wizard would also protect him against a stab from a hastened wizard in the hastened wizard's free turn.\n\n"
	]
	node [
		gesture cw
		id 12
		x 207
		y 336
		name "Magic_Mirror"
		priority 3
		discription "*MAGIC MIRROR* (gestures *c w* (standard/classic))\n\nSpells cast on a subject protected by this spell are reflected back upon the caster of that spell. When a spell is reflected the very internals of the spell is also affected, so that not only do the caster become the target, but the subject of the mirror also assumes the role of caster of the reflected spell.\n\nThe Magic Mirror protects only during the turn in which it was cast The protection includes spells like Magic Missile and Lightning Bolt but does not include attacks by monsters or stabs from wizards. In case of multiple mirrors in a melee, a Magic Mirror will not reflect any spells originating from the wizard that the mirror protects, because the magic of the spell then already will be present inside the mirror.\n\nThe mirror is countered totally if either a Counter Spell or Dispel Magic is cast on the subject in the same turn as the mirror. The mirror has no effect on spells which affect more than one person (such as Fire Storm). Two mirrors cast at one subject simultaneously combine to form a single mirror.\n\ndiffuse -Counter Spell,Dispel_Magic\n"
	]
	node [
		gesture SD
		id 13
		x 241
		y 244
		name "Magic_Missile"
		priority 33
		discription "*MAGIC MISSILE* (gestures *S D* (standard/classic))\n\nThis spell creates a material object of hard substance which is hurled towards the subject of the spell and causes him one point of damage. The spell is thwarted by a Shield in addition to the usual Counter Spell, Dispel Magic and Magic Mirror (the latter causing it to hit whoever cast it instead).\n\ndiffuse - Shield,Dispel_Magic,Counter_Spell,Protection,Magic_Mirror\n"

	]
	node [
		gesture DFFDD
		id 14
		x 221
		y 230
		name "Lightning_Bolt"
		priority 36
		discription "*LIGHTNING BOLT* (gestures *D F F D D* or *W D D c* (standard/classic))\n\nThe subject of this spell is hit by a bolt of lightning and sustains five points of damage. Resistance to heat or cold is irrelevant. There are two gesture combinations for the spell, but the shorter one may be used only once per battle by any wizard. The longer one may be used without restriction. A Shield spell offers no defence.\n\ndiffuse - Magic_Mirror,Dispel_Magic\n"
	]
	node [
		gesture SFW
		id 15
		x 347
		y 88
		name "Summon_Goblin"
		priority 4
		discription "*SUMMON GOBLIN* (gestures *S F W* (standard/classic))\n\n This spell creates a goblin under the control of the target of the spell (or the target's controller, if the target is a monster). The goblin can attack immediately and its victim will be opponent of its controller. It does one point of damage to its victim per turn and is destroyed after one point of damage is inflicted upon it. The summoning spell cannot be cast at an elemental, and if cast at something which doesn't exist, the spell has no effect.\n\ndiffuse - Charm_Monster,Blindness,Invisibility\ndelay - Confusion,Amnisia,Paralysis,Fear\ncancel - Remove_Enchantment,Dispel_Magic\n"
	]
	node [
		gesture DFPW
		id 16
		x 137
		y 304
		name "Cure_Heavy_Wounds"
		priority 25
	 	discription "*CURE HEAVY WOUNDS* (gestures *D F P W* (standard/classic))\n\nThis spell is similar to Cure Light Wounds in effect but two points of damage are cured. Note that only one point is cured if only one point of damage has been sustained and the spell has no effect if the subject is completely undamaged. This spell will also cure any diseases the subject might have at the time.\n\n"
	]
	node [
		gesture SPFP
		id 17
		x 314
		y 84
		name "Anti_Spell"
		priority 26
		discription "*ANTI SPELL* (gestures *S P F P* (standard/classic))\n\nOn the turn following the casting of this spell, the subject cannot include any gestures made on or before this turn in a spell sequence and must restart a new spell from the beginning of that spell sequence. The spell does not affect spells which are cast on the same turn nor does it affect monsters.\n\n"
	]
	node [
		gesture DFW
		id 18
		x 182
		y 18
		name "Cure_Light_Wounds"
		priority 24
		discription "*CURE LIGHT WOUNDS* (gestures *D F W* (standard/classic))\n\nIf the subject has received damage then he is cured by one point as if that point had not been inflicted. Thus, for example, if a wizard had five hit points left and was hit simultaneously by a Cure Light Wounds and a Lightning Bolt he would finish that turn on one hit point rather than zero (or six if there had been no Lightning Bolt). The effect is not removed by a Dispel Magic or Remove Enchantment.\n\n"
	]
	node [
		gesture SPFPSDW
		id 19
		x 89
		y 376
		name Permanency
		priority 29
		discription "*PERMANENCY* (gestures *S P F P S D W* (standard/classic))\n\nThis spell only works if cast upon a wizard. Any spell he completes (i.e. completes the gestures of and casts), provided it is on one of the next 3 turns, and which falls into the category of 'Enchantments' (except Anti Spell, Disease, Poison, or Time Stop) can have its effect made permanent.\n\nNote that the person who has his spell made permanent does not necessarily have to make himself the subject of the spell. A Permanency spell cannot increase the duration of another Permanency, nor a spell which is being banked by Delay Effect.\n\nNote that an attempt to make permanent an enchantment spell that is countered (or fails for any other reason) still uses up the Permanency.\n\n*AMNESIA*,*CONFUSION*,*PARALYSIS*,*FEAR*,*PROTECTION*,*CHARM PERSON*,*CHARM MONSTER*,*RESIST HEAT*,*RESIST COLD*,*BLINDNESS*,*INVISIBILITY*,*HASTE*\n"
	]
	node [
		gesture DPP
		id 20
		x 179
		y 341
		name Amnesia
		priority 17
		discription "*AMNESIA* (gestures *D P P* (standard/classic))\n\nIf the subject of this spell is a wizard, next turn he will repeat identically the gestures he made in the current turn, including stabs.\n\n If the subject of the spell is a monster or elemental, it forgets to attack in that round. If the subject is simultaneously the subject of any of Confusion, Charm Person, Charm Monster, Fear or another Amnesia then none of the spells work.\n\ndiffuse -Confusion,Charm Person,Charm Monster,Fear,another Amnesia\n"
	]
	node [
		gesture SPPc
		id 21
		x 120
		y 239
		name "Time_Stop"
		priority 12
		discription "*TIME STOP* (gestures *S P P c* (standard/classic) or *S P P F D* (standard only))\n\nThe subject of this spell immediately takes an extra turn, on which no one can see or know about unless they are harmed. All non affected beings have no resistance to any form of attack, e.g. a wizard halfway through the duration of a Protection spell can be harmed by a monster which has had its time stopped. Time stopped monsters attack whoever their controller instructs, and time stopped elementals affect everyone, resistance to heat or cold being immaterial in that turn.\n\n"
	]
	node [
		gesture DSF
		id 22
		x 375
		y 96
		name Confusion
		priority 19
		discription "*CONFUSION* (gestures *D S F* (standard/classic))\n\nIf the subject of this spell is a wizard, next turn he submits orders as usual. However, during resolution of the round, the server will randomly determine which hand is affected, and will randomly replace the ordered gesture with one of the six possible spell gestures.\n\nIf the subject of the spell is a monster, it attacks at random that turn. If the subject is also the subject of any of Amnesia, Charm Person, Charm Monster, Fear, or another Confusion, then none of the spells work.\n\ndiffuse -Amnesia,Charm_Person,Charm Monster,Fear,another Confusion\n"
	]
	node [
		gesture SPPFD
		id 23
		x 146
		y 42
		name "Time_Stop"
		notes "standard_only"
		priority 12
		discription ""
	]
	node [
		gesture DSFFFc
		id 24
		x 246
		y 49
		name Disease
		priority 22
		discription "*DISEASE* (gestures *D S F F F c* (standard/classic))\n\nThe subject of this spell immediately contracts a deadly (non contagious) disease which will kill him at the end of 6 turns counting from the one upon which the spell is cast. The malady is cured by Remove Enchantment or Cure Heavy Wounds or Dispel Magic in the meantime.\n\ndiffuse-Magic_Mirror,Counter-Spell\ncanceled-Remove_Enchantment,Dispel_Magic\ncanceled-Remove_Enchantment,Cure_Heavy_Wounds,Dispel_Magic\n"

	]
	node [
		gesture SSFP
		id 25
		x 53
		y 213
		name "Resist_Cold"
		priority 15
		discription "*RESIST COLD* (gestures *S S F P* (standard/classic))\n\n The subject of this spell becomes totally resistant to all forms of cold attack (Ice Storm and ice elementals)  . Only Dispel Magic or Remove Enchantment will terminate this resistance once started (although a Counter Spell will prevent it from working if cast at the subject at the same time as this spell). A Resist Cold cast directly on a ice elemental will destroy it before it can attack that turn, but there is no effect on fire elementals.\n\n"

	]
	node [
		gesture DWFFd
		id 26
		x 337
		y 316
		name Blindness
		priority 27
		discription "*BLINDNESS* (gestures *D W F F d* (standard/classic))\n\nFor the next 3 turns not including the one in which the spell was cast, the subject is unable to see. If he is a wizard, he cannot tell what his opponent's gestures are, although he will be informed of any which affect him (e.g. summons spells, Magic Missile etc. cast at him) but not Counter Spells to his own attacks. Indeed he will not know if his own spells work unless they also affect him (e.g. a Fire Storm when he isn't resistant to fire.) He can control his monsters (he can say 'Attack that cursed Zarquon'), but he cannot direct his spells at things that he cannot see.\n\nBlinded monsters are instantly destroyed and cannot attack in that turn.\n\ndiffuse - Counter_Spell,Magic_Mirror,Dispel_Magic\ncancel - Remove_Enchantment,Dispel_Magic\n"
	]
	node [
		gesture SWD
		id 27
		x 267
		y 77
		name Fear
		priority 18
		discription "*FEAR* (gestures *S W D* (standard/classic))\n\nIf the subject of this spell is a wizard, then on the turn following the casting of this spell, the subject cannot perform a *C*, *D*, *F* or *S* gesture. If the subject of the spell is a monster (excluding elementals, which are unaffected), it will be too afraid to attack in that round. If the subject is also the subject of Amnesia, Confusion, Charm Person, Charm Monster, or another Fear, then none of the spells work.\n\ndiffuse - Amnesia,Confusion,Charm Person,Charm Monster,another Fear\n"
	]
	node [
		gesture DWSSSP
		id 28
		x 182
		y 354
		name "Delay_Effect"
		priority 30
		discription "*DELAY EFFECT *(gestures *D W S S S P* (standard/classic))\n\nThis spell only works if cast upon a wizard. One of the next spells he completes, provided it is on one of the next 3 turns, is 'banked' until needed, i.e. it fails to work until its caster desires. The spell to be banked is selected using the *DELAY* command. Note that spells banked are those cast by the subject not those cast at him. The target of the spell is banked along with the spell itself (i.e. the complete effect of the spell, including target, is what gets saved). Remember that *P* is a Shield spell, and surrender is not a spell. A wizard may only have one spell banked at any one time.\n\n"
	]
	node [
		gesture SWWc
		id 29
		x 368
		y 146
		name "Fire_Storm"
		priority 39
		discription "*FIRE STORM* (gestures *S W W c* (standard/classic))\n\nEverything not resistant to heat sustains 5 points of damage that turn. The spell cancels wholly, causing no damage, with either an Ice Storm or an ice elemental. It will destroy but not be destroyed by a fire elemental. Two Fire Storms act as one.\n\ndiffuse - Resist_Fire,Ice_Storm,Ice_Elemental,Dispel_Magic,Counter_Spell\n"
	]
	node [
		gesture DWWFWc
		id 30
		x 366
		y 292
		name "Raise_Dead"
		priority 10
		discription "*RAISE DEAD* (gestures *D W W F W c* (standard/classic))\n\n The subject of this spell might be any dead human or a dead monster. When the spell is cast, the dead is brought back to life and all damage is cured. All enchantments are also removed (as per the spell) so any diseases or poisons will be neutralized and all other enchantments removed. The subject will be able to act normally immediately after the spell is cast. On the turn a monster is raised it may attack, and if only one Raise Dead spell was cast at that monster, the caster may TARGET the monster. (If more than one Raise Dead was cast at a monster in the same turn, the monster will be confused about who to obey, and so cannot be given a TARGET.) Wizards may begin gesturing on the turn following their return from the dead. This is the only spell which affects the dead. It therefore cannot be stopped by a Counter Spell cast at the dead being. A Dispel Magic spell will prevent its effect, since Dispel Magic affects all spells no matter what their subject. If the spell is cast on a live individual, the effect is that of a cure light wounds recovering five points of damage, or as many as have been sustained if less than five. Note that any diseases the live subject might have are not cured. If the target is subject to both Raise Dead and Finger of Death, the two spells will cancel each other.\n\n"
	]
	node [
		gesture WDDc
		id 31
		x 326
		y 299
		name "Lightning_Bolt"
		notes "only_once_per_battle"
		priority 36
		discription "*LIGHTNING BOLT* (gestures *D F F D D* or *W D D c* (standard/classic))\n\nThe subject of this spell is hit by a bolt of lightning and sustains five points of damage. Resistance to heat or cold is irrelevant. There are two gesture combinations for the spell, but the shorter one may be used only once per battle by any wizard. The longer one may be used without restriction. A Shield spell offers no defence.\n\ndiffuse - Magic_Mirror,Dispel_Magic\n"
	]
	node [
		gesture DWWFWD
		id 32
		x 41
		y 239
		name Poison
		priority 23
		discription "*POISON* (gestures *D W W F W D* (standard/classic))\n\nThe subject of this spell is inflected with a poisoned which will kill him at the end of 6 turns counting from the one upon which the spell is cast. The malady is cured by Remove Enchantment or Dispel Magic in the meantime.\n\ndiffuse-Magic_Mirror,Counter-Spell\ncanceled-Remove_Enchantment,Dispel_Magic\n"
	]
	node [
		gesture WFP
		id 33
		x 26
		y 59
		name "Cause_Light_Wounds"
		priority 34
		discription "*CAUSE LIGHT WOUNDS* (gestures *W F P* (standard/classic))\n\nThe subject of this spell is inflicted with two points of damage. Resistance to heat or cold offers no defence. A simultaneous Cure Light Wounds will serve only to reduce the damage to 1 point. A Shield has no effect.\n\n"

	]
	node [
		gesture FFF
		id 34
		x 247
		y 347
		name Paralysis
		priority 16
		discription "*PARALYSIS* (gestures *F F F* (standard/classic))\n\nIf the subject of the spell is a wizard, then on the following turn the caster selects one of the wizard's hands and on the that turn that hand is paralyzed into the position it was in the turn before. If the wizard already had a paralyzed hand, it must be the same hand which is paralyzed again. Certain gestures remain the same but if the hand being paralyzed is performing a *C*, *S* or *W* it is instead paralyzed into *F*, *D* or *P* respectively, otherwise it will remain in the original position (this allows repeated stabs).\n\nA favourite ploy is to continually paralyze a hand (*F F F F F F* etc.) into a non *P* gesture and then set a monster on the subject so that he has to use his other hand to protect himself, but then has no defence against other magical attacks.\n\nIf the subject of the spell is a monster or an elemental it simply does not attack in the turn in which the spell was cast.\n\nIf the subject of the spell is also the subject of any of Amnesia, Confusion, Charm Person, Charm Monster, Fear, or another Paralysis, Paralysis fails to work.\n\nIf the caster does not choose a hand of the target to be paralyzed (using the *PARALYZE* order), a random hand will be paralyzed.\n\ndiffuse - Amnesia,Confusion,Charm Person,Charm Monster,Fear,another Paralysis\n"
	]
	node [
		gesture WFPSFW
		id 35
		x 69
		y 198
		name "Summon_Giant"
		priority 7
		discription "*SUMMON GIANT* (gestures *W F P S F W* (standard/classic))\n\n This spell is the same as Summon Goblin but the giant created inflicts and is destroyed by four points of damage rather than one. The summoning spell cannot be cast at an elemental, and if cast at something which doesn't exist, the spell has no effect.\n\ndiffuse - Charm_Monster,Blindness,Invisibility\ndelay - Confusion,Amnisia,Paralysis,Fear\ncancel - Remove_Enchantment,Dispel_Magic\n"

	]
	node [
		gesture FPSFW
		id 36
		x 248
		y 97
		name "Summon_Troll"
		priority 6
		discription "*SUMMON TROLL* (gestures *F P S F W* (standard/classic))\n\n This spell is the same as Summon Goblin but the troll created inflicts and is destroyed by three points of damage rather than one. The summoning spell cannot be cast at an elemental, and if cast at something which doesn't exist, the spell has no effect.\n\ndiffuse - Charm_Monster,Blindness,Invisibility\ndelay - Confusion,Amnisia,Paralysis,Fear\ncancel - Remove_Enchantment,Dispel_Magic\n"

	]
	node [
		gesture WPFD
		id 37
		x 142
		y 148
		name "Cause_Heavy_Wounds"
		priority 35
		discription "*CAUSE HEAVY WOUNDS* (gestures *W P F D* (standard/classic))\n\nThe subject of this spell is inflicted with three points of damage. Resistance to heat or cold offers no defence. A simultaneous Cure Heavy Wounds will serve only to reduce the damage to 1 point. A Shield has no effect.\n\n"

	]
	node [
		gesture FSSDD
		id 38
		x 363
		y 58
		name Fireball
		priority 37
		discription "*FIREBALL* (gestures *F S S D D* (standard/classic))\n\nThe subject of this spell is hit by a ball of fire and sustains five points of damage unless he is resistant to fire. If at the same time an Ice Storm prevails, the subject of the Fireball is instead not harmed by either spell, although the storm will affect others as normal. If directed at an ice elemental, the fireball will destroy it before it can attack. Fire Elementals are not affected by Fireballs.\n\ndiffuse - Ice_Storm,Resist_Heat,Magic_mirror,Counter_Spell\n"
	]
	node [
		gesture WPP
		id 39
		x 182
		y 127
		name "Counter_Spell"
		discription "*COUNTER SPELL* (gestures *W P P* or *W W S* (standard/classic))\n\n Any other spell cast upon the subject in the same turn has no effect. In the case of blanket spells, which affect more than one person, the subject of the Counter Spell alone is protected. For example, a Fire Storm spell could kill off a monster but not if a Counter Spell were cast on the monster in the same turn. Everyone else would be affected as usual by the Fire Storm unless they had their own protection.\n\n The Counter Spell will cancel all the spells cast at the subject for that turn (including Remove Enchantment and Magic Mirror) except Dispel Magic and Finger Of Death. It will combine with another spell of its own type for the same effect as if it were alone. The Counter Spell will also act as a Shield for its Target.\n\n"
	]
	node [
		gesture WSSc
		id 40
		x 77
		y 73
		name "Ice_Storm"
		priority 40
		discription "*ICE STORM* (gestures *W S S c* (standard/classic))\n\nEverything not resistant to cold sustains 5 points of damage that turn. The spell cancels wholly, causing no damage, with either a Fire Storm or a fire elemental, and will cancel locally with a Fireball. It will destroy but not be destroyed by an ice elemental. Two Ice Storms act as one.\n\ndiffuse - Resist_Cold,Fire_Storm,Fire_Elemental,Fireball self\n"
	]
	node [
		gesture WWFP
		id 41
		x 100
		y 336
		name "Resist_Heat"
		priority 14
		discription "*RESIST HEAT *(gestures *W W F P* (standard/classic))\n\nThe subject of this spell becomes totally resistant to all forms of heat attack (Fireball, Fire Storm and fire elementals). Only Dispel Magic or Remove Enchantment will terminate this resistance once started (although a Counter Spell will prevent it from working if cast at the subject at the same time as this spell). A Resist Heat cast directly on a fire elemental will destroy it before it can attack that turn, but there is no effect on ice elementals.\n\n"
	]
	node [
		gesture PDWP
		id 42
		x 69
		y 28
		name "Remove_Enchantment"
		priority 31
discription "*REMOVE ENCHANTMENT* (gestures *P D W P* (standard/classic))\n\nTerminates the effects of all Enchantment Spells that have been cast on the subject including those that are cast on the subject at the same time as the Remove Enchantment.\n\nEffects which have already passed are not reversed. For example, the victim of a Blindness spell would not be able to see what their opponent's gestures were on the same turn that the Blindness is removed. Note that all enchantments are removed and the caster may not pick and choose. Remove Enchantment also destroys any monster upon which it is cast, although the monster can attack in that turn.\n\nWizards suffer no adverse effects from this spell, aside from the removal of their enchantments.\n\n"
	]
	node [
		gesture WWP
		id 43
		x 385
		y 295
		name Protection
		priority 13
		discription "*PROTECTION* (gestures *W W P* (standard/classic))\n\nFor this turn and the following 3 turns the subject of this spell is protected as if using a Shield spell, thus leaving both hands free. Concurrent Shield spells offer no further protection and compound Protection spells merely overlap offering no extra cover.\n\n"
	]
	node [
		gesture PPws
		id 44
		x 169
		y 303
		name Invisibility
		priority 28
		discription "*INVISIBILITY* (gestures *P P w s* (standard/classic))\n\nFor the next 3 turns not including the one in which the spell was cast, the subject of the spell becomes invisible to his opponent and all monsters. Any spells he creates that do not affect anyone that is visible, and his gestures, cannot be seen by his opponents.\n\nSpells targeted at invisible mages will miss. An invisible mage cannot be attacked by monsters, although monsters can be directed at him in case he becomes visible prematurely. Wizards can still stab and direct spells at him, with the same hope.\n\nAny monster made invisible is destroyed due to the unstable nature of such magically created creatures, and doesn't get to attack that turn.\n\n"
	]
	node [
		gesture WWS
		id 45
		x 73
		y 196
		name "Counter_Spell"
		priority 2
		discription "*COUNTER SPELL* (gestures *W P P* or *W W S* (standard/classic))\n\nAny other spell cast upon the subject in the same turn has no effect. In the case of blanket spells, which affect more than one person, the subject of the Counter Spell alone is protected. For example, a Fire Storm spell could kill off a monster but not if a Counter Spell were cast on the monster in the same turn. Everyone else would be affected as usual by the Fire Storm unless they had their own protection.\n\nThe Counter Spell will cancel all the spells cast at the subject for that turn (including Remove Enchantment and Magic Mirror) except Dispel Magic and Finger Of Death. It will combine with another spell of its own type for the same effect as if it were alone. The Counter Spell will also act as a Shield for its Target.\n\n"

	]
	node [
		gesture PSDD
		id 46
		x 307
		y 226
		name "Charm_Monster"
		priority 20
		discription "*CHARM MONSTER* (gestures *P S D D* (standard/classic))\n\nExcept for cancellation with other enchantments, this spell only affects monsters (excluding elementals). Control of the monster is transferred to the caster of the spell (or retained by him) as of this turn, i.e. the monster will attack whosoever its new controller dictates from that turn onwards including that turn. Further charms are, of course, possible, transferring as before. If the subject of the charm is also the subject of any of: Amnesia, Confusion, Charm Person, Fear, or another Charm Monster, then none of the spells work.\n\ndiffuse - Amnesia,Confusion,Charm Person,Fear,another Charm_Monster\n"
	]
	node [
		gesture WW
		id 55
		x 32
		y 192
		color red
		notes descisionpoint name descision1
	]
	node [
		gesture WP
		id 51
		x 26
		y 86
		color red
		notes descisionpoint name descision2
	]
	node [
		gesture SW
		id 45
		x 28
		y 131
		color red
		notes descisionpoint name descision3
	]
	node [
		gesture SPP
		id 41
		x 28
		y 22
		color red
		notes descisionpoint name descision4
	]
	node [
		gesture SPF
		id 38
		x 33
		y 382
		color red
		notes descisionpoint name descision5
	]
	node [
		gesture PWP
		id 33
		x 149
		y 497
		color red
		notes descisionpoint name descision6
	]
	node [
		gesture PS
		id 29
		x 75
		y 473
		color red
		notes descisionpoint name descision7
	]
	node [
		gesture DWWFW
		id 19
		x 131
		y 282
		color red
		notes descisionpoint name descision8
	]
	node [
		gesture DW
		id 16
		x 30
		y 272
		color red
		notes descisionpoint name descision9
	]
	node [
		gesture DS
		id 13
		x 29
		y 326
		color red
		notes descisionpoint name descision10
	]
	node [
		gesture DF
		id 8
		x 305
		y 466
		color red
		notes descisionpoint name descision11
	]
	node [
		gesture cSW
		id 3
		x 38
		y 434
		color red
		notes descisionpoint name descision12
	]
	link [
		fromnode 3
		tonode 6
		color green
	]
	link [
		fromnode 3
		tonode 8
		color green
	]
	link [
		fromnode 8
		tonode 14
		color green
	]
	link [
		fromnode 8
		tonode 16
		color green
	]
	link [
		fromnode 8
		tonode 18
		color green
	]
	link [
		fromnode 13
		tonode 22
		color green
	]
	link [
		fromnode 13
		tonode 24
		color green
	]
	link [
		fromnode 16
		tonode 26
		color green
	]
	link [
		fromnode 16
		tonode 28
		color green
	]
	link [
		fromnode 19
		tonode 32
		color green
	]
	link [
		fromnode 19
		tonode 30
		color green
	]
	link [
		fromnode 16
		tonode 19
		color green
	]
	link [
		fromnode 29
		tonode 46
		color green
	]
	link [
		fromnode 29
		tonode 5
		color green
	]
	link [
		fromnode 29
		tonode 7
		color green
	]
	link [
		fromnode 33
		tonode 9
		color green
	]
	link [
		fromnode 33
		tonode 11
		color green
	]
	link [
		fromnode 38
		tonode 17
		color green
	]
	link [
		fromnode 38
		tonode 19
		color green
	]
	link [
		fromnode 41
		tonode 21
		color green
	]
	link [
		fromnode 41
		tonode 23
		color green
	]
	link [
		fromnode 45
		tonode 27
		color green
	]
	link [
		fromnode 45
		tonode 29
		color green
	]
	link [
		fromnode 51
		tonode 37
		color green
	]
	link [
		fromnode 51
		tonode 39
		color green
	]
	link [
		fromnode 55
		tonode 41
		color green
	]
	link [
		fromnode 55
		tonode 43
		color green
	]
	link [
		fromnode 55
		tonode 45
		color green
	]
	link [
		fromnode 4
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 5
		tonode 13
		color black
		notes free
	]
	link [
		fromnode 5
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 6
		tonode 39
		color green
		notes choice
	]
	link [
		fromnode 6
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 7
		tonode 15
		color green
		notes choice
	]
	link [
		fromnode 35
		tonode 7
		color green
		notes choice
	]
	link [
		fromnode 36
		tonode 7
		color green
		notes choice
	]
	link [
		fromnode 7
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 8
		tonode 45
		color green
		notes choice
	]
	link [
		fromnode 9
		tonode 13
		color green
		notes choice
	]
	link [
		fromnode 9
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 10
		tonode 12
		color black
		notes free
	]
	link [
		fromnode 11
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 19
		tonode 13
		color black
		notes free
	]
	link [
		fromnode 38
		tonode 13
		color black
		notes free
	]
	link [
		fromnode 46
		tonode 13
		color black
		notes free
	]
	link [
		fromnode 35
		tonode 15
		color green
		notes choice
	]
	link [
		fromnode 36
		tonode 15
		color green
		notes choice
	]
	link [
		fromnode 16
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 19
		tonode 17
		color black
		notes free
	]
	link [
		fromnode 17
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 19
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 20
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 21
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 24
		tonode 22
		color black
		notes free
	]
	link [
		fromnode 23
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 24
		tonode 34
		color black
		notes free
	]
	link [
		fromnode 25
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 28
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 35
		tonode 33
		color black
		notes free
	]
	link [
		fromnode 41
		tonode 33
		color green
		notes choice
	]
	link [
		fromnode 33
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 35
		tonode 36
		color green
		notes choice
	]
	link [
		fromnode 35
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 36
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 37
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 39
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 41
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 42
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 43
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 44
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 46
		tonode 2
		color black
		notes free
	]
	link [
		fromnode 2
		tonode 2
		color green
		notes choice
	]
	link [
		fromnode 4
		tonode 9
		color blue
		notes step2
	]
	link [
		fromnode 4
		tonode 11
		color blue
		notes step2
	]
	link [
		fromnode 5
		tonode 14
		color blue
		notes step2
	]
	link [
		fromnode 5
		tonode 16
		color blue
		notes step2
	]
	link [
		fromnode 5
		tonode 18
		color blue
		notes step2
	]
	link [
		fromnode 6
		tonode 44
		color blue
		notes step2
	]
	link [
		fromnode 8
		tonode 40
		color blue
		notes step2
	]
	link [
		fromnode 16
		tonode 9
		color blue
		notes step2
	]
	link [
		fromnode 10
		tonode 27
		color blue
		notes step2
	]
	link [
		fromnode 10
		tonode 29
		color blue
		notes step2
	]
	link [
		fromnode 16
		tonode 11
		color blue
		notes step2
	]
	link [
		fromnode 22
		tonode 15
		color blue
		notes step2
	]
	link [
		fromnode 28
		tonode 17
		color blue
		notes step2
	]
	link [
		fromnode 17
		tonode 36
		color blue
		notes step2
	]
	link [
		fromnode 19
		tonode 26
		color blue
		notes step2
	]
	link [
		fromnode 28
		tonode 19
		color blue
		notes step2
	]
	link [
		fromnode 19
		tonode 28
		color blue
		notes step2
	]
	link [
		fromnode 19
		tonode 30
		color blue
		notes step2
	]
	link [
		fromnode 19
		tonode 32
		color blue
		notes step2
	]
	link [
		fromnode 20
		tonode 44
		color blue
		notes step2
	]
	link [
		fromnode 28
		tonode 21
		color blue
		notes step2
	]
	link [
		fromnode 28
		tonode 23
		color blue
		notes step2
	]
	link [
		fromnode 25
		tonode 36
		color blue
		notes step2
	]
	link [
		fromnode 27
		tonode 31
		color blue
		notes step2
	]
	link [
		fromnode 32
		tonode 31
		color blue
		notes step2
	]
	link [
		fromnode 33
		tonode 36
		color blue
		notes step2
	]
	link [
		fromnode 41
		tonode 36
		color blue
		notes step2
	]
	link [
		fromnode 42
		tonode 37
		color blue
		notes step2
	]
	link [
		fromnode 43
		tonode 37
		color blue
		notes step2
	]
	link [
		fromnode 42
		tonode 39
		color blue
		notes step2
	]
	link [
		fromnode 43
		tonode 39
		color blue
		notes step2
	]
	link [
		fromnode 39
		tonode 44
		color blue
		notes step2
	]
	link [
		fromnode 44
		tonode 40
		color blue
		notes step2
	]
	link [
		fromnode 45
		tonode 40
		color blue
		notes step2
	]
	link [
		fromnode 41
		tonode 35
		color red
		notes step3
	]
]


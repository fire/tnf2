text [
notes "launch $PLANETNAME"
notes "landing $PLANETNAME"
notes "move $FROMPLANETNAME $TOPLANETNAME $FUEL"
notes "purchasecargo $PLANETNAME $PRODUCTNAME $NUM $EACHCOST $TOTALCOST"
notes "sellcargo $PLANETNAME $PRODUCTNAME $NUM $EACHCOST $TOTALCOST"
notes "training $SKILL $FEE $COSTVP"
notes "vp $POINTS"
notes "personalbounty $BOUNTY"
notes "shipbounty $PAYOFF"
notes "sellitem $ITEM $COST"
notes "buyitem $ITEM $COST"
notes "race"
notes "race and name"
notes "Terranname"
notes "geckoname"
notes "robotname"

notes "PlanetEventtax $AMOUNT $TOTALTAX"
notes "PlanetEventfine $AMOUNT"
notes "PlanetEventrobbed $ITEM"
notes "PlanetEventrobbednone"
notes "PlanetEventrobbedfail $ITEM"
notes "PlanetEventsteal $ITEM"
notes "PlanetEventstealfail $ITEM $COST"
notes "dontneedit $ITEM"
notes "PlanetEventbattlelose $RACE $DAMAGE"
notes "PlanetEventbattlewin $RACE $DAMAGE"

notes "INC PlanetEventtaxreduced $AMOUNT $FINALTAX $TOTALTAX"
notes "INC PlanetEventfinereduced $AMOUNT $FINALAMOUNT"
notes "INC PlanetEventgbattlewin $RACE $DAMAGE $GUN $ARMOR"
notes "INC PlanetEventgbattlelose $RACE $DAMAGE $GUN $ARMOR"
notes "INC PlanetEventwbattlewin $RACE $DAMAGE $WEAP $ARMOR"
notes "INC PlanetEventwbattlelose $RACE $DAMAGE $WEAP $ARMOR"
notes "afterbattlecost $FINE"
notes "INC PlanetEventrumer $RUMER"
notes "INC PlanetEventrandom $FEE (ie drink,movie,sports)"

notes "SpaceEventcustomspass"
notes "SpaceEventcustomsmiss"
notes "SpaceEventdamage $SYSTEM"
notes "SpaceEventcustomsfail"
notes "customsseisure $NUMcargo"
notes "customsseisurefine $FINE"
notes "customsseisurefineavoid $FINE"
notes "customsseisurenofine"
notes "spoilsreceived $CARGO $NUM"
notes "spoilstaken $CARGO $NUM"
notes "SpaceBattlewin $OPPONENT $OPSHIP" 
notes "SpaceBattlelose $OPPONENT $OPSHIP"
notes "SpaceBattledamage $SYSTEM"
notes "SpaceBattledestroyedescape $PLANET $COST"
notes "SpaceBattledestroyeddie"
notes "INC SpaceEventrandom (ie supernova, blackhole, aliencontact)"

arrivedeepspace "You know you've arrived when you see nothing.  Welcome to deep space."

leavedeepspace "After drifting around for a while, you lock in the coordinates and your ship tears off."

leaveplanet "You lock in the coordinates and your ship tears off into space.  You see $FROMPLANETNAME disappear in the distance behind you."
leaveplanet "You lock in the coordinates and your ship tears off."
leaveplanet "Using your finely tuned button pushing skills, you activate your engines and your ship once again streaks through space.  In moments you can nolonger see $FROMPLANETNAME."

arriveplanet "Then you see $TOPLANETNAME appear ahead and you settle into orbit" 
arriveplanet "Then all is blured, until finally $TOPLANETNAME appears ahead and you settle into orbit."
arriveplanet "Silently your ship approaches $TOPLANETNAME.  Once in orbit, space hobos immediately approach to wash your windows.  You activate your electromagnitic shock defense and they quickly back away."


spacebattlewin "Suddenly a ship swops in on you, and a battle ensues.  Lasars fire and ships race about in a silent ballet of life and death.  In the end your ship escapes the better off."
spoilstaken "A terriable grinding sound is heard as your helpless ship is torn open and $NUM $CARGO is taken from your hull.  At least your still alive."
spoilsreceived "With your opponent dissabled you take a moment to unload $NUM $CARGO from his ship and escape."
SpaceBattlelose "Suddenly a ship swops in on you, and a battle ensues.  Lasars fire and ships race about in a silent ballet of life and death.  Finally you surrender to save your ship from destruction."

Spacebattledamage "Your $SYSTEM has burn damage from the battle."
Spacebattledamage "You notice that your $SYSTEM has some deep lasar holes."
SpaceBattledestroyedescape "The battle was just too much for your ship.  It slowly breaks apart and you sadly enter the escape pod to abandon ship.  You drift in space for days until you finally reach the planet $PLANET where you buy a replacement ship for $COST."


customsseisurenofine "You see them separate the cargo into two halves before loading it into their ship.  Then you receive a warning and receipt for half of the cargo taken."

spaceeventcustomsfail "A loud tapping on your hull confirms your most dreaded fear has come true.  Customs officials board your vessel and search it.  In only minutes they find illegal cargo on your ship."

customsseisure "They immediately confenscate $NUMcargo units of cargo."

customsseisurefine "Next they promptly fine you $FINE."

customsseisurefineavoid "Next they promptly fine you.  But by exploiting a loophole in the law you are able to reduec your fine to $FINE."

SpaceEventcustomsmiss "Out of no where a customs ship appears and demands to board you.  They search your ship but didn't find your illegal cargo.  You thank them as they leave."

SpaceEventdamage "Your ship makes a strange <noise> sound.  You check your instruments to find that your $SYSTEM is damaged."

SpaceEventcustomspass "Your ship is boarded and search for illegal cargo.  After an extensive search they find nothing and leave."

SpaceBattledamage "Your $SYSTEM is damaged."

SpaceBattledestroyedescape "Your ship emplodes like a tin can as you drift away in your escape pod.  Days latter you arrive on $PLANET where you buy a starter ship for $COST."

SpaceBattledestroyeddie "The damage to your ship is too sever.  You shutter to hear the sound of metal shattering and air escaping.  In a moment its all over.  Better luck next time."


afterbattlecost "Before the dust can settle security forces arrive.  They asses the damage and assign you <100>% which comes out to $FINE."

dontneedit "Then you notice that you don't need another $ITEM, so you drop it into the nearest garbage receptical"

dontneedit "After carefully considering keeping the second $ITEM, you finally decide that your's is better anyway.  So you give it to some poor kid on the street"

dontneedit "You through away your old $ITEM and keep the nice new one."

dontneedit "While the thought of keeping a collection of $ITEMs around is tempting you realise that your ship just doesn't have the room, so you trade it in on dinner and a movie."

PlanetEventrobbednone "You're walking around in a crowd windowshopping when suddenly you feel as though you've been robbed.  You check your pockets only to discover that you don't have anything to steal.  Hey thats why you were window shopping in the first place"

PlanetEventrobbedfail "You are taking in the local scenery when a clumsy theif grabs your $ITEM.  You turn around and grab him.  He quickly drops your $ITEM and runs.  You laugh to yourself.  You know you would have done a better job."

PlanetEventstealfail "You enter a large crowd and try to grab something valuable.  You see someone who is carring too much stuff so you grab his $ITEM.  He turns and grabs you.  A crowd gathers as he yells and you bolt away and hide."

PlanetEventsteal "You see an oppertunity to grab something good.  You victem is totally engrossed in his shopping so you quietly grab his $ITEM and sneak away."

PlanetEventrobbed "As you walked through a crowed area, you felt as though you were being watch.  Then you were shoved from behind.  You turn around to see who had pushed you to see nothing.  Then you notice your $ITEM was stolen."

launch "A quick click of the buckle and punch of the red button and your ship screams into the sky.  Moments later you silently glide into orbit above $PLANETNAME." 
launch "You tie down everything and settel back into your chair ready for launch.  A quick roar of the engines and the sky melts away into the blackness of space.  You are in orbit around $PLANETNAME again." 
launch "Your ship rattels as you lift off and settle into orbit around $PLANETNAME."

landing "You decend rappidly into the atmosphere and land on $PLANETNAME.  After some paperwork you are ready for business again."
landing "Your ship steaks though the sky of $PLANETNAME as you approach the docking facility.  You get landing permision and manover your craft into docking bay <1000>."


sellcargo "You enter the market place looking to sell $PRODUCTNAME.  When you find a merchant willing to buy your $NUM $PRODUCTNAME at the cost of $EACHCOST each, you agree and have it off loaded your ship.  The total earnings paid you was $TOTALCOST."

purchasecargo "You enter the market place looking for $PRODUCTNAME. You find a merchant willing to sell you $NUM at the cost of $EACHCOST each, you agree and have it loaded on your ship.  The total cost paid was $TOTALCOST."

purchasecargo "You enter the market place looking to buy $NUM $PRODUCTNAME. You find a merchant, and after haggeling you agree on the price of $EACHCOST each.  Your total bill was $TOTALCOST, and the cargo is loaded on your ship."

PlanetEventbattlelose "Its been far too long since you took a break, so you head into a local establishment for some R and R before you even get to your seat, you see two feirce looking $RACEs headed your way.  Not looking for a beating you turn and head away only to be ambush from behind by another $RACE.  Fists fly and you bounce off the furniture.  Moments later you awaken in the alley.  You've taken $DAMAGE."

PlanetEventbattlewin "Its been far too long since you took a break, so you head into a local establishment for some R and R before you even get to your seat, you see two feirce looking $RACEs headed your way.  Not looking for a beating you turn and head away, suddenly one jumps you.  You quickly duck and throw him off and through a window.  The other charges, but a quick reverse elbow and he crumples to the ground.  You've taken $DAMAGE."

PlanetEventtax "A tax is assess in the amount of $AMOUNT for a total tax due of $TOTALTAX."

PlanetEventtax "With out even knowing it you tax account has just increase $AMOUNT for a total tax due of $TOTALTAX"

PlanetEventfine "As you are about a goverment tax agent corners you and with the help of his computer, you discover some obscure tax code that requires you to pay $AMOUNT.  You pay it quickly in cash and you make yourself scares to avoid further taxes."

PlanetEventfine "A government officer approaches you and begins to explain to you that you have just broken a very serious and obscure local law, as the penelty is described to you you accidently drop some cash.  Suddenly the officer is distracted, you drop a little more and the officer walk away in a daze.  You seem to be missing $AMOUNT, but your sure that was a good investment."

PlanetEventfine "You stop by a shop to buy some supplies and accidently spill some <compound> on a <race and name>.  You quickly appologize and offer to pay for cleaning.  You both agree on $AMOUNT to be fair."

personalbounty "Shortly later a team of police come in and collect your beaten opponent.  It appears that his behavior had earned him a bounty of $BOUNTY, which they pay you.  Suddenly the pain seems worth it."
shipbounty "You recored the results of the battle in you log.  A notice appears stating that you have earned a bounty of $PAYOFF."

vp "You have earned an additional $POINTS Victory Points!"

training "You enroll in the local merchant college to improve you skill at $SKILL.  You pay the admision fee of $FEE and are escorted into the learning chamber.  I moment of bright flashing lights, a slight head acke and you are ready to leave.  You have used $COSTVP Victory Points."
training "You sign up for a course to imporove you $SKILL.  You get the best deal you can at $FEE which is not bad considering it included dinner and a movie.  Your professor is a <race>, but his accent makes it really hard to learn.  After you long day you return feeling a little more skilled.  You have used $COSTVP."

buyitem "You enter the market place looking for a $ITEM.  You find a <race and name> who owns a shop going out of business today.  You get a great deal at $COST. "
buyitem "You check you computer for a local store that has a $ITEM.  You order it and have it dilvered right to you for only $COST."
buyitem "
buyitem "Acquire some items are easier than other.  You spend hours looking for a $ITEM, until finally you find a used on in a childrens toy store.  You don't ask any questions, just pay the $COST and return to your ship."

sellitem "You pop down to the pawn shop and sell you $ITEM for $COST."
sellitem "Since you don't need your $ITEM, you look to find a place to unload it.  You find a merchant dealing with used equipment and he agrees to pay $COST fot it."

compound "degenerant IV"
compound "Spam"
compound "hextor nuckelplex"
compound "Qilaritan brandy"
compound "sulfuric acid"
compound "hydroxial ions"
compound "dehydrated Specmo droppings"
compound "vermilized space puffies"
compound "partialy radiated lethar"
compound "raw cobat"


race Gecko
race Terran
race Mechbot
race Brogians
race Pill
race Rhesians
race Zephyr

"race and name" "Gecko named <geckname>"
"race and name" "Terran named <terranname>"
"race and name" "Mechbot named <robotname>"
mechbotname "<robotname>"

Terranname "Bob"
Terranname "Jeff"
Terranname "Aaron"
Terranname "Derek"
Terranname "Kevin"
Terranname "Donna"
Terranname "Lisa"
Terranname "Rose"
Terranname "Rosey"
Terranname "Sarah"
Terranname "Ryan"
Terranname "Brian"
Terranname "Sam"
Terranname "Sammy"
Terranname "Tom"
Terranname "Joe"
Terranname "Henery"
Terranname "Michael"
Terranname "Mike"
Terranname "Fred"
Terranname "Edward"
Terranname "Ed"
Terranname "Laura"
Terranname "Kyle"
Terranname "Ricky"
Terranname "Dave"
Terranname "Willy"
Terranname "Bob"
Terranname "Robert"
Terranname "Beth"
Terranname "Rey"
Terranname "Ann"
Terranname "Roger"
Terranname "Phil"
Terranname "Ron"
Terranname "John"
Terranname "Doug"

robotname "<AL><ALL><DS><NL><ALL><ALL>"
robotname "<AL><NL><ALL><ALL>"
DS "-"
DS "-"
DS ""
ALL "<DS><AL><AL>"
ALL "<AL><AL>"
ALL "<AL>"
NL "0"
NL "1"
NL "2"
NL "3"
NL "4"
NL "5"
NL "6"
NL "7"
NL "8"
NL "9"
AL "A"
AL "B"
AL "C"
AL "D"
AL "E"
AL "F"
AL "G"
AL "H"
AL "I"
AL "J"
AL "K"
AL "L"
AL "M"
AL "N"
AL "O"
AL "P"
AL "Q"
AL "R"
AL "S"
AL "T"
AL "U"
AL "V"
AL "W"
AL "X"
AL "Y"
AL "Z"

geckname "<geckstart><geckmid><geckend>"
geckstart "La"
geckstart "Lura"
geckstart "Luri"
geckstart "Lathi"
geckstart "Dori"
geckstart "Deral"
geckstart "Draki"
geckstart "Dra"
geckstart "Degi"
geckstart "Ra"
geckstart "Blatha"
geckstart "Brothi"
geckstart "Broti"
geckstart "Ble"
geckstart "Bliti"
geckstart "Lar"
geckstart "Pla"
geckstart "Lur"
geckmid ""
geckmid "cie"
geckmid "cupi"
geckmid "ssu"
geckmid "ssuwu"
geckmid "ssawu"
geckmid "ssio"
geckmid "sso"
geckmid "ssali"
geckmid "sseri"
geckend "ss"
geckend "sss"

"10" "<NL>"
"100" "<NL><NL>"
"1000" "<NL><NL><NL>"
"1000000" "<1000><1000>"

noise "crinkle"
noise "crunch"
noise "sprong"
noise "ziss"
noise "flush"
noise "sputter"
noise "boink"
noise "rumble"
noise "girgle"

]



/* notes

INC buyprof
INC buyloss
 	stole extra purchase
	delevery less than invoice
	some cheap substitues

INC sellprof
INC sellloss
 	claim extra cargo
 	paided for less than delieverd
 	some stock bad and returned as junk

INC spacescaneventfindcargo $CARGO $NUM
INC spacescaneventfindsalvage $REWARD
INC spacescaneventnothing

INC peneltyfine
INC peneltytradeban $DAYS
INC peneltytravban $DAYS
INC peneltyjail $DAYS

INC snoopfail
INC snoopfind

INC hospital $FEE
INC implanets
INC gambeling
INC stockmarketbuy
INC stockmarketsell
INC borrowloan
INC payloan


HTML NOTES
_B_p_E_ = <p>
_B_b_E_ = <b>
_B_center_E_= <center>
_B_applet heigth=100 width=100 code=aj.smug.PlanetView archive=aj.jar_E_
_B_parameter name=size value=200_E_
_B_parameter name=neb value=1_E_
_B_/applet_E_

_BEGIN_html_END_
_BEGIN_body_END_
_BEGIN_/body_END_
_BEGIN_/html_END_

if html then sub(_BEGIN_,"<") sub(_END_,">")
if !html then clearbetweeninclusive(_BEGIN_,_END)


SKILL LIST
steal (pickpocket)
robbed (defense)
smuggle (hide cargo)
law (find loophols to fines)
chr (make up excuses)
pilot
gunner
keyboarding
melefight
gunfight


RISKS
	wrongplace (increase bad)
	wrongtime (increase bad)
	party (movie,drink,watch sports)
	fight (addition to normal fights)
	steal (pick someones pocket)
	swindle (bad books )

POWER RATINGS
(ship=75% skill=25%)
skil 0 ship 7 = 75  = .49
skil 7 ship 0 = 25  = .21
skil 3 ship 0 = 3/7*.25= .06
skil 0 ship 3 = 3/7*.75= .21
combat systems (weap,shie,comp,drive,sens) (hull)
ship rating = weap+shie+comp+manu+sens -hull -drive
worst =-14 (-7 hull -7drive +7comp = -7)
best = 35
all = 35-14=21

**(SHIP POWER RATING = weap+shie+comp+manuver+sens-hull-drive

	SKILLS  =  pilotcheck+cannoncheck+radarcheck+compcheck+techcheck+. . .



attribs =
PER perception
STR strength
AGI agility
MEC mechanical
TOUG toughness


skill increase cost = +1 20  2=40 3=80 4=160 5=320 6=640 7=1280



xenography implants
Agility = 53000 QPs
Strength 47000 QPs
Toughness = 35000 QPs
Mechanics = 41000 QPs
Perception = 60000 QPs 



species


terrans +3mec
brogians +3str +1 tou -1mec
gecko +2agi +2str  (regenerate)
mechbots -1str +3tou +1mec
pill (+3 var)
rhesians +3agi -1tou +1per
zephyr +1agi -1str +3per



Brogians names 
Sample names include Ekdimek, Dur-yk, Gimkhum, Yg'og-khor, Nikmikmak, Nikgumkhek, Khan-eg'khan, Doorkhun, Door-er'mok, Gimkhum, Deer'eak, Ugh'erghnol, Khonergh-khum, Nikgumkhek, Ekdimek, Mok'khir, Ag-nok, Kher-nil, Khunkhorg, Ogdir, Nolkharg'khang, Nelkhim'dar, Khung'daar, Khunkhorg, Yg'kim, Demdeer, Khon'dom, Dor-nekkhirg, Nukdoorfuk, Duur'khur-dim, Kemmok, Khek-khar'argh, Khen-neuk, Mok'nul'khorg, Nyk'khan, and Khar-kem. 

geckonames= terran names
pill = terran names

rhesians names
Sample names include Lukutiru, Pomeno, Momepulu, Lumoto, Komanalo, Pari, Yomoma, Soperola, Reyasa, Pineseyu, Norepo, Rerinomi, Keto, Kapo, Roru, Nolisi, Loyupo, Kaki, Pato, Ruse, Nimosoni, Lokeko, Rure, Loke, Mopoyo, Riruroya, Polike, Ketu, Nitusoki, Noriyite, Kurino, Somosire, Nonee, Tupasoru, Nisite, and Nuneya. 

zephyr names
Sample names include Lukutiru, Pomeno, Momepulu, Lumoto, Komanalo, Pari, Yomoma, Soperola, Reyasa, Pineseyu, Norepo, Rerinomi, Keto, Kapo, Roru, Nolisi, Loyupo, Kaki, Pato, Ruse, Nimosoni, Lokeko, Rure, Loke, Mopoyo, Riruroya, Polike, Ketu, Nitusoki, Noriyite, Kurino, Somosire, Nonee, Tupasoru, Nisite, and Nuneya. 


*/




tx [
	room [
		id 1
		name "room 1 name"
		desc "room 1 desc"
		display[ 
			height 5
			width 5
			shape box
			texture cave
			texture castle
			texture house
			color [ red 5 green 132 blue 72 ]
			seed 1832 
		]
		item [itemname chair itemloc "east"]
		item [itemname table itemloc "center"]
		item [itemname lockeddoor itemloc "east"]
		move [dir e leadto 2 barrier lockeddoor]
	]
	item [
		id 1
		name "item 1 name"
		desc "item 1 desc"
		vaule 50
		use [
			command "crush"
			targetname "item 2 name"
			destroy "item 2 name"
			desc "You crush the item 2 name with your item 1"
		]
		use [
			command open
			target lockeddoor
			replace "unlockeddoor"
			desc "you unlock the door with our item 1 name"
		]
	]

]

Usage

java -cp robots.jar tracefile


Functionality

Displays arena in grids.
Displays robot.
Displays robot damage as bar next to bot
Display heading as arrow
Displays speed as arrow length
Displays scan arc.  Red for miss, green for hit.  Show hit range as arc.
Displays cannon shot target
Displays actual explosion in three hit zones
Displays position history and forcast


Operation modes of GUI

AREAN
  Displays entire arena.  Scales to any screen size.

BATTLE
  Displays smallest screen that includes all robots from full arena to very small.

PERSONAL
  Displays area around one selected robot.  Gives close up view of targeting and explosions.


FrameRate
  Changes the speed that the battle progresses.  0 = stop, 100 = max.  Starts at 50.  This lets you slow down
the progression to see step by step what happenes.

ArenaTime
  Changes/Displays the current time in the arena.  You can slide the bar back and forth to change the time.


//=============================================================================
// target.c                            Richard Rognlie <rrognlie@vtsu.prc.com>
//=============================================================================
//
// 1) Do nothing...  

#include "robots.h"

main()
{
	printlog("Target at %d,%d",loc_x(),loc_y());
	while (1)
		drive(0,0);
}

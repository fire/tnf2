//=============================================================================
// tracker.c                            Richard Rognlie <rrognlie@vtsu.prc.com>
//=============================================================================
//
// 1) Move around the arena in a box pattern.  Full speed.  staying
//    about 2000 units inside from the wall.
// 2) Scan for a target, and shoot it.  If you had seen a target, and
//    have lost sight, back up the scan a bit and continue scanning.

#include "robots.h"

#define BORDER 2000

/* Current position */
static int cx=0;
static int cy=0;

// Shoot at a target if its in range (<= 7000 units) *and* its far
// enough away that we will only be slightly damaged (>200 units) by the
// resulting explosion.
inline shoot(int dir,int range)
{
    if (range <= 7000 && range > 200) {
		printlog("cannon(%d,%d)",dir,range);
		cannon(dir,range);
	}
}

main()
{
    int sdir=0;     /* current scan direction */
    int dir=0;      /* current movement direction */
    int range;      /* range to opponent */
    int hadfix=0;   /* did I have a fix on my opponent from the last scan? */
			
    drive(dir,100);  /* start moving right away.  Do not *ever* sit still! */
    
    while (1) {
	int tdir=dir;    /* save current direction */
	cx = loc_x();
	cy = loc_y();
	
	/* do we need to change direction?  (e.g.  are we approaching a wall?) */
	if (cx > 10000-BORDER)
	    if (cy < 10000-BORDER)
		tdir = 90;              /* approaching east wall */
	    else
		tdir = 180;             /* approaching northeast corner */
	else if (cx < BORDER)
	    if (cy < BORDER)
		tdir = 0;               /* approaching southwest corner */
	    else
		tdir = 270;             /* approaching west wall */
	else if (cy > 10000-BORDER)
	    tdir = 180;                 /* approaching north wall */
	else if (cy < BORDER)
	    tdir = 0;                   /* approaching south wall */
	
	/* if speed() == 0,    restart the drive unit... */
	/* if dir != tdir,     we need to change direction... */
	if (!speed() || dir != tdir)
	    drive(dir=tdir,100);
	
	cx = loc_x();
	cy = loc_y();
	if ((range=scan(sdir,10))) {   /* scan for a target... */
	    shoot(sdir,range);         /*   got one.  shoot it! */
	    hadfix=1;                  /*   remember we saw a target */
	}
	else if (hadfix) {             /*   did we lose a target? */
	    sdir += 40;                /*        back up the scan */
	    hadfix=0;                  /*        forget we had a target */
	}
	else
	    sdir -= 20;                 /*   increment the scan */
    }
}

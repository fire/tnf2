#include "robots.h"
#define max(x,y) ((x)>(y)?(x):(y))
#define min(x,y) ((x)<(y)?(x):(y))
int scandir = 0;
int scandist = 0;
int delaytracking = 0;//false;

int
find ()
{
  scandir = atan2 (5000 - loc_y (), 5000 - loc_x ());
  int found = 0;		//false;
  int clock = 1;
  int attemp = 1;
  while (!found) {
    scandist = scan (scandir, 10);
    if (scandist > 0)
      found = true;
    else {
      scandir += 10 * attemp * clock;
      clock *= -1;
      attemp++;
    }
  }
  if (delaytracking)
    while (time () < 1000)
      drive (scandir, 0);
  return 0;
}

int lastfound = 1;
int driftclock = 1;
int searchclock = 1;
int lastwid = 10;
int attemp = 1;
int driftrecord = 0;
int clockrecord = 0;
float focus = .6;
float dim = 2;
int allowdrift = 1;
int lastscantime=0;

void
track ()
{
  int scanAge=min(100,time()-lastscantime);//only used for minscan and thats only used when already found and were sharping the focus
  int minscan = max (0, (int) (scanAge * 180.0 / 3.14159 / scandist));
//printlog("minscan = %d\n",minscan);
  if (lastfound) {
    lastwid = (int) max (minscan, lastwid * focus);
    if (allowdrift)
      scandir += driftclock * (lastwid > 1);	//if width=1 don't drift
    scandist = scan (scandir, lastwid);
    if (scandist != 0) {
      lastscantime=time();
      lastfound = 1;
      attemp = 1;
    }
    else {
      lastfound = 0;
      attemp = 1;
    }
  }
  else {
    lastwid=max(lastwid,1);
    scandir += searchclock * lastwid * attemp;
    if (attemp % 2 == 0) {
      int newwid = (int) min (10, (lastwid * dim));
      //move scandir so no overlap and no gap on second
      scandir = scandir + searchclock * (newwid - lastwid) / 2;
      lastwid = newwid;
    }

    scandist = scan (scandir, lastwid);
    if (scandist != 0) {
      lastscantime=time();
      lastfound = 1;
      driftclock = searchclock;
    }
    else {
      //scandir+=lastwid*attemp*searchclock;
      searchclock *= -1;
      driftclock *= -1;
      attemp++;
    }
  }
}

main ()
{
  //int cantime=time();
  find ();			//scan 10 secs
  //drive(scandir,50);
  while (1) {
    //      if (cantime<time()){ 
    //              //shoot   
    //              cannon(scandir,scandist);
    //              cantime=time()+200;
    //j     }
    //      else
    track ();
  }
}

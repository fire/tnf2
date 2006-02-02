//**************************************
//BigTarget8
//An argument for simplicity.
//**************************************

#include "robots.h"

//**************************************

//Function Declarations
void Acquire (int &, int &, int &, int &, int &, int &);
void GetPoint (int &, int &, int &, int &, int &, int &, int &, int &, int &);
void CalcTarg (int &, int &, int &, int &, int &, int &, int &, int &);
void Move (int &, int &, int &);
void Reacquire (int &, int &, int &, int &, int &, int &);
void Shotgun (int &, int &, int &, int &);
void Rifle (int &, int &, int &, int &);

//**************************************

int
main ()
{
//"Global" variables
  int targDir = 0;
  int lastDir = 0;
  int range = 0;
  int x0, x1, y0, y1, t0, t1;
  int j = 1;
  int lastShot = 0;
  int res = 10;

//BotControl
  x0 = x1 = y0 = y1 = t0 = t1 = 0;
  targDir = atan2 (5000 - loc_y (), 5000 - loc_x ());
  drive (targDir, 100);
  while (1) {
    Acquire (targDir, range, res, x1, y1, t1);
    lastDir = targDir;
    Shotgun (targDir, range, res, lastShot);
    GetPoint (targDir, range, res, x0, x1, y0, y1, t0, t1);
    while (range) {
      while (range) {
	if (range > 50 && range < 7000) {
	  CalcTarg (targDir, range, x0, x1, y0, y1, t0, t1);
	  if (targDir == lastDir)
	    Rifle (targDir, range, res, lastShot);
	  else
	    Shotgun (targDir, range, res, lastShot);
	}			//end if(range > 50)
	Move (targDir, range, j);
	GetPoint (targDir, range, res, x0, x1, y0, y1, t0, t1);
      }				//end while(range)2
      Reacquire (targDir, range, res, x1, y1, t1);
    }				//end while(range)1
  }				//end while(1)
}				//end main()

//**************************************

void
Acquire (int &targDir, int &range, int &res, int &x1, int &y1, int &t1)
{
  int i;
  do {
    range = scan (targDir, res);
    t1 = (time () / 100);
    if (range) {
      x1 = (int)(loc_x () + range * cos (targDir));
      y1 = (int)(loc_y () + range * sin (targDir));
    }				//end if(range)
    else {
      for (i = 2 * res; !range; i += 2 * res) {
	range = scan (targDir + i, res);
	t1 = (time () / 100);
	if (range) {
	  targDir += i;
	  x1 = (int)(loc_x () + range * cos (targDir));
	  y1 = (int)(loc_y () + range * sin (targDir));
	}			//end if(range)
	else {
	  range = scan (targDir - i, res);
	  t1 = (time () / 100);
	  if (range) {
	    targDir -= i;
	    x1 = (int)(loc_x () + range * cos (targDir));
	    y1 = (int)(loc_y () + range * sin (targDir));
	  }			//end if(range)
	}			//end else
      }				//end for
    }				//end else
    res = res / 2;
  }
  while (res > ((3500 / range) + 1));
  res = (3500 / range) + 1;
}				//end Acquire()

//**************************************

void
GetPoint (int &targDir, int &range, int &res, int &x0, int &x1,
	  int &y0, int &y1, int &t0, int &t1)
{
  x0 = x1;
  y0 = y1;
  t0 = t1;
  range = scan (targDir, res);
  t1 = (time () / 100);
  if (range) {
    x1 = (int)(loc_x () + range * cos (targDir));
    y1 = (int)(loc_y () + range * sin (targDir));
    res = (3500 / range) + 1;
  }				//end if(range)
}				//end GetPoint()

//**************************************

void
CalcTarg (int &targDir, int &range, int &x0, int &x1,
	  int &y0, int &y1, int &t0, int &t1)
{
  int targT = range / 1000;
  int targX = ((x1 - x0) / (t1 - t0)) * ((time () / 100) + targT - t1) + x1;
  int targY = ((y1 - y0) / (t1 - t0)) * ((time () / 100) + targT - t1) + y1;
  int myX = loc_x ();
  int myY = loc_y ();
  targDir = atan2 (targY - myY, targX - myX);
  int tRSquared =
    ((targY - myY) * (targY - myY)) + ((targX - myX) * (targX - myX));
  range = sqrt (tRSquared);
}				//end CalcTarg()

//**************************************

void
Move (int &targDir, int &range, int &j)
{
  int curSpeed = speed ();
  int myX = loc_x ();
  int myY = loc_y ();
  if (range >= 7000 || range <= 1000)
    drive (targDir, 100);
  else if (myX > 1000 && myX < 9000 && myY > 1000 && myY < 9000) {
    if (curSpeed >= 70 || curSpeed <= -70) {
      if (j % 2)
	drive (targDir, 100);
      else
	drive (targDir, -100);
      j++;
    }				//end if(speed() > 75)
  }				//end if(myX..myY)
  else
    drive (atan2 (5000 - myY, 5000 - myX), 100);
}				//end Move()

//**************************************

void
Reacquire (int &targDir, int &range, int &res, int &x1, int &y1, int &t1)
{
  for (int i = 2 * res; i <= 8 * res; i += 2 * res) {
    range = scan (targDir + i, res);
    t1 = (time () / 100);
    if (range) {
      targDir += i;
      x1 = (int)(loc_x () + range * cos (targDir));
      y1 = (int)(loc_y () + range * sin (targDir));
      i = 10 * res;
    }				//end if(range)
    else {
      range = scan (targDir - i, res);
      if (range) {
	targDir -= i;
	x1 = loc_x () + range * cos (targDir);
	y1 = loc_y () + range * sin (targDir);
	i = 10 * res;
      }
    }				//end else
  }				//end for
  if (!range) {
    res = 10;
    targDir += 180;
    Acquire (targDir, range, res, x1, y1, t1);
  }				//end if(!range)
}				//end Reacquire()

//**************************************

void
Shotgun (int &targDir, int &range, int &res, int &lastShot)
{
  if (time () - lastShot >= range / 1000) {
    lastShot = time ();
    cannon (targDir, range);
    cannon (targDir + res, range);
    cannon (targDir - res, range);
  }				//end if(time())
}				//end Shotgun()

//**************************************

void
Rifle (int &targDir, int &range, int &res, int &lastShot)
{
  if (time () - lastShot >= range / 1000) {
    lastShot = time ();
    cannon (targDir, range);
    cannon (targDir + (res / 2) + 1, range);
    cannon (targDir - (res / 2) + 1, range);
  }				//end if(time())
}				//end Rifle()

//**************************************

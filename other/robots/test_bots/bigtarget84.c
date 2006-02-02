//**************************************
//BigTarget8.4
//An argument for simplicity.
//Uses 2 points for targeting and simple
//oscillation for avoidance.
//**************************************

#include "robots.h"

//**************************************

//Function Declarations
void Acquire (int &, int &, int &, int &, int &, int &);
void GetPoint (int &, int &, int &, int &, int &, int &, int &, int &, int &);
void CalcTarg (int &, int &, int &, int &, int &, int &, int &, int &);
void Move (int &, int &, int &, int &);
void Reacquire (int &, int &, int &, int &, int &, int &);
void Shotgun (int &, int &, int &);
void Rifle (int &, int &, int &);

//**************************************

int
main ()
{
//"Global" variables
  int scanDir = 0;
  int targDir = 0;
  int lastDir = 0;
  int range = 0;
  int x0, x1, y0, y1, t0, t1;
  int j = 1;
  int lastShot = 0;
  int res = 10;
  int center = 0;

//BotControl
  x0 = x1 = y0 = y1 = t0 = t1 = 0;
  scanDir = atan2 (5000 - loc_y (), 5000 - loc_x ());
  drive (scanDir, 100);
  Acquire (scanDir, range, res, x1, y1, t1);
  Shotgun (scanDir, range, res);
  GetPoint (scanDir, range, res, x0, x1, y0, y1, t0, t1);
  while (1) {
    while (range) {
      lastDir = targDir;
      if (range > 50 && range < 7000) {
	if (time () - lastShot >= range / 1000) {
	  lastShot = time ();
	  CalcTarg (targDir, range, x0, x1, y0, y1, t0, t1);
	  if (abs (scanDir - targDir) <= res)
	    Rifle (targDir, range, res);
	  else
	    Shotgun (targDir, range, res);
	}			//end if(time())
      }				//end if(range > 50)
      Move (scanDir, range, j, center);
      GetPoint (scanDir, range, res, x0, x1, y0, y1, t0, t1);
    }				//end while(range)
    Reacquire (scanDir, range, res, x1, y1, t1);
  }				//end while(1)
}				//end main()

//**************************************

void
Acquire (int &scanDir, int &range, int &res, int &x1, int &y1, int &t1)
{
  int i, myX, myY;
  do {
    myX = loc_x ();
    myY = loc_y ();
    t1 = time () / 100;
    range = scan (scanDir, res);
    if (range) {
      x1 = (int)(myX + range * cos (scanDir));
      y1 = (int)(myY + range * sin (scanDir));
    }				//end if(range)
    else {
      for (i = 2 * res; !range; i += 2 * res) {
	myX = loc_x ();
	myY = loc_y ();
	t1 = time () / 100;
	range = scan (scanDir + i, res);
	if (range) {
	  scanDir += i;
	  x1 = (int)(myX + range * cos (scanDir));
	  y1 = (int)(myY + range * sin (scanDir));
	}			//end if(range)
	else {
	  myX = loc_x ();
	  myY = loc_y ();
	  t1 = time () / 100;
	  range = scan (scanDir - i, res);
	  if (range) {
	    scanDir -= i;
	    x1 = (int)(myX + range * cos (scanDir));
	    y1 = (int)(myY + range * sin (scanDir));
	  }			//end if(range)
	}			//end else
      }				//end for
    }				//end else
    res = res / 2;
  }
  while (res > ((3000 / range) + 1));
  res = (3000 / range) + 1;
}				//end Acquire()

//**************************************

void
GetPoint (int &scanDir, int &range, int &res,
	  int &x0, int &x1, int &y0, int &y1, int &t0, int &t1)
{
  x0 = x1;
  y0 = y1;
  t0 = t1;
  int myX, myY;
  myX = loc_x ();
  myY = loc_y ();
  t1 = time () / 100;
  range = scan (scanDir, res);
  if (range) {
    x1 = (int)(myX + range * cos (scanDir));
    y1 = (int)(myY + range * sin (scanDir));
    res = (3000 / range) + 1;
  }				//end if(range)
}				//end GetPoint()

//**************************************

void
Reacquire (int &scanDir, int &range, int &res, int &x1, int &y1, int &t1)
{
  int myX, myY;
  for (int i = 2 * res; i <= 5 * res && i <= 10; i += 2 * res) {
    myX = loc_x ();
    myY = loc_y ();
    t1 = time () / 100;
    range = scan (scanDir + i, res);
    if (range) {
      scanDir += i;
      x1 = (int)(myX + range * cos (scanDir));
      y1 = (int)(myY + range * sin (scanDir));
      res = (3000 / range) + 1;
      i = 10 * res;
    }				//end if(range)
    else {
      myX = loc_x ();
      myY = loc_y ();
      t1 = time () / 100;
      range = scan (scanDir - i, res);
      if (range) {
	scanDir -= i;
	x1 = (int)(myX + range * cos (scanDir));
	y1 = (int)(myY + range * sin (scanDir));
	res = (3000 / range) + 1;
	i = 10 * res;
      }				//end if(range)
    }				//end else
  }				//end for
  if (!range) {
    res = 10;
    Acquire (scanDir, range, res, x1, y1, t1);
  }				//end if(!range)
}				//end Reacquire()

//**************************************

void
CalcTarg (int &targDir, int &range, int &x0,
	  int &x1, int &y0, int &y1, int &t0, int &t1)
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
Move (int &scanDir, int &range, int &j, int &center)
{
  int curSpeed = speed ();
  int myX = loc_x ();
  int myY = loc_y ();
  if (range >= 7000) {
    drive (scanDir, 100);
    center = 0;
  }				//end if(range > 7000)
  else if (myX > 1000 && myX < 9000 && myY > 1000 && myY < 9000) {
    center = 0;
    if (curSpeed >= 70 || curSpeed <= -70) {
      if (j % 4 == 1)
	drive (scanDir - 2, -100);
      else if (j % 4 == 3)
	drive (scanDir + 2, -100);
      else
	drive (scanDir, 100);
      j++;
    }				//end if(speed() > 75)
  }				//end else if(myX..myY)
  else if (center == 0) {
    drive (atan2 (5000 - myY, 5000 - myX), 100);
    center = 1;
  }				//end else
}				//end Move()

//**************************************


void
Shotgun (int &targDir, int &range, int &res)
{
  cannon (targDir, range);
  cannon (targDir + res, range);
  cannon (targDir - res, range);
}				//end Shotgun()

//**************************************

void
Rifle (int &targDir, int &range, int &res)
{
  cannon (targDir, range);
  cannon (targDir + (res + 1) / 2, range);
  cannon (targDir - (res + 1) / 2, range);
}				//end Rifle()

//**************************************

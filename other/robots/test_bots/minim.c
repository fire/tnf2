//PROGRAM:    minim
//AUTHOR:     steveg@ccis.adisys.com.au (steveg@ccis.adisys.com.au)


#include "robots.h"

int main ()
{
  int sWide[20] =
    { 9, -9, 27, -27, 45, -45, 63, -63, 81, -81, 99, -99, 117, -117,
    135, -135, 153, -153, 171, -171
  };
  int pWide = 0;

  int sFine[7][2] =
    { {0, 1}, {1, 1}, {-1, 1}, {3, 3}, {-3, 3}, {6, 6}, {-6, 6} };
  int pFine = 0;

  float Hx[6] = { 0, 0, 0, 0, 0, 0 };
  float Hy[6] = { 0, 0, 0, 0, 0, 0 };
  float Ht[6] = { 0, 0, 0, 0, 0, 0 };
  int Hptr = 0;

  float Ex = 0;
  float Ey = 0;
  float Et = 0;
  float Ext = 0;
  float Eyt = 0;
  float Ett = 0;
  float N = 0;

  int mx = loc_x ();
  int my = loc_y ();
  int mt = time ();

  float SSt, SSxt, SSyt;
  float b0x, b1x, b0y, b1y;

  int px, py, pr, pd;
  int fx, fy, fr, fd;

  int fire = 0;
  int look = 0;
  int rng;

  int offd = 50;
  int width;

  int shot1 = 0;
  int shot2 = 0;
  int shot3 = 0;

  int dir = 0;
  int tscan = 0;
  int tdrv, spd;
  int ldir = atan2 (5000 - my, 5000 - mx);

  while (1) {
    if (fire == 1) {
      //bang
      while (abs (fr - pr) > 10) {
	mt = time ();
	pr = fr;
	fx = int ((pr / 10 + mt) * b1x + b0x) - mx;
	fy = int ((pr / 10 + mt) * b1y + b0y) - my;
	fd = atan2 (fy, fx);
	fr = sqrt (fx * fx + fy * fy);
      }
      if (fr < 7400) {
	if (fr > 7000)
	  fr = 7000;
	if (fr < 400)
	  fr = 400;
	if (cannon (fd, fr) == 1) {
	  shot1 = shot2;
	  shot2 = shot3;
	  shot3 = mt + (fr + 5) / 10 + 5;
	}
	else {
	  fire = 0;
	}
      }
      else {
	fire = 0;
      }
    }
    else if (mt > tdrv) {
      //Maneuver
      if (N < 6) {
	px = 5000 - mx;
	py = 5000 - my;
	pr = sqrt (px * px + py * py);
      }
      if (pr > 7000) {
	fx = px;
	fy = py;
	dir = atan2 (fy, fx);
	spd = 100;
      }
      else {
	fx = mx + py;
	fy = my - px;
	if (fx < 200 || fx > 9800 || fy < 200 || fy > 9800) {
	  fx = mx - py;
	  fy = my + px;
	  if (fx < 200 || fx > 9800 || fy < 200 || fy > 9800) {
	    fx = rand (8000) + 1000 - mx;
	    fy = rand (8000) + 1000 - my;
	  }
	}
	dir = atan2 (fy, fx);
	spd = offd;
	offd = -offd;
      }
      tdrv = sqrt (fx * fx + fy * fy) * 10 / abs (spd);
      if (tdrv < 600) {
	tdrv += mt;
      }
      else {
	tdrv = mt + 600;
      }
      drive (dir, spd);
    }
    else if (look == 0) {
      //Wide search
      dir = ldir + sWide[pWide];
      rng = scan (dir, 10);
      if (rng > 0) {
	while (N < 6) {
	  fx = (int)(mx + cos (dir + 10) * rng);
	  fy = (int)(my + sin (dir + 10) * rng);
	  Ht[Hptr] = mt;
	  Hx[Hptr] = fx;
	  Hy[Hptr] = fy;
	  Hptr = (++Hptr) % 6;
	  Ex += fx;
	  Ey += fy;
	  Et += mt;
	  Ext += fx * mt;
	  Eyt += fy * mt;
	  Ett += mt * mt;
	  N++;

	  fx = (int)(mx + cos (dir - 10) * rng);
	  fy = (int)(my + sin (dir - 10) * rng);
	  Ht[Hptr] = mt;
	  Hx[Hptr] = fx;
	  Hy[Hptr] = fy;
	  Hptr = (++Hptr) % 6;
	  Ex += fx;
	  Ey += fy;
	  Et += mt;
	  Ext += fx * mt;
	  Eyt += fy * mt;
	  Ett += mt * mt;
	  N++;

	  mt += 10;
	}

	look++;
	ldir = dir;
	pFine = 0;
	pWide = 0;
      }
      else {
	pWide = (++pWide) % 20;
      }
    }
    else {
      //Fine search & track
      if (pFine == 0) {
	width = atan2 (100, pr);
	if (width < 1)
	  width = 1;
	if (width > 10)
	  width = 10;
      }
      else {
	width = sFine[pFine][1];
      }
      dir = pd + sFine[pFine][0];
      rng = scan (dir, width);
      if (rng > 0) {
	fx = (int)(mx + cos (dir + width) * rng);
	if (fx < 0)
	  fx = 0;
	if (fx > 10000)
	  fx = 10000;
	fy = (int)(my + sin (dir + width) * rng);
	if (fy < 0)
	  fy = 0;
	if (fy > 10000)
	  fy = 10000;
	Ht[Hptr] = mt;
	Hx[Hptr] = fx;
	Hy[Hptr] = fy;
	Hptr = (++Hptr) % 6;
	Ex += fx;
	Ey += fy;
	Et += mt;
	Ext += fx * mt;
	Eyt += fy * mt;
	Ett += mt * mt;
	N++;

	fx = (int)(mx + cos (dir - width) * rng);
	if (fx < 0)
	  fx = 0;
	if (fx > 10000)
	  fx = 10000;
	fy = my + sin (dir - width) * rng;
	if (fy < 0)
	  fy = 0;
	if (fy > 10000)
	  fy = 10000;
	Ht[Hptr] = mt;
	Hx[Hptr] = fx;
	Hy[Hptr] = fy;
	Hptr = (++Hptr) % 6;
	Ex += fx;
	Ey += fy;
	Et += mt;
	Ext += fx * mt;
	Eyt += fy * mt;
	Ett += mt * mt;
	N++;

	if (pFine > 0 || abs (rng - pr) > 20) {
	  printlog ("Purgeing track ");
	  Ex = Hx[0] + Hx[1] + Hx[2] + Hx[3] + Hx[4] + Hx[5];
	  Ey = Hy[0] + Hy[1] + Hy[2] + Hy[3] + Hy[4] + Hy[5];
	  Et = Ht[0] + Ht[1] + Ht[2] + Ht[3] + Ht[4] + Ht[5];
	  Ext =
	    Hx[0] * Ht[0] + Hx[1] * Ht[1] + Hx[2] * Ht[2] +
	    Hx[3] * Ht[3] + Hx[4] * Ht[4] + Hx[5] * Ht[5];
	  Eyt =
	    Hy[0] * Ht[0] + Hy[1] * Ht[1] + Hy[2] * Ht[2] +
	    Hy[3] * Ht[3] + Hy[4] * Ht[4] + Hy[5] * Ht[5];
	  Ett =
	    Ht[0] * Ht[0] + Ht[1] * Ht[1] + Ht[2] * Ht[2] +
	    Ht[3] * Ht[3] + Ht[4] * Ht[4] + Ht[5] * Ht[5];
	  N = 6;
	}

	fire = 1;
	ldir = dir;
	if (pFine == 0) {
	  tscan = mt + 120;
	}
	else {
	  tscan = mt + 60;
	}
	pFine = 0;
      }
      else {
	if (pFine == 0) {
	  if (pr > 5730) {
	    pFine = 1;
	  }
	  else if (pr > 1910) {
	    pFine = 3;
	  }
	  else if (pr > 960) {
	    pFine = 5;
	  }
	  else {
	    pFine = 0;
	    look = 0;
	  }
	}
	else if (pFine > 6) {
	  pFine = 0;
	  look = 0;
	}
	else {
	  pFine++;
	}
      }
    }
    mx = loc_x ();
    my = loc_y ();
    mt = time ();
    if (N > 2) {
      SSt = Ett - (Et * Et) / N;
      SSxt = Ext - (Ex * Et) / N;
      SSyt = Eyt - (Ey * Et) / N;
      b1x = SSxt / SSt;
      b0x = (Ex - b1x * Et) / N;
      b1y = SSyt / SSt;
      b0y = (Ey - b1y * Et) / N;

      px = int (mt * b1x + b0x);
      if (px > 10000)
	px = 10000;
      if (px < 0)
	px = 0;
      px -= mx;
      py = int (mt * b1y + b0y);
      if (py > 10000)
	py = 10000;
      if (py < 0)
	py = 0;
      py -= my;
      pr = sqrt (px * px + py * py);
      pd = atan2 (py, px);
      printlog ("x%5d y%5d a%3d d%5d ", px + mx, py + my, pd, pr);

      fx = int ((mt + 700) * b1x + b0x) - mx;
      fy = int ((mt + 700) * b1y + b0y) - my;
      fr = sqrt (fx * fx + fy * fy);
      fd = atan2 (fy, fx);

      if (fd > 7000 || mt < shot1 || mt > tscan)
	fire = 0;
    }
    else {
      fire = 0;
    }
  }
}

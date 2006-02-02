
#include "robots.h"

int main ()
{
  int sWide[23] = { 0, 2, -2, 5, -5, 10, -10, 30, -30, 50, -50, 70, -70,
    90, -90, 110, -110, 130, -130, 150, -150, 170, -170
  };

  int rWide[23] = { 1, 1, 1, 2, 2, 10, 10, 10, 10, 10, 10, 10, 10, 10,
    10, 10, 10, 10, 10, 10, 10, 10, 10
  };

  int pWide = 5;
  int lWide = 23;

  int offs = 1;

  int Hlim = 10;
  float Hx[12] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  float Hy[12] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  float Ht[12] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  float Hw[12] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  int Hptr = 0;
  int i;

  float Ex = 0;
  float Ey = 0;
  float Et = 0;
  float Exx = 0;
  float Ext = 0;
  float Eyy = 0;
  float Eyt = 0;
  float Ett = 0;
  float N = 0;

  int mx = loc_x ();
  int my = loc_y ();
  int mt = 1;
  int ft = 0;

  int dir, wid, dir1, dir2, rng;

  int movex[4] = { 1000, 9000, 9000, 1000 };
  int movey[4] = { 1000, 1000, 9000, 9000 };
  int mtarg = 0;

  float SSt, SSxt, SSyt;
  float b0x, b1x, b0y, b1y;

  int px, py, pd, pr;
  int fx, fy, fd, fr;

  int px1, py1, pr1;
  int px2, py2, pr2;
  int offx = 0;

  int shot1 = 0;
  int shot2 = 0;
  int shot3 = 0;
  int fnext = 0;

  int search = 1;
  int fire = 0;
  int tdrv = 0;
  int lastx = 5000;
  int lasty = 5000;
  int lastt = 0;

  rng = 30000;
  for (dir = 0; dir < 4; dir++) {
    px = movex[dir] - mx;
    py = movey[dir] - my;
    pr = sqrt (px * px + py * py);
    if (pr < rng) {
      rng = pr;
      mtarg = dir;
    }
  }

  px = lastx - mx;
  py = lasty - my;
  pr = sqrt (px * px + py * py);
  pd = atan2 (py, px);

  while (search == 1) {
    mt = time ();
    dir = pd + sWide[pWide];
    rng = scan (dir, 10);
    if (rng > 0) {
      lastx = (int)(mx + cos (dir) * rng);
      lasty = (int)(my + sin (dir) * rng);
      pWide = 0;
      search = 0;
    }
    else {
      pWide = (++pWide) % lWide;
    }
  }

  while (1) {
    if (fire == 1) {
      fire = 0;
      ft = mt + 20 + int (pr / (10 - (fr - pr) / 700));
      fx = (int)(ft * b1x + b0x - mx);
      fy = (int)(ft * b1y + b0y - my);
      fr = sqrt (fx * fx + fy * fy);
      fd = atan2 (fy, fx);
      if (fr < 7400 && fr > 400) {
	if (cannon (fd, fr) == 1) {
	  shot1 = shot2;
	  shot2 = shot3;
	  shot3 = ft;
	  fnext = mt + int ((ft - mt) / 3);
	  if (shot1 > fnext)
	    fnext = shot1;
	}
      }
    }
    else if (mt > tdrv) {
      px = movex[mtarg] - mx;
      py = movey[mtarg] - my;
      pr = int (sqrt (px * px + py * py));
      if (pr < 1500)
	mtarg = (++mtarg) % 4;
      px = movex[mtarg] - mx;
      py = movey[mtarg] - my;
      pd = atan2 (py, px);
      if (pd < 0)
	pd += 360;
      dir = heading ();
      if (dir != pd)
	drive (pd, 100);
      tdrv = mt + 300;
    }
    else {
      // Wide search
      dir = pd + sWide[pWide];
      wid = rWide[pWide];
      rng = scan (dir, wid);
      if (rng > 0) {
	fx = (int)(mx + cos (dir) * rng);
	fy = (int)(my + sin (dir) * rng);
	if (wid == 1) {
	  Hptr = (++Hptr) % Hlim;
	  Ht[Hptr] = mt;
	  Hx[Hptr] = fx;
	  Hy[Hptr] = fy;
	  Hw[Hptr] = 1;
	}
	lastx = fx;
	lasty = fy;
	pWide = 0;
      }
      else {
	pWide = (++pWide) % lWide;
      }
    }

    mx = loc_x ();
    my = loc_y ();
    mt = time ();
    Ex = 0;
    Ey = 0;
    Et = 0;
    Ext = 0;
    Eyt = 0;
    Ett = 0;
    N = 0;
    for (i = 0; i < Hlim; i++) {
      if (Ht[i] + 200 < mt) {
	Hx[i] = 0;
	Hy[i] = 0;
	Ht[i] = 0;
	Hw[i] = 0;
      }
      else {
	Ex += Hx[i];
	Ey += Hy[i];
	Et += Ht[i];
	Ext += Hx[i] * Ht[i];
	Eyt += Hy[i] * Ht[i];
	Ett += Ht[i] * Ht[i];
	N += Hw[i];
      }
    }
    if (N > 2) {
      SSt = Ett - (Et * Et) / N;
      SSxt = Ext - (Ex * Et) / N;
      SSyt = Eyt - (Ey * Et) / N;
      b1x = SSxt / SSt;
      b0x = (Ex - b1x * Et) / N;
      b1y = SSyt / SSt;
      b0y = (Ey - b1y * Et) / N;

      px = mt * b1x + b0x - mx;
      py = mt * b1y + b0y - my;

      fx = (mt + 700) * b1x + b0x - mx;
      fy = (mt + 700) * b1y + b0y - my;
      fire = 1;
    }
    else {
      px = lastx - mx;
      py = lasty - my;
      fx = px;
      fy = py;
      fire = 0;
    }
    pr = sqrt (px * px + py * py);
    pd = atan2 (py, px);
    printlog ("Log x%5d y%5d a%3d d%5d N%2d", px + mx, py + my, pd, pr,
	      (int) N);
    fr = sqrt (fx * fx + fy * fy);
    fd = atan2 (fx, fy);

    if (mt < fnext || fr > 7000)
      fire = 0;

  }
}

#define BEGIN //
#define END //

/*
 .'"'.        ___,,,___        .'``.
: (\  `."'"```         ```"'"-'  /) ;
 :  \                         `./  .'
  `.                            :.'
    /        _         _        \
   |         0}       {0         |
   |         /         \         |
   |        /           \        |
   |       /             \       |
    \     |      .-.      |     /
     `.   | . . /   \ . . |   .'
       `-._\.'.(     ).'./_.-'
           `\'  `._.'  '/'
             `. --'-- .'
               `-...-'jgs
*/
/****************************************************
**  Program:    PolarBear
**  Author:     Aaron Judd
**  Date:       12/23/98
**  Version:    2.0
**              Replaced version 1.0 at age 2.
**  History:
**  Ver 1.0:   (10/1/98)
**             First working version.  
**             Debut at position 18/20.
*******************************************************/
BEGIN


#include "robots.h"
//checked 8 different targeting
//  B best = DX,DY int with no assumed speed
//checked 5 different walks
//  circle arena best - for targeting
//checked ping count 1-9 - 7 best 
//   check if 5 is okay

//try wiggle walks (castle wall)
//if hurt 3/5 and >6pt  stop/turn/emergency/charge!
//quick find under 3 turns - shoot asap.
//check num ping vs target move type
//check turn delay (don't turn too often)
//optimize code , remove unused parts.

#define MAXSIZE 10000
#define CENTER 5000
#define MAXSCANARC 10
#define MAXSPEED 100
#define MINSPEED -75
#define MAXTURNSPEED 50
#define MAXACCEL 20

#define MAXRANGE 7399
#define MAXSHOOTSPEED 1000
#define MAXSHOTRAN 7000
#define MAXVOLLY 3

#define MAXTURNANGLE 15
#define SCANTIME 100

int max(double x,double y) { return (int)(x>y ? x : y);}
int min(double x,double y) { return (int)(x<y ? x : y);}
int max(int x,int y) { return (int)(x>y ? x : y);}
int min(int x,int y) { return (int)(x<y ? x : y);}
int max(double x,int y) { return (int)(x>y ? x : y);}
int min(double x,int y) { return (int)(x<y ? x : y);}
int max(int x,double y) { return (int)(x>y ? x : y);}
int min(int x,double y) { return (int)(x<y ? x : y);}

int WALLBUFF=20;
int DAMBUFF=200+MAXSPEED;
int nextmovetime,nextshottime;

int dest_x,dest_y;

int clockwise=0;

int minscan=0;

int scan_wid=10;
int found=0;
int tracking=0;
int lost=0;

#define PINGMAX 8
int PINGTOP=PINGMAX-1;
int pingx[PINGMAX],pingy[PINGMAX],pingt[PINGMAX];

//int pingd[PINGMAX],pingr[PINGMAX];



int dist(int x,int y,int x2, int y2) {
  int dx=x-x2;
  int dy=y-y2;
  dx=dx*dx;dy=dy*dy;
  if (dx+dy==0) return 0;
  return (int)sqrt(dx+dy);
}

void move_next_corner(int x,int y,int DIST) {
  if ((x<CENTER && y<CENTER) || (x>CENTER && y>CENTER) ) {
    if (rand(2)) {dest_x=MAXSIZE-DIST;dest_y=DIST;}
    else {dest_y=MAXSIZE-DIST;dest_x=DIST;}
  }
  else {
    if (rand(2)) {dest_x=MAXSIZE-DIST;dest_y=MAXSIZE-DIST;}
    else {dest_y=DIST;dest_x=DIST;}
  }
}

void move_nearest_corner(int x,int y,int DIST) {
  if (x<CENTER) dest_x=DIST;
  else dest_x=MAXSIZE-DIST;
  if (y<CENTER) dest_y=DIST;
  else dest_y=MAXSIZE-DIST;
}

void move_farthest_corner(int x,int y,int DIST) {
  if (x>CENTER) dest_x=DIST;
  else dest_x=MAXSIZE-DIST;
  if (y>CENTER) dest_y=DIST;
  else dest_y=MAXSIZE-DIST;
}

int firstmove=1;
int doturn=0;
int moveclock=1;

void domove() {
  int x=loc_x(),y=loc_y(),t=time();
  if (firstmove) {
    firstmove=0;
    int ang=(int)atan2(CENTER-y,CENTER-x);
    int dis=dist(x,y,CENTER,CENTER);
    int dest_ran=5000-dis;
    int dest_dir=ang;
    int dest_speed=100;
    nextmovetime=t+dest_ran/dest_speed*SCANTIME;
    drive(dest_dir,dest_speed);
    return;
  }

  int ang=(int)atan2(CENTER-y,CENTER-x);
/*
overall  B=22 b=33 a=39 c=41 d=45
mov      B=5 a=6 c=13 d=15 b=20
ran      B=8 b=9 c=13 d=15 a=15
non      b=4 B=8 c=15 d=15 a=18

  //circle arena 
  //  mov tar = 768  bb,a,c,d,b (+100,200,550)
  //  ran tar = 1092 bb,c,b,a,d (+350)
  //  non tar = 706  b,bb,c,d,a (+580)
  int ang=(int)atan2(CENTER-y,CENTER-x);

  //circle opponent 
  // mov tar = 1078  bb,a,d,c,b  (+10,400,500)
  // ran tar = 1092  b,d,a,bb,c  (+260)
  // non tar = 1000  b,bb,d,c,a  (+600)
  int ang=(int)atan2(pingy[PINGTOP]-y,pingx[PINGTOP]-x);

  //charge opponent 
  //  mov tar = 317 a,bb,c,d,b (+200,300,600)
  //  ran tar = 462 bb,c,d,b,a (+420)
  //  non     = 486 b,bb,d,c,a (+520)
  int ang=(int)atan2(pingy[PINGTOP]-y,pingx[PINGTOP]-x)-80*moveclock;

  //fleet oponent  
  //  mov tar = (draw) a,bb,c,d,b (+10,20,40)
  //  ran tar = (draw) b,bb,a,c,d (+50)
  //  non tar = (draw) b,bb,a,c,d (+50)
  int ang=(int)atan2(pingy[PINGTOP]-y,pingx[PINGTOP]-x)+80*moveclock;
*/

  int dest_dir=ang+80*moveclock;
  int dest_speed=70+rand(25);
  int dest_ran;

  int minran=2000;
  if (sin(dest_dir)>0)
    minran=min(minran,(MAXSIZE-200-y)/sin(dest_dir));
  else
    minran=min((y-200)/abs(sin(dest_dir)),minran);
  if (cos(dest_dir)>0)
    minran=min((MAXSIZE-200-x)/cos(dest_dir),minran);
  else
    minran=min((x-200)/abs(cos(dest_dir)),minran);
  if (minran<200) {
    dest_dir-=180;
    moveclock*=-1;
    dest_ran=500;
  }
  else dest_ran=minran;
  nextmovetime=t+dest_ran/dest_speed*SCANTIME;
  drive(dest_dir,dest_speed);
}

int volly[]={0,0,0};

int shotran,shotdir;
int TOP=PINGTOP;
int BOT=0;
int uselock=0;

//shot directly at ping
void shota(int* px,int* py) {
    int x=loc_x(),y=loc_y();
    shotdir=(int)atan2(py[TOP]-y,px[TOP]-x);
    shotran=dist(x,y,px[TOP],py[TOP]);
}

void shotb(int* px,int* py,int* pt) {
  double dt,dx,dy,rdt,tx,ty;
  dt=(pt[TOP]-pt[BOT])/100.0;if (dt==0) dt=1;
  dx=(px[TOP]-px[BOT])/dt;
  dy=(py[TOP]-py[BOT])/dt;
  int ran=dist(px[TOP],py[TOP],loc_x(),loc_y());
  double m=sqrt((int)(dx*dx+dy*dy));
  if (m>30) {dx=dx/m*100;dy=dy/m*100;}

  int t=time();
  rdt=(t-pt[TOP])/100.0+ran/1000.0+1;
  tx=px[TOP]+dx*rdt;
  ty=py[TOP]+dy*rdt;

  int x=loc_x(),y=loc_y();
  shotdir=(int)atan2((int)(ty-y),(int)(tx-x));
  shotran=dist(x,y,(int)tx,(int)ty);
}

void shotB(int* px,int* py,int* pt) {
  int dt,dx,dy,rdt,tx,ty;
  dt=(pt[TOP]-pt[BOT])/100;if (dt==0) dt=1;
  dx=(px[TOP]-px[BOT])/dt;
  dy=(py[TOP]-py[BOT])/dt;
  int ran=dist(px[TOP],py[TOP],loc_x(),loc_y());
  int t=time();
  rdt=(int)((t-pt[TOP])/100.0+ran/1000.0+1);
  tx=px[TOP]+dx*rdt;
  ty=py[TOP]+dy*rdt;

  int x=loc_x(),y=loc_y();
  shotdir=(int)atan2((int)(ty-y),(int)(tx-x));
  shotran=dist(x,y,(int)tx,(int)ty);
}


//shot dangle drange -comp my position
void shotc(int *px,int *py,int*pt,int* pd,int *pr) {
  int h=heading(),s=speed();
  int x=loc_x(),y=loc_y(),t=time();
  double dt,ddir,dran,tx,ty,dx,dy;
  dt=(pt[TOP]-pt[BOT])/100.0;if (dt==0) dt=1;
  ddir=(pd[TOP]-pd[BOT])/dt;
  dran=(pr[TOP]-pr[BOT])/dt;
  double timesincep=(t-pt[TOP])/100.0;
  double estshottime=pr[TOP]/1000.0;
  double tt=estshottime+timesincep;
  int tardir=(int)(pd[TOP]+ddir*tt);
  double tarran=pr[TOP]+dran*tt;
  tx=cos(tardir)*tarran+x+cos(h)*s*tt;
  ty=sin(tardir)*tarran+y+sin(h)*s*tt;
  dx=(tx-px[TOP])/dt;
  dy=(ty-py[TOP])/dt;
  shotdir=(int)atan2((int)(ty-y),(int)(tx-x));
  shotran=(int)dist(x,y,(int)tx,(int)ty);
}


void shotd(int *px,int *py,int*pt,int* pd,int *pr) {
  int TS=100;
  int dt,dran,dx,dy,rdt,tx,ty;
  int x=loc_x(),y=loc_y(),t=time();
  dt=(int)((pt[TOP]-pt[BOT])/100.0);
  if (dt==0) dt=1;

  dran=(pr[TOP]-pr[BOT])/dt;if (dran==0) dran=1;
  int cang=(int)atan(TS/dran);
  int tdir;

  if (dran>0) {
    if (clockwise%2) tdir=pd[TOP]+cang;
    else tdir=pd[TOP]-cang;
  }
  else {
    if (clockwise%2) tdir=pd[TOP]+(180-cang);
    else tdir=pd[TOP]+(180-cang);
  }
  dx=(int)(cos(tdir)*TS);
  dy=(int)(sin(tdir)*TS);

  rdt=(int)((t-pt[TOP])/100.0+pr[TOP]/1000.0);
  tx=px[TOP]+dx*rdt;
  ty=py[TOP]+dy*rdt;
  shotdir=(int)atan2((int)(ty-y),(int)(tx-x));
  shotran=dist(x,y,(int)tx,(int)ty);
}

void doshoot() {
  int x=loc_x(),y=loc_y(),t=time();
//  double tx,ty,dy,dx,rdt,dt,ddir,dran;
  BOT=TOP-7;
  shotB(pingx,pingy,pingt);
//  testc(1,shotdir,shotran);
//  shotb(pingx,pingy,pingt);
//  testc(2,shotdir,shotran);
//  shota(pingx,pingy);
//  testc(3,shotdir,shotran);
//  shotc(pingx,pingy,pingt,pingd,pingr);
//  testc(4,shotdir,shotran);
//  shotd(pingx,pingy,pingt,pingd,pingr);
//  testc(5,shotdir,shotran);
//  report();

  int shottime=(shotran/MAXSHOOTSPEED+1)*SCANTIME;
  int realran=min(shotran,7000);
  if (shotran<=7400) {
    if (cannon(shotdir,realran)) {
      volly[0]=volly[1];
      volly[1]=volly[2];
      volly[2]=time()+shottime;
    }
  }
}

void doscan() {
  heading();
  int x=loc_x(),y=loc_y(),t=time();
  int scandir=(int)atan2(pingy[PINGTOP]-y,pingx[PINGTOP]-x);
  if (!found) {
    scan_wid=10;
    lost++;clockwise++;
    scandir+=scan_wid*2*(lost/2)*(clockwise%2 ? 1 : -1);
  }
  else {
    int minscan=max((5-dist(x,y,pingx[PINGTOP],pingy[PINGTOP])/900),1);
    if (tracking) {
      lost=0;
      scan_wid=max(scan_wid/2,minscan);
      if (scan_wid!=minscan)
        scandir+=scan_wid/2*(clockwise%2 ? 1 : -1);
    }
    else {
      lost++;clockwise++;
      scandir+=scan_wid*((lost+1)/2)*(clockwise%2 ? 1 : -1);
      scan_wid+=2;
      if (lost>4) scan_wid=10;
      scan_wid=min(scan_wid,10);
    }
  }
  
  x=loc_x();y=loc_y();t=time();
  int scanran=scan(scandir,scan_wid);

  if (!scanran) {
    tracking=0;
  }
  else {
    int a;
    if (!found) {
      found=1;tracking=1;
      pingx[PINGTOP]=min(max(x+cos(scandir)*scanran,100),9900);
      pingy[PINGTOP]=min(max(y+sin(scandir)*scanran,100),9900);
      pingt[PINGTOP]=t;
//      pingd[PINGTOP]=scandir;
//      pingr[PINGTOP]=scanran;
      for (a=PINGTOP-1;a>=0;a--) {
        pingx[a]=pingx[a+1];pingy[a]=pingy[a+1];pingt[a]=pingt[a+1];
//        pingd[a]=pingd[a+1];pingr[a]=pingr[a+1];
      }
    }
    else {
      tracking=1;
      int px=min(max(x+(int)(cos(scandir)*scanran),100),9900);
      int py=min(max(y+(int)(sin(scandir)*scanran),100),9900);
      for (a=0;a<PINGTOP;a++) {
        pingx[a]=pingx[a+1];pingy[a]=pingy[a+1];pingt[a]=pingt[a+1];
//        pingd[a]=pingd[a+1];pingr[a]=pingr[a+1];
      }
      pingx[PINGTOP]=px;
      pingy[PINGTOP]=py;
      pingt[PINGTOP]=t;
//      pingd[PINGTOP]=scandir;
//      pingr[PINGTOP]=scanran;
    }
  }
}

void next() {
  doscan();
  if (time()>volly[0] && found) doshoot();
  if (!speed() || time()>nextmovetime) domove();
}

int  main() {
  pingx[PINGTOP]=pingy[PINGTOP]=CENTER;
  domove();
  while (1) {next();}
}
END

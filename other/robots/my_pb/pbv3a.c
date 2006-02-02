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
**  Date:       3/21/99
**  Version:    3.0a
**              Never competed for hill.
**  History:
**
**  Ver 1.0:   (10/1/98)
**             First working version.  
**             Debut at position 18/20.
**  Ver 2.0:   (12/23/98)
**             Replaced version 1.0 at age 2.
**             Debut at position 7/20.
**  Ver 3.0:   (3/20/99)
**             Replaced version 2.0 at age 15 which
**             had falled to position 12/20.
*******************************************************/
BEGIN


#include "robots.h"

#define PINGMAX 6
int MAXSCANDELAY=200;
int MAXSIZE=10000;
int CENTER=MAXSIZE/2;
int MAXSPEED=100;
int MAXSHOOTSPEED=1000;
int MAXSHOTRAN=7000;
int MAXVOLLY=3;
int SCANTIME=100;
int SHOTDELAY=50;
int SHOTTIME=50;
double MYPI=3.14159;

//main vars
int nextmovetime,nextshottime,nextscantime;

//move vars
int dest_x,dest_y;
int moveclock=1;

//shoot vars
int volly[]={0,0,0};
int shotran,shotdir;

//scan vars
struct Point {int x,y;};
Point CENTERPOINT;
int PINGTOP=PINGMAX-1;
int TOP=PINGTOP;
int BOT=0;
Point pingloc[PINGMAX];
int pingt[PINGMAX];
int clockwise=0;
int minscan=0;
int scan_wid=10;
int found=0;
int tracking=0;
int lost=0;


/*********TOOLS TOOLS TOOLS***************************/
int max(double x,double y) { return (int)(x>y ? x : y);}
int min(double x,double y) { return (int)(x<y ? x : y);}
int dist(int x,int y,int x2, int y2) {
  int dx=x-x2;
  int dy=y-y2;
  dx=dx*dx;dy=dy*dy;
  return (int)sqrt(dx+dy);
}
int getDirectAngleTo(Point p) {
  return (int)atan2(p.y-loc_y(),p.x-loc_x());
}
int getDirectDistanceTo(Point p) {
  return dist(p.y,p.x,loc_y(),loc_x());
}



/************MOVE MOVE MOVE****************************/
void domove() {
  int dest_dir,dest_speed;
  int minran=2000;
  int addtime=rand(500)+800;//org500/500

  if (getDirectDistanceTo(pingloc[PINGTOP])<2500) {
    if (speed()==100) 
      {nextmovetime=time()+addtime;return;}
    dest_dir=heading();
    dest_speed=100;
  }
  else {//randomturn
    dest_dir=heading()+(rand(2) ? 1: -1)*(40+rand(5)*10);
    dest_speed=100;
  }
  int tx=loc_x()+(int)(cos(dest_dir)*addtime);
  int ty=loc_y()+(int)(sin(dest_dir)*addtime);
  if (tx<400 || tx>MAXSIZE-400 || ty<400 || ty>MAXSIZE-400) 
    dest_dir=getDirectAngleTo(CENTERPOINT);
  nextmovetime=time()+addtime;
  drive(dest_dir,dest_speed);
}




/**************** SHOOT  SHOOT ****************************/
void shotB(Point *ploc,int* pt) {
  double dt,dx,dy,rdt;
  int tx,ty;
  dt=(pt[TOP]-pt[BOT])/100.0;
  if (dt==0) dt=1.0;
  dx=(ploc[TOP].x-ploc[BOT].x)/dt;
  dy=(ploc[TOP].y-ploc[BOT].y)/dt;
  double ddd=dist((int)dx,(int)dy,0,0);
  if (ddd>100) {//fix 4/21/99
    dx=dx*100/ddd;
    dy=dy*100/ddd;
  }
  int ran=getDirectDistanceTo(ploc[TOP]);
  rdt=(time()-pt[TOP])/100.0+ran/1000.0+1;
  tx=(int)(ploc[TOP].x+(dx*rdt));
  ty=(int)(ploc[TOP].y+(dy*rdt));
//don't shoot off map
  tx=max(tx,100);tx=min(tx,9900);
  ty=max(ty,100);ty=min(ty,9900);
  Point temp;
  temp.x=tx;temp.y=ty;
  shotdir=getDirectAngleTo(temp);
  shotran=getDirectDistanceTo(temp);
}
void doshoot() {
  BOT=TOP-5;
  shotB(pingloc,pingt);
//testc(5,shotdir,shotran);
//report();

  int realran=min(shotran,7000);
  int shottime=realran/10;//fix 4/21/99
  if (shotran<=7400) {
    if (cannon(shotdir,realran)) {
      volly[0]=volly[1];
      volly[1]=volly[2];
      volly[2]=time()+shottime;
    }
  }
//delay shooting until next possible in range
  else {
    volly[0]=volly[1];
    volly[1]=volly[2];
    volly[2]=time()+(shotran-7400)/2;
  }
}



/**************SCAN SCAN SCAN****************************/
void doscan() {
  int scandir=getDirectAngleTo(pingloc[PINGTOP]);
  if (!found) {
    scan_wid=10;
    lost++;clockwise++;
    scandir+=scan_wid*2*(lost/2)*(clockwise%2 ? 1 : -1);
  }
  else {
    int dd=getDirectDistanceTo(pingloc[PINGTOP]);
    if (tracking) {
      lost=0;
      int minscan=(int)(200.0/dd*180/MYPI/2);
      scan_wid=max(scan_wid/2,minscan);
    }
    else {
      lost++;clockwise++;
      scandir+=scan_wid*((lost+1)/2)*(clockwise%2 ? 1 : -1);
      scan_wid+=2;// *2???
      if (lost>4) scan_wid=10;
      scan_wid=min(scan_wid,10);
    }
  }
  
  int x=loc_x(),y=loc_y(),t=time();
  int scanran=scan(scandir,scan_wid);

  if (!scanran) {
    nextscantime=time();
    tracking=0;
  }
  else {
    nextscantime=time()+MAXSCANDELAY;
    if (!tracking) clockwise--;
    int a;
    tracking++;
    if (!found) {
      found=1;
//use position at time of scan
      pingloc[PINGTOP].x=(int)(x+cos(scandir)*scanran);
      pingloc[PINGTOP].y=(int)(y+sin(scandir)*scanran);
      pingt[PINGTOP]=t;
      for (a=PINGTOP-1;a>=0;a--) {
        pingloc[a]=pingloc[a+1];pingt[a]=pingt[a+1]+1;
      }
    }
    else {
//use position at time of scan
      for (a=0;a<PINGTOP;a++) {
        pingloc[a]=pingloc[a+1];pingt[a]=pingt[a+1];
      }
      pingloc[PINGTOP].x=(int)(x+cos(scandir)*scanran);
      pingloc[PINGTOP].y=(int)(y+sin(scandir)*scanran);
      pingt[PINGTOP]=t;
    }
  }
}



/*****************MAIN MAIN MAIN****************************/
int main() {
  domove();
  CENTERPOINT.x=CENTERPOINT.y=CENTER;
  while (1) {
    if (!speed() || time()>nextmovetime) domove();
    else if (time()>nextscantime) doscan();
    else if (time()>volly[0] && found) doshoot();
    else doscan();
  }
}
END


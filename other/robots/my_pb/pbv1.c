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
**  Date:       10/1/98
**  Version:    1.0
**              First working version.  
*******************************************************/
BEGIN

#include "robots.h"


#define MAXSIZE 10000
#define CENTER 5000
#define MAXSCANARC 10
#define MAXSPEED 100
#define MAXREVERSE 75
#define MAXTURNSPEED 50
#define MAXACCEL 20

#define MAXRANGE 7399
#define MAXSHOOTSPEED 1000
#define MAXVOLLY 3
#define MAXSHOTRAN 7000

#define MAXTURNANGLE 15
#define SCANTIME 100

#define max(x,y) (x>y ? x : y)
#define min(x,y) (x<y ? x : y)


int WALLBUFF=20;
int GOBUFF=400;
int BACKBUFF=200;
int CRAWLBUFF=50;


int ACCELDELAY=MAXSPEED/MAXACCEL*SCANTIME/2;
int TURNSPEEDDELAY=MAXTURNSPEED/MAXACCEL*SCANTIME/2;
int TURNDELAY=360/MAXTURNANGLE*SCANTIME/2+TURNSPEEDDELAY;
int DAMBUFF=200+MAXSPEED;
int ZIGZAGTIME=15*SCANTIME;

int lastdam=0;
int hurt=0;
int newhurt=0;

int r_time,r_x,r_y,r_speed,r_damage;

int nextmovetime,nextshottime,nextscantime;

int dest_x,dest_y;
int tar_x,tar_y;
int tar_dx,tar_dy,tar_time,tar_dtime;

int clockwise=0;

int minscan=0,maxscan=360;

int newmove=0;
int safe=1;
int gocorner=1;
int backcorner=0;
int stop=0;
int emergencyback=0;
int emergencycorner=0;
int hitwall=0;
int hitrobot=0;

int min_scan=0,max_scan=360;
int scan_x,scan_y,scan_wid=10;
int found=0;
int tracking=0;
int lost=0;

#define PINGMAX 3
int PINGTOP=PINGMAX-1;
int pingx[PINGMAX],pingy[PINGMAX],pingt[PINGMAX],
    pingd[PINGMAX],pingr[PINGMAX],pingw[PINGMAX];

int turning=0,backing=0,fullspeed=0;


int dist(int x,int y,int x2, int y2) {
  int dx=x-x2;
  int dy=y-y2;
  return (int)sqrt(dx*dx+dy*dy);
}

int onwall() {
  int WB=WALLBUFF;
  int MWB=MAXSIZE-WALLBUFF;
  return (loc_x()<WB || loc_y()<WB || loc_x()>MWB || loc_y()>MWB);
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



int firstmove=1;
int doturn=0;

void domove() {
heading();
  int x=loc_x(),y=loc_y(),t=time();
  if (firstmove) {move_nearest_corner(x,y,400);firstmove=0;}
  else if (!doturn) {move_next_corner(x,y,400);doturn=1;}
  else if (doturn) doturn=0;

  int dest_speed,dest_ran,dest_dir;
  dest_dir = (int)atan2(dest_y-y,dest_x-x);
  dest_ran = dist(dest_x,dest_y,x,y);
  dest_dir=((dest_dir%360)+360)%360;
  
  if (doturn) {
    dest_speed=0;
    nextmovetime=t+600;
  }
  else {dest_speed=100;nextmovetime=t+dest_ran/dest_speed*SCANTIME;}
  drive(dest_dir,dest_speed);
heading();
}



int volly[]={0,0,0};

void doshoot() {
heading();
  int shotdir,shotran,shottime;
  int x=loc_x(),y=loc_y(),t=time();
  int totpingtime=(pingt[PINGTOP]-pingt[0])/100;
  double ddir,dran;
  if (totpingtime==0) {
    ddir=pingd[PINGTOP];
    dran=pingr[PINGTOP];
  }
  else {
    ddir=(pingd[PINGTOP]-pingd[0])/totpingtime;
    dran=(pingr[PINGTOP]-pingr[0])/totpingtime;
  }
  int timesinceping=(t-pingt[PINGTOP])/100;
  int estshottime=pingr[PINGTOP]/1000;
  int tt=estshottime+timesinceping;
  shotdir=(int)(pingd[PINGTOP]+ddir*tt);
  shotran=max((int)(pingr[PINGTOP]+dran*tt),DAMBUFF);
  
  shottime=(shotran/MAXSHOOTSPEED)*SCANTIME;
  shotdir=((shotdir%360)+360)%360;
  int realran=min(shotran,7000);
  if (shotran>7400) {}
  else if (cannon(shotdir,realran)) {
    volly[0]=volly[1];
    volly[1]=volly[2];
    volly[2]=time()+shottime;
  }
heading();
}





void doscan2() {
printlog("do scan\n");
heading();
//find old scan location
  int x=loc_x(),y=loc_y(),t=time();
  int scandir=(int)atan2(tar_y-y,tar_x-x);

//setup new scanlocaiton
  if (!found) {
    scan_wid=10;
    lost++;clockwise++;
    scandir+=scan_wid*2*(lost/2)*(clockwise%2 ? 1 : -1);
  }
  else {
    if (tracking) {
      lost=0;clockwise=0;
      int scandis=dist(x,y,tar_x,tar_y);
      int minscan=max((4-scandis/1000),1);
      scan_wid=max(scan_wid/2,minscan);
      if (scan_wid!=minscan)
        scandir-=scan_wid/2;
    }
    else {
      lost++;clockwise++;
      scandir+=scan_wid*((lost+1)/2)*(clockwise%2 ? 1 : -1);
      scan_wid++;
      if (lost>4) scan_wid=10;
      scan_wid=min(scan_wid,10);
    }
  }
  
//doscan
  x=loc_x();y=loc_y();t=time();
  scandir=(scandir+360)%360;
  int scanran=scan(scandir,scan_wid);

//getlocation

  if (!scanran) {
printlog("no scan math!\n");
    tracking=0;
  }
  else {
printlog("start scan math\n");
    found=1;tracking=1;
    tar_y=y+(int)(sin(scandir)*scanran);
    tar_x=x+(int)(cos(scandir)*scanran);
    tar_x=min(max(tar_x,100),9900);
    tar_y=min(max(tar_y,100),9900);

    int a;
    for (a=0;a<PINGMAX-2;a++) {
      pingx[a]=pingx[a+1];pingy[a]=pingy[a+1];pingt[a]=pingt[a+1];
      pingd[a]=pingd[a+1];pingr[a]=pingr[a+1];pingw[a]=pingw[a+1];
    }
    pingx[PINGTOP]=tar_x;pingy[PINGTOP]=tar_y;pingt[PINGTOP]=t;
    pingd[PINGTOP]=scandir;pingr[PINGTOP]=scanran;pingw[a]=scan_wid;

    tar_dtime=pingt[PINGTOP]-pingt[0];
    tar_time=pingt[PINGTOP];

    if (pingt[PINGTOP]-pingt[0]==0) {
      pingx[0]=pingx[PINGTOP];pingy[0]=pingy[PINGTOP];pingd[0]=pingd[PINGTOP]-1;
      pingr[0]=pingr[PINGTOP];pingw[0]=pingw[PINGTOP];pingt[0]=pingt[PINGTOP]-100;
      tar_dtime=pingt[PINGTOP]-pingt[0];
    }



    double dran=(pingr[PINGTOP]-pingr[0])/tar_dtime;
    if (dran==0) dran=1;
    int pingdir=atan(100/dran);
    int tar_head;
    int dang=pingd[PINGTOP]-pingd[0];
    if (dran>0 && dang<0) tar_head=180-(scandir+pingdir);
    else tar_head=pingdir-scandir;
    tar_dx=(int)(cos(tar_head)*100);
    tar_dy=(int)(sin(tar_head)*100);
printlog("finished scan math\n");

  }
heading();
}


void next() {
  doscan2();
  if (time()>volly[0])   doshoot();
  if (!speed() || time()>nextmovetime)    domove();
}


int main() {
printlog ("starting\n");
  lastdam=damage();
  tar_x=CENTER;
  tar_y=CENTER;
  domove();
  while (1) {next();}
}
END

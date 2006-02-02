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
**  Date:       4/29/99
**  Version:    6.0
*******************************************************/
BEGIN
//BEGIN source verbose
/****************************************************
**  Program:    PolarBear
**  Author:     Aaron Judd
**  Date:       4/29/99
**  Version:    6.0
*******************************************************/

#include "robots.h"

#define PINGMAX 10
/*************VARS VARS VARS*****************/
int addtime=100;
int MINDIST=2000;
int MAXTARSPEED=75;
int MINTARSPEED=0;

double MYPI=3.1415926535897932384626433832795;
int MAXSCANDELAY=100;
int MAXSIZE=10000;
int BORDER=400;
int CENTER=MAXSIZE/2;
int MAXVOLLY=3;

//main vars
int nextmovetime,nextshottime,nextscantime;

//shoot vars
int volly[]={0,0,0},shotran,shotdir;

//scan vars
int SAFEZONE=205;//1 point damage
int clockwise=0;
int minscan=0;
int scan_wid=10;
int found=0;
int tracking=0;
int lost=0;




/****************MATH MATH MATH*********************/
//consider myabs for speed up over #define abs in robots file
//double myabs(double a) {return (a<0 ? -a : a);}

int max(double x,double y) { return (int)(x>y ? x : y);}
int min(double x,double y) { return (int)(x<y ? x : y);}
int dist(double x,double y,double x2, double y2) {
  int  dx=(int)(x-x2);
  int dy=(int)(y-y2);
  return (int)sqrt(dx*dx+dy*dy);
}
//double acos(double d) {}
//double asin(double d) {}
double fsin(double d) {
  int id=(int)d;int tid=(d>id ? 1 : -1)+id;
  double ds=abs(sin(tid)-sin(id));return sin(id)+ds*(d-id);
}
double fcos(double d) {
  int id=(int)d;int tid=(d>id ? 1 : -1)+id;
  double ds=abs(cos(tid)-cos(id));return cos(id)+ds*(d-id);
}
double ftan(double d) {
  int id=(int)d;int tid=(d>id ? 1 : -1)+id;
  double ds=abs(tan(tid)-tan(id));return tan(id)+ds*(d-id);
}
int nearestAngleTo(int base,int one,int two) {
  one=(one%360+360)%360;
  two=(two%360+360)%360;
  int da=abs(base-one);
  int db=abs(base-two);
  if (da>180) da=360-da;
  if (db>180) db=360-db;
  int near,far;
  if (da<db) {near=one;far=two;}
  else {near=two;far=one;}
  return near;
}
int farthestAngleTo(int base,int one,int two) {
  one=(one%360+360)%360;
  two=(two%360+360)%360;
  int da=abs(base-one);
  int db=abs(base-two);
  if (da>180) da=360-da;
  if (db>180) db=360-db;
  int near,far;
  if (da<db) {near=one;far=two;}
  else {near=two;far=one;}
  return far;
}





/****************CLASSES CLASSES ****************/
class Point {
  public: 
  int x,y;

  Point() {x=y=CENTER;}
  Point(int a,int b) {x=a;y=b;}
  Point(double a, double b) {x=(int)a;y=(int)b;}
  int getAngleTo(Point p) {return atan2(p.y-y,p.x-x);}
  int getDistanceTo(Point p) {return dist(p.y,p.x,y,x);}
  int inside(Point UL,Point LR) {
    return ((x<UL.x || x<LR.x) && (x>UL.x || x>LR.x) && (y<UL.y || y<LR.y) && (y>UL.y || y>LR.y));
  }
} CENTERPOINT(CENTER,CENTER);

class ScanPoint{
  public:
  int stime,dir,width,range;//known at scan time
  Point me;
  
  Point pos;//dirived from each scan
  double accuracy;

  ScanPoint() {
  }
  ScanPoint(Point &m, int &t, int &d, int &w, int &r) {
    me=m;stime=t;dir=d;width=w;range=r;
    if (width<1) width=1;
    pos.x=(int)(me.x+cos(dir)*range);
    pos.y=(int)(me.y+sin(dir)*range);

    Point UL,LR;//uperleft lowerright
    Point Clock,CClock;

    CClock.y=(int)(pos.y+fsin(dir-width-.5)*range);
    CClock.x=(int)(pos.x+fcos(dir-width-.5)*range);
    Clock.y=(int)(pos.y+fsin(dir+width+.5)*range);
    Clock.x=(int)(pos.x+fcos(dir+width+.5)*range);

    UL.x=min(Clock.x,min(CClock.x,pos.x));
    UL.y=min(Clock.y,min(CClock.y,pos.y));
    LR.x=max(Clock.x,max(CClock.x,pos.x));
    LR.y=max(Clock.y,max(CClock.y,pos.y));
    accuracy=UL.getDistanceTo(LR);
  }

//accuracy degrades over time 
//  known vel and dir change 20*turns
//  accuracy = best accuracy + 20 worst case / turn
  int getAccuracy(int atTime) {
     int tc=(atTime-stime)*20/100;
     tc=tc*tc;
     return (int)(accuracy+tc);
  }

};

class Memory {
  public:

  int allPingCount;
  ScanPoint allPing[PINGMAX];

  Memory() {allPingCount=0;}

  //total average position
  //recent average position
  // variance
  //notice stationary (average position
  //notice min max area covered
  //notice charging/retreating

  void addScan(ScanPoint &p) {
    if (allPingCount<PINGMAX) {
      allPing[allPingCount++]=p;
    }
    else {
      int a;
      for (a=0;a<PINGMAX-1;a++) {
        allPing[a]=allPing[a+1];
      }
      allPing[PINGMAX-1]=p;
    }
  //propagate back loop -> p.calcPrev(allPing[PINGMAX-2]);
  //propagate forward loop -> allPing[PINGMAX-2].calcNext(p);
  }

  Point getTargetLastSeen() {
    if (allPingCount<=0) return CENTERPOINT;
    else return allPing[allPingCount-1].pos;
  }

  Point getTargetShoot() {
    if (allPingCount==0) return CENTERPOINT;
    if (allPingCount==1) return allPing[0].pos;
//get two most accurate
    int a;
    int b1=allPingCount-1,b2=allPingCount-2;

    int t=getTime();
    for (a=0;a<allPingCount-1;a++) {
      if (allPing[a].getAccuracy(t)<allPing[b1].getAccuracy(t)) {
        b2=b1;b1=a;
      }
      else if (allPing[a].getAccuracy(t)<allPing[b2].getAccuracy(t)) {
        b2=a;
      }
    }

    if (b2<b1) {a=b1;b1=b2;b2=a;}//first ping first

//predict motion
    double dy=0,dx=0,dt;
    dt=(allPing[b2].stime-allPing[b1].stime);
    dy=(allPing[b2].pos.y-allPing[b1].pos.y)/dt;
    dx=(allPing[b2].pos.x-allPing[b1].pos.x)/dt;
//check max speed
    double ddist=dist((int)(dy*100),(int)(dx*100),0,0);
    if (ddist>MAXTARSPEED) {
      dy=dy/ddist*MAXTARSPEED;
      dx=dx/ddist*MAXTARSPEED;
    }
    if (ddist<MINTARSPEED) {
      dy=dy/ddist*MINTARSPEED;
      dx=dx/ddist*MINTARSPEED;
    }

//predict position
    int elapTime=getTime()-allPing[b2].stime;
    Point currPos;
    currPos.x=allPing[b2].pos.x+(int)(dx*elapTime);
    currPos.y=allPing[b2].pos.y+(int)(dy*elapTime);

    Point me(getPos_x(),getPos_y());   
    int range=me.getDistanceTo(currPos);
    Point newPos(currPos.x+range/10*dx,currPos.y+range/10*dy);
    return newPos;
  }

  int _loc_x,_loc_y,_speed,_time,_heading,_damage;
  void getState() {
    _loc_x=loc_x();
    _loc_y=loc_y();
    _speed=speed();
    _heading=heading();
    _time=time();
    _damage=damage();
  }
  int getSpeed() {return _speed;}
  int getTime() {return _time;}
  int getPos_x() {return _loc_x;}
  int getPos_y() {return _loc_y;}
  int getDamage() {return _damage;}
  int getHeading() {return _heading;}
} memory;




/*********************MOVE MOVE MOVE***********************/
int turntime(int oldangle,int newangle) {
  return (int)(abs(newangle-oldangle)%180/15.0*100);
}
int acceltime(int oldspeed,int newspeed=0) {
  return (int)(abs(newspeed-oldspeed)/20.0*100);
}
int speedturntime(int oldspeed, int newspeed, int olddir, int newdir) {
  int at=0,tt=0,vt=0,ft=0;
  if (newdir!=olddir) {
    if (newspeed>50) {ft+=acceltime(newspeed-50);newspeed=50;}
    if (newspeed<-50) {ft+=acceltime(newspeed+50);newspeed=-50;}
    if (oldspeed>50) {ft+=acceltime(oldspeed-50);oldspeed=50;}
    if (oldspeed<-50) {ft+=acceltime(oldspeed+50);oldspeed=-50;}
    tt=turntime(olddir,newdir);
  }
  if (newspeed!=oldspeed)
    at=acceltime(newspeed,oldspeed);
  return (max(at,tt)+ft);
}

int crash(Point me,int newspeed, int mydir, int time) {
  Point p((int)(me.x+cos(mydir)*newspeed*time/100),(int)(me.y+sin(mydir)*newspeed*time/100));
  return (p.x<BORDER || p.y<BORDER || p.x>MAXSIZE-BORDER || p.y>MAXSIZE-BORDER);
}


//tangential to target forward and reverse.
void domove(){
  Point tar=memory.getTargetLastSeen();
  Point me(memory.getPos_x(),memory.getPos_y());
  int h=memory.getHeading();
  int s=memory.getSpeed();

  int dir=me.getAngleTo(tar);
  int fordir,revdir;

  int dist=me.getDistanceTo(tar);

  int comp=45;
  fordir=nearestAngleTo(memory.getHeading(),dir+90,dir-90);
  fordir=nearestAngleTo(dir,fordir+comp,fordir-comp);
  revdir=farthestAngleTo(dir,fordir+comp,fordir-comp);

if (dist<MINDIST) {
   int ttt=fordir;fordir=revdir;revdir=ttt;
}

  int myspeed1,mydir1,myspeed2,mydir2;
  if (s<0) {
    myspeed1=100;mydir1=fordir;
    myspeed2=-75;mydir2=memory.getHeading();
  }
  else {
    myspeed1=-75;mydir1=revdir;
    myspeed2=100;mydir2=memory.getHeading();
  }


  int time1=acceltime(memory.getSpeed(),myspeed1)+addtime;
  int time2=acceltime(memory.getSpeed(),myspeed2)+addtime;

  int setspeed,setdir,settime;

  if (!crash(me,myspeed1,mydir1,addtime)) {
    setspeed=myspeed1;setdir=mydir1;settime=time1;
  }
  else if (!crash(me,myspeed2,mydir2,addtime)) {
    setspeed=myspeed2;setdir=mydir2;settime=time2;
  }
  else {
    setspeed=myspeed1;
    settime=time1;
    if (setspeed<0)
      setdir=me.getAngleTo(CENTERPOINT)+180;
    else
      setdir=me.getAngleTo(CENTERPOINT);
  }

  nextmovetime=time()+addtime+settime;

  if (memory.getSpeed()==setspeed && memory.getHeading()==setdir) return;
  drive(setdir,setspeed);

}




/**************** SHOOT  SHOOT ********************/
void doshoot() {

  Point me(memory.getPos_x(),memory.getPos_y());
  Point tar=memory.getTargetShoot();
//don't shoot off map
  if (tar.x>MAXSIZE-150) tar.x=MAXSIZE-150;
  if (tar.x<150) tar.x=150;
  if (tar.y>MAXSIZE-150) tar.y=MAXSIZE-150;
  if (tar.y<150) tar.y=150;

  shotran=me.getDistanceTo(tar);
  shotdir=me.getAngleTo(tar);
  int realran=min(shotran,7000);
  int shottime=realran/10;

  if (shotran<SAFEZONE) shotran=SAFEZONE;
  if (shotran<7400) {
    if (cannon(shotdir,realran)) {
      volly[0]=volly[1];
      volly[1]=volly[2];
      volly[2]=memory.getTime()+shottime;
    }
  }
//delay shooting until next possible in range
  else {
    volly[0]=volly[1];
    volly[1]=volly[2];
    volly[2]=memory.getTime()+(shotran-7400)/2;
  }

}




/********************SCAN SCAN SCAN********************/
void doscan() {
  Point me(memory.getPos_x(),memory.getPos_y());
  int scandir=me.getAngleTo(memory.getTargetLastSeen());
  if (!found) {
    scan_wid=10;
    lost++;clockwise++;
    scandir+=scan_wid*2*(lost/2)*(clockwise%2 ? 1 : -1);
  }
  else {
    int dd=max(me.getDistanceTo(memory.getTargetLastSeen()),1);
    if (tracking) {
      lost=0;
      int minscan=(int)((memory.getSpeed()*sin(memory.getHeading()-scandir)+100)*180.0/MYPI/dd);
//add compensation for his tangential speed
      scan_wid=max(scan_wid/2,minscan);
    }
    else {
      lost++;
      if (lost>1) clockwise++;
      scandir+=scan_wid*((lost+1)/2)*(clockwise%2 ? 1 : -1);
      scan_wid+=1;// *2???
      if (lost>4) scan_wid=10;
      scan_wid=min(scan_wid,10);
    }
  }
  
  int x=memory.getPos_x(),y=memory.getPos_y(),t=memory.getTime();
  int scanran=scan(scandir,scan_wid);
  if (!scanran) {
    nextscantime=time();
    tracking=0;
  }
  else {
    tracking=1;
    if (!found) {found=1;}
    nextscantime=time()+MAXSCANDELAY;
    Point me(x,y);
    ScanPoint s(me,t,scandir,scan_wid,scanran);
    memory.addScan(s);
  }
}


/********************MAIN MAIN MAIN*******************/
int main() {
  memory.getState();
  domove();
  while (1) {
    memory.getState();
    if (!memory.getSpeed() || memory.getTime()>nextmovetime) domove();
    else if (memory.getTime()>nextscantime) doscan();
    else if (memory.getTime()>volly[0] && found) doshoot();
    else doscan();
  }
}
END

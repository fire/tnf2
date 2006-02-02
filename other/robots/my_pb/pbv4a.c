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
**  Version:    4.0
**              Replaced version 3.0 at age 271 which 
**              had fallen to position 9/50
**              Debut at position 7/50.
**  History:
**  Version 1.0 (10/1/98)
**              First working version.  
**              Debut at position 18/20.
**  Version 2.0 (12/23/98)
**              Replaced version 1.0 at age 2.
**              Debut at position 7/20.
**  Version 3.0 (3/20/99)
**              Replaced version 2.0 at age 15 which
**              had falled to position 12/20.
**              Debut at position 6.
*******************************************************/
BEGIN
//BEGIN source verbose
/****************************************************
**  Program:    PolarBear
**  Author:     Aaron Judd
**  Date:       4/29/99
**  Version:    4.0
**              Replaced version 3.0 at age 271 which 
**              had fallen to position 9/50
*******************************************************/

#include "robots.h"

#define PINGMAX 10
/*************VARS VARS VARS*****************/
double MYPI=3.1415926535897932384626433832795;
int MAXSCANDELAY=200;
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
  int da=abs(base-one)%360;
  int db=abs(base-two)%360;
  if (da<db) return one;
  else return two;
}
int farthestAngleTo(int base,int one,int two) {
  int da=abs(base-one)%360;
  int db=abs(base-two)%360;
  if (da>db) return one;
  else return two;
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
  Point UL,LR;//uperleft lowerright
  Point Clock,CClock;
  double accuracy;

  int maxspeed,minspeed;
  int estinterceptspeed;
  int esttangentialspeed;
  int dirmax,dirmin;
  int estspeed,estdir;

  ScanPoint() {
    maxspeed=100;minspeed=0;dirmax=180;dirmin=-180;
  }
  ScanPoint(Point &m, int &t, int &d, int &w, int &r) {
    maxspeed=100;minspeed=0;dirmax=180;dirmin=-180;
    me=m;stime=t;dir=d;width=w;range=r;
    if (width<1) width=1;
    pos.x=(int)(me.x+cos(dir)*range);
    pos.y=(int)(me.y+sin(dir)*range);

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

  void calcPrev(ScanPoint &p) {
    maxspeed=min(stime*20/100,maxspeed);
    maxspeed=min(100,p.maxspeed+20);
    minspeed=max(p.minspeed-20,0);
    double dt=stime-p.stime;
    double dx=(pos.x-p.pos.x)/dt;
    if (dx==0) dx=.00001;
    double dy=(pos.y-p.pos.y)/dt;
    int maxdist=max(dist(UL.x,UL.y,p.LR.x,p.LR.y),dist(UL.x,LR.y,p.LR.x,p.UL.y));
    maxspeed=min(maxspeed,(maxdist-.5*20*dt*dt)/dt+dt*20/100);
    estspeed=100*dist(dx*100,dy*100,0,0)/100;
    estdir=atan(dy/dx);
    int tomedir=me.getAngleTo(pos);
    estinterceptspeed=(int)(sin(tomedir-estdir)*estspeed);
    esttangentialspeed=(int)(cos(tomedir-estdir)*estspeed); 
    if (!p.UL.inside(UL,LR) && !p.LR.inside(UL,LR)) {
      int mindist=min(dist(UL.x,UL.y,p.LR.x,p.LR.y),dist(UL.x,LR.y,p.LR.x,p.UL.y));
      minspeed=max(minspeed,(mindist+.5*20*dt*dt)/dt-dt*20/100);
      dirmin=UL.getAngleTo(p.LR);
      dirmax=LR.getAngleTo(p.UL);
    }
  }

  void calcNext(ScanPoint &p) {p=p;}

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
  Point estpos;
  int estspeed,estdir;
  double estdx,estdy;

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
    int MAXTARSPEED=75;
if (ddist>MAXTARSPEED) {//max speed is 100
      dy=dy/ddist*MAXTARSPEED;
      dx=dx/ddist*MAXTARSPEED;
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

  int comp=0;
  if (dist>5000) {//too far
    comp=min(5*((dist-5000)/300),20);
  }
  if (dist<4000) {//too close
    comp=min(5*((4000-dist)/300),20);
  }
  fordir=nearestAngleTo(memory.getHeading(),dir+90,dir-90);
  fordir=nearestAngleTo(dir,fordir+comp,fordir-comp);
  revdir=farthestAngleTo(dir,fordir+comp,fordir-comp);


  int myspeed1,mydir1,myspeed2,mydir2;
  if (s<0) {
    myspeed1=100;mydir1=fordir;
    myspeed2=-75;mydir2=memory.getHeading();
  }
  else {
    myspeed1=-75;mydir1=revdir;
    myspeed2=100;mydir2=memory.getHeading();
  }

  int addtime=200;

  int time1=acceltime(memory.getSpeed(),myspeed1)+addtime;
  int time2=acceltime(memory.getSpeed(),myspeed2)+addtime;

  int setspeed,setdir,settime;

  if (!crash(me,myspeed1,mydir1,time1)) {
    setspeed=myspeed1;setdir=mydir1;settime=time1;
  }
  else if (!crash(me,myspeed2,mydir2,time2)) {
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

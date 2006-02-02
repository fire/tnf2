/*
BASED on finder for superior tracking
working on avoidance so must know where target is.
*/


#include "robots.h"
#define max(x,y) ((x)>(y)?(x):(y))
#define min(x,y) ((x)<(y)?(x):(y))

//movetype 0=stop 1=run 2=tan 3=tangeforw/rev 4=runtosafestcorner 5=chargehit 6=chargemiss 
//collisiontype 0=stop  1=redrive  2=followwall 3=rev 4=stop until remove
//movetype=1;collisiontype=2;//run away and followwalls
//movetype=2;collisiontype=3;//circle or moon
//movetype=3;collisiontype=1;//short for/ref arc
//moveypte=4;collisiontype=4;//

//int movetype=0;int collisiontype=0;//stop and stop basic tart	//alive for 21,70,273
//int movetype=1;int collisiontype=2;//run and then follow wall	//alive for 34,173,505
//int movetype=2;int collisiontype=3;//tangent reverse 		//alive for 30,105,260
int movetype=3;int collisiontype=3;//jiggly tangent autoreverse	//alive for 15,66,242
//int movetype=4;int collisiontype=4;//run to safest corner	//alive for 24,165,783
//int movetype=5;int collisiontype=1;//charge and hit		//alive for 16,58,109
//int movetype=6;int collisiontype=1;//charge and miss 		//alive for 21,70,182 

int scandir = 0;
int scandist = 0;
int delaytracking = false;
int SAFTY=205;
int MAXSCANDELAY=100;
int MAXRANGE=8000;//7000+200blastradious+800 enemy position change
int TARGETINGTIME=3;//cycles to calculate target
int MAXTIME=180000;
int DURATION=100;//FORWARD REVERSE TIME AT CONSTANT SPEED, RECHECK FOR CHARGING
int ORBITCYCLE=500;//TANGENT REPLOT and DRIVE
int scantime=0;
int scanlog=0;
int movelog=1;
int cannonlog=0;

#define MAXPING 3
int tarx[MAXPING],tary[MAXPING],tartime[MAXPING];
int currping=0;

void logScan(int time,int fromx,int fromy,int dir,int dist) {
	if (scanlog) printlog("\nlog new Scan at %d from %d,%d in dir %d found at distance %d",time,fromx,fromy,dir,dist);
	if (scanlog) printlog("\nlog new Scan data at at %d (%d,%d)",time,fromx+(int)(cos(dir)*dist),fromy+(int)(sin(dir)*dist));
	if (currping==MAXPING) {
		for (int a=0;a<MAXPING;a++) {
			tarx[a]=tarx[a+1];
			tary[a]=tary[a+1];
			tartime[a]=tartime[a+1];
		}
	}
	currping=min(MAXPING,currping+1);

	tarx[currping-1]=fromx+(int)(cos(dir)*dist);
	tary[currping-1]=fromy+(int)(sin(dir)*dist);
	tartime[currping-1]=time;
	if (scanlog) printlog("\nlog result scan table");
	for (int a=0;a<MAXPING;a++) {
		if (scanlog) printlog("\nlog Scan %d = time %d at (%d,%d)",a,tartime[a],tarx[a],tary[a]);
	}
}
void
find ()
{
  scandir = atan2 (5000 - loc_y (), 5000 - loc_x ());
  int found = 0;		//false;
  int clock = 1;
  int attemp = 1;
  while (!found) {
	int t=time();
	int x=loc_x();
	int y=loc_y();
    scandist = scan (scandir, 10);
    if (scandist > 0) {
      found = true;
      scantime=t+MAXSCANDELAY;
      logScan(t,x,y,scandir,scandist);
    }
    else {
      scandir += 10 * attemp * clock;
      clock *= -1;
      attemp++;
    }
  }
}
int SIZE=10000;
int CENTER=SIZE/2;
int guessx,guessy;
void guessLoc() {
	if (currping==0) {guessx=CENTER;guessy=CENTER;}
	else {guessx=tarx[currping-1];guessy=tary[currping-1];}
	if (scanlog) printlog("\nlog guess loc=%d,%d",guessx,guessy);
}
int preddx,preddy;
void predMove() {
	if (currping-1==0) {
		preddx=0;
		preddy=0;
		if (scanlog) printlog("\nlog cannot predict movement not enough data. pred move= ddx=0, ddy=0");
		return;
	}
	preddx=(tarx[currping-1]-tarx[0]);
	preddy=(tary[currping-1]-tary[0]);
	int dt=(tartime[currping-1]-tartime[0]);
	if (dt>0) {
		preddx/=dt;
		preddy/=dt;
	}
	if (scanlog) printlog("\nlog pred move= ddx=%d, ddy=%d",preddx,preddy);
}
int tardir,tarran,eta;
void predTar(int time,int x,int y,int speed) {
	guessLoc();
	predMove();
	tarran=sqrt( (guessx-x)*(guessx-x)+(guessy-y)*(guessy-y));
	tarran=sqrt( (guessx+preddx*tarran/1000-x)*(guessx+preddx*tarran/1000-x)+
			(guessy+preddy*tarran/1000-y)*(guessy+preddy*tarran/1000-y));
	tardir=atan2(guessy+preddy*tarran/1000-y,guessx+preddx*tarran/1000-x);
	int flighttime=(tarran+speed)/10;
	eta=time+flighttime+TARGETINGTIME;
	if (scanlog) printlog("\nlog tarran=%d speed=%d time=%d",tarran,speed,time);
	if (scanlog) printlog("\nlog pred tar loc=%d,%d",(guessx+preddx*tarran/1000),(guessy+preddy*tarran/1000));
	if (scanlog) printlog("\nlog pred tar=cannon(%d,%d) eta %d flighttime %d",tardir,tarran,eta,flighttime);
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

void
track ()
{
//recalc scan based on guessloc
  guessLoc();
  int t=time();
  int x=loc_x();
  int y=loc_y();
  scandir=atan2(guessy-y,guessx-x);
  int minscan = max (1, (int) (100 * 180.0 / 3.14159 / scandist));//+1 why was that there?
//if (scanlog) printlog("\nlog minscan = %d",minscan);
  if (lastfound) {
    lastwid = (int) max (minscan, lastwid * focus);
    if (allowdrift)
      scandir += driftclock * (lastwid > 1);	//if width=1 don't drift
    scandist = scan (scandir, lastwid);
    if (scandist != 0) {
      lastfound = 1;
      attemp = 1;
      scantime=time()+MAXSCANDELAY;
      logScan(t,x,y,scandir,scandist);
    }
    else {
      lastfound = 0;
      attemp = 1;
    }
  }
  else {
    scandir += searchclock * lastwid * attemp;
    if (attemp % 2 == 0) {
      int newwid = (int) min (10, (lastwid * dim));
      //move scandir so no overlap and no gap on second
      scandir = scandir + searchclock * (newwid - lastwid) / 2;
      lastwid = newwid;
    }

    int t=time();
    int x=loc_x();
    int y=loc_y();
    scandist = scan (scandir, lastwid);
    if (scandist != 0) {
      lastfound = 1;
      driftclock = searchclock;
      scantime=time()+MAXSCANDELAY;
      logScan(t,x,y,scandir,scandist);
    }
    else {
      //scandir+=lastwid*attemp*searchclock;
      searchclock *= -1;
      driftclock *= -1;
      attemp++;
    }
  }
}

int STOPTIME=500;
int movedir=0;
int movespeed=0;
int drivetime=0;
int SAFTYMOVE=50;

//0=fullstop  1=redrive  2=followwall 3=rev 4=stop until remove
void moveCheck(int x,int y) {
	int nx=x+(int)(movespeed*cos(movedir)*STOPTIME/100);
	int ny=y+(int)(movespeed*sin(movedir)*STOPTIME/100);
	if (nx==x && x<SAFTYMOVE) nx=SAFTYMOVE;//position okay because not moving
	if (nx==x && x<SIZE-SAFTYMOVE) nx=SIZE-SAFTYMOVE;//position okay because not moving
	if (ny==y && y<SAFTYMOVE) ny=SAFTYMOVE;//position okay because not moving
	if (ny==y && y<SIZE-SAFTYMOVE) ny=SIZE-SAFTYMOVE;//position okay because not moving
	int spd=speed();
	if (spd==0 && x>SAFTYMOVE && y>SAFTYMOVE && x<SIZE-SAFTYMOVE && y<SIZE-SAFTYMOVE && collisiontype!=0 && collisiontype!=4) {
		//robot collision!
		if (movelog) printlog("\nlog WARNING robot collision detected");
	}
	else if (collisiontype!=0 && spd==0 && collisiontype!=4 ) {
		if (movelog) printlog("\nlog WARNING wall collision");
		collisiontype==0;
	}
	if (nx<SAFTYMOVE || ny<SAFTYMOVE || nx>SIZE-SAFTYMOVE || ny>SIZE-SAFTYMOVE) {
		if (movelog) printlog("\nlog WARNING COLLISION imminate.  Currently at (%d,%d) collision at (%d,%d)",x,y,nx,ny);
		if (collisiontype==0) {
			drive(movedir,0);
			drivetime=MAXTIME;
			movetype=0;
		}
		else if (collisiontype==1){//execute normal move immediately
			drivetime=0;
		}
		else if (collisiontype==2) {//followwall closely
			if (movelog) printlog("\nlog follow wall");
			//movedir=(movedir+720)%360;//fix to posnum 0-360
			int newx,newy;
			if (nx<CENTER && ny<CENTER) {
				newx=SAFTYMOVE;
				newy=SIZE-SAFTYMOVE;
			}
			else if (nx>CENTER && ny<CENTER) {
				newx=SAFTYMOVE;
				newy=SAFTYMOVE;
			}
			else if (nx<CENTER && ny>CENTER) {
				newx=SIZE-SAFTYMOVE;
				newy=SIZE-SAFTYMOVE;
			}
			else if (nx>CENTER && ny>CENTER) {
				newx=SIZE-SAFTYMOVE;
				newy=SAFTYMOVE;
			}
			if (movelog) printlog("\nlog move to (%d,%d)",newx,newy);
			movedir=atan2(newy-y,newx-x); //(1,0)
			if (movelog) printlog("\nlog change dir to %d",movedir);
			drive(movedir,movespeed);
			drivetime=MAXTIME;
		}
		else if (collisiontype==3){//reverse
			movespeed=(movespeed<0?100:-75);
			drive(movedir,movespeed);
		}
		else if (collisiontype==4) {
			drive(movedir,0);
		}
		
	}
}

//movetype 0=stop 1=run 2=tan 3=tangeforw/rev 4=runtosafestcorner 5=chargehit 6=chargemiss

void move(int x,int y) {
	guessLoc();
	if (movetype==0) {drivetime=MAXTIME;return;}//stop
	else if (movetype==1) {//run away
		int tardir=atan2(guessy-y,guessx-x);
		movedir=180+tardir;
		movespeed=100;
		drivetime=MAXTIME;
		if (movelog) printlog("\nlog running");
		if (movelog) printlog("\nlog target in dir %d. run away in dir %d",tardir,movedir);
	}
	else if (movetype==2) {//tangent
		int tardir=atan2(guessy-y,guessx-x);
		movedir=90+tardir;
		movespeed=100;
		drivetime=ORBITCYCLE;
		if (movelog) printlog("\nlog move tan");
	}
	else if (movetype==3) {//tang w for/rev
		int tardir=atan2(guessy-y,guessx-x);
		movedir=90+tardir;
		int newmovespeed=(movespeed<=0?100:-75);
		drivetime=(abs(movespeed-newmovespeed)/20)*100+DURATION;
		movespeed=newmovespeed;
		if (movelog) printlog("\nlog move tan with fow rev %d %d",movedir,movespeed);
	}
	else if (movetype==4) {//safest corner
		int nx=(guessx>CENTER?SAFTYMOVE:SIZE-SAFTYMOVE);
		int ny=(guessy>CENTER?SAFTYMOVE:SIZE-SAFTYMOVE);
		if (x-guessx>CENTER && y-guessy>CENTER) {
			drivetime=sqrt((guessy-y)*(guessy-y)+(guessx-x)*(guessx-x))/2;//time for you to get to me
			collisiontype=4;//stop until remove at wall
			return;//don't move its okay just wait for safe wall
		}
		else {
			movedir=atan2(ny-y,nx-x);
			movespeed=100;
			drivetime=sqrt((ny-y)*(ny-y)+(nx-x)*(nx-x));
		}
	}
	else if (movetype==5) {//chage and hit
		movedir=atan2(guessy-y,guessx-x)+rand(2)-1;//miss by 1 degree
		movespeed=100;
		drivetime=max(sqrt((guessy-y)*(guessy-y)+(guessx-x)*(guessx-x))/2,DURATION);
		if (movelog) printlog("\nlog charge to hit");
	}
	else if (movetype==5) {//chage and hit
		movedir=atan2(guessy-y,guessx-x);
		movespeed=100;
		drivetime=max(sqrt((guessy-y)*(guessy-y)+(guessx-x)*(guessx-x))/2,DURATION);
		if (movelog) printlog("\nlog charge to miss");
	}
	drive(movedir,movespeed);	
	
}

main ()
{
  find ();
  while (1) {
	int t=time();
	int x=loc_x();
	int y=loc_y();
	moveCheck(x,y);//avoid collision
	if (scantime<t) {
		track();
	}
	else if (drivetime<t) {
		move(x,y);
	}
	else {
		track ();
	}
  }
}

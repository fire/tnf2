/*
BASED on finder for superior tracking
basic scanlog
basic shooting
*/

#include "robots.h"
#define max(x,y) ((x)>(y)?(x):(y))
#define min(x,y) ((x)<(y)?(x):(y))
int scandir = 0;
int scandist = 0;
int delaytracking = false;
int SAFTY=205;
int MAXSCANDELAY=100;
int MAXRANGE=8000;//7000+200blastradious+800 enemy position change
int TARGETINGTIME=3;//cycles to calculate target
int scantime=0;


#define MAXPING 3
int tarx[MAXPING],tary[MAXPING],tartime[MAXPING];
int currping=0;
int scanlog=0;
int cannonlog=0;

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
  int t=time();
  int x=loc_x();
  int y=loc_y();
  int scanAge=t-tartime[currping-1];
//recalc scan based on guessloc
  guessLoc();
  scandir=atan2(guessy-y,guessx-x);
  int minscan = max (1, (int) (100 * 180.0 / 3.14159 / scandist));//+1 why was that here?
//printlog("\nlog minscan = %d",minscan);
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

int vollytime[3];
void shoot(int time,int x,int y,int speed) {
	if (vollytime[0]>time) {
		if (cannonlog) printlog("\nlog cannot fire.  Shots already in air.");	
		return;
	}
	for (int a=0;a<2;a++) vollytime[a]=vollytime[a+1];
	predTar(time,x,y,speed);
	if (tarran>MAXRANGE) {track();vollytime[2]=time+800;return;}//don't shoot out of range
	tarran=max(SAFTY,min(7000,tarran));
	cannon(tardir,tarran);
	vollytime[2]=eta;
}
main ()
{
  find ();
  while (1) {
	int t=time();
	int x=loc_x();
	int y=loc_y();
	if (scantime<t) {
		track();
	}
	else if (vollytime[0]<t) {
		int s=speed();
		shoot(t,x,y,s);
	}
	else {
		track ();
	}
  }
}

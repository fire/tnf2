#include "robots.h"
//memory
int nextscantime=0;
int found=0;
int tracking=0;

int MAXSCANDELAY=150;
int lastScanDir,lastScanWidth=10;
int cylon=0;
int searchCount=1;

int driveEnabled=1;

int max(double x, double y) {return(int)(x > y?x:y);}
int min(double x, double y) {return(int)(x < y?x:y);}
int dist(double x, double y, double x2, double y2) {
	int dx = (int)(x - x2);
	int dy = (int)(y - y2);
	return(int)sqrt(dx * dx + dy * dy);
}

class Ping {
	protected:
	int x,y,range,bearing,width;
	public:
	int tarx,tary,speed,heading;
	int time;
	int minx,maxx;
	int miny,maxy;
	int minHead,maxHead;
	int minSpeed,maxSpeed;

	Ping() {
		tarx=0;tary=0;speed=0;heading=0;
		x=0;y=0;range=0;bearing=0;width=0;time=0;
		minSpeed=200;maxSpeed=0;
		minHead=360;maxHead=0;
	}

	Ping(int xx,int yy,int rran, int ddir,int wwidth,int ttime) {
		x=xx;
		y=yy;
		range=rran;
		bearing=ddir;
		width=wwidth;
		time=ttime;
		speed=0;
		heading=0;
		tarx=(int)(x+range*cos(bearing));
		tary=(int)(y+range*sin(bearing));
		//minx=(int)(x+range*cos(bearing-width/2));
		//maxx=(int)(x+range*cos(bearing+width/2));
		//miny=(int)(y+range*sin(bearing-width/2));
		//maxy=(int)(y+range*sin(bearing+width/2));
		//minSpeed=200;
		//minHead=360;
		//maxSpeed=0;
		//maxHead=0;
	}
	int print() {
		printlog("LOG ping %d,%d bearing %d range %d width %d tarloc %d,%d speed %d head %d\n",x,y,bearing,range,width,tarx,tary,speed,heading);
	}
};

int _x,_y,_speed,_heading,_time;

int pingCount=5;
Ping *p=new Ping[pingCount];
int recordAll(int dis,int dir,int wid) {
	for (int a=0;a<pingCount;a++) {
		p[a]=Ping(_x,_y,dis,dir,wid,_time+a);
	}
}
int record(int dis,int dir, int wid) {
	for (int a=0;a<pingCount-1;a++) {
		p[a]=p[a+1];
	}
	Ping m=Ping(_x,_y,dis,dir,wid,_time);
	printlog("LOG record tar found %d,%d \n",m.tarx,m.tary);
	p[pingCount-1]=m;
	Ping m1=p[pingCount-1];
	Ping m2=p[pingCount-2];

	m1.speed=(int)(100*dist(m1.tarx,m1.tary,m2.tarx,m2.tary)/(m1.time-m2.time));
	m1.speed=min(m1.speed,100);
	m1.speed=max(m1.speed,0);
	m1.heading=(int)(atan2(m1.tary-m2.tary,m1.tarx-m2.tarx));

	m1.print();
	return 1;
	//refine data
	for (int a=0;a<pingCount-1;a++) {
		Ping p1=p[a];
		for (int b=a+1;b<pingCount;b++) {
			Ping p2=p[b];
			if (p2.time==p1.time) continue;
			int speed=dist(p1.tarx,p1.tary,p2.tarx,p2.tary)/(p2.time-p1.time);
			int s1=dist(p1.minx,p1.miny,p2.minx,p2.miny)/(p2.time-p1.time);
			int s2=dist(p1.minx,p1.miny,p2.tarx,p2.tary)/(p2.time-p1.time);
			int s3=dist(p1.minx,p1.miny,p2.maxx,p2.maxy)/(p2.time-p1.time);
			int s4=dist(p1.maxx,p1.maxy,p2.minx,p2.miny)/(p2.time-p1.time);
			int s5=dist(p1.maxx,p1.maxy,p2.tarx,p2.tary)/(p2.time-p1.time);
			int s6=dist(p1.maxx,p1.maxy,p2.maxx,p2.maxy)/(p2.time-p1.time);
			printlog("LOG s1=%d\n",s1);
			printlog("LOG s2=%d\n",s2);
			printlog("LOG s3=%d\n",s3);
			printlog("LOG s4=%d\n",s4);
			printlog("LOG s5=%d\n",s5);
			printlog("LOG s6=%d\n",s6);
			p1.speed=speed;
			p1.minSpeed=min(100,p1.minSpeed);
			p1.minSpeed=min(p1.minSpeed,s1);
			p1.minSpeed=min(p1.minSpeed,s2);
			p1.minSpeed=min(p1.minSpeed,s3);
			p1.minSpeed=min(p1.minSpeed,s4);
			p1.minSpeed=min(p1.minSpeed,s5);
			p1.minSpeed=min(p1.minSpeed,s6);

			p1.maxSpeed=max(0,p1.maxSpeed);
			p1.maxSpeed=max(p1.maxSpeed,s1);
			p1.maxSpeed=max(p1.maxSpeed,s2);
			p1.maxSpeed=max(p1.maxSpeed,s3);
			p1.maxSpeed=max(p1.maxSpeed,s4);
			p1.maxSpeed=max(p1.maxSpeed,s5);
			p1.maxSpeed=max(p1.maxSpeed,s6);

			//int minHeading=
			//int maxHeading=
			//int avgHeading=
		}
	}
	//narrow old pings based
	//  calc min speed, min heading
	//  calc max speed, max heading
	//project onto all other pings
}


int updatePos() {
	_x = loc_x();
	_y = loc_y();
	_speed = speed();
	_heading=heading();//costs 1 cycle don't need it
	_time = time();
	//_damage=damage();//cost 1 cycle dont need it
}

int tarx,tary,tarspeed,tarheading;
int currRange,currBearing;

int calcTarget() {
	if (found!=0) {
	//guess curr location from scanns
		tarx=p[pingCount-1].tarx;
		tary=p[pingCount-1].tary;
		tarspeed=p[pingCount-1].speed;
		tarheading=p[pingCount-1].heading;
	}
	printlog("LOG tar pos %d,%d speed %d heading %d\n",tarx,tary,tarspeed,tarheading);
	currRange=(int)dist(_x,_y,tarx,tary);
	currBearing=(int)atan2(tary - _y, tarx - _x);
	//
	printlog("LOG tar range %d tar bearing %d\n",currRange,currBearing);
}


int tarRange,tarBearing;
int projTargetShoot() {
	int shotLeadDist=tarspeed*currRange/1000;
	int shotLeadX=(int)(tarspeed*cos(tarheading)*currRange/1000);
	int shotLeadY=(int)(tarspeed*sin(tarheading)*currRange/1000);
	tarRange=(int)dist(_x,_y,tarx+shotLeadX,tary+shotLeadY);
	tarBearing=(int)atan2(tary +shotLeadY - _y, tarx +shotLeadX - _x);
	printlog("LOG shot range %d tar bearing %d\n",tarRange,tarBearing);
}


int firstfind=1;
int scanHitCount=0;
int doscan() {
	int scandir=currBearing;
	int scanWidth=lastScanWidth;
//not found
	printlog("LOG do scan called\n");
	if (found==0) {
		scandir=scandir+lastScanWidth*(cylon%2==0?1:-1)*(searchCount/2);
		//cylon++;
		//cylon
		printlog("LOG scan type !found\n");
	}
//  tracking
	else if (tracking!=0) {
		scanHitCount++;
		scanWidth=scanWidth*2/3;
		//drift if wider than 1
		scandir=scandir;
		//drift on third hit
		if (scanHitCount%3==0) {
		//	cylon++;
			scandir+=(cylon%2==0?1:-1);
		}
		printlog("LOG scan type tracking\n");
	}
//  reacquiring
	else {
		scanHitCount=0;
		//try to drift for first reacquire
		if (searchCount==1) {
			scandir+=(cylon%2==0?1:-1);
			cylon++;
		}
		scandir=scandir+lastScanWidth*(cylon%2==0?1:-1)*(searchCount/2);
		scanWidth=scanWidth+=2;
		//cylon x2 and wider 1
		//check reverse x1
		//if (searchCount>5)  {
			//searchCount=0;
			//found=false;
		//}
		printlog("LOG scan type reacquiring %d\n",searchCount);
	}
	scanWidth=min(scanWidth,10);
	scanWidth=max(scanWidth,1);
	lastScanWidth=scanWidth;
	lastScanDir=scandir;
	int c=scan(scandir,scanWidth);
	printlog("LOG scan %d by %d found %d\n",scandir,scanWidth,c);
	if (c>0) {
		//fill pingcount on first find
		if (firstfind!=0) {
			recordAll(c,scandir,scanWidth);
			firstfind=0;
		}
		firstfind=false;
		record(c,scandir,scanWidth);
		if (!found && !tracking) cylon--;
		found=true;
		tracking=1;
		searchCount=0;
	}
	else {
		searchCount++;
		cylon++;
		tracking=0;
	}
	nextscantime=_time+MAXSCANDELAY;
}

int volley1=0,volley2=0,volley3=0;
int doshoot() {
	volley1=volley2;
	volley2=volley3;
	tarRange=max(140,tarRange);
	volley3=_time+tarRange/10;
	//avoid 3 quick followed by 3 quick
	//if (tarRange/10<300) volley3+=300;
	if (volley1==0) volley1=volley3;
	if (volley2==0) volley2=volley3;
	printlog("cannon called bearing %d range %d\n",tarBearing,tarRange);
	if (tarRange>7000) tarRange=7000;
	cannon(tarBearing,tarRange);
}

int nextMoveTime=0;
int domove() {
	if (driveEnabled!=0) drive(0,100);
	nextMoveTime=_time+100*20;
}

int readyToScan() {
	return nextscantime<_time;
}
int readyToShoot() {
	return (searchCount<6 && found && volley1<_time && tarRange<7500);
}
int readyToMove() {
	return driveEnabled && nextMoveTime<_time;
}

int main() {
	tarx=5000,tary=5000,tarspeed=0,tarheading=0;
	domove();
	while (1) {
		updatePos();
		calcTarget();
		projTargetShoot();
		if (readyToScan()) doscan();
		else if (readyToShoot()) doshoot();
		else if (readyToMove()) domove();
		else doscan();
	}
	return 0;
}

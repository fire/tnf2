//
// Combat.C  -- C++Robots arena simulator
//
// Copyright (C) 1997  Richard Rognlie <rrognlie@gamerz.net>
//                     All Rights Reserved
//  
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2 of the License, or any later
// version.
//
// If you do make any modifications, please drop Richard a note so that he
// can incorporate interesting patches into the baseline.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
// for more details. 
//
// For a copy of the GNU General Public License, visit
//       http://www.gnu.org/copyleft/gpl.html
// or write to the Free Software Foundation, Inc., 675 Mass Ave,
// Cambridge, MA 02139, USA.
// 

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>
#include <unistd.h>
#include <math.h>

#define abs(x)      ((x) < 0 ? -(x) : (x))

#define min(a,b)    ((a) < (b) ? (a) : (b))

#define max(a,b)    ((a) > (b) ? (a) : (b))

#define ONESEC 10000

int Dir(int dir)
{
	dir = dir%360;

	while (dir<0)
		dir += 360;

	return dir%360;
}

int Arc(int d1, int d2)
{
	int a;

	d1 = Dir(d1);
	d2 = Dir(d2);

	if (d2<d1)
		d2 += 360;

	a = d2-d1;
	if (a>180)
		return a-360;
	else
		return a;
}

struct Missile {
	int x,y;		// target location
	double time; 	// time <= 0.0 ==> detonated/inactive
};

struct Robot {
	char *name;
	int pid;
	long timeout;
	int in,out,err;
	double x,y;             // location					[ 0..10000 ]
	int dir;                // desired direction		[ 0..359 ]
	double cdir;			// current direction
	int speed;		        // desired speed			[ 0..100 ]
	double cspeed;			// current speed
	int scan;               // last direction scanned	[ 0..359 ]
	int damage;             // damage level				[ 0..100 ]
	Missile missile[3];     // missiles
};

enum { Match, Trace, Watch } dispMode = Match;

double total;
Robot *robots=NULL;
int   nrobots=0;

void plot(int x, int y, char c)
{
	int h = int(x)*79/10000;
	int v = 34-int(y)*(31-nrobots)/10000;

	switch (dispMode) {
	case Trace:
		if (c >= 'a' && c <= 'z') {
			printf("  %c Explosion at %4d,%4d\n",c+'A'-'a',x,y);
		}
		break;
	case Watch:
		printf("\033[%d;%dH%c",v+1,h+1,c);
		fflush(stdout);
		break;
	default:
		break;
	}
}

void die_gracefully(int x)
{
	for (int i=0 ; i<nrobots ; i++) {
		if (robots[i].pid) {
			kill(robots[i].pid,SIGTERM);
			robots[i].speed = robots[i].pid = 0;
			robots[i].cspeed = 0.0;
		}
	}
	exit(0);
}

void child_died(int x)
{
	int status;
	int pid = wait(&status);

	for (int i=0 ; i<nrobots ; i++) {
		if (pid == robots[i].pid) {
			robots[i].speed = robots[i].pid = 0;
			robots[i].cspeed = 0.0;
			robots[i].damage = 100;
		}
	}
	signal(SIGCHLD,child_died);	// so flg gets raised on next death...
}

int LoadRobot(char *name,Robot *robot)
{
	// validate that name exists...
	// return NULL if not..

	struct stat fs;
	
	if (stat(name,&fs) != 0)
		return 0;

	if ((fs.st_mode & S_IEXEC) == 0)
		return 0;

	robot->name = strdup(name);
	robot->pid = 0;

	return 1;
}

void StartRobot(Robot *robot,int i)
{
	int input[2];
	int output[2];
	int error[2];

	//		Initialize Arena for each Robot (shared memory segment)

	pipe(input);			// stdin to child
	pipe(output);			// stdout from child
	pipe(error);			// stderr from child
	
	int pid=fork();
	if (pid < 0) {
		fprintf(stderr,"Unable to start %s (can not fork process)\n",robot->name);
		exit(1);
	}

	if (!pid) {	// child
		close(input[1]);
		close(output[0]);
		close(error[0]);
		if (dup2(input[0],fileno(stdin)) < 0)
			fprintf(stderr,"Unable to dup2() stdin\n");
		if (dup2(output[1],fileno(stdout)) < 0)
			fprintf(stderr,"Unable to dup2() stdout\n");
		if (dup2(error[1],fileno(stderr)) < 0)
			fprintf(stderr,"Unable to dup2() stderr\n");

		// find and start the child...
#ifdef SPAWN
		char cmd[1024];
		sprintf(cmd,"%s/bin/spawn",getenv("PBMHOME"));
		execlp(cmd,"spawn",robot->name,NULL);
#else
		execlp(robot->name,robot->name,NULL);
#endif
		exit(1);		// should not get here...
	}

	// parent
	signal(SIGCHLD,child_died);

	robot->pid = pid;
	close(input[0]);	robot->in = input[1];
	close(output[1]);	robot->out = output[0];
	close(error[1]);	robot->err = error[0];

	//HERE HERE
	//robot->x = (random()%990)*10 + 50;
	//robot->y = (random()%990)*10 + 50;
	if (i%2==0) {
		robot->x = 3000;
		robot->y = 3000;
		robot->dir = robot->scan = 180;
	}
	else {
		robot->x = 7000;
		robot->y = 7000;
		robot->dir = robot->scan = 0;
	}
	robot->speed = robot->damage = 0;
	robot->cdir = robot->cspeed = 0.0;
	robot->timeout = 0L;

	for (int i=0 ; i<3 ; i++) {
		robot->missile[i].time = -0.1;
	}
}

//	scan    'a'+i+i
//	cannon  'b'+i+i
//	drive   'c'+i+i
//	damage  'd'
//	speed   'e'
//	heading 'f'
//	loc_x   'g'
//	loc_y   'h'
//	time    'i'
//	sqrt	'j'+i

int Scan(Robot *robot,int dir,int arc)
{
	int range=0;
	dir = Dir(dir);
	arc = abs(arc);

	if (arc > 10)	arc = 10;

	robot->scan = dir;

	for (int i=0 ; i<nrobots ; i++) {
		int dx,dy;
		int td;
		int tr;
		if (robot == robots+i || robots[i].damage >= 100)
			continue;

		dx = int (robots[i].x - robot->x);
		dy = int (robots[i].y - robot->y);

		if (dx || dy) {
			td = (int) (atan2(dy,dx)*180/M_PI+0.5);
			if (abs(Arc(td,dir)) <= arc) {
				tr = int(sqrt(dx*dx+dy*dy));
				if (!range || tr<range)
					range = tr;
			}
		}
	}

	robot->timeout = ONESEC/2;

	return range;
}
	
int Cannon(Robot *robot,int dir,int range)
{
	dir = Dir(dir);
	int i;
	int r = abs(int(robot->cspeed+0.5));
	int dx = (int) (range*cos(dir*M_PI/180));
	int dy = (int) (range*sin(dir*M_PI/180));

	robot->timeout = ONESEC/2;

	if (!r) r=1;

	for (i=0 ; i<3 ; i++) {
		if (robot->missile[i].time <= 0.0) {
			plot(robot->missile[i].x,robot->missile[i].y,' ');
			break;
		}
	}
	if (i==3 || range > 7000 || range < 0)
		return 0;

	robot->missile[i].x = int (robot->x) + dx + rand()%r - r/2;
	robot->missile[i].y = int (robot->y) + dy + rand()%r - r/2;
	robot->missile[i].time = max(double(range)/1000.0,.25);

	return 1;
}

int Drive(Robot *robot,int dir,int speed)
{
	if (speed > 100)	speed = 100;
	if (speed < -75)	speed = -75;

	robot->dir = Dir(dir);
	robot->speed = speed;

	robot->timeout = ONESEC/2;

	return 1;
}

void HandleRequest(int number, Robot *robot)
{
	char cmd;
	int i[2];
	int rc;

	int fd = robot->out;

	read(fd,&cmd,1);
	switch (cmd) {
	case 'a':
		read(fd,i,2*sizeof(int));
		rc = Scan(robot,i[0],i[1]);
		if (dispMode == Trace)	printf("  %c %7.2f scan(%3d,%2d) returns %d\n",'A'+number,total,i[0],i[1],rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'b':
		read(fd,i,2*sizeof(int));
		rc = Cannon(robot,i[0],i[1]);
		if (dispMode == Trace)	printf("  %c %7.2f cannon(%3d,%2d) returns %d\n",'A'+number,total,i[0],i[1],rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'c':
		read(fd,i,2*sizeof(int));
		rc = Drive(robot,i[0],i[1]);
		if (dispMode == Trace)	printf("  %c %7.2f drive(%3d,%2d) returns %d\n",'A'+number,total,i[0],i[1],rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'd':
		rc = robot->damage;
		if (dispMode == Trace)	printf("  %c %7.2f damage() returns %d\n",'A'+number,total,rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'e':
		rc = int(robot->cspeed+0.5);
		if (dispMode == Trace)	printf("  %c %7.2f speed() returns %d\n",'A'+number,total,rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'f':
		rc = int(robot->cdir+0.5);
		if (dispMode == Trace)	printf("  %c %7.2f heading() returns %d\n",'A'+number,total,rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'g':
		rc = int(robot->x+0.5);
		if (dispMode == Trace)	printf("  %c %7.2f loc_x() returns %d\n",'A'+number,total,rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'h':
		rc = int(robot->y+0.5);
		if (dispMode == Trace)	printf("  %c %7.2f loc_y() returns %d\n",'A'+number,total,rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'i':
		rc = int(total*100);
		if (dispMode == Trace)	printf("  %c %7.2f time() returns %d\n",'A'+number,total,rc);
		write(robot->in,&rc,sizeof(int));
		break;
	case 'j':
		read(fd,i,sizeof(int));
		rc = (int) sqrt((double)i[0]);
		write(robot->in,&rc,sizeof(int));
		break;
	default:
		break;
	}
}

void ErrLog(int number, Robot *robot)
{
	char msg[4096];

	int cr = read(robot->err,msg,4095);
	msg[cr] = '\0';

	if (dispMode == Trace) {
		printf("  %c %7.2f log: %s\n",'A'+number,total,msg);
	}
}

void DispRobots(char *msg)
{
	int i;
	static int cleared=0;
	static double nexttime=0.0;
	char line[256];
	static int dead[26] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

	if (dispMode == Match)	return;

	if (!cleared) {
		if (dispMode == Watch)	printf("\033[H\033[J");
		else {
			printf("          Name       Xpos Ypos dir cdir spd cspd scn dam\n");
			printf("    ---------------- ---- ---- --- ---- --- ---- --- ---\n");
		}
		cleared=1;
	}
	if (dispMode == Watch)	printf("\033[H");
	if (total >= nexttime) {
		nexttime += 1.0;
		printf("Time=%10.5f %s\n",total,msg);
		if (dispMode == Watch) {
			printf("          Name       Xpos Ypos dir cdir spd cspd scn dam\n");
			printf("    ---------------- ---- ---- --- ---- --- ---- --- ---\n");
		}
		for (i=0 ; i<nrobots ; i++) {
			if (!dead[i])
				if (dispMode == Watch)	printf("\033[%d;1H",i+4);
				printf("  %c %-16s %4.0f %4.0f %3d %4d %3d %4d %3d %3d",
					i+'A',
					robots[i].name,
					robots[i].x,robots[i].y,
					robots[i].dir,int(robots[i].cdir+0.5),
					robots[i].speed,int(robots[i].cspeed+0.5),
					robots[i].scan,robots[i].damage);
				for (int j=0 ; j<3 ; j++)
					if (robots[i].missile[j].time > 0.0)
						printf(" %5.2f",robots[i].missile[j].time);
					else
						printf("      ");
				printf("\n");
			if (robots[i].damage >= 100)	dead[i] = 1;
		}
	}
}

double AdjustSpeed(double cspeed,int speed, double dt)
{
	double ds = 20.0*dt;

	if (abs(cspeed-speed) < .1)
		cspeed = double(speed);
	else if (cspeed < speed)
		cspeed += min(ds,speed-cspeed);
	else
		cspeed -= min(ds,cspeed-speed);

	return cspeed;
}

double AdjustDir(double cdir, int dir, double dt)
{
	int arc=Arc(int(cdir+0.5),dir);
	double dd = 15.0*dt;

	if (arc) {
		if (cdir > dir)
			cdir -= min(abs(double(arc)),dd);
		else
			cdir += min(double(arc),dd);
	}

	while (cdir < 0.0)
		cdir += 360.0;

	while (cdir > 360.0)
		cdir -= 360.0;

	return cdir;

}

int Between(double x1, double y1, double x2, double y2, double x0, double y0)
{
	double tx,ty;
	/* is (x0,y0) between (x1,y1) - (x2,y2)? */
	if (x1 < x2) {
		tx = x1;	x1 = x2;	x2 = tx;
	}
	if (x0<x1 || x0>x2)
		return 0;

	if (y1 < y2) {
		ty = y1;	y1 = y2;	y2 = ty;
	}
	if (y0<y1 || y0>y2)
		return 0;

	return 1;
}

void Move(Robot *robot,int n,double dt)
{
	int i;
	double x = robot->x;
	double y = robot->y;
	int t;

	if (robots->damage >= 100)	return;

	x = robot->x + dt*robot->cspeed*cos(robot->cdir*M_PI/180);
	y = robot->y + dt*robot->cspeed*sin(robot->cdir*M_PI/180);
	if (x <= 0.0 || x >= 10000.0 || y <= 0.0 || y >= 10000.0) {
		/* collision with wall */
/* printf("Collision with wall\n"); */
		robot->damage += (int(robot->cspeed+0.5)+24)/25;
		robot->cspeed = 0.0;
		robot->speed = 0;
		if (x <= 0.0)		x = 10.0;
		if (x >= 10000.0)	x = 9990.0;
		if (y <= 0.0)		y = 10.0;
		if (y >= 10000.0)	y = 9990.0;
	}
	else {
		/* collision with other robots? */
		for (i=0 ; i<nrobots ; i++) {
			if (robot == robots+i)	continue;
			if (Between(robot->x,robot->y,x,y,robots[i].x,robots[i].y)) {
/* printf("Collision with robot\n"); */
				double dx = dt*robot->cspeed*cos(robot->cdir*M_PI/180) - dt*robots[i].cspeed*cos(robots[i].cdir*M_PI/180);
				double dy = dt*robot->cspeed*sin(robot->cdir*M_PI/180) - dt*robots[i].cspeed*sin(robots[i].cdir*M_PI/180);
				int d = ((int) sqrt(dx*dx+dy*dy)+24)/25;
				robot->damage += d;
				robot->cspeed = robot->speed = 0;
				robots[i].damage += d;
				robots[i].cspeed = robots[i].speed = 0;
			}
		}
	}

	plot(int(robot->x+.5),int(robot->y+.5),' ');
	robot->x = x;
	robot->y = y;

	if (robot->damage >= 100)
		return;

	if (Arc(int(robot->cdir+0.5),robot->dir)) {
		if (robot->cspeed > 50) {
			robot->cspeed = AdjustSpeed(robot->cspeed,50,dt);
			if (robot->cspeed == 50)
				robot->cdir = AdjustDir(robot->cdir,robot->dir,dt);
		}
		else if (robot->cspeed < -50) {
			robot->cspeed = AdjustSpeed(robot->cspeed,-50,dt);
			if (robot->cspeed == -50)
				robot->cdir = AdjustDir(robot->cdir,robot->dir,dt);
		}
		else {
			robot->cdir = AdjustDir(robot->cdir,robot->dir,dt);
			if (robot->cspeed < 0)
				robot->cspeed = AdjustSpeed(robot->cspeed,max(robot->speed,-50),dt);
			else
				robot->cspeed = AdjustSpeed(robot->cspeed,min(robot->speed, 50),dt);
		}
	}
	else if (robot->cspeed != robot->speed) {
		robot->cspeed = AdjustSpeed(robot->cspeed,robot->speed,dt);
	}

	plot(int(robot->x+.5),int(robot->y+.5),n+'A');
}

void Blast(Robot *robot,int n,double dt)
{
	int j,k;

	for (j=0 ; j<3 ; j++)
		if (robot->missile[j].time > 0.0) {
			robot->missile[j].time -= dt;
			if (robot->missile[j].time <= 0.0) {
				plot(robot->missile[j].x,robot->missile[j].y,n+'a');
				for (k=0 ; k<nrobots ; k++) {
					if (robots[k].damage < 100)  {
						int dx = robot->missile[j].x - int(robots[k].x);
						int dy = robot->missile[j].y - int(robots[k].y);
						int d = dx*dx+dy*dy;
						if (d<2500) {
/* printf("Boom.  +8\n"); */
							robots[k].damage += 8;
						}
						else if (d<10000) {
/* printf("Boom.  +4\n"); */
							robots[k].damage += 4;
						}
						else if (d<40000) {
/* printf("Boom.  +2\n"); */
							robots[k].damage += 2;
						}
						else if (d<160000) {
/* printf("Boom.  +1\n"); */
							robots[k].damage += 1;
						}
					}
				}
			}
		}
}

char *Combat(int nrobots, Robot robot[])
{
	int i,j,k;

	int n=nrobots;
	
	total = 0.0;

	int max_fd = 0;
	for (i=0 ; i<nrobots ; i++) {
		if (robot[i].out > max_fd)	max_fd = robot[i].out;
		if (robot[i].err > max_fd)	max_fd = robot[i].err;
	}

	DispRobots("");
	while (n>1 && total < 1800.0) {
		fd_set fds;
		int nfds;
		struct timeval last,now,tv;
		long timeout_usec = ONESEC;;
		double dt;

		FD_ZERO(&fds);
		nfds = 0;
		for (i=0 ; i<nrobots ; i++) {
			if (robot[i].damage<100) {
				if (robot[i].timeout == 0L) {
					FD_SET(robot[i].out,&fds);
					FD_SET(robot[i].err,&fds);
					nfds++;
				}
				else {
					if (robot[i].timeout < timeout_usec)
						timeout_usec = robot[i].timeout;
				}
			}
			for (j=0 ; j<3 ; j++) {
				if (robot[i].missile[j].time > 0.0 &&
					robot[i].missile[j].time < double(timeout_usec)/ONESEC) {
					timeout_usec = long(robot[i].missile[j].time * ONESEC);
				}
			}
		}

		tv.tv_sec = 0;	tv.tv_usec = timeout_usec;	/* default to 1 "sec" */
		if (nfds) {
			gettimeofday(&last,NULL);
			nfds = select(max_fd+1,&fds,NULL,NULL,&tv);
			gettimeofday(&now,NULL);

			tv.tv_usec = now.tv_usec - last.tv_usec;
			if (tv.tv_usec < 0L)
				tv.tv_usec += 1000000L;
		}
		if (tv.tv_usec >= timeout_usec)
			tv.tv_usec = timeout_usec+1;
//printf("tv2 = %7ld tv = %7ld\n",timeout_usec,tv.tv_usec);

		dt = double(tv.tv_usec)/ONESEC;
		total += dt;

		for (i=0 ; i<nrobots ; i++)
			if (robot[i].damage<100 && robot[i].timeout) {
				robot[i].timeout -= tv.tv_usec;
				if (robot[i].timeout < 0L)	robot[i].timeout = 0L;
			}
				
		// Update positions, etc. based on tv...
		for (i=0 ; i<nrobots ; i++)
			if (robots[i].damage < 100)
				Move(robots+i,i,dt);
		DispRobots("");
		for (i=0 ; i<nrobots ; i++)
			Blast(robots+i,i,dt);

		for (i=0,j=0 ; i<nrobots ; i++)
			if (robots[i].damage < 100)
				j++;
			else {
				if (robots[i].pid) {
					kill(robots[i].pid,SIGTERM);
					robots[i].speed = robots[i].pid = 0;
					robots[i].cspeed = 0.0;
				}
				plot(int(robots[i].x+.5),int(robots[i].y+.5),'@');
			}
		if (!j)
			break;
		if (j==1) {
			for (i=0 ; i<nrobots ; i++)
				if (robots[i].damage < 100) {
					return robots[i].name;
				}
		}

		// Process request(s)
		for (i=0 ; nfds && i<=max_fd ; i++) {
			if (FD_ISSET(i,&fds)) {
				for (j=0 ; j<nrobots ; j++) {
					if (i == robot[j].out)	{
						HandleRequest(j,robot+j);
						break;
					}
					if (i == robot[j].err)	{
						ErrLog(j,robot+j);
						break;
					}
				}
				//fd--;
			}
		}


	}

	DispRobots("Draw");
	return NULL;
}

main(int argc,char **argv)
{
	if (argc < 4) {
		fprintf(stderr,"Syntax:  %s {n|trace|watch} robot1 robot2 [ ... robotN ]\n",argv[0]);
		exit(1);
	}

	int i,j,ok=1;

	signal(SIGTERM,die_gracefully);
	signal(SIGHUP ,die_gracefully);

	//HERE HERE
	//srand(time(NULL));
	srand(12);

	nrobots = argc-2;
	robots = (Robot *)malloc(nrobots*sizeof(Robot));
	int *win  = (int *) malloc(nrobots*sizeof(int));
	int *loss = (int *) malloc(nrobots*sizeof(int));
	int *draw = (int *) malloc(nrobots*sizeof(int));

	for (i=0 ; i<nrobots ; i++) {
		if (!LoadRobot(argv[i+2],&robots[i])) {
			fprintf(stderr,"%s: Unable to load robot %s\n",argv[0],argv[i+2]);
			ok=0;
		}
		win[i] = loss[i] = draw[i] = 0;
	}

	if (!ok)
		exit(1);

	int nbouts=atoi(argv[1]);

	if (!nbouts) {
		if (!strcmp(argv[1],"watch")) {
			nbouts=1;
			dispMode = Watch;
		}
		else if (!strcmp(argv[1],"trace")) {
			nbouts=1;
			dispMode = Trace;
		}
		else {
			fprintf(stderr,"Syntax:  %s {n|trace|watch} robot1 robot2 [ ... robotN ]\n",argv[0]);
			exit(1);
		}
	}

	for (i=0 ; i<nbouts ; i++) {
		for (j=0 ; j<nrobots ; j++)
			StartRobot(&robots[j],j);

		sleep (1);
		char *winner = Combat(nrobots,robots);
		for (j=0 ; j<nrobots ; j++)
			if (!winner)
				draw[j]++;
			else if (!strcmp(winner,argv[j+2]))
				win[j]++;
			else
				loss[j]++;

		for (j=0 ; j<nrobots ; j++)
			if (robots[j].pid) {
				kill(robots[j].pid,SIGTERM);
				robots[j].speed = robots[j].pid = 0;
				robots[j].cspeed = 0.0;
			}

		sleep (1);
		int status;
		while (waitpid(0,&status,WNOHANG) > 0)
			;
	}

	if (dispMode == Watch)
		printf("\033[34;1H");

	for (i=0 ; i<nrobots ; i++) {
		for (j=0 ; j<i ; j++)
			if (!strcmp(argv[i+2],argv[j+2]))
				break;
		if (i==j)
			printf("%-16s %d/%d/%d\n",argv[i+2],win[i],loss[i],draw[i]);
	}

	exit(0);
}

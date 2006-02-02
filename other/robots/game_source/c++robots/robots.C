/* robots.C */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <time.h>
#include <sys/types.h>
#include <unistd.h>

#include "robots.h"

#define ROBOTTIMEOUT 1800
static int RobotTimeout = 0;

int Debug(char *format, ...)
{
	static FILE *debug = NULL;

	if (!debug)	debug = fopen("/home/PBM/c++robots/src/debug.log","a");

	va_list	p_arg;

	va_start(p_arg,format);
	vfprintf(debug,format,p_arg);
	va_end(p_arg);
	fflush(debug);
}

volatile int printlog(char *format, ...)
{
	char buf[4096];
	int len;

	va_list	p_arg;

	va_start(p_arg,format);
	len = vsprintf(buf,format,p_arg);
	va_end(p_arg);

	//Debug("printlog(%s)\n",buf);
	write(2,buf,len);
}

volatile int scan(DEGREE dir,int res)	/* range to nearest target */
{
	//	scan    'a'+i+i
	char buf[1+2*sizeof(int)];
	int rc=0;

	buf[0] = 'a';
	memcpy(buf+1,				&dir,sizeof(int));
	memcpy(buf+1+sizeof(int),	&res,sizeof(int));
	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int cannon(DEGREE dir,int range)	/* 1 if fired. 0 if still loading. */
{
	//	cannon  'b'+i+i
	char buf[1+2*sizeof(int)];
	int rc=0;

	buf[0] = 'b';
	memcpy(buf+1,				&dir,sizeof(int));
	memcpy(buf+1+sizeof(int),	&range,sizeof(int));
	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int drive(DEGREE dir,int speed)	/* 1 if direction changed. 0 if not. */
{
	//	drive   'c'+i+i
	char buf[1+2*sizeof(int)];
	int rc=0;

	buf[0] = 'c';
	memcpy(buf+1,				&dir,sizeof(int));
	memcpy(buf+1+sizeof(int),	&speed,sizeof(int));
	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int damage(void)			/* 0..99.  100 => dead robot */
{
	//	damage  'd'
	char buf='d';
	int rc=0;

	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,&buf,sizeof(buf));

	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int speed(void)			/* 0..100 */
{
	//	speed   'e'
	char buf='e';
	int rc=0;

	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,&buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int heading(void)			/* 0..359 */
{
	//	heading 'f'
	char buf='f';
	int rc=0;

	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,&buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int loc_x(void)			/* 0..9999 */
{
	//	loc_x   'g'
	char buf='g';
	int rc=0;

	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,&buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile int loc_y(void)			/* 0..9999 */
{
	//	loc_y   'h'
	char buf='h';
	int rc=0;

	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,&buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile unsigned int time(void)		/* Current time (in CPU cycles) */
{
	//	time    'i'
	char buf='i';
	unsigned int rc=0;

	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,&buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

int sqrt(int n)		/* Sqrt of n */
{
	//	sqrt	'j'+i
	char buf[1+sizeof(int)];
	unsigned int rc=0;

	buf[0] = 'j';
	memcpy(buf+1,&n,sizeof(int));
	if (!RobotTimeout)	alarm(RobotTimeout = ROBOTTIMEOUT);
	write(1,buf,sizeof(buf));
	read(0,&rc,sizeof(rc));
	return rc;
}

volatile unsigned int rand(unsigned int limit)	
{
	static int init=0;
	if (!init) {
		srand(time(NULL));
		init=1;
	}
	return rand()%limit;
}

double sine_table[360] = {
	0.0,		0.0174524,	0.0348995,	0.052336,	0.0697565,	0.0871557,	
	0.104528,	0.121869,	0.139173,	0.156434,	0.173648,	0.190809,	
	0.207912,	0.224951,	0.241922,	0.258819,	0.275637,	0.292372,	
	0.309017,	0.325568,	0.34202,	0.358368,	0.374607,	0.390731,	
	0.406737,	0.422618,	0.438371,	0.45399,	0.469472,	0.48481,	
	0.5,		0.515038,	0.529919,	0.544639,	0.559193,	0.573576,	
	0.587785,	0.601815,	0.615661,	0.62932,	0.642788,	0.656059,	
	0.669131,	0.681998,	0.694658,	0.707107,	0.71934,	0.731354,	
	0.743145,	0.75471,	0.766044,	0.777146,	0.788011,	0.798636,	
	0.809017,	0.819152,	0.829038,	0.838671,	0.848048,	0.857167,	
	0.866025,	0.87462,	0.882948,	0.891007,	0.898794,	0.906308,	
	0.913545,	0.920505,	0.927184,	0.93358,	0.939693,	0.945519,	
	0.951057,	0.956305,	0.961262,	0.965926,	0.970296,	0.97437,	
	0.978148,	0.981627,	0.984808,	0.987688,	0.990268,	0.992546,	
	0.994522,	0.996195,	0.997564,	0.99863,	0.999391,	0.999848,	
	1.0,		0.999848,	0.999391,	0.99863,	0.997564,	0.996195,	
	0.994522,	0.992546,	0.990268,	0.987688,	0.984808,	0.981627,	
	0.978148,	0.97437,	0.970296,	0.965926,	0.961262,	0.956305,	
	0.951057,	0.945519,	0.939693,	0.93358,	0.927184,	0.920505,	
	0.913545,	0.906308,	0.898794,	0.891007,	0.882948,	0.87462,	
	0.866025,	0.857167,	0.848048,	0.838671,	0.829038,	0.819152,	
	0.809017,	0.798636,	0.788011,	0.777146,	0.766044,	0.75471,	
	0.743145,	0.731354,	0.71934,	0.707107,	0.694658,	0.681998,	
	0.669131,	0.656059,	0.642788,	0.62932,	0.615661,	0.601815,	
	0.587785,	0.573576,	0.559193,	0.544639,	0.529919,	0.515038,	
	0.5,		0.48481,	0.469472,	0.45399,	0.438371,	0.422618,	
	0.406737,	0.390731,	0.374607,	0.358368,	0.34202,	0.325568,	
	0.309017,	0.292372,	0.275637,	0.258819,	0.241922,	0.224951,	
	0.207912,	0.190809,	0.173648,	0.156434,	0.139173,	0.121869,	
	0.104528,	0.0871557,	0.0697565,	0.052336,	0.0348995,	0.0174524,	
	0.0,		-0.0174524,	-0.0348995,	-0.052336,	-0.0697565,	-0.0871557,	
	-0.104528,	-0.121869,	-0.139173,	-0.156434,	-0.173648,	-0.190809,	
	-0.207912,	-0.224951,	-0.241922,	-0.258819,	-0.275637,	-0.292372,	
	-0.309017,	-0.325568,	-0.34202,	-0.358368,	-0.374607,	-0.390731,	
	-0.406737,	-0.422618,	-0.438371,	-0.45399,	-0.469472,	-0.48481,	
	-0.5,		-0.515038,	-0.529919,	-0.544639,	-0.559193,	-0.573576,	
	-0.587785,	-0.601815,	-0.615661,	-0.62932,	-0.642788,	-0.656059,	
	-0.669131,	-0.681998,	-0.694658,	-0.707107,	-0.71934,	-0.731354,	
	-0.743145,	-0.75471,	-0.766044,	-0.777146,	-0.788011,	-0.798636,	
	-0.809017,	-0.819152,	-0.829038,	-0.838671,	-0.848048,	-0.857167,	
	-0.866025,	-0.87462,	-0.882948,	-0.891007,	-0.898794,	-0.906308,	
	-0.913545,	-0.920505,	-0.927184,	-0.93358,	-0.939693,	-0.945519,	
	-0.951057,	-0.956305,	-0.961262,	-0.965926,	-0.970296,	-0.97437,	
	-0.978148,	-0.981627,	-0.984808,	-0.987688,	-0.990268,	-0.992546,	
	-0.994522,	-0.996195,	-0.997564,	-0.99863,	-0.999391,	-0.999848,	
	-1.0,		-0.999848,	-0.999391,	-0.99863,	-0.997564,	-0.996195,	
	-0.994522,	-0.992546,	-0.990268,	-0.987688,	-0.984808,	-0.981627,	
	-0.978148,	-0.97437,	-0.970296,	-0.965926,	-0.961262,	-0.956305,	
	-0.951057,	-0.945519,	-0.939693,	-0.93358,	-0.927184,	-0.920505,	
	-0.913545,	-0.906308,	-0.898794,	-0.891007,	-0.882948,	-0.87462,	
	-0.866025,	-0.857167,	-0.848048,	-0.838671,	-0.829038,	-0.819152,	
	-0.809017,	-0.798636,	-0.788011,	-0.777146,	-0.766044,	-0.75471,	
	-0.743145,	-0.731354,	-0.71934,	-0.707107,	-0.694658,	-0.681998,	
	-0.669131,	-0.656059,	-0.642788,	-0.62932,	-0.615661,	-0.601815,	
	-0.587785,	-0.573576,	-0.559193,	-0.544639,	-0.529919,	-0.515038,	
	-0.5,		-0.48481,	-0.469472,	-0.45399,	-0.438371,	-0.422618,	
	-0.406737,	-0.390731,	-0.374607,	-0.358368,	-0.34202,	-0.325568,	
	-0.309017,	-0.292372,	-0.275637,	-0.258819,	-0.241922,	-0.224951,	
	-0.207912,	-0.190809,	-0.173648,	-0.156434,	-0.139173,	-0.121869,	
	-0.104528,	-0.0871557,	-0.0697565,	-0.052336,	-0.0348995,	-0.0174524,	
};

double sin(DEGREE angle)
{
	angle %= 360;
	while (angle<0)
		angle += 360;
	return sine_table[angle];
}

double cos(DEGREE angle)
{
	angle += 90;
	angle %= 360;
	while (angle<0)
		angle += 360;
	return sine_table[angle];
}

double tan(DEGREE angle)
{
	return sin(angle)/cos(angle);
}

DEGREE atan(double ratio)
{
	DEGREE minangle=-90;
	DEGREE maxangle=90;
	DEGREE angle;
	
	while (maxangle-minangle > 1) {
		angle = (maxangle+minangle)/2;
		if (sine_table[(angle+360)%360]/sine_table[(angle+90)%360] < ratio)
			minangle = angle;
		else
			maxangle = angle;
	}
	return (minangle+maxangle)/2;
}

DEGREE atan2(int y,int x)
{

	if (x==0)
		return (y<0 ? 270 : 90);

	DEGREE angle = atan((double)y / (double)x);

	if (x<0)
		angle += 180;

	return angle;
}


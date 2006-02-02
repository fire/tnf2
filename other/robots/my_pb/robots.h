/* robots.h */

/* typedefs */
typedef int	DEGREE;

/* Debug Functions */
int printlog(char *format,...);

/* Control Functions */
int scan(DEGREE dir,int resolution);	/* range to nearest target */
int cannon(DEGREE dir,int range);	/* 1 if fired. 0 if still loading. */
int drive(DEGREE dir,int speed);	/* 1 if direction changed. 0 if not. */
int damage(void);			/* 0..99.  100 => dead robot */
int speed(void);			/* 0..100 */
int heading(void);			/* 0..359 */
int loc_x(void);			/* 0..9999 */
int loc_y(void);			/* 0..9999 */

unsigned int time(void);		/* Current time (in CPU cycles) */

/* Math Functions */
unsigned int rand(unsigned int limit);	/* random number [ 0..limit-1 ] */
int sqrt(int number);				
double sin(DEGREE angle);
double cos(DEGREE angle);
double tan(DEGREE angle);

DEGREE atan(double ratio);
DEGREE atan2(int y,int x);

/* Macros */
#define status(x,y,s,h,d,t)	{ \
	(x) = loc_x();	\
	(y) = loc_y();	\
	(s) = speed();	\
	(h) = heading();	\
	(d) = damage();	\
	(t) = time();	\
}

#define abs(x)	((x)<0 ? -(x) : (x))

#include "robots.h"

int distruct() {
	while(1) cannon(0,0);
}

int movetype=1;//stand still
//int movetype=2;//run fast

int main() {
	while (1) {
		if (movetype==2) {
			if (loc_x()>9500 && speed()==100) drive(180,100);
			else if (loc_x()<500 && speed()==100) drive(0,100);
			else if (speed()==0) drive(0,100);
		}
		if (time()>=30000) distruct();
		scan(0,0);
	}
}

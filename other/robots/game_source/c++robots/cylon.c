#include "robots.h"

Distance(int x1, int y1, int x2, int y2)
{
    int dx = x1-x2;
    int dy = y1-y2;
//printlog("Distance: dx = %d  dy=%d",dx,dy);
    return sqrt(dx*dx + dy*dy);
}

Goto(int x, int y)
{
    int dir = atan2(y-loc_y(),x-loc_x());
    int dist = Distance(x,y,loc_x(),loc_y());
    int t = time();
    int sc = 0;
    int sd = 1;
    int range=0;

    drive(dir,100);

printlog("Goto: x,y = %d,%d  dir,dist = %d,%d",x,y,dir,dist);
    while (speed() && time()-t < dist) {
        if (abs(sc) == 45)
            sd *= -1;
        sc += 3*sd;
        range = scan(dir+sc,5);
        if (range > 200 && range < 7000)
            cannon(dir+sc,range);
        int tdir = atan2(y-loc_y(),x-loc_x());
        if (speed() && tdir != dir)
            drive(dir=tdir,100);
    }

    drive(dir,0);
    while (speed())
        drive(dir,0);
}

main()
{
    while (1)
        Goto(rand(9000)+500,rand(9000)+500);
}

package aj.awt.maze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Conv {
	public static void main(String s[]) {
		if (s.length!=1) {
			System.out.println("Format: myjava aj.awt.maze.Conv <filename>");
			System.exit(0);
		}
		Vector v=new Vector();
		try {
			BufferedReader br=new BufferedReader(new FileReader(s[0]));
			while (true) {
				String ss=br.readLine();
				if (ss==null) break;
				v.addElement(ss);
			}
		} catch (IOException IOE) {}
		char map[][]=new char[v.size()][];
		for (int a=0;a<v.size();a++) {
			String sss=(String)v.elementAt(a);
			map[a]=new char[sss.length()];
			for (int b=0;b<sss.length();b++) {
				map[a][b]=sss.charAt(b);
			}
		}
/*
		for (int a=0;a<map.length;a++) {
			for (int b=0;b<map[a].length;b++) {
				if (map[a][b]!=' ') {
					//wall found
					boolean up=false,down=false,left=false,right=false;
					if (a>0 && map[a-1].length>b) up=(map[a-1][b]!=' ');
					if (a<map.length-1 && map[a+1].length>b) down=(map[a+1][b]!=' ');
					if (b>0) left=(map[a][b-1]!=' ');
					if (b<map[a].length-1) right=(map[a][b+1]!=' ');
					if (up || down) {
						System.out.println(" object { wall translate <"+a+",0,"+b+"> }");
					}
					if (left || right) {
						System.out.println(" object { wall2 translate <"+a+",0,"+b+"> }");
					}
				}
			}
		}
*/
//get lines over X  scale X  cent X trans Z
		int a,b;
		for (a=0;a<map.length;a++) {
			int begin=-1,end=-1;
			for (b=0;b<map[a].length;b++) {
				if (map[a][b]!=' ' && begin==-1 && (
					(b-1>=0 && map[a][b-1]!=' ') || 
					(map[a].length-1>b+1 && map[a][b+1]!=' '))) 
					begin=b;
				if (map[a][b]==' ' && begin!=-1) {
					end=b;
					if (end-begin>1) System.out.println("object {wall2 scale <"+(end-begin)+"-Adj,1,1> translate <"+(begin+end-1)/2.0+",0,"+a+"> }");
					end=-1;begin=-1;
				}
			}
			if (begin !=-1) {
				end=b;
				if (end-begin>1) System.out.println("object {wall2 scale <"+(end-begin)+"-Adj,1,1> translate <"+(begin+end-1)/2.0+",0,"+a+"> }");
				end=-1;begin=-1;
			}
		}
		int maxlen=0;
		for (a=0;a<map.length;a++) maxlen=Math.max(maxlen,map[a].length);

//get lines over Y  scale Z  cent Z trans X
		for (b=0;b<maxlen;b++) {
			int begin=-1,end=-1;
			for (a=0;a<map.length;a++) {
//System.out.println("a="+a+",b="+b);
				if (begin==-1 && (map[a].length>b && map[a][b]!=' ') && (
					(map.length>a+1 && map[a+1].length>b && map[a+1][b]!=' ') ||
					(a-1>=0 && map[a-1].length>b && map[a-1][b]!=' '))) {
					begin =a;
//System.out.println("Begining!1");
				}
				else if (begin!=-1 && map[a].length>b && map[a][b]==' ') {
//System.out.println("ENDING!1");
					end=a;
					if (end-begin>1) System.out.println("object {wall  scale <1,1,"+(end-begin)+"-Adj> translate <"+b+",0,"+(begin+end-1)/2.0+"> }");
					end=-1;begin=-1;
				}
			}
			if (begin !=-1) {
//System.out.println("ENDING!2");
				end=a;
				if (end-begin>1) System.out.println("object {wall  scale <1,1,"+(end-begin)+"-Adj> translate <"+b+",0,"+(begin+end-1)/2.0+"> }");
				end=-1;begin=-1;
			}

		}


	}
}


//search each row.  Find begin and end of each wall.  find wall center and scale wall
//repeat for each col.

/*Pov header 

#include "colors.inc"
#include "stones.inc"
#declare Size=.5;

camera {location <-4,10, -4> look_at <5,0,5> }
light_source { <50,120,-10> color White}
#declare Shave=.125;
#declare wall2 = box { <-Size,-Size,-Size>,<Size,Size,Size> scale <1,1,Shave>}
#declare wall = box { <-Size,-Size,-Size>,<Size,Size,Size> scale <Shave,1,1>}
#declare Adj = 1-Shave;
 union {

*/
/*Pov Tailer
pigment {color Blue } finish {ambient .5 diffuse .5}}
*/
 

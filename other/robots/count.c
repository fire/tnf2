#include <iostream.h>

int len(char *c) {
  int a=0;
  while (*c!=0) {c++;a++;}
  return a;
  }

int main() {
char l[80];
char n[80];
  int w;
int t=0;
while (cin) {
    cin>>n;
    if (l[0]==0) {
      int a;
      for (a=0;a<len(n);a++)
	 l[a]=n[a];

      l[a]=0;
}
cin>>w;
cin>>n;
t+=w;
}
t-=w;
if (l[0]==0) l[0]='E';
cout<<l<<"    "<<t<<"/0/0"<<endl;
}

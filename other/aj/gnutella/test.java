package aj.gnutella;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import aj.misc.Stuff;
class test {
	public static void main(String t[]) throws IOException {
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String s=br.readLine();
			if (s==null) break;
			String tt[]=Stuff.getTokens(s," ");
			for (int a =0;a<tt.length;a++){
				System.out.println(tt[a]);
			}
		}
	}
}

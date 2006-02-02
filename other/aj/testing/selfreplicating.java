package aj.testing;

class t{
public static void main(String s[]){
String a="class t{public static void main(String s[]){String a=;System.out.println(a.substring(0,53)+((char)34)+a+((char)34)+a.substring(53));}}";
System.out.println(a.substring(0,53)+((char)34)+a+((char)34)+a.substring(53));
}
}

/*output 
class t{
public static void main(String s[]){
String a="class t{public static void main(String s[]){String a=;System.out.println(a.substring(0,53)+((char)34)+a+((char)34)+a.substring(53));}}";
System.out.println(a.substring(0,53)+((char)34)+a+((char)34)+a.substring(53));
}
}
*/
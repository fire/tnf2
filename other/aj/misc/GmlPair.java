package aj.misc;

//import aj.netpars.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URL;
import java.util.Vector;

/**
 *  GmlPairs look like this 
 *borderColor[red 0 green 0 blue 0]
 *shape "Oval" 
 *<gml_pair>=<name> <val>|name[<list of gmlpairs>]
 *<name>=String 
 *<val>=<String>|double
 *<String>=no white space or ']' '[', may be inside.  Use " to allow white spaces
 * no \n in strings.  Exponential doubles require "".
 *
 *@author     judda 
 *@created    April 12, 2000 
 */
public class GmlPair
{

  private String special = "~`!@#$%^&*()_+-={}[]:;\"\'<,>.?/|\t\n\r\\ ";
  private String veryspecial = "\t\n\r\\";
  private String TAB = "\t";
  private String name;

  private double nvalue = -1;
  private String svalue;
  private Vector list;
  private boolean terminal;
  private boolean number;
  private boolean string;

  public boolean equals (GmlPair g)
  {
    return g.toString ().equalsIgnoreCase (toString ());
  }


  public GmlPair (String name, double value)
  {
    if (name == null)
      {
	System.out.println ("MyError: null name in GmlPair");
      }
    this.name = name;
    this.svalue = value + "";
    this.nvalue = value;
    terminal = true;
    number = true;
  }


  public GmlPair (String name, String value)
  {
    if (name == null)
      System.out.println ("MyError: null name in GmlPair string. " + name +
			  " " + value);
    if (value == null)
      System.out.println ("MyError: null value in GmlPair string. " + name +
			  " " + value);
    this.name = name;
    this.svalue = value;
    this.nvalue = -1;
    terminal = true;
    number = false;
    if (value == null)
      {
	svalue = "";
	return;
      }
    try
    {
      double d = Stuff.parseDouble (value);
      this.nvalue = d;
    }
    catch (NumberFormatException NFE)
    {
    }
  }



  public GmlPair (String name, Vector list)
  {
    this.name = name;
    this.list = list;
    if (name == null)
      System.out.println ("MyError: null name in GmlPair list. " + name +
			  " " + list);
    if (list == null)
      {
	System.out.println ("MyError: null list in GmlPair list. " + name +
			    " " + list);
	this.svalue = "";
	this.terminal = true;
	this.number = false;
	return;
      }
    terminal = false;
    number = false;
  }



  public void setValue (String value)
  {
    this.svalue = value;
    if (value == null)
      {
	System.out.println ("MyError: null value in GmlPair set. " + name +
			    " " + value);
	this.svalue = "";
      }
    terminal = true;
    number = false;
    if (value == null)
      {
	return;
      }
    try
    {
      double d = Stuff.parseDouble (value);
      this.nvalue = d;
    }
    catch (NumberFormatException NFE)
    {
    }
  }


  public void setValue (Vector value)
  {
    if (value == null)
      {
	svalue = "";
	terminal = true;
	number = false;
	return;
      }
    this.list = value;
    terminal = false;
    number = false;
  }


  public boolean isList ()
  {
    return isSubTree ();
  }


  public boolean isSubTree ()
  {
    return !terminal;
  }


  public boolean isDouble ()
  {
    return number && terminal;
  }


  public boolean isString ()
  {
    return terminal && !number;
  }


  public String getName ()
  {
    return name;
  }


  public double getDouble ()
  {
    return nvalue;
  }


  public String getString ()
  {
    if (isString ())
      {
	return svalue;
      }
    if (isDouble ())
      {
	return "" + getDouble ();
      }
    else
      {
	return null;
      }
  }


  public Vector getListVector ()
  {
    if (!terminal)
      {
	return (Vector) list.clone ();
      }
    else
      return new Vector ();
  }

  public GmlPair[] getList ()
  {
    if (!terminal)
      {
	GmlPair l[] = new GmlPair[list.size ()];
	list.copyInto (l);
	return l;
      }
    else
      {
	return new GmlPair[0];
      }
  }

  public void add (GmlPair g)
  {
    if (list != null)
      list.addElement (g);
  }

  public GmlPair[] getAllByName (String sname)
  {
    if (!isSubTree ())
      {
	return new GmlPair[0];
      }
    if (sname == null)
      {
	return new GmlPair[0];
      }
    int a;
    Vector v = new Vector ();
    for (a = 0; a < list.size (); a++)
      {
	GmlPair g = (GmlPair) list.elementAt (a);
	if (sname.equalsIgnoreCase (g.getName ()))
	  {
	    v.addElement (g);
	  }
      }
    GmlPair v2[] = new GmlPair[v.size ()];
    v.copyInto (v2);
    return v2;
  }

  public GmlPair getOneByName (String name)
  {
    if (name == null)
      {
	return null;
      }
    if (!isSubTree ())
      {
	return null;
      }
    int a;
    for (a = 0; a < list.size (); a++)
      {
	GmlPair g = (GmlPair) list.elementAt (a);
	if (name.equalsIgnoreCase (g.getName ()))
	  {
	    return g;
	  }
      }
    return null;
  }


  public boolean isSpecial (String s)
  {
    for (int a = 0; a < special.length (); a++)
      {
	if (s.indexOf (special.charAt (a) + "") >= 0)
	  return true;
      }
    return false;
  }
  public String fixSpecial (String s)
  {
    if (s == null)
      return null;
    if (s.length () == 0)
      return s;
    String n = "";
    while (s.indexOf ("\\") >= 0)
      {
	n += s.substring (0, s.indexOf ("\\")) + "\\\\";
	s = s.substring (s.indexOf ("\\") + 1);
      }
    s = n + s;
    n = "";
    while (s.indexOf ("\"") >= 0)
      {
	n += s.substring (0, s.indexOf ("\"")) + "\\\"";
	s = s.substring (s.indexOf ("\"") + 1);
      }
    s = n + s;
    while (s.indexOf ("\n") >= 0)
      {
	s =
	  s.substring (0,
		       s.indexOf ("\n")) + "\\n" +
	  s.substring (s.indexOf ("\n") + 1);
      }
    while (s.indexOf ("\t") >= 0)
      {
	s =
	  s.substring (0,
		       s.indexOf ("\t")) + "\\t" +
	  s.substring (s.indexOf ("\t") + 1);
      }
    while (s.indexOf ("\r") >= 0)
      {
	s =
	  s.substring (0,
		       s.indexOf ("\r")) + "\\r" +
	  s.substring (s.indexOf ("\r") + 1);
      }
    return s;
  }

  public String prettyPrint ()
  {
    return prettyPrint ("");
  }
  public String prettyPrint (String pp)
  {
    String s = "";
    s += pp;
    String name = this.name;
    String svalue = this.svalue;
    name = fixSpecial (name);
    svalue = fixSpecial (svalue);
    if (name.length () > 0 && !isSpecial (name)
	&& !Character.isDigit (name.charAt (0)))
      s += "" + name + " ";
    else
      s += "\"" + name + "\" ";
    if (terminal)
      {
	try
	{
	  double d = Stuff.parseDouble (svalue);
	  if (((long) d) == d)
	    s += ((long) d) + "\n";
	  else if ((d + "").toUpperCase ().indexOf ("E") > 0)
	    s += "\"" + svalue + "\"\n";
	  else
	    s += "" + d + "\n";
	}
	catch (NumberFormatException NFE)
	{
	  if (svalue.length () > 0 && !isSpecial (svalue)
	      && !Character.isDigit (svalue.charAt (0)))
	    s += "" + svalue + "\n";
	  else
	    s += "\"" + svalue + "\"\n";
	}
      }
    else
      {
	int a;
	s += "[\n";
	for (a = 0; a < list.size (); a++)
	  {
	    s += ((GmlPair) list.elementAt (a)).prettyPrint (pp + TAB);
	  }
	s += pp + "]\n";
      }
    return s;
  }


  public String toString ()
  {
    String s = "";
    String name = this.name;
    String svalue = this.svalue;
    name = fixSpecial (name);
    svalue = fixSpecial (svalue);
    if (name.length () > 0 && !isSpecial (name)
	&& !Character.isDigit (name.charAt (0)))
      s += "" + name + " ";
    else
      s += "\"" + name + "\" ";
    //s += "" + name + " ";
    if (terminal)
      {
	try
	{
	  double d = Stuff.parseDouble (svalue);
	  if (((long) d) == d)
	    s += ((long) d) + " ";
	  else if ((d + "").toUpperCase ().indexOf ("E") > 0)
	    s += "\"" + svalue + "\" ";
	  else
	    s += "" + d + " ";
	}
	catch (NumberFormatException NFE)
	{
	  if (svalue.length () > 0 && !isSpecial (svalue)
	      && !Character.isDigit (svalue.charAt (0)))
	    s += "" + svalue + " ";
	  else
	    s += "\"" + svalue + "\" ";
	}
      }
    else
      {
	int a;
	s += "[";
	for (a = 0; a < list.size (); a++)
	  {
	    s += list.elementAt (a);
	  }
	s += "]";
      }
    s = s + "";
    return s;
  }


  public static GmlPair parse (String s) throws IOException
  {
    StringReader sr = new StringReader (s.trim ());
    StreamTokenizer ST = new StreamTokenizer (sr);
      ST.nextToken ();
      return parse (ST);
  }


  public static GmlPair parse (File f) throws IOException
  {
    FileReader fr = new FileReader (f);
    StreamTokenizer st = new StreamTokenizer (fr);
      st.nextToken ();
    GmlPair g = parse (st);
      fr.close ();
      return g;
  }

  public static GmlPair parse (URL i) throws IOException
  {
    return parse (i.openStream ());
  }
  public static GmlPair parse (InputStream i) throws IOException
  {
    InputStreamReader ir = new InputStreamReader (i);
    StreamTokenizer st = new StreamTokenizer (ir);
      st.nextToken ();
    GmlPair g = parse (st);
      ir.close ();
      return g;
  }



  public static GmlPair parse (StreamTokenizer ST) throws IOException
  {
    if (ST.ttype == StreamTokenizer.TT_EOF)
      {
	throw new IOException ("GML parse error: Empty parse string");
      }
    String n = ST.sval;
    if (n == null)
      throw new IOException ("GML parse error: Name missing");
    ST.nextToken ();
    if (ST.ttype == StreamTokenizer.TT_EOF)
      {
	throw new IOException ("GML parse error: Name only, value missing");
      }
    else if (ST.ttype == '[')
      {
	Vector v = new Vector ();
	ST.nextToken ();
	if (ST.ttype == StreamTokenizer.TT_EOF)
	  {
	    throw new
	      IOException ("GML parse error: Unmatch closing braces.");
	  }
	while (ST.ttype != ']' && ST.ttype != StreamTokenizer.TT_EOF)
	  {
	    GmlPair g = GmlPair.parse (ST);
	    if (g != null)
	      {
		v.addElement (g);
	      }
	    if (g == null)
	      {
		throw new
		  IOException
		  ("GML parse error: Unable to parse list element.");
	      }
	  }
	ST.nextToken ();
	return new GmlPair (n, v);
      }
    else if (ST.ttype == StreamTokenizer.TT_NUMBER)
      {
	double v = ST.nval;
	ST.nextToken ();
	return new GmlPair (n, v);
      }
    else if (ST.ttype == StreamTokenizer.TT_WORD || ST.ttype == '\"')
      {
	String v = ST.sval;
	if (v == null)
	  {
	    throw new
	      IOException
	      ("GML parse error: String value null or missing error.");
	  }
	ST.nextToken ();
	return new GmlPair (n, v);
      }
    else
      {
	throw new IOException ("MyError: Unknown parse token " + ST.ttype +
			       " " + ST.sval + " " + ST.nval);
      }
  }


  public static void main (String s[])
  {
    //    System.out.println("testing GmlPair");
    if (s.length != 1)
      {
	System.out.println ("testing GmlPair");
	System.out.println ("USAGE: java GmlPair <Gmlfile>");
	System.exit (0);
      }
    try
    {
      StreamTokenizer st = new StreamTokenizer (new FileReader (s[0]));
      st.nextToken ();
      GmlPair p = GmlPair.parse (st);
      String pp = p.prettyPrint ("");
      while (pp.indexOf ("\n") >= 0)
	{
	  System.out.println (pp.substring (0, pp.indexOf ("\n")));
	  pp = pp.substring (pp.indexOf ("\n") + 1);
	}
      System.out.println (pp);
    }
    catch (IOException E)
    {
      System.out.println ("myError:  no file or Gml Error");
    }

    /*
     * String test="graph []";
     * myGmlTest(test);
     * test="graph [ node [id 1] node [id 2]]";
     * myGmlTest(test);
     * test="graph";
     * myGmlTest(test);
     * test="graph [hello there]";
     * myGmlTest(test);
     * test="graph [ node ]";
     * myGmlTest(test);
     * test="graph test test2 test3";
     * myGmlTest(test);
     * test="graph[node[edge[a b]edge[b c]]node[edge[edge [d e]]]]";
     * myGmlTest(test);
     */
  }


  public static void myGmlTest (String s)
  {
    try
    {
      System.out.println ("test <" + s + ">");
      GmlPair g = GmlPair.parse (s);
      System.out.println ("ORG " + g);
      System.out.println ("PP " + g.prettyPrint (""));
    }
    catch (IOException E)
    {
      System.out.println ("myError:  Gml Error" + E);
    }
  }
}

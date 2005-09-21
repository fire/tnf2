SRCDIR	=.
BINDIR	=../bin
WWWDIR	=aj/nf
CLSDIR	=.
DOCDIR	=.

CPATH	=.

JCCPATH	=-classpath $(CPATH)
JDCPATH	=-J"$(JCCPATH)"

JC	=javac
JCFLAGS	=$(JCCPATH) -d $(CLSDIR) -deprecation
JD	=javadoc
JDFLAGS	=$(JDCPATH) -private -d $(DOCDIR)
JAR	=jar
JARFLAGS=cvf

SOURCES= \
$(WWWDIR)/Action.java \
$(WWWDIR)/ActiveDesign.java \
$(WWWDIR)/Active.java \
$(WWWDIR)/AuctionItem.java \
$(WWWDIR)/Auction.java \
$(WWWDIR)/Body.java \
$(WWWDIR)/Corp.java \
$(WWWDIR)/DPoint.java \
$(WWWDIR)/FacilityDesign.java \
$(WWWDIR)/Facility.java \
$(WWWDIR)/GenPlanet.java \
$(WWWDIR)/GmlPair.java \
$(WWWDIR)/ITThing.java \
$(WWWDIR)/Location.java \
$(WWWDIR)/Main.java \
$(WWWDIR)/MarketItem.java \
$(WWWDIR)/Market.java \
$(WWWDIR)/NFObject.java \
$(WWWDIR)/Prefab.java \
$(WWWDIR)/Program.java \
$(WWWDIR)/Project.java \
$(WWWDIR)/StockPile.java \
$(WWWDIR)/Stuff.java \
$(WWWDIR)/Tech.java \
$(WWWDIR)/UniVarApplet.java \
$(WWWDIR)/UniVar.java \
$(WWWDIR)/Universe.java
OBJECTS	= $(SOURCES:.java=.class)

all: build install

build:
	(cd $(SRCDIR); $(JC) $(JCFLAGS) $(SOURCES))

install:
	(cd $(SRCDIR); $(JAR) $(JARFLAGS) ../bin/nf.jar $(OBJECTS))

doc:
	(cd $(SRCDIR); $(JD) $(JDFLAGS) aj/nf)

clean:
	find . -name "*.class" -exec rm {} \;

distclean: clean
	/bin/rm -f ../bin/nf.jar 


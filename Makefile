SRCDIR	=.
BINDIR	=.
CLSDIR	=.
DOCDIR	=.

##java 1.1 version (1.1.3 - 1.1.7+)
#CPATH	=.:/usr/lib/jdk1.1/lib/classes.zip
#java 1.2 version
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
aj/awt/*.java \
aj/awt/mand/*.java \
aj/awt/maze/*.java \
aj/awt/my3d/*.java \
aj/misc/*.java \
aj/checkers/*.java \
aj/chess/*.java \
aj/combat/*.java \
aj/fm/*.java \
aj/games/*.java \
aj/gems/*.java \
aj/glad/*.java \
aj/gnubi/*.java \
aj/gnutella/*.java \
aj/iem/*.java \
aj/io/*.java \
aj/life/*.java \
aj/loan/*.java \
aj/man/*.java \
aj/neaconing/*.java \
aj/net/*.java \
aj/proof/*.java \
aj/robot/*.java \
aj/school/*.java \
aj/smug/*.java \
aj/space/*.java \
aj/stock/*.java \
aj/testing/*.java 
OBJECTS	= $(SOURCES:.java=.class)

GEMSSOURCES= \
aj/gems/*.java
GEMSOBJECTS = $(GEMSSOURCES:.java=.class)

IEMSOURCES= \
aj/iem/*.java \
aj/misc/Stuff.java \
aj/io/MLtoText.java
IEMOBJECTS = $(IEMSOURCES:.java=.class)

all: gems iem build install

gems:
	(cd $(SRCDIR); $(JC) $(JCFLAGS) $(GEMSSOURCES))

iem:
	(cd $(SRCDIR); $(JC) $(JCFLAGS) $(IEMSOURCES))

build:
	(cd $(SRCDIR); $(JC) $(JCFLAGS) $(SOURCES))

install:
	(cd $(SRCDIR); $(JAR) $(JARFLAGS) ./gems.jar $(GEMSOBJECTS))
	(cd $(SRCDIR); $(JAR) $(JARFLAGS) ./iem.jar $(IEMOBJECTS))
	(cd $(SRCDIR); $(JAR) $(JARFLAGS) ./all.jar $(OBJECTS))

doc:
	(cd $(SRCDIR); $(JD) $(JDFLAGS) aj/nf)

clean:
	find . -name "*.class" -exec rm {} \;
	find . -name "*~" -exec rm {} \;

distclean: clean
	/bin/rm -f ./all.jar


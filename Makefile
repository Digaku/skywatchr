
VERSION = $(shell cat build.sbt | grep -e version | grep -P \"\\d+.\\d+.[\\d\\-_a-z]+\" -o | sed 's/"//g')

TARGETS =

all: version $(TARGETS)

version:
	sed -i.bak 's/VERSION \= ".*"/VERSION = "$(VERSION)"/' src/main/scala/com/ansvia/skywatchr/SkyWatchr.scala
	echo $(VERSION) > VERSION

clean:
	rm -f $(TARGETS)



.PHONY: clean version

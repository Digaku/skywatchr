Skywatchr
==========

Remote log viewer that using [Kafka](http://incubator.apache.org/kafka/ "Kafka") as the log backends.

Setup
=============

	$ git clone git://github.com/Digaku/skywatchr.git
	$ sbt assembly
	
Or build single directory with `start.sh` script:

	$ sbt onedir
	
NOTE: needs sbt onedir directory.

Usage
======

	java -jar skywatchr-0.0.1.jar [OPTIONS] [CHANNEL]
	
	OPTIONS are:
		--group    consumer group id.
				   ex: java -jar skywatchr-0.0.1.jar --group AnlabMac log.digaku
	
If `--group` not specified then use default `group01`.
	
Example:

	java -jar skywatchr-0.0.1.jar "log.digaku"
	
CHANNEL can be regex, ex:

	java -jar skywatchr-0.0.1.jar "log.*"
	


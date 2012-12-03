Skywatchr
==========

Remote log viewer that using [Kafka](http://incubator.apache.org/kafka/ "Kafka") as the log backends.

Setup
=============

	$ git clone git://github.com/Digaku/skywatchr.git
	$ sbt proguard

Usage
======

	java -jar skywatchr-0.0.1.jar [TOPIC]
	
Example:

	java -jar skywatchr-0.0.1.jar "log.digaku"
	
TOPIC can be regex, ex:

	java -jar skywatchr-0.0.1.jar "log.*"
	


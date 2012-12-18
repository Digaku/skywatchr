
package com.ansvia.skywatchr

import java.util.Properties
import kafka.consumer.{KafkaListener, ConsumerConfig}

object SkyWatchr {

    private lazy val ERROR_RE = """\[ERROR.*?\]""".r
  
    def getArg(args:Array[String],key:String):Option[String] = {
      var i = 0
      var rv:Option[String] = None
      args.foreach { arg =>
        if (arg.startsWith("--")){
          if(arg.substring(2) == key){
            val nextI = i + 1
            if (args.length > nextI)
              rv = Some(args(nextI).trim)
          }
        }
        i += 1
      }
      rv
    }
    
    def getChannels(args:Array[String])={
      val ss = args.reduce(_ + "|" + _)
      val rv = """\-\-[\w\.\-_\:]+\|[\w\.\-_\:]+""".r.replaceAllIn(ss,"").split("\\|")
      rv.filter(a => a.length > 0 && !a.startsWith("--"))
    }
  
    def main(args:Array[String]){
        
        if(args.length == 0){
            println("Usage: java -jar skywatchr.jar [OPTIONS] [CHANNEL]")
            println("")
            println("OPTIONS are: ")
            println("     --group    consumer group id.")
            println("     --zkhost   zookeeper host and port [IP]:[PORT].\n" +
                    "                default: localhost:2181")
            return
        }
        
        val group = getArg(args, "group").getOrElse("group01").toLowerCase
        val zkhost = getArg(args, "zkhost").getOrElse("localhost:2181")
        
        val props = new Properties()
        props.put("zk.connect", zkhost)
        props.put("groupid", group)
        props.put("zk.sessiontimeout.ms", "400")
        props.put("zk.synctime.ms", "200")
        props.put("autocommit.interval.ms", "1000")

        val config = new ConsumerConfig(props)
        val cons = new KafkaListener[String](config)

        val channels = getChannels(args)
        
        println("using zookeeper: " + zkhost)
        println("[%s] listening for %s".format(group, channels.reduce(_ + "," + _)))
        
        Runtime.getRuntime.addShutdownHook(new Thread(){
          override def run(){
            println("Closed.")
            cons.shutdown()
          }
        })

        cons.listen(args: _*){ d =>
            if(ERROR_RE.findFirstIn(d.message).isDefined){
                println("\\e[0;31m" + d.topic + " | " + d.message + "\\e[0m")
            }else{
                println(d.topic + " | " + d.message)
            }
        }
    }
}

package com.ansvia.skywatchr

import java.util.Properties
import kafka.consumer.{KafkaListener, ConsumerConfig}

object SkyWatchr {
  
    def getArg(args:Array[String],key:String):Option[String] = {
      var i = 0
      var rv:Option[String] = None
      args.foreach { arg =>
        if (arg.startsWith("--")){
          if(arg.substring(2) == key){
            val nextI = i + 1
            if (args.length > nextI)
              rv = Some(args(nextI))
          }
        }
        i += 1
      }
      rv
    }
    
    def getChannels(args:Array[String])={
      val ss = args.reduce(_ + "|" + _)
      val rv = """\-\-\w+\|\w+""".r.replaceAllIn(ss,"").split("\\|")
      rv.filter(_.length > 0)
    }
  
    def main(args:Array[String]){
        
        if(args.length == 0){
            println("Usage: java -jar skywatchr.jar [OPTIONS] [CHANNEL]")
            println("")
            println("OPTIONS are: ")
            println("     --group    consumer group id.")
            return
        }
        
        val group = getArg(args, "group").getOrElse("group01").toLowerCase
        
        val props = new Properties()
        props.put("zk.connect", "localhost:2181")
        props.put("groupid", group)
        props.put("zk.sessiontimeout.ms", "400")
        props.put("zk.synctime.ms", "200")
        props.put("autocommit.interval.ms", "1000")

        val config = new ConsumerConfig(props)
        val cons = new KafkaListener[String](config)

        Runtime.getRuntime.addShutdownHook(new Thread(){
          override def run(){
            println("Closed.")
            cons.shutdown()
          }
        })

        val channels = getChannels(args)
        println("[%s] listening for %s".format(group, channels.reduce(_ + "," + _)))

        cons.listen(args: _*){ d =>
          println(d.topic + " | " + d.message)
        }
    }
}
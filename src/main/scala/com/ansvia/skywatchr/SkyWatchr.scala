
package com.ansvia.skywatchr

import java.util.Properties
import kafka.consumer.{KafkaListener, ConsumerConfig}

object SkyWatchr {
    def main(args:Array[String]){
        val props = new Properties()
        props.put("zk.connect", "localhost:2181")
        props.put("groupid", "group01")
        props.put("zk.sessiontimeout.ms", "400")
        props.put("zk.synctime.ms", "200")
        props.put("autocommit.interval.ms", "1000")

        val config = new ConsumerConfig(props)
        val cons = new KafkaListener[String](config)

        Runtime.getRuntime.addShutdownHook(new Thread(){
          override def run(){
            println("^C detected.")
            cons.shutdown()
          }
        })

        println("Listening to: " + args.reduce(_ + "," + _))

        cons.listen(args: _*){ d =>
          println(d.topic + " | " + d.message)
        }
    }
}
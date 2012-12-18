
package com.ansvia.skywatchr

import java.util.Properties
import kafka.consumer.{KafkaListener, ConsumerConfig}
import com.typesafe.config.{ConfigValue, ConfigFactory}
import java.io.File
import collection.{JavaConversions, immutable}
import java.util
import scala.sys.process._
import util.Map.Entry

/**
 * SkyWatchr is a tool for easy capturing log on distributed
 * servers in one interface via Kafka engine.
 */
object SkyWatchr {

    val VERSION = "0.0.4"

    /**
     * log level capture
     * in regex.
     */
    private lazy val ERROR_RE = """\[ERROR.*?\]""".r
    private lazy val WARN_RE = """\[WARN(ING)?.*?\]""".r
    private lazy val INFO_RE = """\[INFO?.*?\]""".r


    /**
     * Get parameter value.
     * @param args arguments.
     * @param key parameter option name.
     * @return
     */
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

    /**
     * capture channels name from parameter.
     * @param args parameter array.
     * @return
     */
    def getChannels(args:Array[String])={
        val ss = args.reduce(_ + "|" + _)
        val rv = """\-\-[\w\.\-_\:]+\|[\w\.\-_\:]+""".r.replaceAllIn(ss,"").split("\\|")
        rv.filter(a => a.length > 0 && !a.startsWith("--"))
    }

    /**
     * Main entrypoint.
     * @param args arguments.
     */
    def main(args:Array[String]){

        if(args.length == 0){
            println("Usage: java -jar skywatchr.jar [OPTIONS] [CHANNEL]")
            println("")
            println("OPTIONS are: ")
            println("     --group    consumer group id.")
            println("     --zkhost   zookeeper host and port [IP]:[PORT].\n" +
                    "                default: localhost:2181")
            println("     --config   configuration file if any (optional).")
            return
        }

        val group = getArg(args, "group").getOrElse("group01").toLowerCase
        val zkhost = getArg(args, "zkhost").getOrElse("localhost:2181")
        val configFile = getArg(args, "config").getOrElse("")

        var actionsShellExecute:util.Set[Entry[String, ConfigValue]] = null

        if (configFile.length > 0){
            println("Using config file: " + configFile)
            val conf = ConfigFactory.parseFile(new File(configFile))
//            actionsShellExecute = JavaConversions.collectionAsScalaIterable(conf.getStringList("action.script-execute")).toSeq
            if (conf.hasPath("action.script-execute")){
                val x = conf.getConfig("action.script-execute")
                actionsShellExecute = x.entrySet()
            }
        }

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
                println(Console.RED + d.topic + " | " + d.message + Console.RESET)
            }else if(WARN_RE.findFirstIn(d.message).isDefined){
                println(Console.YELLOW + d.topic + " | " + d.message + Console.RESET)
            }else if(INFO_RE.findFirstIn(d.message).isDefined){
                println(Console.CYAN + d.topic + " | " + d.message + Console.RESET)
            }else{
                println(d.topic + " | " + d.message)
            }
            if(actionsShellExecute != null){
                val it = actionsShellExecute.iterator()
                while(it.hasNext){
                    val c = it.next()
                    var key = c.getKey.replaceAll("""^"|"$""", "")
                    key = key.replaceAll("\\\\\\\\", "\\\\")
//                    println(key)
                    val regex = key.r
                    regex.findFirstIn(d.message) map { _ =>
                        val script = c.getValue.unwrapped().asInstanceOf[String]
                        println("action.script-execute: " + script)
                        Seq("sh", script) !
                    }
                }
            }
        }
    }
}

import AssemblyKeys._

organization := "com.ansvia.skywatchr"

name := "skywatchr"

version := "0.0.2"

scalaVersion := "2.9.1"

crossPaths := false

seq(ProguardPlugin.proguardSettings :_*)

assemblySettings

proguardOptions ++= Seq(
    keepMain("com.ansvia.skywatchr.SkyWatchr"),
    "-keep class org.apache.log4j.*",
    "-keep class ch.qos.*",
    "-keepclassmembers class org.apache.commons.logging.impl.Log4JLogger { *; }",
    "-keepclassmembers class org.apache.commons.logging.impl.Jdk14Logger { *; }",
    "-keepclassmembers class akka.event.slf4j.Slf4jEventHandler { *; }",
    "-keep class ch.qos.logback.classic.PatternLayout",
    "-keep class ch.qos.logback.core.ConsoleAppender",
    "-keep class * implements org.slf4j.Logger",
    "-keep class * extends ch.qos.logback.core.encoder.*",
    "-keepclassmembers class * extends com.ansvia.rootr.plugin.BaseRootrPlugin { *; }",
    "-keepclassmembers class ch.qos.logback.core.* { *; }",
    "-keepclassmembers class * extends ch.qos.logback.core.encoder.EncoderBase<E> { *; }",
    "-keep class kafka.consumer.ZookeeperConsumerConnector",
    "-keepclassmembers class kafka.consumer.ZookeeperConsumerConnector { *; }",
    "-keep class kafka.*",
    "-keepclassmembers class kafka.* { *; }",
    "-keep class kafka.utils.Log4jController",
    "-keepclassmembers class kafka.utils.Log4jController { *; }",
    "-keep public class javax.management.*",
    "-keepnames public class javax.management.*",
    "-keepclassmembers class javax.* { *; }",
    "-keep class * implements *MBean",
    "-keepclassmembers class *MBean { *; }"
    )

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"Ansvia repo" at "http://scala.repo.ansvia.com/releases/",
	"Local Repo" at "file://" + Path.userHome + "/.m2/repository"
)

libraryDependencies ++= Seq(
    "kafka" % "kafka-core" % "0.7.2",
	"ch.qos.logback" % "logback-classic" % "1.0.7",
	"ch.qos.logback" % "logback-core" % "1.0.7",
	"org.slf4j" % "slf4j-api" % "1.6.6"
)

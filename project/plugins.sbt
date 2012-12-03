resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"Ansvia repo" at "http://scala.repo.ansvia.com/releases/",
	"Local Repo" at "file://" + Path.userHome + "/.im2/repository"
)

resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.ansvia" % "onedir" % "0.4")

addSbtPlugin("com.github.siasia" % "xsbt-proguard-plugin" % "0.1.2")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.5")

name := "Hash Rename"

scalaVersion := "2.12.7"

scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:postfixOps",
    "-language:reflectiveCalls",
    "-unchecked"
)

libraryDependencies += "commons-codec" % "commons-codec" % "1.11"

mainClass in assembly := Some("com.worthlesscog.utils.HashRename")
test in assembly := {}

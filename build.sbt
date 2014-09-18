name := """variants"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

libraryDependencies +="org.scalax" %% "semweb-sesame" % "0.6.11" //my lib to deal with sesame

libraryDependencies += "org.openrdf.sesame" % "sesame-sail-memory" % "2.6.10"

libraryDependencies += "org.openrdf.sesame" % "sesame-repository-sail" % "2.6.10"

libraryDependencies += "org.openrdf.sesame" % "sesame-rio-turtle" % "2.6.10" //TURTLE parser

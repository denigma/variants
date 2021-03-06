name := """variants"""

version := "0.1"

scalaVersion := "2.11.2"

bintraySettings


libraryDependencies +="org.scalax" %% "semweb-sesame" % "0.6.14" //my lib to deal with sesame

libraryDependencies += "org.openrdf.sesame" % "sesame-sail-memory" % "2.7.13"//"2.6.10"

libraryDependencies += "org.openrdf.sesame" % "sesame-repository-sail" % "2.7.13"//"2.6.10"

libraryDependencies += "org.openrdf.sesame" % "sesame-rio-turtle" % "2.7.13"//TURTLE parser

libraryDependencies += "org.apache.jena" % "jena-arq" % "2.12.0" //ARQ for remote endpoints


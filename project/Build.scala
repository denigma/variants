import sbt.Keys._
import sbt._
//import play.Keys._
import bintray.Opts
import bintray.Plugin.bintraySettings
import bintray.Keys._
import com.typesafe.sbt.packager.universal.UniversalKeys
import play._
import play.Play._
import play.Play.autoImport._


object Build extends sbt.Build with UniversalKeys {



  lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
    resolvers += Opts.resolver.repo("scalax", "scalax-releases")
  )

}
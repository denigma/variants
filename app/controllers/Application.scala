package controllers

import java.io.File

import org.openrdf.model.Statement
import org.openrdf.model.impl.URIImpl
import org.scalax.semweb.sesame._
import play.api.Play.current

import parser.{InMemoryParser, Database}
import play.api._
import play.api.mvc._

import scala.util.Try

object Application extends Controller {

  def index = Action {

    val parser = new InMemoryParser()
    val fileToParse = play.api.Play.getFile("./files/variants.ttl")
    parser.parseFile(fileToParse)


    /*
        // first three arguments are: subject | predicate | object
        // if you put "null" it means any subject | predicate | object will pass
    */
    val items: List[Statement] = Database.repo.getConnection.getStatements(null,null,null,true).toList //get a list of statements
    /*  If you want to see all statements about some particular object, just change null to corresponding URI, it will look like this:
        val items: List[Statement] = Database.repo.getConnection.getStatements(null,null,new URIImpl("http://denigma.org/resource/Genetic_Association"),true).toList //get a list of statements
    */



    Ok(views.html.facts(items))
  }

}
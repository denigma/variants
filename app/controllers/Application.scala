package controllers

import java.io.{FileOutputStream, File, ByteArrayOutputStream}

import com.hp.hpl.jena.query.QuerySolution
import org.openrdf.model.impl.StatementImpl
import org.openrdf.model.{Resource, Statement, URI, Value}
import org.openrdf.rio.RDFParser.DatatypeHandling
import org.openrdf.rio.{Rio, ParserConfig}
import org.openrdf.rio
import org.scalax.semweb.rdf._
import parser.{Database, InMemoryParser}
import play.api.Play.current
import play.api.mvc._
import play.twirl.api.Html
import org.scalax.semweb.sesame._
import scala.collection.JavaConversions._
import scala.collection.immutable.VectorBuilder
import org.openrdf.rio.turtle.TurtleWriter


object Application extends Controller {

  def index = Action {

    val config =  new ParserConfig(false,false,true, DatatypeHandling.IGNORE)
    val parser = new InMemoryParser(config)


    val mouseFile = "./files/Mouse_ontology.ttl"

    //val fileName = "./files/All.ttl"//
    //val fileName =  "./files/Denigma.ttl"

    val fileToParse = play.api.Play.getFile("./files/variants.ttl")
    //val fileToParse = play.api.Play.getFile(fileName)

    parser.parseFile(fileToParse)


    /*
        // first three arguments are: subject | predicate | object
        // if you put "null" it means any subject | predicate | object will pass
    */
    val items: List[Statement] = Database.repo.getConnection.getStatements(null, null, null, true).toList //get a list of statements
    /*  If you want to see all statements about some particular object, just change null to corresponding URI, it will look like this:
        val items: List[Statement] = Database.repo.getConnection.getStatements(null,null,new URIImpl("http://denigma.org/resource/Genetic_Association"),true).toList //get a list of statements
    */



    Ok(views.html.facts(items))
  }

  import com.hp.hpl.jena.query.QuerySolution

  def qs2Result(qs:QuerySolution): List[(String, String)] = {
    qs.varNames().map(q=> q->qs.get(q).toString ).toList
  }

  def qs2Sesame(qs:QuerySolution): List[(String, RDFValue)] = {
    qs.varNames().map { case q =>

      q->(qs.get(q) match
      {
        case s: com.hp.hpl.jena.rdf.model.Literal=>
          val dt: String = s.getDatatypeURI
          if(dt!=null && dt.contains(":") && !dt.contains("data")&& !dt.contains("percent")) new TypedLiteral(s.getString,new IRI(dt))  else new StringLiteral(s.getString)

        case node if node.isAnon => new BlankNode( node.asNode().getBlankNodeId.getLabelString)
        case node if node.isURIResource=>
          val uri = node.asResource().getURI
            new IRI(uri)

      })
    }.toList
  }



  def st2str(lst:List[(String, String)] ) = lst.foldLeft("<tr>"){case (acc,(key,value)) => acc+s"<th>$key</th><td>$value</td>" }+"</tr>"

  def sesameStr(lst:List[(String,RDFValue)]) = lst.foldLeft("<tr>"){case (acc,(key,value)) => acc+s"<th>$key</th><td>${value.stringValue}</td>" }+"</tr>"



}





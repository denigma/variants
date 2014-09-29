package controllers

import java.io.{ByteArrayOutputStream, File, FileOutputStream}

import com.hp.hpl.jena.query.QuerySolution
import org.openrdf.model.impl.StatementImpl
import org.openrdf.model.{Resource, Statement, URI, Value}
import org.openrdf.rio.turtle.TurtleWriter
import org.scalax.semweb.rdf._
import play.api.mvc._

import scala.collection.immutable.VectorBuilder


object DenigmaTester extends Controller with DenigmaSelector{
  import org.scalax.semweb.rdf.vocabulary



  def statements(sub:Option[Res] = None, prop:Option[IRI] = None, obj:Option[RDFValue] = None): Vector[Statement] =
  {
    import org.scalax.semweb.sesame._
    val q = s"SELECT * WHERE { ${sub.map(_.toString).getOrElse("?sub")} ${prop.map(_.toString).getOrElse("?prop")} ${obj.map(_.toString).getOrElse("?obj")} } LIMIT 100000000".stripMargin
    val results: List[QuerySolution] = this.query(q)
    val vb = results.foldLeft(new VectorBuilder[StatementImpl]){
      case (acc,el: QuerySolution)=>
        val s:Resource  =    res2Resource(sub.getOrElse(jena2Sesame(el.get("sub")).asInstanceOf[Res]))
        val p:URI   =  IRI2URI(prop.getOrElse(prop.getOrElse(jena2Sesame(el.get("prop")).asInstanceOf[IRI])))
        val o:Value = rdfValue2Value(obj.getOrElse(jena2Sesame(el.get("obj"))))
        acc +=new StatementImpl(s,p,o)
    }
    vb.result()
  }

  def saveIntoFile(sts:Seq[Statement],fileName:String):Int = {
    val count = sts.size
    val file = new File("./files/"+fileName)
    val stream =  new FileOutputStream(file)
    val writer = new TurtleWriter(stream)
    writer.startRDF()
    writer.handleNamespace("", "http://denigma.org/resource/")
    writer.handleNamespace("de", "http://denigma.org/resource/")
    writer.handleNamespace("rdf",vocabulary.RDF.namespace)
    writer.handleNamespace("rdfs",vocabulary.RDFS.namespace)
    sts.foreach{s=>writer.handleStatement(s) }
    writer.endRDF()
    stream.close()
    count
  }


  def saveFile()= Action {
    import vocabulary._

    val sts = this.statements(obj = Some(RDFS.CLASS))++
      this.statements(prop = Some(vocabulary.RDFS.SUBCLASSOF))++
      this.statements( obj = Some(RDF.PROPERTY))++
      this.statements(prop = Some(vocabulary.RDFS.SUBPROPERTYOF))++
      this.statements(obj = Some(vocabulary.OWL.CLASS))
    val count = saveIntoFile(sts,fileName = "Denigma.ttl")

    Ok(s"${count.toString} statement were processed and saved to Denigma.ttl file").as("text/plain")
  }

  def saveDump()= Action {
    import vocabulary._

    val sts = this.statements()
    val count = saveIntoFile(sts,fileName = "All.ttl")

    Ok(s"${count.toString} statement were processed and saved to Denigma.ttl file").as("text/plain")
  }


  def accessDenigma = Action {

    val sts = this.statements(prop = Some(vocabulary.RDFS.SUBCLASSOF))

    val stream = new ByteArrayOutputStream()
    val writer = new TurtleWriter(stream)
    writer.startRDF()
    sts.foreach{s=>writer.handleStatement(s) }
    writer.endRDF()
    Ok(stream.toString).as("text/plain")
  }
}

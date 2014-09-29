package controllers

import org.scalax.semweb.rdf._

import scala.collection.JavaConversions._


trait DenigmaSelector
{

  def sparqlEndpoint:String = "http://denigma.org/sparql"

  import com.hp.hpl.jena.query.QuerySolution
  import com.hp.hpl.jena.rdf.model.RDFNode

  def qs2Result(qs:QuerySolution): List[(String, String)] = {
    qs.varNames().map(q=> q->qs.get(q).toString ).toList
  }

  implicit def jena2Sesame(node:RDFNode): RDFValue  = node match {
    case s: com.hp.hpl.jena.rdf.model.Literal=>
      val dt: String = s.getDatatypeURI
      if(dt!=null && dt.contains(":") && !dt.contains("date")&& !dt.contains("percent")) new TypedLiteral(s.getString,new IRI(dt))  else new StringLiteral(s.getString)

    case node if node.isAnon => new BlankNode( node.asNode().getBlankNodeId.getLabelString)
    case node if node.isURIResource=>
      val uri = node.asResource().getURI
      //play.Logger.info(s"URI IS $uri")
      new IRI(uri)
  }

  def qs2Sesame(qs:QuerySolution): List[(String, RDFValue)] = {
    qs.varNames().map { case q =>    q->jena2Sesame(qs.get(q))  }.toList
  }




  def st2str(lst:List[(String, String)] ) = lst.foldLeft("<tr>"){case (acc,(key,value)) => acc+s"<th>$key</th><td>$value</td>" }+"</tr>"

  def sesameStr(lst:List[(String,RDFValue)]) = lst.foldLeft("<tr>"){case (acc,(key,value)) => acc+s"<th>$key</th><td>${value.stringValue}</td>" }+"</tr>"




  import com.hp.hpl.jena.query._
  import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP

  val defQuery =    """
                      |SELECT *
                      |WHERE {
                      |    ?sub <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?obj
                      |} LIMIT 10000
                      |
                    """.stripMargin

  def query(sparqlQuery:String = defQuery) = {

    val q = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ)
    val querySolutionMap = new QuerySolutionMap()
    val parameterizedSparqlString = new ParameterizedSparqlString(q.toString, querySolutionMap)

    val httpQuery = new QueryEngineHTTP(sparqlEndpoint, parameterizedSparqlString.asQuery())
    // execute a Select query
    httpQuery.execSelect().toList

  }



}

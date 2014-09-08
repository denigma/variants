package parser


import org.openrdf.repository.Repository
import org.openrdf.repository.Repository
import org.openrdf.repository.sail.{SailRepositoryConnection, SailRepository}
import org.openrdf.sail.memory.MemoryStore
import org.{openrdf=>se}
import org.openrdf
import org.scalax.semweb.sesame._
import org.scalax.semweb.commons.LogLike
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.sesame.files.{SesameFileListener, SesameFileParser}

/**
 * Singletone for database
 */
object Database {
  /**
   *  Initialize inmemory database repository
   */
  lazy val repo = {
    val repo =new SailRepository(new MemoryStore())
    repo.initialize()
    repo
  }

}

class InMemoryParser extends SesameFileParser
{
  override def makeListener(filename: String, con: WriteConnection, context: IRI, lg: LogLike): SesameFileListener = new MemoryListener(filename,context,lg)

  override def writeConnection: WriteConnection = Database.repo.getConnection //gets a connection from the database

  override type WriteConnection = SailRepositoryConnection //connection type alias

  override def lg: LogLike =  AppLogger //just take a play logger
}

/**
 * Just a listener that deals with parser event
 * @param fileName name of the file
 * @param context Context with wich it will be parsed
 * @param lg Logger that will write debue/error/info messages to the log
 */
class MemoryListener(fileName:String, context: se.model.Resource = null, val lg:LogLike) extends SesameFileListener(fileName,context)( lg)
{
  override type WriteConnection = SailRepositoryConnection //define a type alias of write collection

  override def writeConnection: WriteConnection = Database.repo.getConnection

}
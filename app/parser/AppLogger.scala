package parser

import org.scalax.semweb.commons.LogLike
import play.api.LoggerLike

/**
 * Just a Logger class
 */
object AppLogger extends LoggerLike with LogLike
{
   val logger: org.slf4j.Logger  = play.api.Logger.logger
}

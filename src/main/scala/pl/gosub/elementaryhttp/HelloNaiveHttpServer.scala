package pl.gosub.elementaryhttp

import io.shaka.http.ContentType.{TEXT_PLAIN, APPLICATION_JSON}
import io.shaka.http.HttpServer
import io.shaka.http.Request._
import io.shaka.http.Response.respond
import io.shaka.http.Status.NOT_FOUND

object HelloNaiveHttpServer extends App {
  //val httpServer = HttpServer(request => respond("Hello World!")).start()
  val httpServer = HttpServer(9124).handler(request => respond("Hello World!")).start()

  import io.shaka.http.RequestMatching._
  httpServer.handler{
    case GET("/hello") => respond("Hello world")
    case GET(echoUrl) => respond(echoUrl)
    case request@POST("/some/restful/thing") => respond("Hello")
    case req@GET(_) if req.accepts(APPLICATION_JSON) => respond("""{"hello":"world"}""").contentType(APPLICATION_JSON)
    case GET(url"/tickets/$ticketId?messageContains=$messageContains") => respond(s"Ticket $ticketId, messageContains $messageContains").contentType(TEXT_PLAIN)
    case _ => respond("doh!").status(NOT_FOUND)
  }
}

package pl.gosub.elementaryhttp

import com.sun.net.httpserver.{HttpExchange => SunHttpExchange, HttpHandler => SunHttpHandler}
import scala.io.Source
import Http._
import Handlers._

object Http {
  type HttpHandler = (Request) => (Response)
  type Url = String
  type Header = (HttpHeader, String)
}

case class HttpMethod(name: String)

object ValidMethods {
  val GET = HttpMethod("GET")
  val HEAD = HttpMethod("HEAD")
  val POST = HttpMethod("POST")
  val PUT = HttpMethod("PUT")
  val DELETE = HttpMethod("DELETE")
  val UNKNOWN_METHOD = HttpMethod("")
}

sealed trait HttpHeader {def name: String }
case object ACCEPT extends HttpHeader {val name = "Accept"}
case object HOST extends HttpHeader {val name = "Host"}
case object USER_AGENT extends HttpHeader {val name = "User-Agent"}
case object CONTENT_TYPE extends HttpHeader {val name = "Content-Type"}
case object CONTENT_LENGTH extends HttpHeader {val name = "Content-Length"}
case object UNKNOWN_HEADER extends HttpHeader {val name = ""}

object HttpConversions {
  import ValidMethods._
  val validMethods = List(
      GET,
      HEAD,
      POST,
      PUT,
      DELETE
      )

  implicit def stringToMethod(s: String): HttpMethod = validMethods.find(_.name == s).getOrElse(UNKNOWN_METHOD)

  val headers = List[HttpHeader](
    ACCEPT,
    HOST,
    USER_AGENT,
    CONTENT_TYPE,
    CONTENT_LENGTH
  )
  import scala.collection.JavaConversions.mapAsScalaMap
  import scala.collection.JavaConversions.collectionAsScalaIterable
  private def stringToHeader(s: String): HttpHeader = headers.find(_.name == s).getOrElse(UNKNOWN_HEADER)
  def toHeaders(rawHeaders: java.util.Map[String, java.util.List[String]]) : List[Header] = {
    val rhl = rawHeaders.toList
    val rh = rhl.map {case (headerName, valuesList) => (stringToHeader(headerName), valuesList.headOption.getOrElse(""))}
    rh
  }
}

case class HttpStatus(code: Int)

object Request {

  object GET {
    // def apply(url: Url) = Request(GET.asInstanceOf[HttpMethod], url)
    def unapply(req: Request): Option[String] = if (req.method == ValidMethods.GET) Some(req.url) else None
  }

  object POST {
    // def apply(url: Url) = Request(POST.asInstanceOf[HttpMethod], url)
    def unapply(req: Request): Option[String] = if (req.method == ValidMethods.POST) Some(req.url) else None
  }

  object PUT {
    // def apply(url: Url) = Request(PUT.asInstanceOf[HttpMethod], url)
    def unapply(req: Request): Option[String] = if (req.method == ValidMethods.PUT) Some(req.url) else None
  }

  object HEAD {
    // def apply(url: Url) = Request(HEAD.asInstanceOf[HttpMethod], url)
    def unapply(req: Request): Option[String] = if (req.method == ValidMethods.HEAD) Some(req.url) else None
  }

  object DELETE {
    // def apply(url: Url) = Request(DELETE.asInstanceOf[HttpMethod], url)
    def unapply(req: Request): Option[String] = if (req.method == ValidMethods.DELETE) Some(req.url) else None
  }
}

case class Request(method: HttpMethod, url: Url, headers: List[Http.Header] = Nil, body: Option[String] = None) {
  def header(header: HttpHeader, value: String): Request = copy(headers = (header, value) :: headers)
  def header(header: HttpHeader) = headers.find(_._1 == header).map(_._2)
  def contentType(contentType: String) = header(CONTENT_TYPE, contentType)
  def accept(contentType: String) = header(ACCEPT, contentType)
  def body(content: String) = copy(body = Some(content))
  def changeMethod(newMethod: HttpMethod): Request = copy(method = newMethod)
}

case class Response(status: HttpStatus = HttpStatus(200), headers: List[Header] = Nil, body: Option[String] = None) {
  def status(newStatus: Int): Response = copy(status = HttpStatus(newStatus))
  def header(header: HttpHeader, value: String) = copy(headers = (header, value) :: headers)
  def header(header: HttpHeader) = headers.find(_._1 == header).map(_._2)
  def contentType(contentType: String) = header(CONTENT_TYPE, contentType)
  def body(content: String) = copy(body = Some(content))
}

object Handlers {
  object HEADRequestHandler {
    import ValidMethods._
    def ~>(handler: HttpHandler): HttpHandler = (request) => {
      def foldHeadRequest[T](original: T)(doWhenHead: T => T): T = {
        if(request.method == HEAD) doWhenHead(original) else original
      }
      val response = handler(foldHeadRequest(request)(_.changeMethod(GET)))
      foldHeadRequest(response)(_.header(CONTENT_LENGTH, response.body.fold("0")(_.length.toString)))


    }
  }

  object RequestHandler {
    def ~>(handler: HttpHandler): HttpHandler = (request) => try {
      handler(request)
    } catch {
      case e: Throwable => Response().body(s"Server error: ${e.getMessage}").status(500)
    }
  }

}

class ElementaryHttpHandlerAdapter(handler: HttpHandler) extends SunHttpHandler {
  import HttpConversions._

  override def handle(exchange: SunHttpExchange): Unit = {
    respond(exchange, (RequestHandler ~> (HEADRequestHandler ~> handler))(request(exchange)))
    exchange.close()
  }

  private def request(exchange: SunHttpExchange) = Request(
    exchange.getRequestMethod,
    exchange.getRequestURI.toString,
    toHeaders(exchange.getRequestHeaders),
    Some(Source.fromInputStream(exchange.getRequestBody).map(_.toByte).toArray.toString)
  )

  private def respond(exchange: SunHttpExchange, response: Response) {
    response.headers.foreach {
      header =>
        exchange.getResponseHeaders.add(header._1.name, header._2)
    }
    exchange.sendResponseHeaders(response.status.code, response.body.map(_.length).getOrElse(0).toLong)
    response.body.foreach {
      b =>
        val os = exchange.getResponseBody
        os.write(b.getBytes)
        os.close()
    }
  }

}




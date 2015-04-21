import pl.gosub.elementaryhttp.Request.{POST, GET}
import pl.gosub.elementaryhttp.{Response, HttpStatus, ElementaryHttpServer}

object HelloElementaryHttpServer extends App {
	println("Hello")
	val httpServer = ElementaryHttpServer(9124).handler(request => Response(HttpStatus(200), Nil, Some("Hello World!"))).start()
	httpServer.handler {
		case GET("/hello") => Response(HttpStatus(200), Nil, Some("Hello World!"))
		case GET("/goodbye") => Response(HttpStatus(200), Nil, Some("Goodbye!"))
		case request@POST("/rest") => Response(HttpStatus(200), Nil, Some("Rest response!"))
		case _ => Response(HttpStatus(404), Nil, Some("not found!"))
	}
}
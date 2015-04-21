package pl.gosub.elementaryhttp

import java.net.InetSocketAddress

import com.sun.net.httpserver.HttpServer
import Http._

class ElementaryHttpServer(private val usePort: Int = 9123) {
  val server = HttpServer.create(new InetSocketAddress(usePort), 0)
  server.setExecutor(null)
  server.createContext("/", new ElementaryHttpHandlerAdapter((req) => Response(HttpStatus(404), Nil, Some("No handler defined!"))))

  def start() = {
    val startedAt = System.nanoTime()
    server.start()
    val elapsedTime = BigDecimal((System.nanoTime() - startedAt) / 1000000.0).formatted("%.2f")
    println(s"elementary-http-server started on port ${port()} in $elapsedTime milli seconds")
    this
  }

  def stop() {
    val delayInSeconds = 0
    server.stop(delayInSeconds)
  }

  def port() = server.getAddress.getPort

  def handler(handler: HttpHandler) = {
    server.removeContext("/")
    server.createContext("/", new ElementaryHttpHandlerAdapter(handler))
    this
  }
}

object ElementaryHttpServer {
  def apply(): ElementaryHttpServer = apply(9123)

  def apply(port: Int): ElementaryHttpServer = new ElementaryHttpServer(port)

  def apply(handler: HttpHandler, port: Int = 0): ElementaryHttpServer = apply(port).handler(handler)
}


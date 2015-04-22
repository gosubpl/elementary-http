package pl.gosub.elementaryhttp

case class RequestC(reqType: String = "GET")

sealed trait RequestT {
  def name: String
}

case object GETX extends RequestT {
  val name = "GET"
}

case object PUTX extends RequestT {
  val name = "PUT"
}

object HelloCaseClasses extends App {

  class RequestCProcessor(val r: RequestC) {
    def changeType(newType: String): RequestCProcessor = new RequestCProcessor(RequestC(newType))
  }

  class RequestTProcessor(val r: RequestT) {
    def changeType(newType: RequestT): RequestTProcessor = new RequestTProcessor(newType)
  }

  object RequestTMatch {

    object GETT {
      def unapply(req: RequestTProcessor): Option[String] = if (req.r == GETX) Some("GET") else None
    }

    object PUTT {
      def unapply(req: RequestTProcessor): Option[String] = if (req.r == PUTX) Some("PUT") else None
    }

  }

  val rcp = new RequestCProcessor(RequestC("GET"))
  val rcp2 = rcp.changeType("PUT")

  val rtp = new RequestTProcessor(GETX)
  val rtp2 = rtp.changeType(PUTX)

  def tellMeTheType(rp: RequestTProcessor) = {
    import RequestTMatch._
    rp match {
      case GETT(s) => s"Haha $s"
      case PUTT(s) => s"Hihi $s"
    }
  }
  println(tellMeTheType(rtp))
  println(tellMeTheType(rtp2))

}

package pl.gosub.elementaryhttp

import unfiltered.request._
import unfiltered.response._
import unfiltered.filter.Intent

object UnfilteredExtendedJettyLocal extends App {
  val echoNice = unfiltered.filter.Planify {
    case Path("/hello") => ResponseString("Hello World from Unfiltered!")
    case Path(Seg("hello" :: name :: Nil)) => ResponseString(s"Hello $name")
    case _ => NotFound ~> ResponseString(
      "I can echo exactly one path element."
    )
  }
  unfiltered.jetty.Server.local(9124).plan(echoNice).run()
}

object UnfilteredExtended2JettyLocal extends App {
  val intent = Intent {
    case Path("/hello") => ResponseString("Hello World from Unfiltered!")
    case Path(Seg("hello" :: name :: Nil)) => ResponseString(s"Hello $name")
    case _ => NotFound ~> ResponseString(
      "I can echo exactly one path element."
    )
  }
  val echoNice = unfiltered.filter.Planify { intent }
  unfiltered.jetty.Server.local(9124).plan(echoNice).run()
}
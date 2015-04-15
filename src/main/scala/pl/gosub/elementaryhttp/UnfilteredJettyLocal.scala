package pl.gosub.elementaryhttp

import unfiltered.request._
import unfiltered.response._

object UnfilteredJettyLocal extends App {
  val echoNice = unfiltered.filter.Planify {
    case Path(Seg(p :: Nil)) => ResponseString(p)
    case _ => ResponseString(
      "I can echo exactly one path element."
    )
  }
  unfiltered.jetty.Server.anylocal.plan(echoNice).run()
}

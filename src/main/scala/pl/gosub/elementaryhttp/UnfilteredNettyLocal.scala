package pl.gosub.elementaryhttp
import unfiltered.request._
import unfiltered.response._

object UnfilteredNettyLocal extends App {
  import unfiltered.request._
  import unfiltered.response._
  val hello = unfiltered.netty.cycle.Planify {
    case Path(Seg(p :: Nil)) => ResponseString(p)
    case _ => ResponseString("hello world")
  }
  unfiltered.netty.Server.http(63218).plan(hello).run()
}

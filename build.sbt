name := "elementaryhttp"

version := "1.0"

scalaVersion := "2.11.5"

val unfilteredVersion = "0.8.1"

resolvers += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.0"

libraryDependencies += "org.scalaz" %% "scalaz-effect" % "7.1.0"

libraryDependencies += "io.shaka" %% "naive-http" % "70"

libraryDependencies += "io.shaka" %% "naive-http-server" % "37"

libraryDependencies ++= Seq(
  "net.databinder"    %% "unfiltered-filter"         % unfilteredVersion,
  "net.databinder"    %% "unfiltered-netty-server"   % unfilteredVersion,
  "net.databinder"    %% "unfiltered-netty"          % unfilteredVersion,
  "net.databinder"    %% "unfiltered-jetty"          % unfilteredVersion
)
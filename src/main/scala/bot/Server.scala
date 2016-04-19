package bot

import play.api.libs.json._
import scalaj.http.{ Http, HttpOptions }
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import scala.util.Try
import scalaj.http.Http.Request

final class Server(
    endpoint: String,
    key: String) {

  def arena: Input = send {
    proxy(Http.post(s"$endpoint/arena")).params("key" -> key)
  }

  def proxy(req: Request) = sys.props.get("http.proxyHost") match {
    case None       => req
    case Some(host) => req.proxy(host, sys.props("http.proxyPort").toInt)
  }

  def training(turns: Int, map: Option[String] = None): Input = send {
    proxy(Http.post(s"$endpoint/training")).params(
      "key" -> key,
      "turns" -> turns.toString,
      "map" -> map.getOrElse(""))
  }

  def move(url: String, dir: Dir.Value): Input = send {
    proxy(Http.post(url)).params(
      "dir" -> dir.toString)
  }

  private val timeout = 24 * 60 * 60 * 1000

  def send(req: Http.Request): Input = {
    val res = req
      .option(HttpOptions.connTimeout(5000))
      .option(HttpOptions.readTimeout(timeout))
      .asString
    Json.parse(res).as[Input]
  }

  // JSON parsing

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import scala.util.Try

  implicit val posReads = Json.reads[Pos]
  implicit val boardReads = (
    (__ \ "size").read[Int] and
    (__ \ "tiles").read[String].map { _.grouped(2).toVector map parseTile })(Board.apply _)
  implicit val heroReads = Json.reads[Hero]
  implicit val gameReads = Json.reads[Game]
  implicit val inputReads = Json.reads[Input]

  def parseTile(str: String): Tile = str.toList match {
    case List(' ', ' ') ⇒ Tile.Air
    case List('#', '#') ⇒ Tile.Wall
    case List('[', ']') ⇒ Tile.Tavern
    case List('$', x)   ⇒ Tile.Mine(int(x))
    case List('@', x)   ⇒ Tile.Hero(int(x) getOrElse sys.error(s"Can't parse $str"))
    case x              ⇒ sys error s"Can't parse $str"
  }

  private def int(c: Char): Option[Int] = Try(java.lang.Integer.parseInt(c.toString)).toOption
}

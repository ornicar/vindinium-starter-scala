package bot

import play.api.libs.json._
import scalaj.http.{ Http, HttpOptions }

final class Server(
  endpoint: String,
  key: String) {

  def arena: Input = send {
    Http.post(s"$endpoint/arena").params("key" -> key)
  }

  def training(turns: Int, map: Option[String] = None): Input = send {
    Http.post(s"$endpoint/training").params(
      "key" -> key,
      "turns" -> turns.toString,
      "map" -> map.getOrElse(""))
  }

  def move(url: String, dir: Dir.Value): Input = send {
    Http.post(url).params(
      "dir" -> dir.toString)
  }

  private val timeout = 10 * 60 * 1000

  def send(req: Http.Request): Input =
    Json.parse(req
      .option(HttpOptions.connTimeout(3000))
      .option(HttpOptions.readTimeout(timeout))
      .asString).as[Input]

  // JSON parsing

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import scala.util.Try

  implicit val posReads = Json.reads[Pos]
  implicit val boardReads = (
    (__ \ "size").read[Int] and
    (__ \ "tiles").read[String].map { _.grouped(2).toVector map parseTile }
  )(Board.apply _)
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

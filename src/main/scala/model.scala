package bot

case class Pos(x: Int, y: Int)

case class Board(size: Int, tiles: Vector[String])

case class Hero(
  id: Int,
  name: String,
  pos: Pos,
  life: Int,
  gold: Int,
  crashed: Boolean)

case class Game(
  id: String,
  turn: Int,
  maxTurns: Int,
  heroes: List[Hero],
  board: Board,
  finished: Boolean)

case class Input(
  game: Game,
  hero: Hero,
  token: String,
  playUrl: String)

object JsonFormat {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import Reads.constraints._

  implicit val posReads = Json.reads[Pos]

  def boardTransformer = (__ \ 'tiles).json.update(of[JsString] map {
    case JsString(v) ⇒ JsArray(v.grouped(2).toList map JsString.apply)
  })

  implicit val boardReads =
    (__.json update boardTransformer) andThen Json.reads[Board]

  implicit val heroReads = Json.reads[Hero]

  implicit val gameReads = Json.reads[Game]

  implicit val inputReads = Json.reads[Input]
}

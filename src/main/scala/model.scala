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
  viewUrl: String,
  playUrl: String)

object Dir extends Enumeration {
  type Dir = Value
  val Stay, North, South, East, West = Value
}

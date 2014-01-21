package bot

object Dir extends Enumeration {
  type Dir = Value
  val Stay, North, South, East, West = Value
}

import Dir._

case class Pos(x: Int, y: Int) {

  def neighbors = Set(North, South, West, East) map to

  def to(dir: Dir) = dir match {
    case Stay  ⇒ this
    case North ⇒ copy(x = x - 1)
    case South ⇒ copy(x = x + 1)
    case East  ⇒ copy(y = y + 1)
    case West  ⇒ copy(y = y - 1)
  }

  def isIn(size: Int) = (x >= 0 && x < size && y >= 0 && y < size)
}

sealed trait Tile
object Tile {
  case object Air extends Tile
  case object Wall extends Tile
  case object Tavern extends Tile
  case class Hero(id: Int) extends Tile
  case class Mine(heroId: Option[Int]) extends Tile
}

case class Board(size: Int, tiles: Vector[Tile]) {

  def at(pos: Pos): Option[Tile] =
    if (pos isIn size) tiles lift (pos.x * size + pos.y) else None
}

case class Hero(
  id: Int,
  name: String,
  pos: Pos,
  life: Int,
  gold: Int,
  mineCount: Int,
  spawnPos: Pos,
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

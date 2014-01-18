package bot

import Dir._
import Tile._

trait Bot {
  def move(input: Input): Dir
}

class RandomBot extends Bot {

  def move(input: Input) = scala.util.Random.shuffle(Dir.values.toList).head
}

class ThirstyBot extends Bot {

  def move(input: Input) = {
    import input._
    val traverser = Traverser(game.board, hero.pos)
    val path = traverser pathTo { pos ⇒
      pos.neighbors map game.board.at exists { Some(Tile.Tavern) == }
    }
    // println(path)
    North
  }
}

case class Traverser(board: Board, from: Pos) {

  implicit class kc[A](a: A) { def pp = { println(a); a } }

  def pathTo(goal: Pos ⇒ Boolean): Option[List[Pos]] = {

    def step(toVisit: List[Pos], visited: Set[Pos], path: List[Pos]): Option[List[Pos]] =
      toVisit match {
        case Nil                     ⇒ None
        case next :: _ if goal(next) ⇒ Some(path)
        case next :: rest ⇒ {
          val succ = (walkableFrom(next) -- visited -- rest).toList
          step(rest ++ succ, visited + next, next :: path)
        }
      }

    step(List(from), Set.empty, Nil)
  }

  private def walkableFrom(pos: Pos) = pos.neighbors filter { p ⇒
    (board at p) == Some(Air)
  }
}

// class LeftyBot extends Bot {

//   private var dir: Dir = North

//   def front(d: Dir) = d
//   def left(d: Dir) = d match {
//     case North ⇒ West
//     case West  ⇒ South
//     case South ⇒ East
//     case _     ⇒ North
//   }
//   def rear(d: Dir) = left(left(dir))
//   def right(d: Dir) = left(rear(dir))

//   def move(input: Input) = {
//     import input._
//     def look(d: Dir) = game.board at (hero.pos to d)
//     def can(f: Dir ⇒ Dir): Boolean = look(f(dir)) exists (Air==)

//     dir = (if (can(front) && !can(left)) front _
//     else if (can(left)) left
//     else if (can(right)) right
//     else rear)(dir)
//     dir
//   }
// }

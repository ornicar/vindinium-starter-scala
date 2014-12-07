package bot

import Dir._
import Tile._
import scala.util.Random
import scala.concurrent._
import ExecutionContext.Implicits.global

trait Bot {
  def move(input: Input): Future[Dir]
}

class RandomBot extends Bot {

  def move(input: Input) = future {
    {
      Random.shuffle(List(Dir.North, Dir.South, Dir.East, Dir.West)) find { dir ⇒
        input.game.board at input.hero.pos.to(dir) exists (Wall!=)
      }
    } getOrElse Dir.Stay
  }
}

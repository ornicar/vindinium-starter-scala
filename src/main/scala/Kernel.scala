package bot

import akka.actor.{ Actor, ActorSystem, Props }
import akka.kernel.Bootable

case object Start

class BotActor(server: Server, shutdown: () ⇒ Unit) extends Actor {

  def receive = {

    case Start ⇒ {
      val input = server.trainingAlone
      println("Start! " + input.viewUrl)
      self ! input
    }

    case input: Input ⇒ {
      if (input.game.finished) {
        println("Game is finished, terminating!")
        context stop self
      }
      else {
        import input._
        println(s"Turn ${game.turn}/${game.maxTurns}, I have ${hero.life} HP and ${hero.gold} gold")
        Thread sleep 200
        self ! server.playRandom(playUrl)
      }
    }
  }

  override def postStop() {
    shutdown()
  }
}

class Kernel extends Bootable {

  val system = ActorSystem("botkernel")
  val serverUrl = System.getProperty("server") + "/api"

  def startup = {
    system.actorOf(
    Props(new BotActor(new Server(serverUrl), shutdown)),
    name = "bot") ! Start
  }

  def shutdown = {
    system.shutdown()
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    (new Kernel).startup()
  }
}

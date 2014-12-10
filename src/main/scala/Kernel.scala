package bot

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._

object Main {

  val bot: Bot = new RandomBot

  def main(args: Array[String]) = makeServer match {
    case Left(error) ⇒ println(error)
    case Right(server) ⇒ args match {
      case Array() ⇒
        training(server, _.training(100))
      case Array("arena") ⇒
        arena(server, Int.MaxValue)
      case Array("arena", games) ⇒
        arena(server, int(games))
      case Array("training", turns) ⇒
        training(server, _.training(int(turns)))
      case Array("training", turns, map) ⇒
        training(server, _.training(int(turns), Some(map)))
      case a ⇒ println("Invalid arguments: " + a.mkString(" "))
    }
  }

  def arena(server: Server, games: Int) {
    @annotation.tailrec
    def oneGame(it: Int) {
      println(s"[$it/$games] Waiting for pairing...")
      val input = server.arena
      println(s"[$it/$games] Start arena game ${input.viewUrl}")
      steps(server, input)
      println(s"\n[$it/$games] Finished arena game ${input.viewUrl}")
      if (it < games) oneGame(it + 1)
    }
    failsafe {
      oneGame(1)
    }
  }

  def training(server: Server, boot: Server ⇒ Input) {
    failsafe {
      val input = boot(server)
      println("Training game " + input.viewUrl)
      steps(server, input)
      println(s"\nFinished training game ${input.viewUrl}")
    }
  }

  def steps(server: Server, input: Input) {
    failsafe {
      step(server, input)
    }
  }

  def failsafe(action: ⇒ Unit) {
    try {
      action
    }
    catch {
      case e: scalaj.http.HttpException ⇒ println(s"\n[${e.code}] ${e.body}")
      case e: Exception                 ⇒ println(s"\n$e")
    }
  }

  @annotation.tailrec
  def step(server: Server, input: Input) {
    if (!input.game.finished) {
      print(".")
      step(server, server.move(input.playUrl, Await.result(bot move input, 60.minutes)))
    }
  }

  def makeServer = (
    Option(System.getProperty("server")) getOrElse "http://vindinium.org/",
    System.getProperty("key")
  ) match {
      case (_, null)  ⇒ Left("Specify the user key with -Dkey=mySecretKey")
      case (url, key) ⇒ Right(new Server(url + "/api", key))
    }

  def int(str: String) = java.lang.Integer.parseInt(str)
}

package bot

object Main {

  def main(args: Array[String]) = makeServer match {
    case Left(error) ⇒ println(error)
    case Right(server) ⇒ {
      val input = server.trainingAlone
      println("Start! " + input.viewUrl)
      play(server, input)
    }
  }

  @annotation.tailrec
  def play(server: Server, input: Input) {
    if (input.game.finished) {
      println("\nGame is finished! " + input.viewUrl)
    }
    else {
      print(".")
      play(server, server.playRandom(input.playUrl))
    }
  }

  def makeServer = System.getProperty("server") match {
    case null ⇒ Left("Specify the server url with -Dserver=http://server-url.org")
    case url  ⇒ Right(new Server(url + "/api"))
  }
}

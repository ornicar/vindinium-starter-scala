package bot

object Main {

  val bot: Bot = new RandomBot

  def main(args: Array[String]) = makeServer match {
    case Left(error)   ⇒ println(error)
    case Right(server) ⇒ start(server)
  }

  def start(server: Server) {
    @annotation.tailrec
    def move(input: Input) {
      if (input.game.finished) println("\nGame is finished! " + input.viewUrl)
      else {
        print(".")
        move(server.move(input.playUrl, bot move input))
      }
    }
    val input = server.trainingAlone
    println("Start! " + input.viewUrl)
    move(input)
  }

  def makeServer = System.getProperty("server") match {
    case null ⇒ Left("Specify the server url with -Dserver=http://server-url.org")
    case url  ⇒ Right(new Server(url + "/api"))
  }
}

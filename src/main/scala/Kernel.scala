package bot

object Main {

  // val bot: Bot = new RandomBot
  // val bot: Bot = new LeftyBot
  val bot: Bot = new ThirstyBot

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
      @annotation.tailrec
      def move(input: Input) {
        if (!input.game.finished) {
          print(".")
          move(server.move(input.playUrl, bot move input))
        }
      }
      println("Waiting for pairing...")
      val input = server.arena
      println(s"Start arena game $it of $games" + input.viewUrl)
      move(input)
      println("\nFinished training game " + input.viewUrl)
      if (it < games) oneGame(it + 1)
    }
    oneGame(1)
  }

  def training(server: Server, boot: Server ⇒ Input) {
    @annotation.tailrec
    def move(input: Input) {
      if (input.game.finished) println("\nFinished training game " + input.viewUrl)
      else {
        print(".")
        move(server.move(input.playUrl, bot move input))
      }
    }
    val input = boot(server)
    println("Start training game " + input.viewUrl)
    move(input)
  }

  def makeServer = (
    System.getProperty("server"),
    System.getProperty("key")
  ) match {
      case (null, _)  ⇒ Left("Specify the server url with -Dserver=http://server-url.org")
      case (_, null)  ⇒ Left("Specify the user key with -Dkey=mySecretKey")
      case (url, key) ⇒ Right(new Server(url + "/api", key))
    }

  def int(str: String) = java.lang.Integer.parseInt(str)
}

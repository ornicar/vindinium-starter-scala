package bot

import JsonFormat._
import play.api.libs.json._
import scalaj.http.{ Http, HttpOptions }

final class Server(endpoint: String) {

  def trainingAlone: Input =
    parseInput(Http(s"$endpoint/training/alone"))

  def playRandom(url: String): Input = {
    val dir = scala.util.Random.shuffle(List("north", "south", "west", "east")).head
    parseInput(Http.post(url).params("dir" -> dir))
  }

  private def parseInput(req: Http.Request) =
    Json.parse(req.option(HttpOptions.connTimeout(10000)).asString).as[Input]
}

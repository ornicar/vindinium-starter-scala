package bot

import play.api.libs.json._
import scalaj.http.{ Http, HttpOptions }

final class Server(endpoint: String) {

  def trainingAlone: Input =
    send(Http(s"$endpoint/training/alone"))

  def move(url: String, dir: Dir.Value): Input =
    send(Http.post(url).params("dir" -> dir.toString))

  def send(req: Http.Request): Input =
    Json.parse(req
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))
      .asString).as[Input]

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import Reads.constraints._

  implicit val posReads = Json.reads[Pos]
  def boardTransformer = (__ \ 'tiles).json.update(of[JsString] map {
    case JsString(v) ⇒ JsArray(v.grouped(2).toList map JsString.apply)
  })
  implicit val boardReads = (__.json update boardTransformer) andThen Json.reads[Board]
  implicit val heroReads = Json.reads[Hero]
  implicit val gameReads = Json.reads[Game]
  implicit val inputReads = Json.reads[Input]
}

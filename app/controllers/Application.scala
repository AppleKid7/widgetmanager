package controllers

/**
  * Created by imoreno on 8/13/17.
  */
import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

@Singleton
class Application @Inject()(cc: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi) extends AbstractController(cc)
  with MongoController with ReactiveMongoComponents {

  override lazy val parse: PlayBodyParsers = cc.parsers

  def jsonCollection = reactiveMongoApi.db.collection[JSONCollection]("widgets")
  def bsonCollection = reactiveMongoApi.db.collection[BSONCollection]("widgets")

  def index = Action {
    Logger.info("Application startup...")

    val posts = List(
      Json.obj(
        "name" -> "Widget One",
    "description" -> "My first widget",
    "author" -> "Ibrahim"
    ),
    Json.obj(
      "name" -> "Widget Two: The Return",
    "description" -> "My second widget",
    "author" -> "Ibrahim"
    ))

    val query = BSONDocument("name" -> BSONDocument("$exists" -> true))
    val command = Count(query)
    val result: Future[CountResult] = bsonCollection.runCommand(command)

    result.map { res =>
      val numberOfDocs: Int = res.value
      if(numberOfDocs <= 1) {
        jsonCollection.bulkInsert(posts.toStream, ordered = true).foreach(i => Logger.info("Record added."))
      }
    }

    Ok("Your database is ready.")
  }

  def cleanup = Action {
    jsonCollection.drop().onComplete {
      case _ => Logger.info("Database collection dropped")
    }
    Ok("Your database is clean.")
  }
}

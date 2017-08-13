package controllers

import javax.inject.Inject

import play.api.mvc._
import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import repos.WidgetRepoImpl

/**
  * Created by imoreno on 8/13/17.
  */
object WidgetFields {
  val Id = "_id"
  val Name ="name"
  val Description = "description"
  val Author = "author"
}

class Widgets @Inject()(cc: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi) extends AbstractController(cc)
  with MongoController with ReactiveMongoComponents
{
  import controllers.WidgetFields._

  override lazy val parse: PlayBodyParsers = cc.parsers
  def widgetRepo = new WidgetRepoImpl(reactiveMongoApi)

  def index = Action.async {
    implicit request => widgetRepo.find().map(widgets => Ok(Json.toJson(widgets)))
  }

  def create = {
    Action.async(BodyParsers.parse.json) { implicit request =>
      val name = (request.body \ Name).as[String]
      val description = (request.body \ Description).as[String]
      val author = (request.body \ Author).as[String]
      widgetRepo.save(BSONDocument(
        Name -> name,
      Description -> description,
      Author -> author
      )).map(result => Created)
    }
  }

  def read(id: String) = {
    Action.async {
      implicit request => widgetRepo.select(BSONDocument(Id -> BSONObjectID(id))).map(widget => Ok(Json.toJson(widget)))
    }
  }

  def update(id: String) = {
    Action.async(BodyParsers.parse.json) { implicit request =>
      val name = (request.body \ Name).as[String]
      val description = (request.body \ Description).as[String]
      val author = (request.body \ Author).as[String]
      widgetRepo.update(BSONDocument(Id -> BSONObjectID(id)),
      BSONDocument("$set" -> BSONDocument(Name -> name, Description -> description, Author -> author)))
      .map(result => Accepted)
    }
  }

  def delete(id: String) = {
    Action.async {
      widgetRepo.remove(BSONDocument(Id -> BSONObjectID(id))).map(result => Accepted)
    }
  }
}

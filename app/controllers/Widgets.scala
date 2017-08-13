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
class Widgets @Inject()(cc: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi) extends AbstractController(cc)
  with MongoController with ReactiveMongoComponents
{
  override lazy val parse: PlayBodyParsers = cc.parsers
  def widgetRepo = new WidgetRepoImpl(reactiveMongoApi)

  def index = TODO

  def create = TODO

  def read(id: String) = TODO

  def update(id: String) = TODO

  def delete(id: String) = TODO
}

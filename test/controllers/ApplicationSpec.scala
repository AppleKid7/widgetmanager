package controllers

/**
  * Created by imoreno on 8/13/17.
  */
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.mvc.ControllerComponents
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{JsArray, Json}
import repos.WidgetRepoImpl
import play.modules.reactivemongo.ReactiveMongoApi
import org.specs2.mock._
import reactivemongo.api.commands.LastError
import reactivemongo.bson.BSONDocument
import play.api.mvc.{Result, _}

import org.scalatest._
import play.api.test._
import play.api.test.Helpers._

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


class ApplicationSpec extends PlaySpec with Injecting with GuiceOneAppPerTest with Mockito {
  val port = 9000
  val mockWidgetRepo = mock[WidgetRepoImpl]
  val reactiveMongoApi = mock[ReactiveMongoApi]
  val mockCc = mock[ControllerComponents]
  val documentId = "56a0ddb6c70000c700344254"
  val lastRequestStatus = new LastError(true, None, None, None, 0, None, false, None, None, false, None, None)

  val widgetOne = Json.obj(
    "name" -> "Widget One",
    "description" -> "My first widget",
    "author" -> "Ibrahim"
  )

  val posts = List(
    widgetOne,
    Json.obj(
      "name" -> "Widget Two: The Return",
      "description" -> "My second widget",
      "author" -> "Ibrahim"
    ))

  class TestController() extends Widgets(mockCc, reactiveMongoApi) {
    override def widgetRepo: WidgetRepoImpl = mockWidgetRepo
  }

  "Application" should {
    "send 404 on a bad request" in {
      val request = FakeRequest(GET, "/boum")
      val boum = route(app, request).get
      status(boum) mustBe NOT_FOUND
    }

    "Widgets#delete" should {
      "remove widget" in {
        val controller = new TestController()
        bind(classOf[Widgets]) to classOf[TestController]
        mockWidgetRepo.remove(any[BSONDocument])(any[ExecutionContext]) returns Future(lastRequestStatus)

        val result: Future[Result] = controller.delete(documentId).apply(FakeRequest())

        status(result) mustBe ACCEPTED
        there was one(mockWidgetRepo).remove(any[BSONDocument])(any[ExecutionContext])
      }
    }

    "Widgets#delete" should {
      "remove widget alternate" in {
        mockWidgetRepo.remove(any[BSONDocument])(any[ExecutionContext]) returns Future(lastRequestStatus)
        bind(classOf[WidgetRepoImpl]) toInstance mockWidgetRepo
        val request = FakeRequest(DELETE, s"/api/widget${documentId}")
        val deleted = route(app, request).get
        status(deleted) mustBe ACCEPTED
      }
    }
  }
}

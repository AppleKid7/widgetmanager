package controllers

/**
  * Created by imoreno on 8/13/17.
  */
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{JsArray, Json}
import repos.WidgetRepoImpl
import play.modules.reactivemongo.ReactiveMongoApi
import org.specs2.mock._
import reactivemongo.api.commands.LastError
import reactivemongo.bson.BSONDocument
import play.api.mvc.{Result, _}

import play.api.test._
import play.api.test.Helpers._


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends PlaySpec with Injecting with GuiceOneAppPerTest with Mockito {
  val port = 9000
  val mockWidgetRepo = mock[WidgetRepoImpl]
  val reactiveMongoApi = mock[ReactiveMongoApi]
  val mockCc = stubControllerComponents()
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

  val controller = new TestController()

  "Application" should {
    "send 404 on a bad request" in {
      val request = FakeRequest(GET, "/boum")
      val boum = route(app, request).get
      status(boum) mustBe NOT_FOUND
    }

    "Widgets#delete" should {
      "remove widget" in {
        mockWidgetRepo.remove(any[BSONDocument])(any[ExecutionContext]) returns Future(lastRequestStatus)

        val result: Future[Result] = controller.delete(documentId).apply(FakeRequest())

        status(result) mustBe ACCEPTED
        there was one(mockWidgetRepo).remove(any[BSONDocument])(any[ExecutionContext])
      }
    }

    "Widgets#list" should {
      "list widgets" in {
        mockWidgetRepo.find()(any[ExecutionContext]) returns Future(posts)

        val result: Future[Result] = controller.index().apply(FakeRequest())

        contentAsJson(result) mustBe JsArray(posts)
        there was one(mockWidgetRepo).find()(any[ExecutionContext])
      }
    }

    "Widgets#read" should {
      "read widget" in {
        mockWidgetRepo.select(any[BSONDocument])(any[ExecutionContext]) returns Future(Option(widgetOne))

        val result: Future[Result] = controller.read(documentId).apply(FakeRequest())

        contentAsJson(result) mustBe widgetOne
        there was one(mockWidgetRepo).select(any[BSONDocument])(any[ExecutionContext])
      }
    }

    "Widgets#create" should {
      "create widget" in {
        mockWidgetRepo.save(any[BSONDocument])(any[ExecutionContext]) returns Future(lastRequestStatus)

        val request = FakeRequest().withBody(widgetOne)
        val result: Future[Result] = controller.create()(request)

        status(result) mustBe CREATED
        there was one(mockWidgetRepo).save(any[BSONDocument])(any[ExecutionContext])
      }
    }

    "Widgets#update" should {
      "update widget" in {
        mockWidgetRepo.update(any[BSONDocument], any[BSONDocument])(any[ExecutionContext]) returns Future(lastRequestStatus)

        val request = FakeRequest().withBody(widgetOne)
        val result: Future[Result] = controller.update(documentId)(request)

        status(result) mustBe ACCEPTED
        there was one(mockWidgetRepo).update(any[BSONDocument], any[BSONDocument])(any[ExecutionContext])
      }
    }
  }
}

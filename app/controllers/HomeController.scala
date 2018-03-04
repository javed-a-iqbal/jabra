package controllers

import java.util
import javax.inject._

import play.api._
import play.api.i18n.MessagesApi
import play.api.mvc._
import forms.PersonForms._
import models.Person
import play.api.libs.json.{JsError, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson._
import reactivemongo.api.collections.bson.BSONCollection



@Singleton
class HomeController @Inject() (ws: WSClient)(val reactiveMongoApi: ReactiveMongoApi) (implicit val messagesApi: MessagesApi) extends Controller with i18n.I18nSupport {

//  val driver1 = new reactivemongo.api.MongoDriver
 // def connection1 = driver1.connection(List("localhost:27017"))
 // def usersF = reactiveMongoApi.database.map(_.collection[JSONCollection]("movie"))

 // def findOlder1(collection: BSONCollection): Future[Option[BSONDocument]] = {
    // { "age": { "$gt": 27 } }
 //   val query = BSONDocument("age" -> BSONDocument("$gt" -> 27))

    // MongoDB .findOne
  //  collection.find(query).one[BSONDocument]
//  }

  def welcome = Action { implicit request =>
    Ok("welcome")
  }

  def index = Action { implicit request =>

    Ok(views.html.index(userform))
  }

  def submit = Action { implicit request =>

    userform.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(formWithErrors))
      },
      successSub => {

        Ok("Success "+ successSub.firstName +" " + successSub.lastName + Json.toJson(successSub))

      }
    )

  }


  case class Person(firstName: String, lastName: String, age:Int)

  object Person {
    implicit val formats = Json.format[Person]
  }

  def receiveData(): Action[AnyContent] = Action { implicit request =>
    println("############## Request Received : " + request)
    request.body.asJson.map { json =>
      json.validate[Person].map{
        case person: Person => println("############## Request Received  json : ")
          println("Hello " + person.firstName + " "+ person.lastName + ", you're "+person.age)
          Ok("Hello " + person.firstName + " "+ person.lastName + ", you're "+person.age)
      }.recoverTotal{
        e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }
}

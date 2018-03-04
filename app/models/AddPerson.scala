package models
import play.api.libs.json.Json

case class Person(firstName: String, lastName: String, age:Int)

object Person {
  implicit val formats = Json.format[Person]
}
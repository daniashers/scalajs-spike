package spatutorial.client

import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.scalajs.js
import scalajs.concurrent.JSExecutionContext.Implicits.queue

object AjaxClient {

  val CartographerLoginUrl = "https://demo.cartographer.io/api/v1/auth/login"

  case class Creds(email: String, password: String)

  def login(email: String, password: String): Future[String] = {
    val result = Ajax.post(
      url = CartographerLoginUrl,
      data = s"""{"email":"$email","password":"$password"}""",
      responseType = "json",
      headers = Map("Content-Type" -> "application/json")
    )
    result.map(_.response.asInstanceOf[js.Dynamic].value.token.toString)
  }

}

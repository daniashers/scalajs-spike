package spatutorial.client

import diode._
import diode.data._
import diode.react.ReactConnector
import diode.util._

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Await

// Actions
case class UpdateEmail(email: String) extends Action
case class UpdatePassword(password: String) extends Action
case class SubmitLogin(email: String, password: String) extends Action
case class SetToken(token: Option[String]) extends Action


// The base model of our application
case class MyModel(email: String, password: String, token: Pot[String])

class SubmitHandler[M](modelRW: ModelRW[M, Unit]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action @ SubmitLogin(email, password) =>
      import scala.concurrent.duration._
      val tokenFuture = AjaxClient.login(email, password)
      effectOnly(Effect(tokenFuture.map(token => SetToken(Some(token)))))
  }
}

class ChangeTokenHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case SetToken(tokenOption) =>
      updated(Pot.fromOption(tokenOption))
  }
}

class FormHandler[M](modelRW: ModelRW[M, MyModel]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case UpdateEmail(email) =>
      updated(modelRW.value.copy(email = email))
    case UpdatePassword(password) =>
      updated(modelRW.value.copy(password = password))
  }
}

// Application circuit
object MyCircuit extends Circuit[MyModel] with ReactConnector[MyModel] {
  // initial application model
  override protected def initialModel = MyModel("", "", Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new SubmitHandler(MyCircuit.zoomRW(_ => ())((m, v) => m)),
    new FormHandler(MyCircuit.zoomRW(m => m)((m, v) => v)),
    new ChangeTokenHandler(MyCircuit.zoomRW(_.token)((m, v) => m.copy(token = v)))
  )
}

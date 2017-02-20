package spatutorial.client

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport("SPAMain")
object MyMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object LoginLoc extends Loc
  case object LoggedInLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    val loginWrapper = MyCircuit.connect(m => m)
    val loggedInWrapper = MyCircuit.connect(_.token)
    // wrap/connect components to the circuit
    (staticRoute(root, LoginLoc) ~> renderR(ctl => loginWrapper(proxy => LoginComponent(LoginComponent.Props(ctl, proxy))))
      | staticRoute("#loggedin", LoggedInLoc) ~> renderR(ctl => loggedInWrapper(proxy => LoggedInComponent(LoggedInComponent.Props(ctl, proxy))))
      ).notFound(redirectToPage(LoginLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      <.div("CARTOGRAPHER LOGIN."),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {

    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}

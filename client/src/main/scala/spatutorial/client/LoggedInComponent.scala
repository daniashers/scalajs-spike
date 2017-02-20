package spatutorial.client

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.MyMain.{Loc, LoginLoc}


object LoggedInComponent {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[String]])

  // create the React component for holding the Message of the Day
  val LoggedInComponent = ReactComponentB[Props]("LoggedInComponent")
    .render_P { props =>
      <.div(
        <.div(
          "Logged in page.",
          // render messages depending on the state of the Pot
          props.proxy().renderEmpty(<.p("You are not logged in. Please visit the login page.")),
          props.proxy().renderPending(_ => <.p("Processing login, please wait...")),
          props.proxy().renderFailed(ex => <.p("Login failed. Check your credentials and try again.")),
          props.proxy().render(token => <.p("You have successfully logged in! Your token is " + token))
        ),
        <.div(
          props.router.link(LoginLoc)("Log out!"),
          ^.onClick --> props.proxy.dispatchCB(SetToken(None))
        )
      )
    }
    .build

  def apply(props: Props) = LoggedInComponent(props)
}

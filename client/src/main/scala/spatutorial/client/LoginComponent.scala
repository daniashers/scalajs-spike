package spatutorial.client

import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.MyMain.{Loc, LoggedInLoc}

object LoginComponent {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[MyModel])

  // create the React component for holding the Message of the Day
  val LoginComponent = ReactComponentB[Props]("LoginComponent")
    .render_P { props =>
      <.div(
        <.div(
          <.input.text(
            ^.value := props.proxy.value.email,
            ^.onChange ==> { e: ReactEventI =>
              props.proxy.dispatchCB(UpdateEmail(e.target.value))
            }
          ),
          " <- Email"
        ),
        <.div(
          <.input.text(
            ^.value := props.proxy.value.password,
            ^.onChange ==> { e: ReactEventI =>
              props.proxy.dispatchCB(UpdatePassword(e.target.value))
            }
          ),
          " <- Password"
        ),
        <.div(
          <.button(
            ^.onClick --> props.proxy.dispatchCB(SubmitLogin(props.proxy.value.email, props.proxy.value.password)),
            props.router.link(LoggedInLoc)("Login")
          )
        ),
        <.div(
          props.router.link(LoggedInLoc)("Think you're already logged in? Check here...")
        )
      )
    }
    .build

  def apply(props: Props) = LoginComponent(props)
}

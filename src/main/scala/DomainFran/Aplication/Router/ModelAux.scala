package Router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object ModelAux {

  case class AccountInfomationAux(
      nroCuenta : Long = 0,
      var saldo : Int = 0
  ) {  }

  object AccountInformationAuxJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val PortofolioFormats = jsonFormat2(AccountInfomationAux)
  }

}
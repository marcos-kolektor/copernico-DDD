package Router

import Model.Model.AccountInfomation
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object ModelAux {

  case class AccountInfomationAux(nroCuenta : Long = 0, saldo : Int = 0)
  case class ResponseToClient(message: String = "")

  object AccountInformationJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val PortofolioFormats = jsonFormat3(AccountInfomation)
    implicit val PortofolioFormats2 = jsonFormat2(AccountInfomationAux)
    implicit val PortofolioFormats3 = jsonFormat1(ResponseToClient)
  }

}

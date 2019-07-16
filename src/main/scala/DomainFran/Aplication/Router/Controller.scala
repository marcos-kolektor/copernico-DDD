package Router

import Aplication.Aplication.{accountInfomation, accountsActor}
import Model.Model._
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, post, put}
import akka.http.scaladsl.server.Route
import akka.actor.ActorRef

import Model.Model.AccountInfomation
import ModelAux.AccountInfomationAux
import ModelAux.AccountInformationJsonSupport._

object Controller {

  def _allAccounts(actorRef: ActorRef): Route =
    get {
      actorRef ! Show
      complete {
        "All accounts in console. :c"
      }
    }

  def _searchAccount(numberAccount: Long, actorRef: ActorRef): Route =
    get {
      actorRef ! ShowOne(numberAccount)
      complete {
        "Account " + numberAccount + " is on console. :c"
      }
    }

  def _createAccount(actorRef: ActorRef): Route =
    post {
      entity(as[AccountInfomation]) { information =>

        val newAccount: AccountInfomation = new AccountInfomation(
          nroCuenta = information.nroCuenta,
          saldo = information.saldo,
          state = information.state
        )

        actorRef ! Create(newAccount)

        Thread.sleep(1000)
        actorRef ! Show

        complete(
          s"Account: ${information.nroCuenta} " +
            s"- saldo: ${information.saldo}" +
            s"- status: ${information.state}")
      }
    }

  def _editAccount(actorRef: ActorRef): Route =
    put {
      entity(as[AccountInfomation]) { information =>

        val account: AccountInfomation = new AccountInfomation(
          nroCuenta = information.nroCuenta,
          saldo = information.saldo,
          state = information.state
        )

        actorRef ! Update(account)

        Thread.sleep(1000)
        actorRef ! Show

        complete(
          s"Account: ${information.nroCuenta} " +
            s"- saldo: ${information.saldo}" +
            s"- status: ${information.state}")
      }
    }

  def _accredit(actorRef: ActorRef): Route =
    put {
      entity(as[AccountInfomationAux]) { information =>

        actorRef ! Accredit(information.nroCuenta, information.saldo)

        complete(
          s"Account: ${information.nroCuenta} " +
            s"- saldo: ${information.saldo}")
      }
    }

  def _debit(actorRef: ActorRef): Route =
    put {
      entity(as[AccountInfomationAux]) { information =>

        actorRef ! Debit(information.nroCuenta, information.saldo)

        complete(
          s"Account: ${information.nroCuenta} " +
            s"- saldo: ${information.saldo}")
      }
    }

}
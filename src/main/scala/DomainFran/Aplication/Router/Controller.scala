package Router

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.http.scaladsl.server.Directives.{as, complete, entity, get, post, put}
import akka.http.scaladsl.server.Route
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import Model.Model.AccountInfomation
import Model.Model.AccountState
import Model.Model._
import ModelAux.ResponseToClient
import ModelAux.AccountInfomationAux
import ModelAux.AccountInformationJsonSupport._


import scala.concurrent.Await

object Controller {

  implicit val timeout = Timeout(5 seconds)

  def _allAccounts(actorRef: ActorRef): Route =
    get  {
      val future = actorRef ? Show
      val result = Await.result(future, timeout.duration).asInstanceOf[AccountState]
      complete(result.accounts)
    }

  def _searchAccount(numberAccount: Long, actorRef: ActorRef): Route =
    get  {
      val future = actorRef ? ShowOne(numberAccount)
      val result = Await.result(future, timeout.duration).asInstanceOf[AccountInfomation]
      complete(result)
    }

  def _createAccount(actorRef: ActorRef): Route =
    post {
      entity(as[AccountInfomation]) { information =>

        val future = actorRef ? ShowOne(information.nroCuenta)
        val result = Await.result(future, timeout.duration).asInstanceOf[AccountInfomation]

        if(result.nroCuenta == 0){

          val newAccount: AccountInfomation = new AccountInfomation(
            nroCuenta = information.nroCuenta,
            saldo = information.saldo,
            state = information.state
          )

          val future = actorRef ? Create(newAccount)
          val result = Await.result(future, timeout.duration).asInstanceOf[Boolean]

          if(!result){
            complete(ResponseToClient("The account was not created correctly"))
          } else {
            complete(ResponseToClient("Account created correctly"))
          }

        } else {
          complete(ResponseToClient("Account already exists"))
        }
      }
    }

  def _editAccount(actorRef: ActorRef): Route =
    put  {
      entity(as[AccountInfomation]) { information =>

          val future = actorRef ? Update(information)
          val result = Await.result(future, timeout.duration).asInstanceOf[Boolean]

          if(!result){
            complete(ResponseToClient("The account was not updated correctly"))
          } else {
            complete(ResponseToClient("Account updated correctly"))
          }

      }
    }

  def _accredit(actorRef: ActorRef): Route =
    put  {
      entity(as[AccountInfomationAux]) { information =>

        val future = actorRef ? Accredit(information.nroCuenta, information.saldo)
        val result = Await.result(future, timeout.duration).asInstanceOf[Int]

        if(result == 0){
          complete(ResponseToClient("The balance did not update correctly"))
        } else {
          complete(ResponseToClient(s"The balance was updated correctly. New balance of the account ${information.nroCuenta} is ${result}."))
        }
      }
    }

  def _debit(actorRef: ActorRef): Route =
    put  {
      entity(as[AccountInfomationAux]) { information =>

        val future = actorRef ? Debit(information.nroCuenta, information.saldo)
        val result = Await.result(future, timeout.duration).asInstanceOf[Int]

        if(result == 0){
          complete(ResponseToClient("The balance did not update correctly"))
        } else {
          complete(ResponseToClient(s"The balance was updated correctly. New balance of the account ${information.nroCuenta} is ${result}."))
        }
      }
    }

}
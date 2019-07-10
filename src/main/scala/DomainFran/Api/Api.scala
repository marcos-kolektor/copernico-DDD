package Api

import akka.actor.{ActorSystem, Props}
import Domain._
import Model.Model._
import akka.actor.ReceiveTimeout

import scala.concurrent.duration._

object Main extends App {

    // DATA
    val accountInfomation: AccountInfomation = new AccountInfomation(
        nroCuenta = 12345678910112L,
        saldo = 0,
        state = 'A'
    )
    val accountInfomation2: AccountInfomation = new AccountInfomation(
        nroCuenta = 12345678910111L,
        saldo = 100,
        state = 'D'
    )
    val newInfoAccount: AccountInfomation = new AccountInfomation(
        nroCuenta = 12345678910111L,
        saldo = 100,
        state = 'A'
    )

    // SYSTEM 
    val system = ActorSystem("AccountSystem")

    val accountsActor = system.actorOf(Props(new AccountActor(Array())), name = "accountsActor")

    accountsActor ! Create(accountInfomation2) 
    Thread.sleep(2000)
    accountsActor ! Show 
    accountsActor ! Update(newInfoAccount) 
    Thread.sleep(3000)
    accountsActor ! Show

    system.terminate
    
}
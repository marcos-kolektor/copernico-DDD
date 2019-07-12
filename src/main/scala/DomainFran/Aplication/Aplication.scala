package Aplication

import akka.actor.{ActorSystem, Props}
import Domain._
import Model.Model._

object Aplication extends App {

    // DATA
    val accountInfomation: AccountInfomation = new AccountInfomation(
        nroCuenta = 12345678910111L,
        saldo = 0,
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

    accountsActor ! Create(accountInfomation)
    Thread.sleep(2000)
    accountsActor ! Show 
    accountsActor ! Update(newInfoAccount) 
    Thread.sleep(3000)
    accountsActor ! Show
    accountsActor ! Accredit(12345678910111L, 250)
    Thread.sleep(3000)
    accountsActor ! Show
    accountsActor ! Debit(12345678910111L, 100)
    Thread.sleep(3000)
    accountsActor ! Show


    system.terminate
    
}
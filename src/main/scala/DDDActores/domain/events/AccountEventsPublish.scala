package DDDActores.domain.events

import DDDActores.domain.events.AccountEventsPublish.{ Subscribe, Unsubscribe }
import akka.actor.{ Actor, ActorLogging, ActorRef }
import akka.event.{ ActorEventBus, LookupClassification }

object AccountEventsPublish {

  case class Subscribe(classifier: String)

  case class Unsubscribe(classifier: String)

}

class AccountEventsPublish extends Actor
  with ActorLogging
  with ActorEventBus
  with LookupClassification {

  override type Event = DomainEvent
  override type Classifier = String

  override protected def mapSize(): Int = 10 // number of different event types

  override protected def classify(event: DomainEvent): String = event.classifier

  override protected def publish(event: DomainEvent, subscriber: ActorRef): Unit = {
    subscriber ! event
  }

  override def receive: Receive = {
    case domainEvent: DomainEvent =>
      log info s"publishing event from: $sender, origin: ${domainEvent.origin}"
      publish(domainEvent)

    case Subscribe(classifier) =>
      log info s"subscribing for $classifier from $sender"
      subscribe(sender, classifier)

    case Unsubscribe(classifier) =>
      log info s"unsubscribing for $classifier from $sender"
      unsubscribe(sender, classifier)
  }

}

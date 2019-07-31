package manager

import DDDActores.domain.model.AccountActor
import DDDActores.infrastructure.AccountPersistentActor
import akka.actor.ActorSystem
import akka.testkit.{ TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

class ManagerPersistemTest(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  import DDDActores.infrastructure.AccountPersistentActor._
  def this() = this(utils.ClusterArditiSystem.system)

  "A ManagerpersistemTest..." should {
    "demonstrate compensation in case of insufficient balance" in {
      val testProbe = TestProbe()
      val diana = system.actorOf(AccountActor.props(mount  = 20000, active = "A"), "Diana")
      //val arthur = system.actorOf(AccountActor.props(mount  = 5000, active = "B"), "Arthur")
      val tm = system.actorOf(AccountPersistentActor.props(), "tm2")
      diana.!("initial")(testProbe.ref)
      //tm ! AddMonetAccount(123456L, 20L, "A", 350000L)
      Thread.sleep(5000)
      diana.!("initial")(testProbe.ref)
    }
  }
}

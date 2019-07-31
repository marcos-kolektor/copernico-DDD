package DDDActores.domain.repository

import java.util.UUID

object AccountRepository {
  case class Send(idTransaction: String, named: String, account: String)
}

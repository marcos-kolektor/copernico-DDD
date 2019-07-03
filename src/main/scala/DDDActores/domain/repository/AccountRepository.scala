package DDDActores.domain.repository

import java.util.UUID

object AccountRepository {
  case class send(account: UUID, message: Any)
}

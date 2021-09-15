package kleisli

import kleisli.Entity.EntityBId

case class EntityA(entityBId: EntityBId) {
  def idOfB: EntityBId = entityBId
}
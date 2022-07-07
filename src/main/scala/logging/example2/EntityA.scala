package logging.example2

import Types.EntityBId

case class EntityA(entityBId: EntityBId):
  def idOfB: EntityBId = entityBId

/**
 * bloogi
 *
 * Copyright 2016 juanitodread
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package daos

import models.User
import reactivemongo.api.commands.WriteResult
import scala.concurrent.Future
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import org.slf4j.LoggerFactory

// Implicit conversions model
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.play.json.JsObjectDocumentWriter
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONObjectID
import util.Utils

/**
 * Users DAO
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
class UserDao(reactiveMongoApi: ReactiveMongoApi)
    extends Dao[User, WriteResult] {

  private final val logger = LoggerFactory.getLogger(classOf[UserDao])
  protected def users = reactiveMongoApi.db.collection[JSONCollection]("user")

  def find(): Future[List[User]] = {
    logger.info("Finding all users...")
    users.find(Json.obj()).cursor[User]().collect[List]()
  }

  def findById(id: String): Future[Option[User]] = {
    logger.info(s"Find user by id: $id")
    users.find(Json.obj("_id" -> id)).one[User]
  }

  def save(entity: User): Future[WriteResult] = {
    logger.info(s"Save a new user: $entity")
    val persistenUser = UserDao.persistentUser(entity)
    users.insert(persistenUser)
  }

  def update(id: String, entity: User): Future[WriteResult] = {
    logger.info(s"Update user with id: $id")
    users.update(Json.obj("_id" -> id), entity)
  }

  def delete(id: String): Future[WriteResult] = {
    logger.info(s"Delete user with id: $id")
    users.remove(Json.obj("_id" -> id))
  }
}

object UserDao {
  def apply(reactiveMongoApi: ReactiveMongoApi) = new UserDao(reactiveMongoApi)

  def persistentUser(user: User) = {
    val id = Utils.newMongoId
    User(
      Some(id.stringify),
      user.firstName,
      user.secondName,
      user.username,
      user.password,
      Some(Utils.now)
    )
  }
}
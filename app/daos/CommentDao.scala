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

import reactivemongo.api.commands.WriteResult
import models.Comment
import org.slf4j.LoggerFactory
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future

// Implicit conversions model
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.play.json.JsObjectDocumentWriter
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Utils

/**
 * Comments DAO
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
class CommentDao(reactiveMongoApi: ReactiveMongoApi) extends Dao[Comment, WriteResult] {

  private final val logger = LoggerFactory.getLogger(classOf[CommentDao])
  protected def posts = reactiveMongoApi.db.collection[JSONCollection]("comment")

  def find(): Future[List[Comment]] = {
    logger.info("Finding all comments...")
    posts.find(Json.obj()).cursor[Comment]().collect[List]()
  }

  def findById(id: String): Future[Option[Comment]] = {
    logger.info(s"Find comment by id: $id")
    posts.find(Json.obj("_id" -> id)).one[Comment]
  }

  def save(entity: Comment): Future[WriteResult] = {
    logger.info(s"Save a new comment: $entity")
    val persistentComment = CommentDao.persistentComment(entity)
    posts.insert(persistentComment)
  }

  def update(id: String, entity: Comment): Future[WriteResult] = {
    logger.info(s"Update comment with id: $id")
    posts.update(Json.obj("_id" -> id), entity)
  }

  def delete(id: String): Future[WriteResult] = {
    logger.info(s"Delete comment with id: $id")
    posts.remove(Json.obj("_id" -> id))
  }
}

object CommentDao {
  def apply(reactiveMongoApi: ReactiveMongoApi) = new CommentDao(reactiveMongoApi)

  def persistentComment(comment: Comment) = {
    val id = Utils.newMongoId
    Comment(
      Some(id.stringify),
      comment.content,
      Some(Utils.now),
      comment.author
    )
  }
}
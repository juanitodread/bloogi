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

import play.modules.reactivemongo.ReactiveMongoApi
import models.Post
import reactivemongo.api.commands.WriteResult
import org.slf4j.LoggerFactory
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import play.api.libs.json.Json
import util.Utils

// Implicit conversions model
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.play.json.JsObjectDocumentWriter
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Posts DAO
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
class PostDao(reactiveMongoApi: ReactiveMongoApi)
    extends Dao[Post, WriteResult] {

  private final val logger = LoggerFactory.getLogger(classOf[PostDao])
  protected def posts = reactiveMongoApi.db.collection[JSONCollection]("post")

  def find(): Future[List[Post]] = {
    logger.info("Finding all posts...")
    posts.find(Json.obj()).cursor[Post]().collect[List]()
  }

  def findById(id: String): Future[Option[Post]] = {
    logger.info(s"Find post by id: $id")
    posts.find(Json.obj("_id" -> id)).one[Post]
  }

  def save(entity: Post): Future[WriteResult] = {
    logger.info(s"Save a new post: $entity")
    val persistentPost = PostDao.persistentPost(entity)
    posts.insert(persistentPost)
  }

  def update(id: String, entity: Post): Future[WriteResult] = {
    logger.info(s"Update post with id: $id")
    posts.update(Json.obj("_id" -> id), entity)
  }

  def delete(id: String): Future[WriteResult] = {
    logger.info(s"Delete post with id: $id")
    posts.remove(Json.obj("_id" -> id))
  }
}

object PostDao {
  def apply(reactiveMongoApi: ReactiveMongoApi) = new PostDao(reactiveMongoApi)

  def persistentPost(post: Post) = {
    val id = Utils.newMongoId
    val now = Utils.now
    val url = Utils.generateUrlTitlePost(post.title)
    Post(
      Some(id.stringify),
      post.title,
      Some(url),
      post.content,
      Some(now),
      Some(now),
      post.author,
      post.tags,
      post.comments
    )
  }

}
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
package controllers

import play.api.mvc.Controller
import org.slf4j.LoggerFactory
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoComponents
import daos.PostDao
import play.api.mvc.Action
import play.api.libs.json.Json
import models.Post
import util.Utils

// Implicit conversions model
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Posts controller
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
class PostController @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger = LoggerFactory.getLogger(classOf[PostController])
  val postDao = PostDao(reactiveMongoApi)

  def getAll() = Action.async { request =>
    postDao.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def getById(id: String) = Action.async { request =>
    postDao.findById(id).map { post =>
      post match {
        case Some(post) => Ok(Json.toJson(post))
        case None => NotFound
      }
    }
  }

  def save() = Action.async(parse.json) { request =>
    request.body.validate[Post].map { post =>
      postDao.save(post).map { lastError =>
        Created
      }
    }.getOrElse(Utils.invalidJsonMessage())
  }

  def update(id: String) = Action.async(parse.json) { request =>
    request.body.validate[Post].map { post =>
      postDao.update(id, post).map { lastError =>
        Ok
      }
    }.getOrElse(Utils.invalidJsonMessage())
  }

  def delete(id: String) = Action.async { request =>
    postDao.delete(id).map { lastError =>
      Ok
    }
  }

}
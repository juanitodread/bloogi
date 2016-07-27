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

import javax.inject.Inject

import org.slf4j.LoggerFactory

import play.api.mvc.{
  Controller,
  Action
}
import play.modules.reactivemongo.{
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import daos.CommentDao
import models.Comment
import util.Utils
import play.api.libs.json.Json

// Implicit conversions model
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Comments controller
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
class CommentController @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger = LoggerFactory.getLogger(classOf[CommentController])
  val commentDao = CommentDao(reactiveMongoApi)

  def getAll() = Action.async { request =>
    commentDao.find.map { comments =>
      Ok(Json.toJson(comments))
    }
  }

  def getById(id: String) = Action.async { request =>
    commentDao.findById(id).map { comment =>
      comment match {
        case Some(comment) => Ok(Json.toJson(comment))
        case None => NotFound
      }
    }
  }

  def save() = Action.async(parse.json) { request =>
    request.body.validate[Comment].map { comment =>
      commentDao.save(comment).map { lastError =>
        Created
      }
    }.getOrElse(Utils.invalidJsonMessage())
  }

  def update(id: String) = Action.async(parse.json) { request =>
    request.body.validate[Comment].map { comment =>
      commentDao.update(id, comment).map { lastError =>
        Ok
      }
    }.getOrElse(Utils.invalidJsonMessage())
  }

  def delete(id: String) = Action.async { reques =>
    commentDao.delete(id).map { lastError =>
      Ok
    }
  }
}
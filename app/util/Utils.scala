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
package util

import scala.concurrent.Future
import play.api.mvc.Results._
import play.api.libs.json.{ JsObject, Json }
import reactivemongo.bson.BSONObjectID

/**
 * Utility class related to common functionality.
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
object Utils {

  def now = System.currentTimeMillis

  def newMongoId = BSONObjectID.generate

  def invalidJsonMessage(message: JsObject = Json.obj("reason" -> "Invalid JSON message")) = {
    Future.successful(BadRequest(message))
  }

  def generateUrlTitlePost(title: String) = {
    val trimTitle = title.trim
    val sbTitle = new StringBuilder()
    var i = 0
    while (i < trimTitle.length) {
      val chr = trimTitle.charAt(i)
      if (chr == ' ') {
        sbTitle.append('-')
        i += 1
        while (trimTitle.charAt(i) == ' ') {
          i += 1
        }
        i -= 1
      } else {
        sbTitle.append(chr.toLower)
      }
      i += 1
    }
    sbTitle.toString
  }

}
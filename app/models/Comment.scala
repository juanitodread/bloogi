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
package models

import play.api.libs.json.Json

/**
 * Comment case class
 *
 * @author juanitodread
 * @version 1.0.0
 *
 */
case class Comment(
  _id: String,
  content: String,
  created: Option[Long],
  author: SimpleUser,
  upVotes: Array[String],
  downVotes: Array[String]
)

object Comment {
  implicit val commentFormater = Json.format[Comment]
}
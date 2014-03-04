/**
 * Copyright (C) 2014 Johannes Unterstein, unterstein@me.com, unterstein.info
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package filesystem

import java.io.File
import global.Global

object FileSystem {

  def get(): FileSystem = {
    if ("LocalFileSystem" == Global.getFileSystemImplementation()) {
      new LocalFileSystem
    } else {
      // TODO S3 implementation
      null
    }
  }
}

abstract class FileSystem {

  def load(token: String): File

  def store(token: String, file: File): Boolean
}

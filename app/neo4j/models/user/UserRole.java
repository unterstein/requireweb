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
package neo4j.models.user;

import helper.ViewEnumModel;

import java.io.Serializable;

public enum UserRole implements Serializable {
  ADMIN(40), //
  USER(20), //
  ;

  private int roleNumber;

  private UserRole(int roleNumber) {
    this.roleNumber = roleNumber;
  }

  public static ViewEnumModel enumModel() {
    ViewEnumModel result = new ViewEnumModel();
    for (UserRole value : values()) {
      result.keys.add(value.name());
      result.values.put(value.name(), value.name());
    }
    return result;
  }

  public int getRoleNumber() {
    return roleNumber;
  }
}

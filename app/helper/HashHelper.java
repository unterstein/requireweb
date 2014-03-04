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
package helper;

import play.Logger;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashHelper implements Serializable {

  private static final HashHelper INSTANCE = new HashHelper();

  private static SecureRandom RANDOM = new SecureRandom();

  private MessageDigest digest;

  private HashHelper() {
    try {
      digest = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      Logger.error("unable to create sha digest", e);
    }
  }

  public static final String createId(int length) {
    return new BigInteger(length * 3, RANDOM).toString(32).substring(3, 3 + length);
  }

  public static final String createId() {
    return createId(64);
  }

  public static HashHelper getInstance() {
    return INSTANCE;
  }

  public String sha(String in) {
    String hash = new String(digest.digest(in.getBytes()));
    return new BigInteger(1, hash.getBytes()).toString(16);
  }
}

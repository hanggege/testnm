/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mei.orc.util.encrypt;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DigestUtils {

    private static byte[] getBytesUtf8(String data) {
        return data.getBytes(Charset.forName("UTF-8"));
    }

    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }


    public static byte[] md5(byte[] data) {
        return getMd5Digest().digest(data);
    }


    public static byte[] md5(String data) {
        return md5(getBytesUtf8(data));
    }


    public static String md5Hex(String data) {
        return Hex.encodeHexString(md5(data));
    }


}

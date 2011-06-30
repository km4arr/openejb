/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openejb.client;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class AuthenticationRequest implements Request {

    private transient String realm;
    private transient String username;
    private transient String credentials;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String principal, String credentials) {
        this(null, principal, credentials);
    }

    public AuthenticationRequest(String realm, String principal, String credentials) {
        this.realm = realm;
        this.username = principal;
        this.credentials = credentials;
    }

    public byte getRequestType() {
        return RequestMethodConstants.AUTH_REQUEST;
    }

    public String getRealm() {
        return realm;
    }

    public String getUsername() {
        return username;
    }

    public String getCredentials() {
        return credentials;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte(); // future use

        realm = (String) in.readObject();
        username = (String) in.readObject();
        credentials = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // write out the version of the serialized data for future use
        out.writeByte(1);

        out.writeObject(realm);
        out.writeObject(username);
        out.writeObject(credentials);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append(realm).append(':');
        sb.append(username);
        return sb.toString();
    }
}


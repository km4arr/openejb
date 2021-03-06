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

package org.apache.openejb.jee.sun;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resourceAdapterMid",
    "activationConfig"
})
@XmlRootElement(name = "mdb-resource-adapter")
public class MdbResourceAdapter {

    @XmlElement(name = "resource-adapter-mid", required = true)
    protected String resourceAdapterMid;
    @XmlElement(name = "activation-config")
    protected ActivationConfig activationConfig;

    /**
     * Gets the value of the resourceAdapterMid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceAdapterMid() {
        return resourceAdapterMid;
    }

    /**
     * Sets the value of the resourceAdapterMid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceAdapterMid(String value) {
        this.resourceAdapterMid = value;
    }

    /**
     * Gets the value of the activationConfig property.
     * 
     * @return
     *     possible object is
     *     {@link ActivationConfig }
     *     
     */
    public ActivationConfig getActivationConfig() {
        return activationConfig;
    }

    /**
     * Sets the value of the activationConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivationConfig }
     *     
     */
    public void setActivationConfig(ActivationConfig value) {
        this.activationConfig = value;
    }

}

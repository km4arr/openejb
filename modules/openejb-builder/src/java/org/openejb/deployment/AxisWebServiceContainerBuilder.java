/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "OpenEJB" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of The OpenEJB Group.  For written permission,
 *    please contact openejb@openejb.org.
 *
 * 4. Products derived from this Software may not be called "OpenEJB"
 *    nor may "OpenEJB" appear in their names without prior written
 *    permission of The OpenEJB Group. OpenEJB is a registered
 *    trademark of The OpenEJB Group.
 *
 * 5. Due credit should be given to the OpenEJB Project
 *    (http://openejb.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE OPENEJB GROUP AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE OPENEJB GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001 (C) The OpenEJB Group. All Rights Reserved.
 *
 * $Id$
 */
package org.openejb.deployment;

import java.util.jar.JarFile;
import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.gbean.GBeanData;
import org.apache.geronimo.j2ee.deployment.EARContext;
import org.apache.geronimo.j2ee.deployment.EJBModule;
import org.apache.geronimo.security.deploy.Security;
import org.apache.geronimo.xbeans.j2ee.SessionBeanType;
import org.apache.geronimo.axis.builder.AxisServiceBuilder;
import org.apache.axis.description.JavaServiceDesc;
import org.openejb.server.axis.WSContainerGBean;
import org.openejb.xbeans.ejbjar.OpenejbSessionBeanType;

public class AxisWebServiceContainerBuilder {


    private static final Log log = LogFactory.getLog(AxisWebServiceContainerBuilder.class);

    /*
     * The ultimate goal of this method is to create an XFireService GBean that wraps the EJBContainer with
     * the corresponding sessionObjectname and is capable of being indexed by its WSDL address location.
     */
    public void addGbean(EARContext earContext, EJBModule ejbModule, ClassLoader cl, ObjectName sessionObjectName, ObjectName listener, SessionBeanType sessionBean, OpenejbSessionBeanType openejbSessionBean, TransactionPolicyHelper transactionPolicyHelper, Security security) throws DeploymentException {

        boolean isStateless = "Stateless".equals(sessionBean.getSessionType().getStringValue());
        String serviceEndpointName = OpenEJBModuleBuilder.getJ2eeStringValue(sessionBean.getServiceEndpoint());
        String ejbName = sessionBean.getEjbName().getStringValue().trim();

        if (!isStateless || serviceEndpointName == null) {
            return;
        }

        serviceEndpointName = serviceEndpointName.trim();

        GBeanData gBean = buildGBeanData(sessionObjectName, listener, ejbName, serviceEndpointName, ejbModule.getModuleFile(), cl);

        earContext.addGBean(gBean);
    }

    public GBeanData buildGBeanData(ObjectName sessionObjectName, ObjectName listener, String ejbName, String serviceEndpointName, JarFile jarFile, ClassLoader cl) throws DeploymentException {
        JavaServiceDesc ejbServiceDesc = AxisServiceBuilder.createEJBServiceDesc(jarFile, ejbName, cl);

        URL wsdlURL = null;
        try {
            wsdlURL = new URL(ejbServiceDesc.getWSDLFile());
        } catch (MalformedURLException e) {
            throw new DeploymentException("Invalid WSDL URL: "+ ejbServiceDesc.getWSDLFile(), e);
        }
        URI location = null;
        try {
            location = new URI(ejbServiceDesc.getEndpointURL());
        } catch (URISyntaxException e) {
            throw new DeploymentException("Invalid address location URI: "+ejbServiceDesc.getEndpointURL(), e);
        }

        GBeanData gBean = WSContainerGBean.createGBean(ejbName,sessionObjectName,listener, location, wsdlURL, ejbServiceDesc);
        return gBean;
    }
}

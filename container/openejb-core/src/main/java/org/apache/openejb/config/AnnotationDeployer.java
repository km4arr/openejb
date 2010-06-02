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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.openejb.config;

import org.apache.xbean.finder.AbstractFinder;
import org.apache.xbean.finder.ClassFinder;

import java.io.File;
import java.net.URL;

import static java.util.Arrays.asList;

/**
 * @version $Rev$ $Date$
 */
public class AnnotationDeployer extends AbstractAnnotationDeployer {

    public AnnotationDeployer() {
        super(new DiscoverAnnotatedBeans());
    }

    public static class DiscoverAnnotatedBeans extends AbstractDiscoverAnnotatedBeans {

        @Override
        protected AbstractFinder newFinder(ClientModule clientModule) throws Exception {
            AbstractFinder finder;
            if (clientModule.getJarLocation() != null) {
                String location = clientModule.getJarLocation();
                File file = new File(location);

                URL url;
                if (file.exists()) {
                    url = file.toURI().toURL();
                } else {
                    url = new URL(location);
                }
                finder = new ClassFinder(clientModule.getClassLoader(), url);
            } else {
                finder = new ClassFinder(clientModule.getClassLoader());
            }
            return finder;
        }

        @Override
        protected AbstractFinder newFinder(WebModule webModule) {
            AbstractFinder finder;
            File file = new File(webModule.getJarLocation());
            URL[] urls = DeploymentLoader.getWebappUrls(file);
            final ClassLoader webClassLoader = webModule.getClassLoader();
            finder = new ClassFinder(webClassLoader, asList(urls));
            return finder;
        }

        @Override
        protected AbstractFinder newFinder(EjbModule ejbModule) throws Exception {
            AbstractFinder finder;
            if (ejbModule.getJarLocation() != null) {
                String location = ejbModule.getJarLocation();
                File file = new File(location);

                URL url;
                if (file.exists()) {
                    url = file.toURI().toURL();
                } else {
                    url = new URL(location);
                }
                finder = new ClassFinder(ejbModule.getClassLoader(), url);
            } else {
                finder = new ClassFinder(ejbModule.getClassLoader());
            }
            return finder;
        }
    }

}

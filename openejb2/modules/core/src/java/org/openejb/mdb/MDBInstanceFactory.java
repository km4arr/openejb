/* ====================================================================
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce this list of
 *    conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name "OpenEJB" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of The OpenEJB Group.  For written permission,
 *    please contact openejb-group@openejb.sf.net.
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
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenEJB Project.  For more information
 * please see <http://openejb.org/>.
 *
 * ====================================================================
 */
package org.openejb.mdb;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import javax.ejb.MessageDrivenBean;

import net.sf.cglib.reflect.FastClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.naming.java.ReadOnlyContext;
import org.apache.geronimo.naming.java.RootContext;
import org.openejb.EJBOperation;
import org.openejb.cache.InstanceFactory;

/**
 * @version $Revision$ $Date$
 */
public class MDBInstanceFactory implements InstanceFactory, Serializable {
    private static final Log log = LogFactory.getLog(MDBInstanceFactory.class);

    private final ReadOnlyContext componentContext;
    private final MDBInstanceContextFactory factory;
    private final Class beanClass;

    private transient final int createIndex;
    private transient final FastClass implClass;

    public MDBInstanceFactory(ReadOnlyContext componentContext, MDBInstanceContextFactory factory, Class beanClass) {
        this.componentContext = componentContext;
        this.factory = factory;
        this.beanClass = beanClass;

        implClass = FastClass.create(beanClass);
        createIndex = implClass.getIndex("ejbCreate", new Class[0]);
    }

    public Object createInstance() throws Exception {
        ReadOnlyContext oldContext = RootContext.getComponentContext();

        try {
            // Disassociate from JNDI Component Context whilst creating instance
            RootContext.setComponentContext(null);

            // create the StatelessInstanceContext which contains the instance
            MDBInstanceContext ctx = (MDBInstanceContext) factory.newInstance();

            try {
                ctx.setContext();
            } catch (Throwable t) {
                //TODO check this error handling
                if (t instanceof Exception) {
                    throw (Exception) t;
                } else {
                    throw (Error) t;
                }
            }
            MessageDrivenBean instance = (MessageDrivenBean) ctx.getInstance();
            assert(instance != null);

            // Activate this components JNDI Component Context
            RootContext.setComponentContext(componentContext);

            //TODO make ejbcreate go through the stack also!
            ctx.setOperation(EJBOperation.EJBCREATE);
            try {
                implClass.invoke(createIndex, instance, null);
            } catch (InvocationTargetException ite) {
                Throwable t = ite.getTargetException();
                if (t instanceof Exception) {
                    throw (Exception) t;
                } else {
                    throw (Error) t;
                }
            } finally {
                ctx.setOperation(EJBOperation.INACTIVE);
            }

            return ctx;
        } finally {
            RootContext.setComponentContext(oldContext);
        }
    }

    public void destroyInstance(Object instance) {
        MDBInstanceContext ctx = (MDBInstanceContext) instance;
        MessageDrivenBean beanInstance = (MessageDrivenBean) ctx.getInstance();

        ctx.setOperation(EJBOperation.EJBREMOVE);

        // Activate this components JNDI Component Context
        ReadOnlyContext oldContext = RootContext.getComponentContext();
        RootContext.setComponentContext(componentContext);
        try {
            beanInstance.ejbRemove();
        } catch (Throwable t) {
            // We're destroying this instance, so just log and continue
            log.warn("Unexpected error removing Message Driven instance", t);
        } finally {
            ctx.setOperation(EJBOperation.INACTIVE);
            RootContext.setComponentContext(oldContext);
        }
        //TODO shouldn't we call setMessageDrivenContext(null);
    }

    private Object readResolve() {
        return new MDBInstanceFactory(componentContext, factory, beanClass);
    }
}

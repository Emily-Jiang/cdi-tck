/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.cdi.tck.interceptors.tests.contract.interceptorLifeCycle;

import static org.testng.Assert.assertTrue;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

@SpecVersion(spec = "int", version = "1.2")
public class InterceptorLifeCycleTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(InterceptorLifeCycleTest.class).build();
    }

    @Test
    @SpecAssertion(section = "2.2", id = "e")
    public void testInterceptorMethodsCalledAfterDependencyInjection() {
        createCallAndDestroyBazInstance();
        // assertions checking dependency injection are made in interceptors
        assertTrue(AroundInvokeInterceptor.called);
        assertTrue(PostConstructInterceptor.called);
        assertTrue(PreDestroyInterceptor.called);
    }

    private void createCallAndDestroyBazInstance() {
        Class<Baz> clazz = Baz.class;
        Bean<Baz> bean = getUniqueBean(clazz);
        CreationalContext<Baz> ctx = getCurrentManager().createCreationalContext(bean);
        Baz instance = (Baz) getCurrentManager().getReference(bean, clazz, ctx);
        instance.doSomething();
        bean.destroy(instance, ctx);
    }
}

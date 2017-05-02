/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.hibernate.support;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Method;

/**
 * Extension of {@link TransactionAspectSupport} thas accepts a callback function to be executed
 * in a fixed transaction.  If an outer transaction is already present, it should be reused.
 * If no transaction active yet, a new one will be created with the default attributes.
 * <p/>
 * Unless a different {@link org.springframework.transaction.interceptor.TransactionAttributeSource} is configured
 * the default transaction only has the PROPAGATION_REQUIRED attribute set.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TransactionWrapper extends TransactionAspectSupport
{
	public TransactionWrapper() {
		setTransactionAttributeSource( ( method, targetClass ) -> {
			DefaultTransactionAttribute ta = new DefaultTransactionAttribute();
			ta.setPropagationBehavior( TransactionDefinition.PROPAGATION_REQUIRED );

			return ta;
		} );
	}

	public interface InvocationCallback<T>
	{
		T invoke() throws Throwable;
	}

	@SuppressWarnings("unchecked")
	public <R> R invokeWithinTransaction( InvocationCallback<R> callback ) throws Throwable {
		return (R) super.invokeWithinTransaction( null, null, callback::invoke );
	}

	@Override
	protected String methodIdentification( Method method, Class<?> targetClass ) {
		return getTransactionManagerBeanName();
	}
}

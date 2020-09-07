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

package com.foreach.across.modules.hibernate.util;

import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.BeanCreationStrategy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class DomainTypesBeanCreationStrategy implements BeanCreationStrategy
{
	@Override
	public boolean isApplicable( BeanCreationDirective directive ) {
		return Arrays.stream( directive.getActualClass().getDeclaredMethods() ).anyMatch( m -> "of".equals( m.getName() ) );
	}

	@Override
	public Object create( BeanCreationDirective directive ) {
		Constructor<?> constructor = directive.getActualClass().getConstructors()[0];
		try {
			Field declaredField = directive.getSrcClass().getDeclaredFields()[0];
			ReflectionUtils.makeAccessible( declaredField );
			Object o = ReflectionUtils.getField( declaredField, directive.getSrcObject() );
			return constructor.newInstance( o );
		}
		catch ( InstantiationException | IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace();
		}
		return null;
	}
}

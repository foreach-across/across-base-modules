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
package com.foreach.across.modules.entity.views.elements;

import org.springframework.beans.BeanUtils;

/**
 * Simple implementation of a {@link ViewElementBuilderFactory}
 * that takes an instance of a {@link ViewElementBuilder} as a template,
 * and when creating a builder will create a new instance and copy all bean properties from the template.
 * <p/>
 * This assumes that the builder class has a parameterless constructor available.
 *
 * @author Arne Vandamme
 */
public class CloningViewElementBuilderFactory<T extends ViewElementBuilder>
		implements ViewElementBuilderFactory<T>
{
	private final Class<T> builderClass;

	private T builderTemplate;

	public CloningViewElementBuilderFactory( Class<T> builderClass ) {
		this.builderClass = builderClass;
	}

	public T getBuilderTemplate() {
		return builderTemplate;
	}

	public void setBuilderTemplate( T builderTemplate ) {
		this.builderTemplate = builderTemplate;
	}

	@Override
	public T createBuilder() {
		T duplicate = newInstance();
		if ( builderTemplate != null ) {
			BeanUtils.copyProperties( builderTemplate, duplicate );
		}

		return duplicate;
	}

	private T newInstance() {
		try {
			return builderClass.newInstance();
		}
		catch ( IllegalAccessException | InstantiationException iae ) {
			throw new RuntimeException(
					getClass().getSimpleName() + " requires the template to have a parameterless constructor", iae
			);
		}
	}
}

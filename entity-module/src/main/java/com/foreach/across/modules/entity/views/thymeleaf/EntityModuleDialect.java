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
package com.foreach.across.modules.entity.views.thymeleaf;

import com.foreach.across.modules.entity.views.elements.ViewElementsHelper;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author niels
 * @since 6/02/2015
 */
public class EntityModuleDialect extends AbstractDialect implements IExpressionEnhancingDialect
{
	public static final String UTILITY_ELEMENTS = "elements";

    public enum AttributeNames {
        ATTRIBUTES("attributes"),
        DEPENDENCIES("dependencies");

		private String name;

		private AttributeNames( String name ) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Override
	public String getPrefix() {
		return "em";
	}

	@Override
	public Map<String, Object> getAdditionalExpressionObjects( IProcessingContext processingContext ) {
		Map<String, Object> objects = new HashMap<>();
		objects.put( UTILITY_ELEMENTS, new ViewElementsHelper() );

		return objects;
	}

	@Override
	public Set<IProcessor> getProcessors() {
		Set<IProcessor> processors = new HashSet<>();
		processors.add( new MultipleAttributeProcessor() );
        processors.add(new DependencyProcessor());
		return processors;
	}
}

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
package com.foreach.across.modules.entity.views.helpers;

import com.foreach.across.modules.entity.business.EntityPropertyDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Gets the value of an expression (usually property) for an object.
 *
 * @author Arne Vandamme
 */
@Deprecated
public class SpelObjectValueFetcher
{
	private final ExpressionParser expressionParser;

	private EvaluationContext evaluationContext;

	public SpelObjectValueFetcher() {
		this.expressionParser = new SpelExpressionParser();
	}

	public void setEntity( Object entity ) {
		evaluationContext = new StandardEvaluationContext( entity );
	}

	public Object getValue( EntityPropertyDescriptor propertyDescriptor ) {
		return getValue( propertyDescriptor.getName() );
	}

	public Object getValue( String expression ) {
		return expressionParser.parseExpression( expression ).getValue( evaluationContext, Object.class );
	}
}

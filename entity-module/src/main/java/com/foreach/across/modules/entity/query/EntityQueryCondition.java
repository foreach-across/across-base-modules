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
package com.foreach.across.modules.entity.query;

import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @author Arne Vandamme
 */
public class EntityQueryCondition implements EntityQueryExpression
{
	private String property;
	private EntityQueryOps operand;
	private Object[] arguments = new Object[0];

	public EntityQueryCondition( String property, EntityQueryOps operand, Object... arguments ) {
		this.property = property;
		this.operand = operand;
		this.arguments = arguments;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty( String property ) {
		this.property = property;
	}

	public EntityQueryOps getOperand() {
		return operand;
	}

	public void setOperand( EntityQueryOps operand ) {
		Assert.notNull( operand );
		this.operand = operand;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments( Object[] arguments ) {
		Assert.notNull( arguments );
		this.arguments = arguments;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		EntityQueryCondition that = (EntityQueryCondition) o;

		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if ( !Arrays.equals( arguments, that.arguments ) ) {
			return false;
		}
		if ( operand != that.operand ) {
			return false;
		}
		if ( property != null ? !property.equals( that.property ) : that.property != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = property != null ? property.hashCode() : 0;
		result = 31 * result + ( operand != null ? operand.hashCode() : 0 );
		result = 31 * result + ( arguments != null ? Arrays.hashCode( arguments ) : 0 );
		return result;
	}

	@Override
	public String toString() {
		return operand.toString( property, arguments );
	}

	public Object getFirstArgument() {
		return arguments.length > 0 ? arguments[0] : null;
	}
}

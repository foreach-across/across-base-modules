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
package com.foreach.across.modules.entity.views.elements.form.select;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

/**
 * Represents a single option in the select.
 */
public class SelectOption implements Comparable<SelectOption>
{
	private String label, value;
	private Object rawValue;

	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public Object getRawValue() {
		return rawValue;
	}

	public void setRawValue( Object rawValue ) {
		this.rawValue = rawValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue( String value ) {
		this.value = value;
	}

	public boolean isSelected( Object value ) {
		if ( value instanceof Collection ) {
			return ((Collection) value).contains( getRawValue() );
		}

		return value != null && value.equals( getRawValue() );
	}

	@Override
	public int compareTo( SelectOption other ) {
		return ObjectUtils.compare( getLabel(), other.getLabel() );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof SelectOption ) ) {
			return false;
		}

		SelectOption option = (SelectOption) o;

		if ( label != null ? !label.equals( option.label ) : option.label != null ) {
			return false;
		}
		if ( rawValue != null ? !rawValue.equals( option.rawValue ) : option.rawValue != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = label != null ? label.hashCode() : 0;
		result = 31 * result + ( rawValue != null ? rawValue.hashCode() : 0 );
		return result;
	}
}

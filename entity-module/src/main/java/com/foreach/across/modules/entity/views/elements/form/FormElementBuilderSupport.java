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
package com.foreach.across.modules.entity.views.elements.form;

import com.foreach.across.modules.entity.views.elements.ViewElementBuilderSupport;
import com.foreach.across.modules.entity.views.elements.form.dependencies.Qualifiers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public abstract class FormElementBuilderSupport<T extends FormElementSupport> extends ViewElementBuilderSupport<T>
{
	private boolean required;

    private Map<String, Qualifiers> dependencies = new HashMap<>();

	protected FormElementBuilderSupport( Class<T> builderClass ) {
		super( builderClass );
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired( boolean required ) {
		this.required = required;
	}

    public Map<String, Qualifiers> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, Qualifiers> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		if ( !super.equals( o ) ) {
			return false;
		}

		FormElementBuilderSupport that = (FormElementBuilderSupport) o;

		if ( required != that.required ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ( required ? 1 : 0 );
		return result;
	}
}

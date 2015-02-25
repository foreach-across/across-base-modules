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

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.util.EntityUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Generates form options for an enum.
 *
 * @author Arne Vandamme
 */
public class EnumSelectOptionGenerator implements SelectOptionGenerator
{
	private Class<? extends Enum> enumType;
	private boolean shouldBeSorted = false;

	public EnumSelectOptionGenerator( Class<? extends Enum> enumType ) {
		this.enumType = enumType;
	}

	public boolean isShouldBeSorted() {
		return shouldBeSorted;
	}

	public void setShouldBeSorted( boolean shouldBeSorted ) {
		this.shouldBeSorted = shouldBeSorted;
	}

	@Override
	public Collection<SelectOption> generateOptions( EntityMessageCodeResolver codeResolver ) {
		Enum[] enumValues = enumType.getEnumConstants();
		List<SelectOption> options = new ArrayList<>( enumValues.length );

		for ( Enum enumValue : enumValues ) {
			SelectOption option = new SelectOption();

			String messageCode = "enums." + enumType.getSimpleName() + "." + enumValue.name();
			String defaultLabel = EntityUtils.generateDisplayName( enumValue.name() );

			option.setLabel( codeResolver.getMessageWithFallback( messageCode, defaultLabel ) );
			option.setRawValue( enumValue );
			option.setValue( enumValue.name() );

			options.add( option );
		}

		if ( shouldBeSorted ) {
			Collections.sort( options );
		}

		return options;
	}
}

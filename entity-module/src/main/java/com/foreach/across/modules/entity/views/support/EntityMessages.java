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
package com.foreach.across.modules.entity.views.support;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.ObjectUtils;

/**
 * Base class for resolving common entity messages.
 *
 * @author Arne Vandamme
 */
public class EntityMessages
{
	public static final String ACTION_CREATE = "actions.create";
	public static final String ACTION_UPDATE = "actions.update";
	public static final String ACTION_DELETE = "actions.delete";
	public static final String ACTION_VIEW = "actions.view";

	public static final String PAGE_TITLE_CREATE = "pageTitle.create";
	public static final String PAGE_TITLE_UPDATE = "pageTitle.update";
	public static final String PAGE_TITLE_DELETE = "pageTitle.delete";
	public static final String PAGE_TITLE_VIEW = "pageTitle.view";

	protected final EntityMessageCodeResolver messageCodeResolver;

	public EntityMessages( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	public String createAction( Object... arguments ) {
		return withNameSingular( ACTION_CREATE, arguments );
	}

	public String updateAction( Object... arguments ) {
		return withNameSingular( ACTION_UPDATE, arguments );
	}

	public String deleteAction( Object... arguments ) {
		return withNameSingular( ACTION_DELETE, arguments );
	}

	public String viewAction( Object... arguments ) {
		return withNameSingular( ACTION_VIEW, arguments );
	}

	public String createPageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_CREATE, arguments );
	}

	public String updatePageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_UPDATE, arguments );
	}

	public String deletePageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_DELETE, arguments );
	}

	public String viewPageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_VIEW, arguments );
	}

	public String withNameSingular( String code, Object... arguments ) {
		return messageWithFallback( code,
		                            messageCodeResolver.getNameSingular(),
		                            messageCodeResolver.getNameSingularInline(),
		                            arguments );
	}

	public String withNamePlural( String code, Object... arguments ) {
		return messageWithFallback( code,
		                            messageCodeResolver.getNamePlural(),
		                            messageCodeResolver.getNamePluralInline(),
		                            arguments );
	}

	public String message( String code, Object... arguments ) {
		return messageCodeResolver.getMessage( code, arguments( arguments ), null );
	}

	public String messageWithFallback( String code, Object... arguments ) {
		return messageCodeResolver.getMessageWithFallback( code, arguments( arguments ), null );
	}

	protected Object[] arguments( Object... candidates ) {
		if ( candidates.length > 0 ) {
			int last = candidates.length - 1;
			if ( ObjectUtils.isArray( candidates[last] ) ) {
				return ArrayUtils.addAll(
						ArrayUtils.subarray( candidates, 0, last ),
						(Object[]) candidates[last]
				);
			}
		}
		return candidates;
	}
}

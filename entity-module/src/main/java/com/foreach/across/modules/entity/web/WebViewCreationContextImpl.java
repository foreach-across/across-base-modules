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
package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.support.AttributeSupport;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceUtils;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @author Arne Vandamme
 */
public class WebViewCreationContextImpl extends AttributeSupport implements WebViewCreationContext
{
	private NativeWebRequest request;
	private EntityConfiguration entityConfiguration;
	private EntityAssociation entityAssociation;

	public NativeWebRequest getRequest() {
		return request;
	}

	public void setRequest( NativeWebRequest request ) {
		this.request = request;
	}

	public EntityConfiguration getEntityConfiguration() {
		return entityConfiguration;
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
	}

	@Override
	public EntityAssociation getEntityAssociation() {
		return entityAssociation;
	}

	@Override
	public void setEntityAssociation( EntityAssociation entityAssociation ) {
		this.entityAssociation = entityAssociation;
		if ( entityConfiguration == null ) {
			entityConfiguration = entityAssociation.getTargetEntityConfiguration();
		}
	}

	@Override
	public boolean isForAssociation() {
		return getEntityAssociation() != null;
	}

	@Override
	public WebResourceRegistry getWebResourceRegistry() {
		return WebResourceUtils.getRegistry( request );
	}
}

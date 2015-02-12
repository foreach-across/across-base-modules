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
package com.foreach.across.modules.entity.controllers;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Command object for entity view binding.
 *
 * @author Arne Vandamme
 */
public class EntityViewCommand
{
	@Valid
	private Object entity;

	@Valid
	private Map<String, Object> extensions = new HashMap<>();

	public EntityViewCommand() {
	}

	/**
	 * @return The entity (dto) instance for the command.
	 */
	public Object getEntity() {
		return entity;
	}

	public void setEntity( Object entity ) {
		this.entity = entity;
	}

	/**
	 * @return Map of possible command extensions that have been registered.
	 */
	public Map<String, Object> getExtensions() {
		return extensions;
	}

	public void addExtensions( String name, Object extension ) {
		extensions.put( name, extension );
	}
}

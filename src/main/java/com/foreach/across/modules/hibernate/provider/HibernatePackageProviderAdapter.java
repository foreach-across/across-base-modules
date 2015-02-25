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
package com.foreach.across.modules.hibernate.provider;

import java.util.Collections;
import java.util.Map;

public class HibernatePackageProviderAdapter implements HibernatePackageProvider
{
	public String[] getPackagesToScan() {
		return new String[0];
	}

	public Class<?>[] getAnnotatedClasses() {
		return new Class<?>[0];
	}

	public String[] getMappingResources() {
		return new String[0];
	}

	@Override
	public Map<String, String> getTableAliases() {
		return Collections.emptyMap();
	}
}
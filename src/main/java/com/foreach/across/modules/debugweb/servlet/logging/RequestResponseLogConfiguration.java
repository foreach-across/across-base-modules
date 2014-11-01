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
package com.foreach.across.modules.debugweb.servlet.logging;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

/**
 * Configuration instance for request/response debug logging.
 *
 * @author Arne Vandamme
 */
public class RequestResponseLogConfiguration
{
	private int maxEntries = 100;
	private Collection<String> includedPathPatterns;
	private Collection<String> excludedPathPatterns;
	private Collection<String> urlFilterMappings = Collections.singleton( "/*" );
	private Collection<String> servletNameFilterMappings = Collections.emptySet();
	private boolean paused = false;

	public int getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries( int maxEntries ) {
		this.maxEntries = maxEntries;
	}

	public Collection<String> getIncludedPathPatterns() {
		return includedPathPatterns;
	}

	public void setIncludedPathPatterns( Collection<String> includedPathPatterns ) {
		this.includedPathPatterns = includedPathPatterns;
	}

	public Collection<String> getExcludedPathPatterns() {
		return excludedPathPatterns;
	}

	public void setExcludedPathPatterns( Collection<String> excludedPathPatterns ) {
		this.excludedPathPatterns = excludedPathPatterns;
	}

	public Collection<String> getUrlFilterMappings() {
		return urlFilterMappings;
	}

	public void setUrlFilterMappings( Collection<String> urlFilterMappings ) {
		Assert.notNull( urlFilterMappings );
		this.urlFilterMappings = urlFilterMappings;
	}

	public Collection<String> getServletNameFilterMappings() {
		return servletNameFilterMappings;
	}

	public void setServletNameFilterMappings( Collection<String> servletNameFilterMappings ) {
		Assert.notNull( servletNameFilterMappings );
		this.servletNameFilterMappings = servletNameFilterMappings;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused( boolean paused ) {
		this.paused = paused;
	}
}



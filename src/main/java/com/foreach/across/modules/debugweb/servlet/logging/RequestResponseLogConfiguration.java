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
}



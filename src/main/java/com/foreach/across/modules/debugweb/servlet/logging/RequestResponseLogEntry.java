package com.foreach.across.modules.debugweb.servlet.logging;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.*;

public class RequestResponseLogEntry
{
	private final UUID id;
	private final long started, finished;
	private LogRequestWrapper request;
	private LogResponseWrapper response;

	private String requestData, responseData;

	private String sessionId, contentType, url, callerInfo, requestMethod;

	private Map<String, String> requestCookies = new TreeMap<>();
	private Map<String, String> requestHeaders = new TreeMap<>();

	public RequestResponseLogEntry( long started,
	                                long finished,
	                                LogRequestWrapper request,
	                                LogResponseWrapper response ) {
		id = UUID.randomUUID();

		this.started = started;
		this.finished = finished;

		buildRequestInfo( request );
		this.response = response;

	}

	private void buildRequestInfo( LogRequestWrapper request ) {
		url = request.getRequestURL().toString();
		requestMethod = request.getMethod();
		contentType = request.getContentType();

		Enumeration<String> headerNames = request.getHeaderNames();

		while ( headerNames.hasMoreElements() ) {
			String headerName = headerNames.nextElement();
			requestHeaders.put( headerName, request.getHeader( headerName ) );

		}

		callerInfo = String.format( "%s @ %s:%s", StringUtils.defaultString( request.getRemoteUser(), "unknown" ),
		                            request.getRemoteAddr(),
		                            request.getRemotePort() );

		for ( Cookie cookie : request.getCookies() ) {
			requestCookies.put( cookie.getName(), cookie.getValue() );
		}

		HttpSession session = request.getSession( false );

		if ( session != null ) {
			sessionId = session.getId();
		}
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getUrl() {
		return url;
	}

	public String getCallerInfo() {
		return callerInfo;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public Map<String, String> getRequestCookies() {
		return requestCookies;
	}

	public String getRequestInfo() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put( "url", getUrl() );
		data.put( "method", getRequestMethod() );
		if ( getContentType() != null ) {
			data.put( "contentType", getContentType() );
		}
		if ( getSessionId() != null ) {
			data.put( "sessionId", getSessionId() );
		}
		data.put( "remote", getCallerInfo() );
		data.put( "headers", getRequestHeaders() );
		data.put( "cookies", getRequestCookies() );

		Yaml yaml = new Yaml();
		return yaml.dump( data );
	}

	public UUID getId() {
		return id;
	}

	public String getRequestData() {
		return requestData;
	}

	public String getResponseData() {
		return responseData;
	}

	public Date getStarted() {
		return new Date( started );
	}

	public Date getFinished() {
		return new Date( finished );
	}

	public long getDuration() {
		return finished - started;
	}

	public LogRequestWrapper getRequest() {
		return request;
	}

	public LogResponseWrapper getResponse() {
		return response;
	}
}

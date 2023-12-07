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
package com.foreach.across.modules.logging.requestresponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

public class RequestResponseLogEntry
{
	private static final String ILLEGAL_PARAMETER_PATTERN = ".*password.*";

	private final UUID id;
	private final long started, finished;

	private String requestData, responseData, requestPayloadSize, responsePayloadSize;
	private boolean requestDataTruncated, responseDataTruncated;

	private int responseStatus;
	private String sessionId, responseContentType, contentType, url, uri, callerInfo, requestMethod, remoteIp,
			responseCharacterEncoding;

	private Map<String, String> requestCookies = new TreeMap<>();
	private Map<String, String> requestHeaders = new TreeMap<>();
	private Map<String, String> responseHeaders = new TreeMap<>();
	private Map<String, String[]> requestParameters = new TreeMap<>();

	public RequestResponseLogEntry( long started,
	                                long finished,
	                                LogRequestWrapper request,
	                                LogResponseWrapper response ) {
		id = UUID.randomUUID();

		this.started = started;
		this.finished = finished;

		buildRequestInfo( request );
		buildResponseInfo( response );
	}

	private void buildRequestInfo( LogRequestWrapper request ) {
		url = request.getRequestURL().toString();
		uri = request.getRequestURI();

		if ( !StringUtils.isBlank( request.getQueryString() ) ) {
			url += "?" + request.getQueryString();
		}

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
		remoteIp = request.getRemoteAddr();

		Cookie[] cookies = request.getCookies();

		if ( cookies != null ) {
			for ( Cookie cookie : request.getCookies() ) {
				requestCookies.put( cookie.getName(), cookie.getValue() );
			}
			requestHeaders.remove( "cookie" );
		}

		HttpSession session = request.getSession( false );

		if ( session != null ) {
			sessionId = session.getId();
		}

		if ( !isMultipart( request ) ) {
			if ( request.payloadSize() > 1024 * 512 ) {
				requestData = "Request data is too big: " + request.payloadSize() + " bytes";
			}
			else {
				try {
					String charEncoding =
							request.getCharacterEncoding() != null ? request.getCharacterEncoding() :
									"UTF-8";
					requestData = new String( request.toByteArray(), charEncoding );
				}
				catch ( UnsupportedEncodingException usee ) {
					requestData = "Unable to parse request payload.";
				}
			}
		}
		else {
			requestData = "Request payload is multipart data.";
		}

		request.getParameterMap()
		       .forEach( ( key, value ) -> {
			       if ( Pattern.matches( ILLEGAL_PARAMETER_PATTERN, StringUtils.lowerCase( key ) ) ) {
				       requestParameters.put( key, new String[] { "*" } );
			       }
			       else {
				       requestParameters.put( key, value );
			       }
		       } );

		requestDataTruncated = request.isMaximumReached();
		requestPayloadSize = FileUtils.byteCountToDisplaySize( request.payloadSize() );
		if ( StringUtils.isEmpty( requestData ) ) {
			requestData = "no request payload";
		}
	}

	private boolean isMultipart( HttpServletRequest request ) {
		return request.getContentType() != null && request.getContentType().startsWith( "multipart/form-data" );
	}

	private void buildResponseInfo( LogResponseWrapper response ) {
		responseStatus = response.getStatus();
		responseContentType = response.getContentType();
		responseCharacterEncoding = response.getCharacterEncoding();

		for ( String headerName : response.getHeaderNames() ) {
			responseHeaders.put( headerName, response.getHeader( headerName ) );
		}

		if ( response.payloadSize() > 1024 * 512 ) {
			responseData = "Response data is too big: " + response.payloadSize() + " bytes";
		}
		else {
			try {
				String charEncoding =
						response.getCharacterEncoding() != null ? response.getCharacterEncoding() : "UTF-8";
				responseData = new String( response.toByteArray(), charEncoding );
			}
			catch ( UnsupportedEncodingException usee ) {
				responseData = "Unable to parse response payload.";
			}
		}

		responseDataTruncated = response.isMaximumReached();
		responsePayloadSize = FileUtils.byteCountToDisplaySize( response.payloadSize() );
		if ( StringUtils.isEmpty( responseData ) ) {
			responseData = "no response payload";
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

	public int getResponseStatus() {
		return responseStatus;
	}

	public String getResponseContentType() {
		return responseContentType;
	}

	public String getUri() {
		return uri;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public String getResponseCharacterEncoding() {
		return responseCharacterEncoding;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public Map<String, String[]> getRequestParameters() {
		return requestParameters;
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

	public boolean isRequestDataTruncated() {
		return requestDataTruncated;
	}

	public boolean isResponseDataTruncated() {
		return responseDataTruncated;
	}

	public String getRequestPayloadSize() {
		return requestPayloadSize;
	}

	public String getResponsePayloadSize() {
		return responsePayloadSize;
	}
}

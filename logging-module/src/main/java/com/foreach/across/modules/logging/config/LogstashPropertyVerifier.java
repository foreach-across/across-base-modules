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

package com.foreach.across.modules.logging.config;

import com.foreach.across.modules.logging.LoggingModuleSettings;
import net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConditionalOnProperty(name = LoggingModuleSettings.LOGSTASH_CONFIGURATION_SERVER)
@ConditionalOnMissingClass(value = { LoggingEventAsyncDisruptorAppender.class })
public class LogstashPropertyVerifier
{
	public LogstashPropertyVerifier() {
		throw new RuntimeException( "No logstash-logback-encoder dependency found. Did you forget to include the 'logstash-logback-encoder' dependency?" );
	}
}

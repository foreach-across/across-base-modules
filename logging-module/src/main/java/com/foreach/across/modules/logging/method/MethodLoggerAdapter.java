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
package com.foreach.across.modules.logging.method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class to be implemented for a {@link MethodLogger}.
 * Usually extended into an {@link org.aspectj.lang.annotation.Aspect} with the
 * {@link org.aspectj.lang.annotation.Around} advice on {@link #proceedAndLogExecutionTime(ProceedingJoinPoint)}.
 * The name of a logger must be unique.
 *
 * @author Arne Vandamme
 */
public abstract class MethodLoggerAdapter implements MethodLogger, Ordered
{
	private static final ThreadLocal<AtomicInteger> methodLevelId = new ThreadLocal<>();

	private final String name;
	private final Logger LOG;

	private boolean enabled;
	private int minimumDuration;

	private int order = Ordered.HIGHEST_PRECEDENCE;

	protected MethodLoggerAdapter( String name ) {
		this.name = name;
		LOG = LoggerFactory.getLogger( MethodLogger.class.getName() + "." + name );
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder( int order ) {
		this.order = order;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	@Override
	public final void setEnabled( boolean enabled ) {
		this.enabled = enabled;

		if ( minimumDuration < 0 ) {
			this.enabled = false;
		}
	}

	@Override
	public final int getMinimumDuration() {
		return minimumDuration;
	}

	@Override
	public final void setMinimumDuration( int minimumDuration ) {
		this.minimumDuration = minimumDuration;

		if ( minimumDuration < 0 ) {
			enabled = false;
		}
	}

	protected Object proceedAndLogExecutionTime( ProceedingJoinPoint point ) throws Throwable {
		if ( enabled ) {
			boolean removeAfter = false;

			AtomicInteger previousId = methodLevelId.get();
			AtomicInteger newId = new AtomicInteger( 0 );

			if ( previousId == null ) {
				removeAfter = true;
				previousId = new AtomicInteger( 0 );
			}

			long startTime = System.currentTimeMillis();

			try {
				methodLevelId.set( newId );

				return point.proceed();
			}
			finally {
				long duration = System.currentTimeMillis() - startTime;

				if ( duration >= minimumDuration ) {
					try {
						LOG.info(
								"{}\t{}.{}\t{}",
								newId.intValue() + previousId.incrementAndGet(),
								point.getSignature().getDeclaringType().getName(),
								point.getSignature().getName(),
								duration
						);
					}
					catch ( Exception e ) {
						LOG.warn( "Exception during method time logging\t{}", duration, e );
					}
				}

				if ( removeAfter ) {
					methodLevelId.remove();
				}
				else {
					methodLevelId.set( previousId );
				}
			}
		}

		return point.proceed();
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof MethodLoggerAdapter ) ) {
			return false;
		}
		MethodLoggerAdapter that = (MethodLoggerAdapter) o;
		return Objects.equals( name, that.name );
	}

	@Override
	public int hashCode() {
		return Objects.hash( name );
	}
}

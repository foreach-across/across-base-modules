package com.foreach.across.modules.spring.security.infrastructure.config;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.infrastructure.services.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Configures a {@link com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService}.
 * This implementation requires a {@link com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy}
 * instance to be available.
 *
 * @author Arne Vandamme
 */
@Configuration
public class SecurityPrincipalServiceConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	public CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy() {
		return new CurrentSecurityPrincipalProxyImpl();
	}

	/**
	 * Create a SecurityPrincipalService that fetches the retrieval strategy upon first use.
	 * The entire context will be scanned for
	 * {@link com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy}
	 * implementations and the first one (according to module orders) will be returned.
	 */
	@Bean
	public SecurityPrincipalService securityPrincipalService() {
		FirstOrderedBeanTargetSource targetSource = new FirstOrderedBeanTargetSource();
		targetSource.setContextBeanRegistry( contextBeanRegistry );
		targetSource.setBeanType( SecurityPrincipalRetrievalStrategy.class );

		SecurityPrincipalRetrievalStrategy strategy = ProxyFactory.getProxy( SecurityPrincipalRetrievalStrategy.class,
		                                                                     targetSource );

		return new SecurityPrincipalServiceImpl( strategy );
	}

	static class FirstOrderedBeanTargetSource extends AbstractLazyCreationTargetSource
	{
		private AcrossContextBeanRegistry contextBeanRegistry;
		private Class<?> beanType;

		public void setContextBeanRegistry( AcrossContextBeanRegistry contextBeanRegistry ) {
			this.contextBeanRegistry = contextBeanRegistry;
		}

		public void setBeanType( Class<?> beanType ) {
			this.beanType = beanType;
		}

		@Override
		protected Object createObject() throws Exception {
			Assert.notNull( contextBeanRegistry );
			Assert.notNull( beanType );

			List beans = contextBeanRegistry.getBeansOfType( beanType, true );

			if ( beans.isEmpty() ) {
				throw new BeanCreationException(
						"No bean of " + beanType + " found in the entire AcrossContext.  One is required before " +
								"you can use a SecurityPrincipalService." );
			}

			return beans.get( 0 );
		}
	}
}

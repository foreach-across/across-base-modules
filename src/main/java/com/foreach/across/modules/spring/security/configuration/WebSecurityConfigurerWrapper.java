package com.foreach.across.modules.spring.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.lang.reflect.Method;

/**
 * Wraps a SpringSecurityWebConfigurer into an ordered WebSecurityConfigurerAdapter that Spring security
 * can actually work with.  Reason for this wrapping is that SpringSecurityConfigurers can be defined
 * in other modules, but Spring security only supports WebSecurityConfigurers in its own ApplicationContext.
 * <p/>
 * With this approach we allow SpringSecurityWebConfigurer beans to be created in their own module, but the
 * security specific settings (requiring beans from the SpringSecurityModule) will be added to the wrapper
 * that will delegate the extension methods to the SpringSecurityWebConfigurer bean.
 * <p/>
 * Additionally the wrapper can enforce configurer ordering based on the module definition order.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.core.registry.RefreshableRegistry
 * @since 1.0.3
 */
public class WebSecurityConfigurerWrapper extends WebSecurityConfigurerAdapter implements Ordered
{
	private final SpringSecurityWebConfigurer configurer;
	private final int order;

	private ObjectPostProcessor<Object> objectPostProcessor;

	public WebSecurityConfigurerWrapper( SpringSecurityWebConfigurer configurer, int order ) {
		super( configurer.isDisableDefaults() );
		this.configurer = configurer;
		this.order = order;
	}

	@Autowired
	@Override
	public void setObjectPostProcessor( ObjectPostProcessor<Object> objectPostProcessor ) {
		super.setObjectPostProcessor( objectPostProcessor );

		this.objectPostProcessor = objectPostProcessor;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	protected void configure( final AuthenticationManagerBuilder auth ) throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( AuthenticationManagerBuilder.class );

		AuthenticationManagerBuilderDelegateInterceptor interceptor =
				new AuthenticationManagerBuilderDelegateInterceptor( auth );

		enhancer.setCallback( interceptor );

		AuthenticationManagerBuilder proxy = (AuthenticationManagerBuilder) enhancer.create(
				new Class[] { ObjectPostProcessor.class }, new Object[] { objectPostProcessor }
		);

		configurer.configure( proxy );

		// If it has not been configured, ignore the local authentication manager
		if ( !interceptor.isCalled() ) {
			super.configure( auth );
		}
	}

	@Override
	public void configure( WebSecurity web ) throws Exception {
		configurer.configure( web );
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception {
		configurer.configure( http );
	}

	class AuthenticationManagerBuilderDelegateInterceptor implements InvocationHandler
	{
		private transient final AuthenticationManagerBuilder builder;
		private transient boolean called = false;

		AuthenticationManagerBuilderDelegateInterceptor( AuthenticationManagerBuilder builder ) {
			this.builder = builder;
		}

		public boolean isCalled() {
			return called;
		}

		@Override
		public Object invoke( Object o, Method method, Object[] objects ) throws Throwable {
			// Only object methods
			if ( method.getDeclaringClass() != Object.class ) {
				called = true;
			}
			return method.invoke( builder, objects );
		}
	}

	@Override
	public String toString() {
		return "WebSecurityConfigurerWrapper{" +
				"configurer=" + configurer +
				", order=" + order +
				'}';
	}
}

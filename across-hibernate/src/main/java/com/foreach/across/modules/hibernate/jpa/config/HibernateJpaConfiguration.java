package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.database.DatabaseInfo;
import com.foreach.across.core.registry.IncrementalRefreshableRegistry;
import com.foreach.across.core.registry.RefreshableRegistry;
import com.foreach.across.modules.hibernate.aop.EntityInterceptor;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import com.foreach.across.modules.hibernate.jpa.intercept.EntityInterceptorEntityListener;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.strategy.AbstractTableAliasNamingStrategy;
import org.hibernate.ejb.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configures a JPA EntityManagerFactory.
 *
 * @see com.foreach.across.modules.hibernate.config.HibernateConfiguration
 * @see com.foreach.across.modules.hibernate.config.TransactionManagerConfiguration
 */
@Configuration
public class HibernateJpaConfiguration
{
	public static final String TRANSACTION_MANAGER = "jpaTransactionManager";

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossHibernateJpaModule module;

	@Autowired
	private AcrossHibernateJpaModuleSettings settings;

	@Autowired
	private HibernatePackage hibernatePackage;

	@Autowired
	private ApplicationContext ctx;

	@Bean
	@Exposed
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase( determineDatabase( module.getDataSource() ) );

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter( vendorAdapter );
		factory.setDataSource( module.getDataSource() );
		factory.setPackagesToScan( hibernatePackage.getPackagesToScan() );
		factory.getJpaPropertyMap().putAll( settings.getHibernateProperties() );

		Map<String, String> tableAliases = hibernatePackage.getTableAliases();

		if ( !tableAliases.isEmpty() ) {
			// Create a unique naming strategy class referring to the defined table aliases
			factory.getJpaPropertyMap().put( AvailableSettings.NAMING_STRATEGY,
			                                 createTableAliasNamingStrategyClass( tableAliases ).getName() );
		}

		return factory;
	}

	private Class createTableAliasNamingStrategyClass( Map<String, String> tableAliases ) {
		InterfaceMaker interfaceMaker = new InterfaceMaker();
		Class dynamicInterface = interfaceMaker.create();

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( AbstractTableAliasNamingStrategy.class );
		enhancer.setInterfaces( new Class[] { dynamicInterface } );
		enhancer.setUseFactory( false );
		enhancer.setCallbackType( NoOp.class );

		Class strategyClass = enhancer.createClass();

		AbstractTableAliasNamingStrategy.registerTableAliases( strategyClass, tableAliases );

		return strategyClass;
	}

	// Use a static method to set entity interceptors - so we do not require @Configurable and load time weaving.
	// This effectively limits the use of AcrossHibernateJpaModule to once per class loader however.
	@Bean
	public RefreshableRegistry<EntityInterceptor> entityInterceptors() {
		IncrementalRefreshableRegistry<EntityInterceptor> interceptors =
				new IncrementalRefreshableRegistry<>( EntityInterceptor.class, true );
		EntityInterceptorEntityListener.setEntityInterceptors( interceptors );

		return interceptors;
	}

	private Database determineDatabase( DataSource dataSource ) {
		DatabaseInfo databaseInfo = DatabaseInfo.retrieve( dataSource );

		if ( databaseInfo.isHsql() ) {
			return Database.HSQL;
		}
		if ( databaseInfo.isOracle() ) {
			return Database.ORACLE;
		}
		if ( databaseInfo.isMySQL() ) {
			return Database.MYSQL;
		}
		if ( databaseInfo.isSqlServer() ) {
			return Database.SQL_SERVER;
		}

		return null;
	}
}

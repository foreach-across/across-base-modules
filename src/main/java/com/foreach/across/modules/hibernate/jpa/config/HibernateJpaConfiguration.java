package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.database.DatabaseInfo;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

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

		return factory;
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

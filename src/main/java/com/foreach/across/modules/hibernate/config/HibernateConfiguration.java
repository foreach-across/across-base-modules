package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.strategy.TableAliasNamingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.batch.internal.FixedBatchBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import java.util.Map;

@Configuration
public class HibernateConfiguration
{
	@Autowired
	@Qualifier(AcrossModule.CURRENT_MODULE)
	private AcrossHibernateModule module;

	@Bean
	@Exposed
	public LocalSessionFactoryBean sessionFactory( HibernatePackage hibernatePackage ) {

		String version = org.hibernate.Version.getVersionString();
		if( StringUtils.startsWith( version, "4.2" ) ) {
			// WORKAROUND bug: https://hibernate.atlassian.net/browse/HHH-8853
			int batchSize = Integer.valueOf( module.getHibernateProperties().getProperty( Environment.STATEMENT_BATCH_SIZE, "0" ) );
			FixedBatchBuilderImpl.setSize( batchSize );
			module.setHibernateProperty( "hibernate.jdbc.batch.builder", FixedBatchBuilderImpl.class.getName() );
		}

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource( module.getDataSource() );

		sessionFactory.setAnnotatedClasses( hibernatePackage.getAnnotatedClasses() );
		sessionFactory.setPackagesToScan( hibernatePackage.getPackagesToScan() );
		sessionFactory.setMappingResources( hibernatePackage.getMappingResources() );

		Map<String, String> tableAliases = hibernatePackage.getTableAliases();

		if ( !tableAliases.isEmpty() ) {
			sessionFactory.setNamingStrategy( new TableAliasNamingStrategy( tableAliases ) );
		}

		sessionFactory.setHibernateProperties( module.getHibernateProperties() );

		return sessionFactory;
	}

	@Bean
	@Exposed
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
}

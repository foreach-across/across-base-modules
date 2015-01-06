package com.foreach.across.modules.hibernate.testmodules.springdata;

import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(transactionManagerRef = HibernateJpaConfiguration.TRANSACTION_MANAGER)
public class ClientConfig
{
}

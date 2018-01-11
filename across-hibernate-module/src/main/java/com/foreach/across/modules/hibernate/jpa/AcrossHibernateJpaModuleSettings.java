package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SuppressWarnings("all")
@ConfigurationProperties("acrossHibernate")
@Data
@EqualsAndHashCode(callSuper = true)
public class AcrossHibernateJpaModuleSettings extends AcrossHibernateModuleSettings
{
	public static final String PERSISTENCE_UNIT_NAME = "acrossHibernate.persistenceUnitName";

	/**
	 * Name of the persistence unit that is being managed by this module. Defaults to the module name.
	 */
	private String persistenceUnitName;
}

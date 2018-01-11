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
	public static final String PRIMARY = "acrossHibernate.primary";

	/**
	 * Name of the persistence unit that is being managed by this module. Defaults to the module name.
	 */
	private String persistenceUnitName;

	/**
	 * Should this module register its {@link org.springframework.transaction.PlatformTransactionManager},
	 * {@link org.springframework.transaction.support.TransactionTemplate} and
	 * {@link com.foreach.across.modules.hibernate.services.HibernateSessionHolder} as primary when exposing.
	 * <p/>
	 * If set to {@code true} this will also register the default aliases for the
	 * {@link org.springframework.transaction.PlatformTransactionManager} and
	 * {@link org.springframework.transaction.support.TransactionTemplate}.
	 */
	private Boolean primary;
}

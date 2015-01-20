package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityModelImpl;
import com.foreach.across.modules.entity.registry.PersistentEntityFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.support.CrudInvokerUtils;

import java.io.Serializable;

/**
 * Builds an {@link com.foreach.across.modules.entity.registry.EntityModel} for a Spring data repository.
 */
public class RepositoryEntityModelBuilder
{
	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> EntityModel<T, ID> buildEntityModel(
			RepositoryFactoryInformation<T, ID> repositoryFactoryInformation,
			Repository<T, ID> repository
	) {
		EntityModelImpl<T, ID> entityModel = new EntityModelImpl<>();
		entityModel.setCrudInvoker(
				CrudInvokerUtils.createCrudInvoker( repositoryFactoryInformation.getRepositoryInformation(),
				                                    repository )
		);
		entityModel.setEntityFactory(
				new PersistentEntityFactory( repositoryFactoryInformation.getPersistentEntity() )
		);
		entityModel.setEntityInformation( repositoryFactoryInformation.getEntityInformation() );

		return entityModel;
	}

}

package org.springframework.data.repository.support;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.CrudInvoker;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryInformation;

import java.io.Serializable;

/**
 * Helper to provide access to package protected classes from Spring data commons.
 */
public class CrudInvokerUtils
{
	public static <T> CrudInvoker<T> crudRepositoryInvoker( CrudRepository<T, Serializable> repository ) {
		return new CrudRepositoryInvoker<>( repository );
	}

	public static <T> CrudInvoker<T> reflectionRepositoryInvoker( Repository<T, Serializable> repository,
	                                                              CrudMethods crudMethods ) {
		return new ReflectionRepositoryInvoker<T>( repository, crudMethods );
	}

	@SuppressWarnings("unchecked")
	public static <T, ID extends Serializable> CrudInvoker<T> createCrudInvoker(
			RepositoryInformation repositoryInformation,
			Repository<T, ID> repository
	) {
		if ( repository instanceof CrudRepository ) {
			return crudRepositoryInvoker( (CrudRepository<T, Serializable>) repository );
		}
		else {
			return reflectionRepositoryInvoker( (Repository<T, Serializable>) repository,
			                                    repositoryInformation.getCrudMethods() );
		}
	}
}

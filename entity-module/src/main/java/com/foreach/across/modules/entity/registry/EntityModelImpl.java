/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.registry;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.core.CrudInvoker;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Arne Vandamme
 */
public class EntityModelImpl<T, ID extends Serializable> implements EntityModel<T, ID>
{
	private EntityFactory<T> entityFactory;
	private EntityInformation<T, ID> entityInformation;
	private CrudInvoker<T> crudInvoker;
	private Printer<T> labelPrinter;

	public void setEntityFactory( EntityFactory<T> entityFactory ) {
		this.entityFactory = entityFactory;
	}

	public void setEntityInformation( EntityInformation<T, ID> entityInformation ) {
		this.entityInformation = entityInformation;
	}

	public void setCrudInvoker( CrudInvoker<T> crudInvoker ) {
		this.crudInvoker = crudInvoker;
	}

	public void setLabelPrinter( Printer<T> labelPrinter ) {
		this.labelPrinter = labelPrinter;
	}

	@Override
	public String getLabel( T entity ) {
		return getLabel( entity, LocaleContextHolder.getLocale() );
	}

	@Override
	public String getLabel( T entity, Locale locale ) {
		return labelPrinter.print( entity, locale );
	}

	@Override
	public T createNew( Object... args ) {
		return entityFactory.createNew( args );
	}

	@SuppressWarnings("unchecked")
	@Override
	public T createDto( T entity ) {
		return entityFactory.createDto( entity );
	}

	@Override
	public T findOne( ID id ) {
		return crudInvoker.invokeFindOne( id );
	}

	@Override
	public T save( T entity ) {
		return crudInvoker.invokeSave( entity );
	}

	@Override
	public boolean isNew( T entity ) {
		return entityInformation.isNew( entity );
	}

	@Override
	public ID getId( T entity ) {
		return entityInformation.getId( entity );
	}

	@Override
	public Class<ID> getIdType() {
		return entityInformation.getIdType();
	}

	@Override
	public Class<T> getJavaType() {
		return entityInformation.getJavaType();
	}
}

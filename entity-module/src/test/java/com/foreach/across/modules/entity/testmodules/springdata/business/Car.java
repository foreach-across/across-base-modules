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
package com.foreach.across.modules.entity.testmodules.springdata.business;

import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Arne Vandamme
 */
@Entity
public class Car implements Persistable<String>
{
	@Id
	private String id;

	@ManyToOne
	private Company company;

	@Override
	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany( Company company ) {
		this.company = company;
	}

	@Override
	public boolean isNew() {
		return false;
	}
}

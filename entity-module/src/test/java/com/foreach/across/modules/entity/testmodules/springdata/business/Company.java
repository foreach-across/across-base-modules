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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
@Entity
public class Company implements Persistable<String>
{
	@Id
	private String id;

	@Column
	private CompanyStatus status;

	@ManyToMany
	private Set<Representative> representatives = new HashSet<>();

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return true;
	}

	public CompanyStatus getStatus() {
		return status;
	}

	public void setStatus( CompanyStatus status ) {
		this.status = status;
	}

	public Set<Representative> getRepresentatives() {
		return representatives;
	}

	public void setRepresentatives( Set<Representative> representatives ) {
		this.representatives = representatives;
	}
}

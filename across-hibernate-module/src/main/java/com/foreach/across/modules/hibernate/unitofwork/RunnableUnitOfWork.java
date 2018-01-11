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
package com.foreach.across.modules.hibernate.unitofwork;

public class RunnableUnitOfWork implements Runnable
{
	private final UnitOfWorkFactory unitOfWorkFactory;
	private final Runnable runnable;

	public RunnableUnitOfWork( UnitOfWorkFactory unitOfWorkFactory, Runnable runnable ) {
		this.unitOfWorkFactory = unitOfWorkFactory;
		this.runnable = runnable;
	}

	public void run() {
		try (UnitOfWork ignore = unitOfWorkFactory.start()) {
			runnable.run();
		}
	}
}

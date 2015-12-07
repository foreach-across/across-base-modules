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

import java.io.Closeable;

public class UnitOfWork implements Closeable
{
	private final UnitOfWorkFactory unitOfWorkFactory;

	public UnitOfWork( UnitOfWorkFactory unitOfWorkFactory ) {
		this.unitOfWorkFactory = unitOfWorkFactory;
	}

	/**
	 * Stop the unit of work, delegates to {@link UnitOfWorkFactory#stop()}.
	 */
	@Override
	public void close() {
		unitOfWorkFactory.stop();
	}

	/**
	 * Restart the unit of work, delegates to {@link UnitOfWorkFactory#restart()}.
	 */
	public void restart() {
		unitOfWorkFactory.restart();
	}
}

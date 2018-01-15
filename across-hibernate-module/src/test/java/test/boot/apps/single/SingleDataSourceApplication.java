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

package test.boot.apps.single;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import test.boot.apps.single.application.Book;

/**
 * @author Arne Vandamme
 */
@AcrossApplication(modules = AcrossHibernateJpaModule.NAME)
public class SingleDataSourceApplication
{
	@Bean
	public BookInterceptor bookInterceptor() {
		return new BookInterceptor();
	}

	public static void main( String[] args ) {
		SpringApplication.run( SingleDataSourceApplication.class );
	}

	public static class BookInterceptor extends EntityInterceptorAdapter<Book>
	{
		@Getter
		private Book createdBook;

		@Override
		public boolean handles( Class<?> entityClass ) {
			return Book.class.isAssignableFrom( entityClass );
		}

		@Override
		public void beforeCreate( Book entity ) {
			this.createdBook = entity;
		}
	}
}

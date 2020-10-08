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

package com.foreach.across.modules.hibernate.util;

import com.github.dozermapper.core.CustomFieldMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.BeanCreationStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDozerMapperCustomizationRegistry
{
	private DozerMapperCustomizationRegistry dozerMapperCustomizationRegistry;
	private Mapper mapper;

	@BeforeEach
	public void setUp() {
		dozerMapperCustomizationRegistry = new DozerMapperCustomizationRegistry();
		mapper = DozerBeanMapperBuilder.create()
		                               .withCustomFieldMapper( dozerMapperCustomizationRegistry.getCustomFieldMapper() )
		                               .withBeanMappingsBuilders(
				                               ( beanContainer, destBeanCreator, propertyDescriptorFactory ) -> {
					                               destBeanCreator.addPluggedStrategy(
							                               dozerMapperCustomizationRegistry.getBeanCreationStrategy() );
					                               return Collections.emptyList();
				                               } )
		                               .build();
	}

	/**
	 * Attempts to clean up the plugged strategies which is a static arraylist, and as such shared over instances.
	 * During these unit tests, we expect a new fresh {@link com.github.dozermapper.core.DozerBeanMapper} to be present each time, without shared state.
	 */
	@AfterEach
	public void cleanUpPluggedStrategies() {
		mapper = DozerBeanMapperBuilder.create()
		                               .withCustomFieldMapper( dozerMapperCustomizationRegistry.getCustomFieldMapper() )
		                               .withBeanMappingsBuilders(
				                               ( beanContainer, destBeanCreator, propertyDescriptorFactory ) -> {
					                               try {
						                               Field pluggedStrategies = destBeanCreator.getClass()
						                                                                        .getDeclaredField( "pluggedStrategies" );
						                               ReflectionUtils.makeAccessible( pluggedStrategies );
						                               List o = (List) pluggedStrategies.get( destBeanCreator );
						                               o.clear();
					                               }
					                               catch ( NoSuchFieldException | IllegalAccessException e ) {
						                               e.printStackTrace();
					                               }
					                               return Collections.emptyList();
				                               } )
		                               .build();
	}

	@Test
	public void registerBeanCreationMapper() {
		CallsHolder testMapper = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER, "testMapper" );
		assertThat( testMapper.getTimesCalled() ).isEqualTo( 0 );
		SimpleObject so = SimpleObject.builder()
		                              .name( "myObject" )
		                              .year( 1990 )
		                              .build();
		mapper.map( so, SimpleObject.class );
		assertThat( testMapper.getTimesCalled() ).isEqualTo( 1 );
	}

	@Test
	public void firstMatchingBeanCreationMapperIsExecuted() {
		CallsHolder firstMapper = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER, "firstMapper" );
		SimpleObject so = SimpleObject.builder()
		                              .name( "firstObject" )
		                              .year( 1990 )
		                              .build();
		mapper.map( so, SimpleObject.class );
		assertThat( firstMapper.getTimesCalled() ).isEqualTo( 1 );
		CallsHolder secondMapper = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER - 100,
		                                                                  "secondMapper" );
		mapper.map( so, SimpleObject.class );
		assertThat( firstMapper.getTimesCalled() ).isEqualTo( 1 );
		assertThat( secondMapper.getTimesCalled() ).isEqualTo( 1 );

		firstMapper = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER + 100,
		                                                     "firstMapper",
		                                                     directive -> directive.getTargetClass() == SimpleObject.class
				                                                     && ( (SimpleObject) directive.getSrcObject() ).getName()
				                                                                                                   .startsWith( "first" ) );
		secondMapper = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER,
		                                                      "secondMapper",
		                                                      directive -> directive.getTargetClass() == SimpleObject.class
				                                                      && ( (SimpleObject) directive.getSrcObject() ).getName()
				                                                                                                    .startsWith(
						                                                                                                    "second" ) );
		mapper.map( so, SimpleObject.class );
		assertThat( firstMapper.getTimesCalled() ).isEqualTo( 1 );
		assertThat( secondMapper.getTimesCalled() ).isEqualTo( 0 );
	}

	@Test
	public void registerCustomFieldMapper() {
		CallsHolder testMapper = createAndRegisterCustomFieldMapper( DozerMapperCustomizationRegistry.DEFAULT_ORDER, "testMapper" );
		assertThat( testMapper.getTimesCalled() ).isEqualTo( 0 );
		SimpleObject so = SimpleObject.builder()
		                              .name( "myObject" )
		                              .year( 1990 )
		                              .build();
		mapper.map( so, SimpleObject.class );
		assertThat( testMapper.getTimesCalled() ).isEqualTo( 2 );
	}

	@Test
	public void firstMatchingCustomFieldMapperIsExecuted() {
		CallsHolder testMapper = createAndRegisterCustomFieldMapper( DozerMapperCustomizationRegistry.DEFAULT_ORDER, "testMapper" );
		SimpleObject so = SimpleObject.builder()
		                              .name( "myObject" )
		                              .year( 1990 )
		                              .build();
		mapper.map( so, SimpleObject.class );
		assertThat( testMapper.getTimesCalled() ).isEqualTo( 2 );
		CallsHolder firstMapper = createAndRegisterCustomFieldMapper( DozerMapperCustomizationRegistry.DEFAULT_ORDER - 100,
		                                                              "firstMapper" );
		mapper.map( so, SimpleObject.class );
		assertThat( testMapper.getTimesCalled() ).isEqualTo( 2 );
		assertThat( firstMapper.getTimesCalled() ).isEqualTo( 2 );
	}

	@Test
	public void registeringCustomizationsWithSameName() {
		CallsHolder beanCreationStrategy = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER,
		                                                                          "testMapper" );
		SimpleObject so = SimpleObject.builder()
		                              .name( "myObject" )
		                              .year( 1990 )
		                              .build();
		mapper.map( so, SimpleObject.class );
		assertThat( beanCreationStrategy.getTimesCalled() ).isEqualTo( 1 );
		CallsHolder replacedBeanCreationStrategy = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER,
		                                                                                  "testMapper" );
		mapper.map( so, SimpleObject.class );
		assertThat( beanCreationStrategy.getTimesCalled() ).isEqualTo( 1 );
		assertThat( replacedBeanCreationStrategy.getTimesCalled() ).isEqualTo( 1 );

		CallsHolder customFieldMapper = createAndRegisterCustomFieldMapper( DozerMapperCustomizationRegistry.DEFAULT_ORDER, "testMapper" );
		mapper.map( so, SimpleObject.class );
		assertThat( customFieldMapper.getTimesCalled() ).isEqualTo( 2 );
		assertThat( replacedBeanCreationStrategy.getTimesCalled() ).isEqualTo( 2 );
		CallsHolder replacedCustomFieldMapper = createAndRegisterCustomFieldMapper( DozerMapperCustomizationRegistry.DEFAULT_ORDER,
		                                                                            "testMapper" );
		mapper.map( so, SimpleObject.class );
		assertThat( customFieldMapper.getTimesCalled() ).isEqualTo( 2 );
		assertThat( replacedCustomFieldMapper.getTimesCalled() ).isEqualTo( 2 );
		assertThat( replacedBeanCreationStrategy.getTimesCalled() ).isEqualTo( 3 );
	}

	@Test
	public void removeCustomization() {
		CallsHolder beanCreationStrategy = createAndRegisterBeanCreationStrategy( DozerMapperCustomizationRegistry.DEFAULT_ORDER,
		                                                                          "testMapper" );
		CallsHolder customFieldMapper = createAndRegisterCustomFieldMapper( DozerMapperCustomizationRegistry.DEFAULT_ORDER, "testMapper" );
		SimpleObject so = SimpleObject.builder()
		                              .name( "myObject" )
		                              .year( 1990 )
		                              .build();
		mapper.map( so, SimpleObject.class );
		assertThat( beanCreationStrategy.getTimesCalled() ).isEqualTo( 1 );
		assertThat( customFieldMapper.getTimesCalled() ).isEqualTo( 2 );
		dozerMapperCustomizationRegistry.removeBeanCreationStrategy( "testMapper" );
		dozerMapperCustomizationRegistry.removeCustomFieldMapper( "testMapper" );
		mapper.map( so, SimpleObject.class );
		assertThat( beanCreationStrategy.getTimesCalled() ).isEqualTo( 1 );
		assertThat( customFieldMapper.getTimesCalled() ).isEqualTo( 2 );
	}

	private CallsHolder createAndRegisterBeanCreationStrategy( int defaultOrder, String mapperName ) {
		return createAndRegisterBeanCreationStrategy( defaultOrder, mapperName,
		                                              directive -> directive.getTargetClass() == SimpleObject.class );
	}

	private CallsHolder createAndRegisterBeanCreationStrategy( int defaultOrder,
	                                                           String mapperName,
	                                                           Function<BeanCreationDirective, Boolean> condition ) {
		CallsHolder testMapper = new CallsHolder();
		BeanCreationStrategy beanCreationStrategy = new BeanCreationStrategy()
		{
			@Override
			public boolean isApplicable( BeanCreationDirective directive ) {
				return condition.apply( directive );
			}

			@Override
			public Object create( BeanCreationDirective directive ) {
				testMapper.call();
				return new SimpleObject();
			}
		};
		dozerMapperCustomizationRegistry.register( DozerMapperCustomizationRegistry.beanCreationStrategyRegistrar()
		                                                                           .name( mapperName )
		                                                                           .customization( beanCreationStrategy )
		                                                                           .order( defaultOrder ) );
		return testMapper;
	}

	private CallsHolder createAndRegisterCustomFieldMapper( int defaultOrder, String mapperName ) {
		CallsHolder testMapper = new CallsHolder();
		CustomFieldMapper customFieldMapper = ( source, destination, sourceFieldValue, classMap, fieldMapping ) -> {
			testMapper.call();
			return true;
		};

		dozerMapperCustomizationRegistry.register( DozerMapperCustomizationRegistry.customFieldMapperRegistrar()
		                                                                           .name( mapperName )
		                                                                           .customization( customFieldMapper )
		                                                                           .order( defaultOrder ) );
		return testMapper;
	}

	@Data
	@NoArgsConstructor
	private static class CallsHolder
	{
		private int timesCalled = 0;

		void call() {
			timesCalled++;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	private static class SimpleObject
	{
		private String name;
		private int year;
	}
}

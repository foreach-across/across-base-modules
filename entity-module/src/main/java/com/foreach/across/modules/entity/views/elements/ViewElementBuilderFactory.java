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
package com.foreach.across.modules.entity.views.elements;

/**
 * Creates a {@link ViewElementBuilder} that creates a
 * specific {@link com.foreach.across.modules.entity.views.elements.ViewElement} type.
 * The factory is expected to be immutable and create a builder usually using a base builder as a template.
 * The builder itself can then be modified for a particular view before creating the actual
 * {@link com.foreach.across.modules.entity.views.elements.ViewElement}.
 *
 * @author Arne Vandamme
 */
public interface ViewElementBuilderFactory<T extends ViewElementBuilder>
{
	T createBuilder();
}

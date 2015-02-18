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
package com.foreach.across.modules.entity.views.thymeleaf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.entity.views.elements.form.dependencies.Qualifiers;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andy Somers
 */
public class DependencyProcessor extends AbstractAttributeModifierAttrProcessor {

    public DependencyProcessor() {
        super(EntityModuleDialect.AttributeNames.DEPENDENCIES.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> processedAttributes = new HashMap<>();

        Configuration configuration = arguments.getConfiguration();
        String attributeValue = element.getAttributeValue(attributeName);
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        IStandardExpression expression = parser.parseExpression(configuration, arguments, attributeValue);
        Object parseResult = expression.execute(configuration, arguments);
        if (parseResult == null) {
            return processedAttributes;
        }
        if (!(parseResult instanceof Map)) {
            throw new IllegalArgumentException(
                    "Expect argument of type Map<String, Qualifiers> but was given" + parseResult.getClass());
        }
        Map<String, Qualifiers> parsedAttributeValue = (Map<String, Qualifiers>) parseResult;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String print = objectMapper.writeValueAsString(parsedAttributeValue);
            processedAttributes.put("data-dependson", print);
        } catch (JsonProcessingException e) {
            // do nothing
        }
        return processedAttributes;
    }

    @Override
    protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return true;
    }

    @Override
    protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String attributeName) {
        return true;
    }

    /**
     * Sets the precedence for this processor to execute before other attribute processors.
     * This guarantees that we get our map back.
     *
     * @return the precedence
     */
    @Override
    public int getPrecedence() {
        return 701;
    }
}

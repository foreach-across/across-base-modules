package com.foreach.across.modules.entity.views.thymeleaf;

import org.apache.commons.lang3.StringUtils;
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
 * Splits its value into multiple attributes and sets them on the element.
 * Attributes are comma-separated and attribute/value-pairs are separated with the equals ('=') sign.
 *
 * This will always be the last processor in the chain, to avoid
 *
 * @author niels
 * @since 6/02/2015
 */
public class MultipleAttributeProcessor extends AbstractAttributeModifierAttrProcessor {

    public MultipleAttributeProcessor() {
        super(EntityModuleDialect.AttributeNames.ATTRIBUTES.getName());
    }


    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> processedAttributes = new HashMap<>();

        Configuration configuration = arguments.getConfiguration();
        String attributeValue = element.getAttributeValue(attributeName);
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        IStandardExpression expression = parser.parseExpression(configuration, arguments, attributeValue);
        String parsedAttributeValue = (String) expression.execute(configuration, arguments);
        if (parsedAttributeValue != null) {
            for (String attribute : parsedAttributeValue.split(",")) {
                if (StringUtils.isBlank(attribute)) {
                    // empty can be ignored
                    continue;
                }
                String[] parts = attribute.split("=");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid attribute: expect name=value, but was supplied: \"" + attribute + "\"");
                }
                IStandardExpression parseExpression = parser.parseExpression(configuration, arguments, parts[1]);
                processedAttributes.put(parts[0], parseExpression.execute(configuration, arguments).toString());
            }
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
     * Sets the precedence for this processor to execute as the final processor.
     * This makes sure that all expressions have been parsed before reaching this process.
     *
     * @return Integer.MAX_VALUE
     */
    @Override
    public int getPrecedence() {
        return Integer.MAX_VALUE;
    }
}

package com.foreach.across.modules.entity.views.thymeleaf;

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
 * <p/>
 * This will always be the last processor in the chain, to avoid
 *
 * @author niels
 * @since 6/02/2015
 */
public class MultipleAttributeProcessor extends AbstractAttributeModifierAttrProcessor
{

	public MultipleAttributeProcessor() {
		super( EntityModuleDialect.AttributeNames.ATTRIBUTES.getName() );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, String> getModifiedAttributeValues( Arguments arguments,
	                                                          Element element,
	                                                          String attributeName ) {
		Map<String, String> processedAttributes = new HashMap<>();

		Configuration configuration = arguments.getConfiguration();
		String attributeValue = element.getAttributeValue( attributeName );
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser( configuration );
		IStandardExpression expression = parser.parseExpression( configuration, arguments, attributeValue );
		Object parseResult = expression.execute( configuration, arguments );
		if ( !(parseResult instanceof Map)) {
			throw new IllegalArgumentException(
					"Expect argument of type Map<String, String> but was given" + parseResult.getClass() );
		}
		Map<String, String> parsedAttributeValue = (Map<String, String>) parseResult;
		for ( Map.Entry<String, String> entry : parsedAttributeValue.entrySet() ) {
			IStandardExpression parseExpression = parser.parseExpression( configuration, arguments, entry.getValue() );
			processedAttributes.put( entry.getKey(), parseExpression.execute( configuration, arguments ).toString() );
		}
		return processedAttributes;
	}

	@Override
	protected ModificationType getModificationType( Arguments arguments,
	                                                Element element,
	                                                String attributeName,
	                                                String newAttributeName ) {
		return ModificationType.SUBSTITUTION;
	}

	@Override
	protected boolean removeAttributeIfEmpty( Arguments arguments,
	                                          Element element,
	                                          String attributeName,
	                                          String newAttributeName ) {
		return true;
	}

	@Override
	protected boolean recomputeProcessorsAfterExecution( Arguments arguments, Element element, String attributeName ) {
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
		return 700;
	}
}

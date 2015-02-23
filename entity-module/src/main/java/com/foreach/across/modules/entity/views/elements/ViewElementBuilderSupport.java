package com.foreach.across.modules.entity.views.elements;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderSupport;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewElementBuilderSupport<T extends ViewElementSupport> implements ViewElementBuilder<T>
{
	private final Class<T> builderClass;

	private String name, label, labelCode, customTemplate;
	private boolean field;

	private EntityMessageCodeResolver messageCodeResolver;
	private ValuePrinter valuePrinter;

	private Map<String, String> attributes = new HashMap<>(  );

	protected ViewElementBuilderSupport( Class<T> builderClass ) {
		this.builderClass = builderClass;
	}

	public EntityMessageCodeResolver getMessageCodeResolver() {
		return messageCodeResolver;
	}

	@Override
	public void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	public ValuePrinter getValuePrinter() {
		return valuePrinter;
	}

	public void setValuePrinter( ValuePrinter valuePrinter ) {
		this.valuePrinter = valuePrinter;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public String getLabelCode() {
		return labelCode;
	}

	public void setLabelCode( String labelCode ) {
		this.labelCode = labelCode;
	}

	public String getCustomTemplate() {
		return customTemplate;
	}

	public void setCustomTemplate( String customTemplate ) {
		this.customTemplate = customTemplate;
	}

	public boolean isField() {
		return field;
	}

	public void setField( boolean field ) {
		this.field = field;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes( Map<String, String> attributes ) {
		this.attributes = attributes;
	}

	protected String resolve( String code, String defaultMessage ) {
		if ( messageCodeResolver != null && code != null ) {
			return messageCodeResolver.getMessageWithFallback( code, defaultMessage );
		}

		return defaultMessage;
	}

	@Override
	public T createViewElement( ViewElementBuilderContext builderContext ) {
		T element = newInstance();
		BeanUtils.copyProperties( this, element, "label" );

		element.setLabel( resolve( getLabelCode(), getLabel() ) );

		buildCustomProperties( element );

		return element;
	}

	protected void buildCustomProperties( T element ) {
	}

	protected T newInstance() {
		try {
			return builderClass.newInstance();
		}
		catch ( IllegalAccessException | InstantiationException iae ) {
			throw new RuntimeException(
					getClass().getSimpleName() + " requires the template to have a parameterless constructor", iae
			);
		}
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof FormElementBuilderSupport ) ) {
			return false;
		}

		ViewElementBuilderSupport that = (ViewElementBuilderSupport) o;

		return Objects.equals( name, that.name )
				&& Objects.equals( customTemplate, that.customTemplate )
				&& Objects.equals( label, that.label )
				&& Objects.equals( labelCode, that.labelCode )
				&& Objects.equals( field, that.field )
				&& Objects.equals( messageCodeResolver, that.messageCodeResolver )
				&& Objects.equals( valuePrinter, that.valuePrinter );
	}

	@Override
	public int hashCode() {
		return Objects.hash( name, customTemplate, label, labelCode, field, messageCodeResolver, valuePrinter );
	}
}

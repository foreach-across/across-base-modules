package com.foreach.across.modules.entity.views.thymeleaf;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author niels
 * @since 6/02/2015
 */
public class EntityModuleDialect extends AbstractDialect {

    public enum AttributeNames {
        ATTRIBUTES("attributes");

        private String name;

        private AttributeNames( String name ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public String getPrefix() {
        return "em";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        Set<IProcessor> processors = new HashSet<>();
        processors.add(new MultipleAttributeProcessor());
        return processors;
    }
}

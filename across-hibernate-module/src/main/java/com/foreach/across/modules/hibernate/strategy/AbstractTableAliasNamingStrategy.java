package com.foreach.across.modules.hibernate.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTableAliasNamingStrategy extends PhysicalNamingStrategyStandardImpl
{
	private final static Map<Class, Map<String, String>> REGISTERED_ALIAS_MAPS = new HashMap<>();

	private final Map<String, String> aliasMap;

	protected AbstractTableAliasNamingStrategy() {
		aliasMap = REGISTERED_ALIAS_MAPS.get( getClass() );
	}

	@Override
	public Identifier toPhysicalTableName( Identifier name, JdbcEnvironment context ) {
		return new Identifier( alias( name.getText() ), name.isQuoted() );
	}

	private String alias( String tableName ) {
		if ( aliasMap.containsKey( tableName ) ) {
			return aliasMap.get( tableName );
		}

		return tableName;
	}

	public static void registerTableAliases( Class strategyClass, Map<String, String> tableAliases ) {
		REGISTERED_ALIAS_MAPS.put( strategyClass, tableAliases );
	}
}

package com.foreach.across.modules.hibernate.strategy;

import org.hibernate.cfg.ImprovedNamingStrategy;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTableAliasNamingStrategy extends ImprovedNamingStrategy
{
	private final static Map<Class, Map<String, String>> REGISTERED_ALIAS_MAPS = new HashMap<>();

	private final Map<String, String> aliasMap;

	protected AbstractTableAliasNamingStrategy() {
		aliasMap = REGISTERED_ALIAS_MAPS.get( getClass() );
	}

	@Override
	public String classToTableName( String className ) {
		return alias( super.classToTableName( className ) );
	}

	@Override
	public String tableName( String tableName ) {
		return alias( super.tableName( tableName ) );
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

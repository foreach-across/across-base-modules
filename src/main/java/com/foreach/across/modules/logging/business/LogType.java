package com.foreach.across.modules.logging.business;

public enum LogType
{
	FUNCTIONAL,
	TECHNICAL;

	public interface Constants
	{
		String FUNCTIONAL = "1";
		String TECHNICAL = "2";
	}
}

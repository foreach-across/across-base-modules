# Logging module

This module enables different kinds of logging for your Across project.

## Automatic logging

The RequestLogFilter will log all web requests with their attributes, including duration.

## Manual logging

The logging service provides a generic mechanism to log custom events. Depending on your configuration, these will be logged to file and/or database. The LoggingService is the main utility to log this information.

### Configuration options

The following configuration options are available for both functional and technical logging, seperately:

 - Database logging strategy: Which strategy should be used for logging to the database ( `loggingModule.functionalDBStrategy` and `loggingModule.technicalDBStrategy`). The available options here are:
     - "NONE" for no database logging (this is the default, and should be used when the AcrossHibernateModule is not enabled)
     - "SINGLE_TABLE" for logging to the database without periodic backups
     - "ROLLING" for logging to the database with periodic backups. This requires extra config options.
 - Rolling database schedule: The time at which we should backup old data and clear it from the main table. This is a CRON expression, including seconds. By default, a backup is ran every month. `loggingModule.functionalDBRollingSchedule` and `loggingModule.technicalDBRollingSchedule`.
 - Rolling database timespan: The amount of time covered by one table (except for the rolling table). This is a ISO 8601 Duration expression using only PnYnMnD components. `loggingModule.functionalDBRollingTimeSpan` and `loggingModule.technicalDBRollingTimeSpan`
 - File logging strategy: Which strategy should be used for logging to the filesystem. `loggingModule.functionalFileStrategy` and `loggingModule.technicalFileStrategy`.
     - "NONE" for no file logging
     - "LOGBACK" for logging using a logback-configuration (this is the default).

If you'd like to log functional events, you'll have to configure a logger in logback for this. The name of this logger can be set with the option `loggingModule.functionalFileLogger`. By default this is "functional-logger".

## Database log reading

If you have configured database logging you can retrieve your database-logged events using the LogDBReaderService.

## Dependencies
### Required

The logging module requires the Across Web Module in order to log web requests.

### Optional

Optional dependencies include Across Hibernate to log to the database.
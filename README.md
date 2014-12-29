# Logging module

This module enables different kinds of logging for your Across project.

## Automatic logging

The RequestLogFilter will log all web requests with their attributes, including duration.

## Manual logging

The logging service provides a generic mechanism to log custom events. Depending on your configuration, these will be logged to file and/or database. 

## Dependencies
### Required

The logging module requires the Across Web Module in order to log web requests.

### Optional

Optional dependencies include Across Hibernate to log to the database.
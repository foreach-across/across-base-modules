# ApplicationInfo module

The ApplicationInfo module configures an AcrossApplicationInfo bean that provides basic information of the running application.  
It also provides a debug web controller to view the running application information.

## Dependency

```xml
<dependencies>
  <dependency>
    <groupId>across-standard-modules</groupId>
    <artifactId>application-info</artifactId>
    <version>1.1.0</version>
  </dependency>
</dependencies>
```

## Module dependencies

| Module         | Type      | Description                                                              |
| ---            | ---       | ---                                                                      |
| DebugWebModule | extension | Registers an application info overview as dashboard page for debug web. |

## Configuration #
### Application info ###
The purpose of the ApplicationInfoModule is to be able to uniquely identify a running application instance.  This is usually done through the 
combination of 3 parameters:

 * name of the application (eg. My Application)
 * name of the environment  (eg. Test)
 * name of the infrastructure hosting the application - usually a hostname (eg. my-server.lan)
 
Every parameter can be configured manually.

### Quickstart ###
```java
ApplicationInfoModule applicationInfoModule = new ApplicationInfoModule();
applicationInfoModule.setProperty( ApplicationInfoModuleSettings.APPLICATION_ID, "demo-webapp");
applicationInfoModule.setProperty( ApplicationInfoModuleSettings.APPLICATION_NAME, "Across Demo Webapplication");
applicationInfoModule.setProperty( ApplicationInfoModuleSettings.ENVIRONMENT_ID, "demo");
applicationInfoModule.setProperty( ApplicationInfoModuleSettings.ENVIRONMENT_NAME, "Demo environment");

applicationInfoModule.setProperty( ApplicationInfoModuleSettings.BUILD_ID, "demo-webapp");
applicationInfoModule.setProperty( ApplicationInfoModuleSettings.BUILD_DATE, new Date());

context.addModule( applicationInfoModule );
```

### Common properties ###
In many cases you would want to configure the parameters using external properties, set for example by a buildserver.
The following properties are available:

| Property                        | Description                                                        |
| ---                             | ---                                                                |
| applicationInfo.applicationId   | Internal id of the application.                                    |
| applicationInfo.applicationName | Descriptive name of the application.                               |
| applicationInfo.environmentId   | Internal id of the environment the application is running in.      |
| applicationInfo.environmentName | Descriptive name of the environment the application is running in. |
| applicationInfo.hostName        | Name for the infrastructure hosting the application.               |
| applicationInfo.buildId         | Id of the running build of the application.                        |
| applicationInfo.buildDate       | Date when this build was created.                                  |

_Timestamps in property files should be provided in yyyy-MM-dd HH:mm:ss format._
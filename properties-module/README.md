# Properties module

The Properties module provides facilities to extend existing entities with custom properties, with optional revision tracking.

## Dependency

```xml
<dependencies>
  <dependency>
    <groupId>across-standard-modules</groupId>
    <artifactId>properties-module</artifactId>
    <version>1.1.0</version>
  </dependency>
</dependencies>
```

## Configuration
### Installers
In order to extend existing entities with properties, the tables for these properties have to be installed. 
The client application can do this by registering installers that extend from EntityPropertiesInstaller or RevisionBasedEntityPropertiesInstaller.

### Repositories
The Properties module offers a base implementation of EntityPropertiesRepository that can be extended with a typed implementation.

### Services
The client application should then extend either AbstractEntityPropertiesService or AbstractRevisionBasedEntityPropertiesService. 
These services can then be wired into the entity services and used as described below.

### Registries
The entities with properties should have a registry class extending from EntityPropertiesRegistry.
Registries are classes that contain a map for the properties added to a specific entity. 
For example, a User entity class can have a corresponding UserPropertiesRegistry class.

### Properties configuration
The properties for a specific entity are configured in a configuration class extending from AbstractEntityPropertiesConfiguration.
This class will specify the entity class, table name, the identifying column name and wire all classes extended as described above.

### Property registration
The client application should register the properties during application startup as follows:
```java
@PostConstruct
protected void registerProperties() {
    userPropertiesRegistry.register( currentModule, "registration_code", String.class );
}
```
This snippet should be contained in a configuration class and autowire the current module and the specific EntityPropertiesRegistry for which this configuration is happening:
```java
@Autowired
@Module(AcrossModule.CURRENT_MODULE)
private AcrossModule currentModule;

@Autowired
private UserPropertiesRegistry userPropertiesRegistry;
```

This registry contains the definition of the property key to its implementation class and optionally a default value. 
See EntityPropertiesRegistry for all available options.

#### Defaults
The Properties module supports simple types and parametrized types for registries. When registering a mapping, the client application can supply a default value.
These defaults should not be changed by client code. In order to enforce this, the Properties module uses PropertyFactory from the Foreach common libraries as a way to construct these defaults.
TypeDescriptors (from Spring) are used to describe the parameter types.

Examples:
```java
// A SingletonPropertyFactory used for an enum:
userPropertiesRegistry.register( currentModule, "enum_property", AnEnum.class,
                                 SingletonPropertyFactory.<String, AnEnum>forValue( AnEnum.SOME_VALUE ) );
// An anonymous implementation of PropertyFactory for a Set of Foo (which is an entity):
userPropertiesRegistry.register( currentModule,
                                 "userFoo",
                                 TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( Foo.class ) ),
                                 new PropertyFactory<String, Object>()
                                 {
                                     @Override
                                     public Object create( PropertyTypeRegistry registry, String propertyKey ) {
                                         return new HashSet<Foo>();
                                     }
                                 }
);
```

## Usage
If the Properties module is configured correctly and the services are wired, the properties of a specific entity can be read and set as follow:
```java
Foo foo = new Foo( "aFoo" );
UserProperties userProperties = userPropertiesService.getProperties( userId );
Set<Foo> foos = userProperties.getValue( "userFoo" );
foos.add( foo );
userPropertiesService.saveProperties( userProperties );
```
_Note that properties should always be saved after updating._
# jdbcTemplate-runtime-update
### Changing DataSources and JdbcTemplate at runtime with Spring Boot

During the execution of a project to connect multiple databases, I ran into the problem of dynamically changing the source at runtime.
The main idea is that the user must define the parameters for the connection himself.  
There are two files presented here. The "ConfigClass.java" file specifies two database connections that use the "DataSourceBuilder".

    DataSourceBuilder builder = DataSourceBuilder.create();
    builder.build();

In my case, I need to update all the data required to connect:

    builder.driverClassName(someEntity.getDriver_name());
    builder.url(someEntity.getUrl()+"?useUnicode=true&serverTimezone=UTC");
    builder.username(someEntity.getUsername());
    builder.password(someEntity.getPassword());
	
someEntity - an object of class of type Entity, but here you can insert data from where you want

we need a two major annotation  
1 - @Lazy  
2 - @Scope  

(1) Spring allows you to lazily initialize your application. When lazy initialization is enabled, beans are created as needed, not at application startup time.
But we use that only for several beans, not for all app. Do it with @Lazy

(2) When used as a method-level annotation in conjunction with @Bean, @Scope indicates the name of a scope to use for the instance returned from the method.
[@Scope](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Scope.html "")

During startup, @Bean will be processed automatically, and since the data is empty, an error will occur. To do this, all code is enclosed in "try and catch" with ignoring the NullPointerException

### As a result, for a class with configuration  
1 - for DataSource  

    @Lazy
    @Bean(name = "target")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataSource dataSourceTarget() {
        DataSourceBuilder builder = DataSourceBuilder.create();
	try {
	    builder.driverClassName(	<--- desired parameters
	    builder.url( 	  			<---
	    builder.username( 			<---
	    builder.password( 			<---
        } catch (NullPointerException ignored) {}
	return builder.build();
    }

2 - for JdbcTemplate  

	@Lazy
	@Bean(name = "jdbcTemplateTarget")
    public JdbcTemplate jdbcTemplateTarget(@Qualifier("target") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
	
Next, go to the repository class  
ApplicationContext is the main interface in a Spring application that provides application configuration information. It can be rebooted if necessary, and this is our way.
The fields are defined as "private final" because we need to dependency injection via constructor  
[dependency-injection](https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/using-boot-spring-beans-and-dependency-injection.html "")  

### As a result, for the repository class  

    @Repository
    public class SourceRepository {
        private final JdbcTemplate jdbcTemplate;
        private final ApplicationContext context;

    @Autowired
    public SourceRepository(@Qualifier("jdbcTemplateSource") JdbcTemplate jdbcTemplate, ApplicationContext context) {
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;
    }
	
And we need a method that will initiate a context update with the data we need.  
(change to connect before calling this method)  
	
    public void updateJdbcContext() {
        DataSource dataSource = (DataSource) context.getBean("source");
        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplateSource");
		
		jdbcTemplate.setDataSource(dataSource);
	}
	
If the program does not plan to terminate between the execution of actions, the necessary data can be loaded into the class through a static field
Otherwise, you can write data to the internal database and constantly call read when changing configurations

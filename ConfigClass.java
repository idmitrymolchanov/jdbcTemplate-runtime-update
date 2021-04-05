package psychotest.config.profile;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
@Profile("local")
public class ConfigLocal {
    public static DatasourceEntity datasourceEntity;

    private final DatasourceConnectionService datasourceConnectionService;

    @Autowired
    public ConfigLocal(DatasourceConnectionService datasourceConnectionService) {
        this.datasourceConnectionService = datasourceConnectionService;
    }

    @Bean(name = "target")
    @ConfigurationProperties(prefix = "spring.target")
    public DataSource dataSourceTarget() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcTemplateTarget")
    public JdbcTemplate jdbcTemplateTarget(@Qualifier("target") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Lazy
    @Bean(name = "source")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataSource dataSourceSource() {

        DatasourceEntityConnection entityConnection = datasourceConnectionService.getConnById(5L);
        List<DatasourceEntity> entityList = datasourceConnectionService.getSourceTargetConfigs(entityConnection);

        if (datasourceEntity == null) {
            datasourceEntity = entityList.get(0);
            System.out.println("not null");
        }
        else {
            datasourceEntity = entityList.get(1);
            System.out.println("NULLLLLLLLLLLLLL");
        }


        DataSourceBuilder dsBuilder = DataSourceBuilder.create();
        dsBuilder.driverClassName(datasourceEntity.getDriver_name());

        dsBuilder.url(datasourceEntity.getUrl()+"?useUnicode=true&serverTimezone=UTC");
        dsBuilder.username(datasourceEntity.getUsername());
        dsBuilder.password(datasourceEntity.getPassword());

        return dsBuilder.build();
    }

    @Lazy
    @Bean(name = "jdbcTemplateSource")
    public JdbcTemplate jdbcTemplateSource(@Qualifier("source") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean(name = "userdb")
    @ConfigurationProperties(prefix = "spring.userdb")
    public DataSource dataSourceUser() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcTemplateUser")
    public JdbcTemplate jdbcTemplateUser(@Qualifier("userdb") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
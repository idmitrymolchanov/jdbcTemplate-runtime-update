package psychotest.config.profile;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import psychotest.entity.DatasourceEntity;

@Configuration
@Profile("local")
public class ConfigLocal {
    public static DatasourceEntity datasourceEntitySource;
    public static DatasourceEntity datasourceEntityTarget;

    @Lazy
    @Bean(name = "target")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataSource dataSourceTarget() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        try {
            builder.driverClassName(datasourceEntityTarget.getDriver_name());
            builder.url(datasourceEntityTarget.getUrl()+"?useUnicode=true&serverTimezone=UTC");
            builder.username(datasourceEntityTarget.getUsername());
            builder.password(datasourceEntityTarget.getPassword());
        } catch (NullPointerException ignored) {}
        return builder.build();
    }

    @Lazy
    @Bean(name = "jdbcTemplateTarget")
    public JdbcTemplate jdbcTemplateTarget(@Qualifier("target") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Lazy
    @Bean(name = "source")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataSource dataSourceSource() {

        DataSourceBuilder builder = DataSourceBuilder.create();
        try {
            builder.driverClassName(datasourceEntitySource.getDriver_name());
            builder.url(datasourceEntitySource.getUrl() + "?useUnicode=true&serverTimezone=UTC");
            builder.username(datasourceEntitySource.getUsername());
            builder.password(datasourceEntitySource.getPassword());
        } catch (NullPointerException ignored) {}

        return builder.build();
    }

    @Lazy
    @Bean(name = "jdbcTemplateSource")
    public JdbcTemplate jdbcTemplateSource(@Qualifier("source") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
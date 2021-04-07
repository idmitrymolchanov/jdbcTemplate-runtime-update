package psychotest.repository.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static psychotest.repository.base.SourceDAOImpl.getStrings;

@Repository
public class SourceRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext context;

    @Autowired
    public SourceRepository(@Qualifier("jdbcTemplateSource") JdbcTemplate jdbcTemplate, ApplicationContext context) {
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;
    }


    public void updateJdbcContext() {
        DataSource dataSource = (DataSource) context.getBean("source");
        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplateSource");
		
        jdbcTemplate.setDataSource(dataSource);
    }


    public List<String> getNameTables() {
		
        updateJdbcContext(); // <--- refresh

        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        DatabaseMetaData metaData;
        List<String> tablesNames = new ArrayList<>();

        return getStrings(connection, tablesNames);
    }

    static List<String> getStrings(Connection connection, List<String> tablesNames) {
        DatabaseMetaData metaData;
        try {
            metaData = connection.getMetaData();
            String[] types = {"TABLE"};
            metaData.getConnection();
            //Retrieving the columns in the database
            ResultSet tables = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%", types);
            while (tables.next())
                tablesNames.add(tables.getString("TABLE_NAME"));
        } catch (SQLException ignored)
        {
            return Collections.EMPTY_LIST;
        }

        return tablesNames;
    }
}

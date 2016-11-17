package de.ahus1.example.jdbi;

import lombok.Builder;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public abstract class _00_AbstractJdbiBaseTest {

    protected DBI dbi;

    @Before
    public void shouldSetup() {
        dbi = new DBI("jdbc:oracle:thin:@localhost:1521:XE", "test", "test");
        dbi.withHandle(handle -> {
            handle.execute("CREATE TABLE sighting (id int, name varchar(200), status varchar(200), CONSTRAINT pk_id PRIMARY KEY (id))");
            handle.execute("INSERT INTO sighting (id, name, status) VALUES (1, 'Klingon Warbird', 'RED_ALERT')");
            return null;
        });
    }

    @After
    public void tearDown() throws Exception {
        dbi.withHandle(handle -> {
            handle.execute("DROP TABLE sighting");
            return null;
        });
    }

}

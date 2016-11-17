package de.ahus1.example.jdbi;

import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.Sighting;
import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.SightingResultMapper;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _30_CrudJbdiInsertWithSequence extends _00_AbstractJdbiBaseTest {

    @Before
    public void shouldSetupSequence() {
        dbi = new DBI("jdbc:oracle:thin:@localhost:1521:XE", "test", "test");
        dbi.withHandle(handle -> {
            handle.execute("CREATE SEQUENCE seq INCREMENT BY 1 START WITH 100");
            return null;
        });
    }

    @After
    public void tearDownSequence() throws Exception {
        dbi.withHandle(handle -> {
            handle.execute("DROP SEQUENCE seq");
            return null;
        });
    }

    /**
     * Oracle needs to be queried by index and not id (like
     * {@link org.skife.jdbi.v2.sqlobject.FigureItOutResultSetMapper} does).
     */
    public static class OracleGeneratedKeyMapper implements ResultSetMapper<Long> {
        @Override
        public Long map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return r.getLong(1);
        }
    }

    public interface SightingRepsitory extends _20_CrudJbdiSelectSingleTest.SightingRepsitory {
        @SqlUpdate("INSERT INTO sighting (id, name) values (:s.id, :s.name)")
        void createNewSightingWithKnownId(@BindBean("s") Sighting sighting);

        @SqlUpdate("INSERT INTO sighting (id, name) VALUES (seq.nextval, :s.name)")
        @GetGeneratedKeys(columnName = "id", value = OracleGeneratedKeyMapper.class)
        int createNewSightingAutogeneratedId(@BindBean("s") Sighting sighting);
    }

    @Test
    public void shouldInsertWithKnownId() {
        dbi.registerMapper(new SightingResultMapper());
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        sightingRepsitory.createNewSightingWithKnownId(Sighting.builder()
                .id(2).name("Enterprise")
                .build());
        List<Sighting> sightings = sightingRepsitory.findByName("Enterprise");
        Assertions.assertThat(sightings)
                .containsExactly(Sighting.builder()
                        .id(2).name("Enterprise")
                        .build());
    }

    @Test
    public void shouldInsertWithAutogeneratedId() {
        dbi.registerMapper(new SightingResultMapper());
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        int id = sightingRepsitory.createNewSightingAutogeneratedId(Sighting.builder()
                .name("Enterprise")
                .build());
        List<Sighting> sightings = sightingRepsitory.findByName("Enterprise");
        Assertions.assertThat(sightings)
                .containsExactly(Sighting.builder()
                        .id(id).name("Enterprise")
                        .build());
    }

}
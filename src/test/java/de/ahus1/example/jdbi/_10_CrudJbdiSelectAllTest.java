package de.ahus1.example.jdbi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.MaxRows;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _10_CrudJbdiSelectAllTest extends _00_AbstractJdbiBaseTest {

    @RegisterMapper(SightingResultMapper.class)
    public interface SightingRepsitory {
        @SqlQuery("SELECT id, name FROM sighting")
        List<Sighting> findAll();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sighting {
        private int id;
        private String name;
    }

    public static class SightingResultMapper implements ResultSetMapper<Sighting> {
        @Override
        public Sighting map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return Sighting.builder()
                    .id(r.getInt("id"))
                    .name(r.getString("name"))
                    .build();
        }
    }

    @Test
    public void shouldFindAllRows() {
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        List<Sighting> sightings = sightingRepsitory.findAll();
        Assertions.assertThat(sightings)
                .containsExactly(Sighting.builder().id(1).name("Klingon Warbird").build());
    }

}

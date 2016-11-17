package de.ahus1.example.jdbi;

import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.Sighting;
import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.SightingResultMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _20_CrudJbdiSelectSingleTest extends _00_AbstractJdbiBaseTest {

    public interface SightingRepsitory extends _10_CrudJbdiSelectAllTest.SightingRepsitory{
        @SqlQuery("SELECT id, name FROM sighting WHERE name = :name")
        List<Sighting> findByName(@Bind("name") String name);
    }

    @Test
    public void shouldFindByName() {
        dbi.registerMapper(new SightingResultMapper());
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        List<Sighting> sightings = sightingRepsitory.findByName("Klingon Warbird");
        Assertions.assertThat(sightings)
                .containsExactly(Sighting.builder().id(1).name("Klingon Warbird").build());
    }

}

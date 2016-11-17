package de.ahus1.example.jdbi;

import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.Sighting;
import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.SightingResultMapper;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.MaxRows;

import java.util.Iterator;
import java.util.List;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _40_CrudJbdiSelectPotentiallyLongResult extends _00_AbstractJdbiBaseTest {

    @Before
    public void shouldSetupAnotherRow() {
        dbi = new DBI("jdbc:oracle:thin:@localhost:1521:XE", "test", "test");
        dbi.withHandle(handle -> {
            handle.execute("INSERT INTO sighting (id, name) VALUES (3, 'Event Horizon')");
            return null;
        });
    }

    public interface SightingRepsitory extends _10_CrudJbdiSelectAllTest.SightingRepsitory {
        @SqlQuery("SELECT id, name FROM sighting ORDER BY id")
        @MaxRows(1)
        List<Sighting> findAllLimitedToOne();

        @SqlQuery("SELECT id, name FROM sighting ORDER BY id")
        Iterator<Sighting> findAllAsIterator();
    }

    @Test
    public void shouldFindWithALimit() {
        dbi.registerMapper(new SightingResultMapper());
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        List<Sighting> sightings = sightingRepsitory.findAllLimitedToOne();
        Assertions.assertThat(sightings)
                .containsExactly(Sighting.builder().id(1).name("Klingon Warbird").build());
    }

    @Test
    public void shouldReturnAsAnIterator() {
        dbi.registerMapper(new SightingResultMapper());
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        Iterator<Sighting> sightings = sightingRepsitory.findAllAsIterator();
        Assertions.assertThat(sightings)
                .containsExactly(
                        Sighting.builder().id(1).name("Klingon Warbird").build(),
                        Sighting.builder().id(3).name("Event Horizon").build()
                );
    }

}

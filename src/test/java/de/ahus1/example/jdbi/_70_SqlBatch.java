package de.ahus1.example.jdbi;

import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.Sighting;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.MaxRows;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _70_SqlBatch extends _00_AbstractJdbiBaseTest {

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

    public interface SightingRepsitory {
        @SqlBatch("INSERT INTO sighting (id, name) VALUES (seq.nextval, :name)")
        @BatchChunkSize(1000)
        /* we could pass an Iterator to deliver Sightings just in time - this could save some heap space */
        void createNewSightings(@BindBean List<Sighting> sighting);

        @SqlQuery("SELECT id, name FROM sighting")
        @MaxRows(100)
        @MapResultAsBean
        Iterator<Sighting> selectSomeSightings();
    }

    @Test
    public void shouldInsertSeveralAndThenSelectSome() {
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        List<Sighting> sightings = new ArrayList<>();
        for (Integer i = 0; i < 2000; ++i) {
            sightings.add(Sighting.builder().name(Integer.toString(i)).build());
        }
        sightingRepsitory.createNewSightings(sightings);

        Iterator<Sighting> someSightings = sightingRepsitory.selectSomeSightings();
        while(someSightings.hasNext()) {
            Sighting s = someSightings.next();
            System.out.println("sighting: " +  s);
        }
    }

}
package de.ahus1.example.jdbi;

import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.Sighting;
import de.ahus1.example.jdbi._10_CrudJbdiSelectAllTest.SightingResultMapper;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.QueryTimeOut;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _50_CrudJbdiQueryTimeout extends _00_AbstractJdbiBaseTest {

    @After
    public void tearDown() throws Exception {
        dbi.withHandle(handle -> {
            handle.execute("LOCK TABLE sighting IN EXCLUSIVE MODE");
            handle.execute("DROP TABLE sighting");
            return null;
        });
    }


    public interface SightingRepsitory extends _10_CrudJbdiSelectAllTest.SightingRepsitory,
            Transactional<SightingRepsitory> {
        @SqlQuery("SELECT id, name FROM sighting ORDER BY id FOR UPDATE")
        @QueryTimeOut(2)
        @Transaction
        Iterator<Sighting> findWithQueryTimeout();
    }

    @Test
    @Ignore("doesn't work well")
    public void shouldHaveAQueryTimeout() throws InterruptedException {
        dbi.registerMapper(new SightingResultMapper());
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        CountDownLatch latch = new CountDownLatch(1);


        (new Thread(() -> dbi.onDemand(SightingRepsitory.class).inTransaction((transactional, status) -> {
            transactional.findWithQueryTimeout();
            latch.countDown();
            Thread.sleep(5000);
            return null;
        }))).start();

        latch.await();

        sightingRepsitory.findWithQueryTimeout();
    }

}

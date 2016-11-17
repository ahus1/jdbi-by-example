package de.ahus1.example.jdbi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

/**
 * @author Alexander Schwartz 2016
 */
public class _15_CrudJbdiSelectAllTestWithMapper extends _00_AbstractJdbiBaseTest {

    public interface SightingRepsitory {
        @MapResultAsBean
        @SqlQuery("SELECT id, name, status FROM sighting")
        List<Sighting> findAll();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sighting {
        private int id;
        private String name;
        private Status status;

    }

    public enum Status {
        RED_ALERT,
        FRIEND
    }

    @Test
    public void shouldFindAllRows() {
        SightingRepsitory sightingRepsitory = dbi.onDemand(SightingRepsitory.class);
        List<Sighting> sightings = sightingRepsitory.findAll();
        Assertions.assertThat(sightings)
                .containsExactly(Sighting.builder()
                        .id(1)
                        .name("Klingon Warbird")
                        .status(Status.RED_ALERT)
                        .build());
    }

}

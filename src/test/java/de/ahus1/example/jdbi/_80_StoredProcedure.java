package de.ahus1.example.jdbi;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.OutParameters;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlCall;
import org.skife.jdbi.v2.sqlobject.customizers.OutParameter;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import java.sql.Types;

/**
 * @author Alexander Schwartz (msg systems ag) 2016
 */
public class _80_StoredProcedure extends _00_AbstractJdbiBaseTest {

    @Before
    public void shouldSetupSequence() {
        dbi = new DBI("jdbc:oracle:thin:@localhost:1521:XE", "test", "test");
        dbi.withHandle(handle -> {
            handle.execute("CREATE OR REPLACE\n" +
                    "PROCEDURE calculate_score\n" +
                    "( distance IN NUMBER\n" +
                    ", weight IN NUMBER\n" +
                    ", score OUT NUMBER\n" +
                    ") AS\n" +
                    "BEGIN\n" +
                    "  score \\:= distance * weight;\n" +
                    "END calculate_score;");
            return null;
        });
    }

    @After
    public void tearDownSequence() throws Exception {
        dbi.withHandle(handle -> {
            handle.execute("DROP PROCEDURE calculate_score");
            return null;
        });
    }

    public interface Calculator {
        @SqlCall("{ CALL calculate_score(:distance, :weight, :score) }")
        @OutParameter(name = "score", sqlType = Types.INTEGER)
        OutParameters calculateScore(@Bind("distance") long distance, @Bind("weight") long weight);
    }

    @Test
    public void shouldCallProcedureDirectly() throws InterruptedException {
        Long score = dbi.withHandle(h -> {
            OutParameters ret = h.createCall("CALL calculate_score(:distance, :weight, :score)")
                    .bind("distance", 4)
                    .bind("weight", 2)
                    .registerOutParameter("score", Types.NUMERIC)
                    .invoke();
            return ret.getLong("score");
        });

        Assertions.assertThat(score).isEqualTo(8);
    }

    @Test
    public void shouldCallProcedureViaInterface() throws InterruptedException {
        Calculator calculator = dbi.onDemand(Calculator.class);
        long score = calculator.calculateScore(4,2).getLong("score");
        Assertions.assertThat(score).isEqualTo(8);
    }

}

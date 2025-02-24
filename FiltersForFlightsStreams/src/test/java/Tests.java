import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class Tests {

    @Test
    // test departing in the future
    void testDepartingInTheFuture() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);

        TestFlight testFlight = new TestFlight(Arrays.asList(
                new PartFlight(threeDaysFromNow, LocalDateTime.now().plusHours(2)),
                new PartFlight(threeDaysFromNow, LocalDateTime.now().plusHours(4))
        ));

        TestFilter filter = new FutureDepartures();
        assertTrue(filter.execute(testFlight));
    }

    @Test
        // test arriving after Departing
    void testArrivingAfterDeparting() {
        TestFlight testFlight = new TestFlight(Arrays.asList(
                new PartFlight(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4)),
                new PartFlight(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1))
        ));
        TestFilter filter = new DepartingBeforeArriving();

        assertFalse(filter.execute(testFlight));
    }

    @Test
        // test time spent on the ground exceeding two hours
    void testHasLongGroundTime() {
        TestFlight testFlight = new TestFlight(Arrays.asList(
                new PartFlight(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new PartFlight(LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6))
        ));
        TestFilter filter = new HasLongGroundTime();
        assertFalse(filter.execute(testFlight));
    }

    @Test
        // test all Filters
    void testFilterFlights() {
        TestFlight testFlight1 = new TestFlight(Arrays.asList(
                new PartFlight(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new PartFlight(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4))
        ));
        TestFlight testFlight2 = new TestFlight(Arrays.asList(
                new PartFlight(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new PartFlight(LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6))
        ));

        FilterFlights filterFlights = new FilterFlights();
        filterFlights.addItem(testFlight1);
        filterFlights.addItem(testFlight2);

        filterFlights.addAllFilters(new FutureDepartures(), new DepartingBeforeArriving(), new HasLongGroundTime());

        List<TestFlight> result = filterFlights.apply();
        assertEquals(1, result.size());
        assertEquals(testFlight1, result.get(0));
    }
}


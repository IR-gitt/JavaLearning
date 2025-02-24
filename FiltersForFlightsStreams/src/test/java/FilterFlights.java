import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FilterFlights class
 * allows to conveniently add chain implementations of filters and methods
 */
public class FilterFlights {

    private List<String> filtersNames;
    private List<TestFlight> listTestFlight;
    private List<TestFilter> listFilters;

    public FilterFlights() {
        init();
    }

    private void init() {
        listFilters = new ArrayList<>();
        listTestFlight = new ArrayList<>();
        filtersNames = new ArrayList<>();
    }

    public List<String> getFiltersNames() {
        return filtersNames;
    }

    public void addFilterName(String filterName) {
        filtersNames.add(filterName);
    }

    public List<String> listNameFilters() {
        return listFilters.stream()
                .map(obj -> obj.getClass().getName()).collect(Collectors.toList());
}

    @Override
    public String toString() {
        return "flights=" + listTestFlight + '\n' +
                "filters=" + listFilters.stream()
                .map(obj -> obj.getClass().getName().trim()).collect(Collectors.toList());
    }

    public void addItem(TestFlight item) {
        listTestFlight.add(item);
    }

    public void addAllFilters(TestFilter... filters) {
        Collections.addAll(listFilters, filters);
    }

    public void addListItem(List <TestFlight> items) {
        listTestFlight.addAll(items);
    }

    public void addFilter(TestFilter filter) {
        listFilters.add(filter);
    }

    public void removeFilter(int index) {
        listFilters.remove(index);
    }

    public List<TestFlight> apply() {

        for (TestFilter filter : listFilters) {
            listTestFlight = listTestFlight.stream().filter(filter::execute)
                    .collect(Collectors.toList());
        }
        return listTestFlight;
    }
}

/**
 * Part of the filter excludes total time spent on the ground exceeding two hours
 */
class HasLongGroundTime implements TestFilter {

    @Override
    public boolean execute(TestFlight testFlight) {
        List<PartFlight> partFlights = testFlight.getSegments();
        boolean isValid = true;
        Duration accumulatedGroundTime = Duration.ZERO;

        for (int i = 1; i < partFlights.size(); i++) {
            PartFlight prevPartFlight = partFlights.get(i - 1);
            PartFlight curPartFlight = partFlights.get(i);

            // Рассчитываем общее время на земле между сегментами (время посадки + время вылета)
            Duration groundTime = Duration.between(prevPartFlight.getArrivalDate(), curPartFlight.getDepartureDate());
            accumulatedGroundTime = accumulatedGroundTime.plus(groundTime);
        }

        if (accumulatedGroundTime.toHours() > 2) {
            isValid = false;
        }

        return isValid;
    }
}

/**
 * Part of the filter that excludes departures until the current moment in time
 */
class DepartingBeforeArriving implements TestFilter {
    @Override
    public boolean execute(TestFlight testFlight) {
        return testFlight.getSegments().stream()
                // Получаем сегменты отправляющиеся раньше даты прилета
                .allMatch(partFlight -> partFlight.getArrivalDate()
                        .isAfter(partFlight.getDepartureDate()));
    }
}

/**
 * Part of the filter that excludes departures until the current moment in time
 */
class FutureDepartures implements TestFilter {

    @Override
    public boolean execute(TestFlight testFlight) {
        return testFlight.getSegments().stream()
                // Сегменты отправляются после текущего времени
                .allMatch(partFlight -> partFlight.getDepartureDate()
                        .isAfter(LocalDateTime.now()));
    }
}

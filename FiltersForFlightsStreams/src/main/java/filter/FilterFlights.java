package filter;

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
    private List<Flight> listFlight;
    private List<Filter> listFilters;

    public FilterFlights() {
        init();
    }

    private void init() {
        listFilters = new ArrayList<>();
        listFlight = new ArrayList<>();
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
        return "flights=" + listFlight + '\n' +
                "filters=" + listFilters.stream()
                .map(obj -> obj.getClass().getName().trim()).collect(Collectors.toList());
    }

    public void addItem(Flight item) {
        listFlight.add(item);
    }

    public void addAllFilters(Filter... filters) {
        Collections.addAll(listFilters, filters);
    }

    public void addListItem(List <Flight> items) {
        listFlight.addAll(items);
    }

    public void addFilter(Filter filter) {
        listFilters.add(filter);
    }

    public void removeFilter(int index) {
        listFilters.remove(index);
    }

    public List<Flight> apply() {
        for (Filter filter : listFilters) {
            listFlight = listFlight.stream().filter(filter::execute)
                    .collect(Collectors.toList());
        }
        return listFlight;
    }
}

/**
 * Part of the filter excludes total time spent on the ground exceeding two hours
 */
class HasLongGroundTime implements Filter {

    @Override
    public boolean execute(Flight flight) {
        List<PartFlight> partFlights = flight.getSegments();
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
class DepartingBeforeArriving implements Filter {
    @Override
    public boolean execute(Flight flight) {
        return flight.getSegments().stream()
                // Получаем сегменты отправляющиеся раньше даты прилета
                .allMatch(partFlight -> partFlight.getArrivalDate()
                        .isAfter(partFlight.getDepartureDate()));
    }
}

/**
 * Part of the filter that excludes departures until the current moment in time
 */
class FutureDepartures implements Filter {

    @Override
    public boolean execute(Flight flight) {
        return flight.getSegments().stream()
                // Сегменты отправляются после текущего времени
                .allMatch(partFlight -> partFlight.getDepartureDate()
                        .isAfter(LocalDateTime.now()));
    }
}

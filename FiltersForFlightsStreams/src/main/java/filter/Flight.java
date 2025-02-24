package filter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


class Flight {
    private final List<PartFlight> partFlights;

    Flight(final List<PartFlight> partFlights) {
        this.partFlights = partFlights;
    }

    List<PartFlight> getSegments() {
        return partFlights;
    }

    @Override
    public String toString() {
        return partFlights.stream().map(Object::toString)
            .collect(Collectors.joining(" "));
    }
}


class PartFlight {
    private final LocalDateTime departureDate;

    private final LocalDateTime arrivalDate;

    PartFlight(final LocalDateTime dep, final LocalDateTime arr) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
    }

    LocalDateTime getDepartureDate() {
        return departureDate;
    }

    LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return  "Departure Date: " + departureDate.format(fmt) + " Arrival Date: "  + arrivalDate.format(fmt);
    }
}
package filter;

public class Main {
    public static void main(String[] args) {
        // Создаем фильтр
        FilterFlights filterFlights = new FilterFlights();

        // Добавляем список полетов
        filterFlights.addListItem(FlightsList.createFlights());

        // Добавляем фильтр, применим и отоброзим результат
        filterFlights.addFilter(new DepartingBeforeArriving());
        filterFlights.apply();
        System.out.println(filterFlights);

        // Добавим оставшиеся фильтры, применим и отоброзим результат
        filterFlights.addAllFilters(new FutureDepartures(), new HasLongGroundTime());
        filterFlights.apply();
        System.out.println(filterFlights);

        // Удалим первый фильтр, применим отоброзим результат
        filterFlights.removeFilter(0);
        filterFlights.apply();
        System.out.println(filterFlights);
    }
}
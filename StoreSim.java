import java.util.Scanner;
import java.util.Random;

public class StoreSim{
      // Store constants
    private static final int STORE_MINUTES = 840;
    private static final int LAST_30_MINUTES = 810;
    private static final double CHECKOUT_TIME_PER_ITEM = 0.15;
    private static final double MIN_SHOPPING_TIME = 0.5;
    private static final double MAX_SHOPPING_TIME = 1.0;
    private static final double EPSILON = 0.0000001;

    // Simulation data
    private MyArrayList<MyCustomer> shoppers;
    private CheckoutQueue[] checkouts;
    private Random random;

    private double currentTime;
    private boolean hasRun;

    // PRE: numberOfCheckouts must be between 1 and 10
    // POST: creates the shopper list and requested checkout lines
    public StoreSim(int numberOfCheckouts) {

        if (numberOfCheckouts < 1 || numberOfCheckouts > 10) {
            throw new IllegalArgumentException(
                "Number of checkout lines must be between 1 and 10."
            );
        }

        shoppers = new MyArrayList<>();
        checkouts = new CheckoutQueue[numberOfCheckouts];
        random = new Random();

        currentTime = 0.0;
        hasRun = false;

        for (int i = 0; i < checkouts.length; i++) {
            checkouts[i] = new CheckoutQueue();
        }
    }

    // PRE: simulation has not already been run
    // POST: simulates the entire store day, including overtime
    public void runSimulation() {

        if (hasRun) {
            throw new IllegalStateException(
                "This simulation has already been run."
            );
        }

        // Store is open for 840 minutes
        for (int minute = 0; minute < STORE_MINUTES; minute++) {

            currentTime = minute;

            // Generate 1-3 new customers
            int arrivals = random.nextInt(3) + 1;

            for (int i = 0; i < arrivals; i++) {
                createCustomer(minute);
            }

            // Process shopping and checkout events until next minute
            processUntil(minute + 1.0);
        }

        // Store is closed. Finish everyone still shopping or checking out.
        while (hasWork()) {

            double nextEvent = findNextEventTime();

            if (nextEvent < currentTime) {
                nextEvent = currentTime;
            }

            processUntil(nextEvent);
        }

        hasRun = true;
    }

    // PRE: arrivalTime is between 0 and 839
    // POST: creates a customer with randomly assigned items and shopping time
    //       and adds the customer to the store's shopper list
    private void createCustomer(double arrivalTime) {

        int items = generateItemCount(arrivalTime);

        // Random shopping time per item: 0.5 to 1.0 minutes
        double timePerItem = MIN_SHOPPING_TIME
            + random.nextDouble()
            * (MAX_SHOPPING_TIME - MIN_SHOPPING_TIME);

        double shoppingTime = items * timePerItem;

        MyCustomer customer = new MyCustomer(
            items,
            shoppingTime,
            arrivalTime
        );

        shoppers.add(customer);
    }

    // PRE: arrivalTime is a valid store arrival time
    // POST: returns a random item count using assignment percentages
    private int generateItemCount(double arrivalTime) {

        // During the last 30 minutes, customers generally buy 15 or fewer
        if (arrivalTime >= LAST_30_MINUTES) {
            return random.nextInt(15) + 1;
        }

        int percentage = random.nextInt(100) + 1;

        if (percentage <= 48) {
            // 48% purchase 1-10 items
            return random.nextInt(10) + 1;
        }
        else if (percentage <= 81) {
            // 33% purchase 11-20 items
            return random.nextInt(10) + 11;
        }
        else if (percentage <= 93) {
            // 12% purchase 21-30 items
            return random.nextInt(10) + 21;
        }
        else {
            // 7% purchase 31-60 items
            return random.nextInt(30) + 31;
        }
    }

    // PRE: endTime is not earlier than currentTime
    // POST: processes all shopper arrivals and checkout completions
    //       occurring up to endTime
    private void processUntil(double endTime) {

        while (true) {

            double nextTime = endTime;

            // Find the next shopper ready to check out
            for (int i = 0; i < shoppers.size(); i++) {

                MyCustomer customer = shoppers.get(i);
                double readyTime = customer.getQueueTime();

                if (readyTime < currentTime) {
                    readyTime = currentTime;
                }

                if (readyTime < nextTime - EPSILON) {
                    nextTime = readyTime;
                }
            }

            // Find the next checkout completion
            for (int i = 0; i < checkouts.length; i++) {

                MyCustomer current = checkouts[i].getCurrentCustomer();

                if (current != null) {

                    double departure =
                        checkouts[i].getDepartureTime();

                    if (departure < currentTime) {
                        departure = currentTime;
                    }

                    if (departure < nextTime - EPSILON) {
                        nextTime = departure;
                    }
                }
            }

            // Update every checkout line to the same simulation time
            for (int i = 0; i < checkouts.length; i++) {
                checkouts[i].update(nextTime);
            }

            currentTime = nextTime;

            // Customers who have finished shopping join checkout lines
            moveReadyShoppers();

            // Stop when the requested time has been reached
            if (currentTime >= endTime - EPSILON) {
                break;
            }
        }
    }

    // PRE: checkout lines have been updated to currentTime
    // POST: moves every shopper ready to check out into the shortest line
    private void moveReadyShoppers() {

        int i = 0;

        while (i < shoppers.size()) {

            MyCustomer customer = shoppers.get(i);

            if (customer.getQueueTime() <= currentTime + EPSILON) {

                int shortestLine = findShortestLine();

                checkouts[shortestLine].addCustomer(
                    customer,
                    currentTime
                );

                // Remove customer from the store's shopper list
                shoppers.remove(i);
            }
            else {
                i++;
            }
        }
    }

    // PRE: at least one checkout line exists
    // POST: returns the index of the line with the fewest items
    private int findShortestLine() {

        int shortestIndex = 0;

        int fewestItems =
            checkouts[0].getTotalItemsInLine();

        for (int i = 1; i < checkouts.length; i++) {

            int currentItems =
                checkouts[i].getTotalItemsInLine();

            if (currentItems < fewestItems) {
                fewestItems = currentItems;
                shortestIndex = i;
            }
        }

        return shortestIndex;
    }

    // PRE: none
    // POST: returns the earliest remaining shopper or checkout event
    private double findNextEventTime() {

        double nextTime = Double.MAX_VALUE;

        // Check shoppers still in the store
        for (int i = 0; i < shoppers.size(); i++) {

            double readyTime =
                shoppers.get(i).getQueueTime();

            if (readyTime < nextTime) {
                nextTime = readyTime;
            }
        }

        // Check customers currently checking out
        for (int i = 0; i < checkouts.length; i++) {

            if (checkouts[i].getCurrentCustomer() != null) {

                double departure =
                    checkouts[i].getDepartureTime();

                if (departure < nextTime) {
                    nextTime = departure;
                }
            }
        }

        return nextTime;
    }

    // PRE: none
    // POST: returns true if shoppers or checkout customers remain
    private boolean hasWork() {

        if (!shoppers.isEmpty()) {
            return true;
        }

        for (int i = 0; i < checkouts.length; i++) {

            if (!checkouts[i].isEmpty()) {
                return true;
            }
        }

        return false;
    }

    // PRE: simulation has completed
    // POST: prints statistics for every checkout line
    public void printReport() {

        if (!hasRun) {
            throw new IllegalStateException(
                "Run the simulation before printing the report."
            );
        }

        int totalCustomers = 0;
        int totalItems = 0;
        int totalMaxLineLength = 0;

        double totalIdleMinutes = 0.0;
        double totalOvertime = 0.0;

        System.out.println();
        System.out.println("==============================================");
        System.out.println("          GROCERY STORE SIMULATION");
        System.out.println("==============================================");
        System.out.println("Store hours: 8:00 AM - 10:00 PM");
        System.out.println("Checkout lines: " + checkouts.length);
        System.out.println("----------------------------------------------");

        for (int i = 0; i < checkouts.length; i++) {

            CheckoutQueue checkout = checkouts[i];

            int customers = checkout.getCustomersHelped();
            int items = checkout.getItemsProcessed();
            int maxLength = checkout.getMaxLineLength();

            double idle = checkout.getIdleMinutes();
            double overtime = checkout.getOvertimeMinutes(
                STORE_MINUTES
            );

            System.out.println();
            System.out.println("CHECKOUT LINE " + (i + 1));
            System.out.println("----------------------------------------------");

            System.out.println("Customers served: " + customers);
            System.out.println("Items processed: " + items);
            System.out.println("Maximum line length: " + maxLength);

            System.out.printf("Idle minutes: %.2f%n", idle);
            System.out.printf("Overtime minutes: %.2f%n", overtime);

            totalCustomers += customers;
            totalItems += items;

            if (maxLength > totalMaxLineLength) {
                totalMaxLineLength = maxLength;
            }

            totalIdleMinutes += idle;
            totalOvertime += overtime;
        }

        System.out.println();
        System.out.println("==============================================");
        System.out.println("                 DAILY TOTALS");
        System.out.println("==============================================");

        System.out.println("Total customers served: " + totalCustomers);
        System.out.println("Total items processed: " + totalItems);
        System.out.println("Largest line length: " + totalMaxLineLength);

        System.out.printf("Total idle minutes: %.2f%n",
            totalIdleMinutes);

        System.out.printf("Total overtime minutes: %.2f%n",
            totalOvertime);

        System.out.println("----------------------------------------------");
        System.out.println("Customers remaining in store: " + shoppers.size());
        System.out.println("Simulation complete.");
        System.out.println("==============================================");
    }

    // PRE: none
    // POST: asks user for a valid number of checkout lines (1-10)
    //       runs the simulation and prints its report

}
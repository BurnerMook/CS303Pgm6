public class CheckoutQueue {
    //Queue of the waiting line
    private MyArrayDeque<MyCustomer> line;

    //Customer being checked out
    private MyCustomer currentCustomer;
    
    //Time until current customer is done
    private double departureTime;
    private double lastEventTime;
    //Stats
    private int totalCustomersHelped;
    private int totalItemsProcessed;
    private int maxLineLength;
    private double idleMinutes;

    //last customer's time
    private double lastCompletionTime;

    //Constructor
    //Post: creates an empty checkout line
    public CheckoutQueue(){
        line = new MyArrayDeque<>();

        currentCustomer=null;

        departureTime= 0.0;
        lastEventTime = 0.0;

        totalCustomersHelped = 0;
        totalItemsProcessed = 0;
        maxLineLength = 0;

        idleMinutes = 0.0;
        lastCompletionTime = 0.0;
    }
    // PRE: customer is not null; currentTime is a valid simulation time
    // POST: adds customer to the line or starts checkout immediately
    public void addCustomer(MyCustomer customer, double currentTime) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        advanceTo(currentTime);

        if (currentCustomer == null) {
            startCheckout(customer, currentTime);
        } else {
            line.addLast(customer);
        }

        // Includes the customer currently being served
        int currentLineLength = getLineLength();

        if (currentLineLength > maxLineLength) {
            maxLineLength = currentLineLength;
        }

    }
    // PRE: customer is not null
    // POST: begins checkout and calculates the departure time
    private void startCheckout(MyCustomer customer, double startTime) {
        currentCustomer = customer;

        departureTime = startTime + customer.getCheckoutTime();
    }
    // PRE: currentTime is not earlier than the last recorded event time
    // POST: processes completed customers and starts the next customer
    //       updates idle time when the checkout line is empty
    public void update(double currentTime) {
        advanceTo(currentTime);
    }

    // Processes all checkout events up to the specified time
    // is used through update and therefore has the same pre and post conditions
    private void advanceTo(double currentTime) {

        if (currentTime < lastEventTime) {
            throw new IllegalArgumentException(
                "Simulation time cannot move backwards"
            );
        }

        if (currentCustomer == null) {
            idleMinutes += currentTime - lastEventTime;
        }
        else {
            while (currentCustomer != null &&
                departureTime <= currentTime) {

                totalCustomersHelped++;
                totalItemsProcessed += currentCustomer.getItems();

                lastCompletionTime = departureTime;

                double completionTime = departureTime;

                currentCustomer = null;

                if (!line.isEmpty()) {
                    MyCustomer nextCustomer = line.removeFirst();

                    startCheckout(nextCustomer, completionTime);
                }
                else {
                    idleMinutes += currentTime - completionTime;
                }
            }
        }

        lastEventTime = currentTime;
    }
    // PRE: none
    // POST: returns number of customers waiting and being served
    public int getLineLength() {
        int length = line.size();

        if (currentCustomer != null) {
            length++;
        }

        return length;
    }

        // PRE: none
    // POST: returns number of customers waiting in line
    public int getWaitingCount() {
        return line.size();
    }

    // PRE: none
    // POST: returns total number of items belonging to customers
    //       waiting or currently checking out
    public int getTotalItemsInLine() {
        int total = 0;

        if (currentCustomer != null) {
            total += currentCustomer.getItems();
        }

        for (MyCustomer customer : line) {
            total += customer.getItems();
        }

        return total;
    }

    // PRE: none
    // POST: returns true if nobody is waiting or checking out
    public boolean isEmpty() {
        return currentCustomer == null && line.isEmpty();
    }

    // PRE: none
    // POST: returns number of customers completely served
    public int getCustomersHelped() {
        return totalCustomersHelped;
    }

    // PRE: none
    // POST: returns total number of items processed
    public int getItemsProcessed() {
        return totalItemsProcessed;
    }

    // PRE: none
    // POST: returns maximum line length recorded
    public int getMaxLineLength() {
        return maxLineLength;
    }

    // PRE: none
    // POST: returns total idle checkout minutes
    public double getIdleMinutes() {
        return idleMinutes;
    }

    // PRE: none
    // POST: returns the completion time of the last customer
    public double getLastCompletionTime() {
        return lastCompletionTime;
    }

    // PRE: closeTime represents the store closing time
    // POST: returns minutes worked after closing
    public double getOvertimeMinutes(double closeTime) {
        return Math.max(0.0, lastCompletionTime - closeTime);
    }

    // PRE: none
    // POST: returns the customer currently checking out
    public MyCustomer getCurrentCustomer() {
        return currentCustomer;
    }

    // PRE: none
    // POST: returns the expected departure time of the current customer
    public double getDepartureTime() {
        if (currentCustomer == null) {
            return 0.0;
        }

        return departureTime;
    }
}
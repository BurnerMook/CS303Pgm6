public class MyCustomer {
    private int items;
    private double shopTime;
    private double arrivalTime;
    private double QueueTime;
    private double checkoutTime;

    //Constructor
    //Pre: items > 0
    //     shopTime > 0
    //Post: Creates a customer
    public MyCustomer(int items, double shopTime, double arrivalTime){
        this.items = items;
        this.shopTime = shopTime;
        this.arrivalTime = arrivalTime;

        this.QueueTime = arrivalTime + shopTime;
        this.checkoutTime = items * 0.15;
    }

    //getter for items
    //Post: returns number of items purchased
    public int getItems(){
        return items;
    }
    //getter for shopTime
    //Post: returns shopping time
    public double getShopTime(){
        return shopTime;
    }
    //getter for arrival
    //Post: returns time the customer arrived
    public double getArrivalTime(){
        return arrivalTime;
    }
    //getter for entering checkout
    //Post: returns time customer finishes shopping
    public double getQueueTime(){
        return QueueTime;
    }
    //getter for checkout
    //Post: Returns time required to check out
    public double getCheckoutTime(){
        return checkoutTime;
    }
    // Calculate checkout departure time
    // PRE: checkoutStartTime is a valid checkout start time
    // POST: Returns the time customer finishes checking out
    public double getDepartureTime(double checkoutStartTime) {
        return checkoutStartTime + checkoutTime;
    }

    // Display customer information
    // POST: Returns a String containing customer information
    @Override
    public String toString() {
        return "Items: " + items +
               " | Arrival Time: " + arrivalTime +
               " | Shopping Time: " + shopTime +
               " | Enter Queue Time: " + QueueTime +
               " | Checkout Time: " + checkoutTime;
    }
}
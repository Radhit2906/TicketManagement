package models;

import java.sql.Timestamp;

public class Ticket {
    private int id;
    private Journey journey;
    private Customer customer;
    private int quantity;
    private double totalPrice;
    private Timestamp bookingDatetime;

    // Getters and setters for all fields, including bookingDatetime
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getBookingDatetime() {
        return bookingDatetime;
    }

    public void setBookingDatetime(Timestamp bookingDatetime) {
        this.bookingDatetime = bookingDatetime;
    }
}

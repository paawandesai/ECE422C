/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Paawan Desai>
* <pkd397>
* <17140>
* Spring 2023
*/
package auction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import auction.Server.AuctionItemObserver;

public class AuctionItem {
    Integer auctionItemId;
    String name;
    String description;
    String startPrice;
    String highestBid;
    private boolean closed;
    private boolean sold;
    private String highestBidderId;
    private int delay = 300000; // 5 minutes
    private Timer timer;
    private List<AuctionItemObserver> observers = new ArrayList<>();

    public AuctionItem(Integer auctionItemId, String name, String description, String startPrice) {
        this.auctionItemId = auctionItemId;
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.highestBid = startPrice;
        this.highestBidderId = null;
        this.sold = false;
    }

    public Integer getAuctionItemId() {
        return auctionItemId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
    	this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
    	this.description = description;
    }

    public String getStartPrice() {
        return startPrice;
    }
    public void setStartPrice(String startPrice) {
    	this.startPrice = startPrice;
    }

    public String getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(String highestBid) {
        this.highestBid = highestBid;
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }
    public void registerObserver(AuctionItemObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(AuctionItemObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (AuctionItemObserver observer : observers) {
            observer.onUpdate(this);
        }
    }

    public synchronized boolean bid(String customerId, String bidAmount) {
        double amount = Double.parseDouble(bidAmount);
        if (closed() || amount <= Double.parseDouble(highestBid) || amount <= Double.parseDouble(startPrice)) {
            return false;
        }
        highestBid = bidAmount;
        highestBidderId = customerId;
        timer.restart();
        return true;
    }

    public synchronized void closeAuction() {
        if (!closed()) {
            sold = true;
            timer.stop();
            System.out.println("Auction for item " + auctionItemId + " closed. Sold to customer " + highestBidderId + " for " + highestBid);
        }
    }

    public synchronized boolean closed() {
        return sold || !timer.isRunning();
    }

    public synchronized int getTimeLeft() {
        if (closed()) {
            return 0;
        }
        return (int) (delay - timer.getDelay()) / 1000;
    }

    public synchronized void setTimeLeft(int seconds) {
        int timeLeft = (seconds * 1000) - (delay - timer.getDelay());
        if (timeLeft > 0) {
            timer.setDelay(timeLeft);
        }
    }

}

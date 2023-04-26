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

        this.timer = new Timer(delay, new ActionListener() {
            private int counter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (counter == 20) {
                    closed = true;
                    timer.stop();
                }
            }
        });
    }

    public Integer getAuctionItemId() {
        return auctionItemId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartPrice() {
        return startPrice;
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
}

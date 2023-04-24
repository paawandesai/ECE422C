package server;

import java.time.Duration;

public class AuctionItem {
    private String auctionItemId;
    private double reservePrice;
    private Auction auction;
    private String name;
    private String description;
    private double currentBid;
    private Duration timeLeft;

    public AuctionItem(String auctionItemId, double reservePrice, String name, String description) {
        this.auctionItemId = auctionItemId;
        this.reservePrice = reservePrice;
        this.name = name;
        this.description = description;
    }

    public String getAuctionItemId() {
        return auctionItemId;
    }

    public double getReservePrice() {
        return reservePrice;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public Duration getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(Duration duration) {
        this.timeLeft = duration;
    }
    public void resetAuction() {
        this.currentBid = 0.0;
        this.auction = null;
        this.timeLeft = null;
    }
}



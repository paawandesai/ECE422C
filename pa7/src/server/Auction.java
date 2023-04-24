package server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Auction {
    private AuctionItem item;
    private List<Bid> allBids;
    private LocalDateTime endTime;
    private String highestBidder;

    public Auction(AuctionItem item) {
        this.item = item;
        this.allBids = new ArrayList<>();
        this.endTime = LocalDateTime.now().plus(item.getTimeLeft());
        this.highestBidder = null;
    }

    public void placeBid(String bidder, Bid bid, List<Bid> allBids) {
        allBids.add(bid);
        Collections.sort(allBids);
        this.highestBidder = bidder;
    }

    public List<Bid> getAllBids() {
        return allBids;
    }

    public double getHighestBid() {
        if (allBids.isEmpty()) {
            return 0.0;
        }
        return allBids.get(allBids.size() - 1).getAmount();
    }

    public Duration getRemainingTime() {
        LocalDateTime now = LocalDateTime.now();
        if (endTime.isBefore(now)) {
            return Duration.ZERO;
        }
        return Duration.between(now, endTime);
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String bidder) {
        this.highestBidder = bidder;
    }

    public boolean hasEnded() {
        return item.getReservePrice() <= getHighestBid();
    }
    public List<Bid> getBidHistory() {
        List<Bid> bidHistory = new ArrayList<>();
        for (Bid bid : allBids) {
            if (bid.getItemId().equals(item.getAuctionItemId())) {
                bidHistory.add(bid);
            }
        }
        return bidHistory;
    }


    public synchronized void setHighestBid(String bidderName, double bidAmount) {
        if (allBids.isEmpty() || getHighestBid() < bidAmount) {
            Bid bid = new Bid(bidderName, bidAmount);
            if (BidValidator.isBidValid(item, bid, allBids)) {
                placeBid(bidderName, bid, allBids);
                item.setCurrentBid(bidAmount);
            }
        }
    }
}

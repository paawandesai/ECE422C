package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuctionManager {
    private static Map<String, AuctionItem> auctionItems;
    private Map<String, Auction> auctions;
    private List<Bid> allBids;

    public AuctionManager() {
        this.auctionItems = new HashMap<>();
        this.auctions = new HashMap<>();
        this.allBids = new ArrayList<>();
    }

    public void createAuction(String auctionItemId, double reservePrice) {
        if (!auctionItems.containsKey(auctionItemId)) {
            AuctionItem item = new AuctionItem(auctionItemId, reservePrice, auctionItemId, auctionItemId);
            auctionItems.put(auctionItemId, item);
            auctions.put(auctionItemId, new Auction(item));
        }
    }

    public void handleBid(String auctionItemId, Bid bid) throws InvalidBidException {
        AuctionItem item = getItemByName(auctionItemId);
        if (item == null) {
            throw new IllegalArgumentException("Item " + auctionItemId + " not found.");
        }

        Auction auction = item.getAuction();
        if (auction == null) {
            throw new IllegalStateException("Auction for item " + auctionItemId + " not found.");
        }

        if (BidValidator.isBidValid(item, bid, allBids)) {
            auction.placeBid(bid.getBidderName(), bid, allBids);
            allBids.add(bid);
            Collections.sort(allBids);
        } else {
            throw new InvalidBidException("Bid " + bid.toString() + " is invalid.");
        }
    }

    public List<Bid> getAllBids() {
        return allBids;
    }

    public static AuctionItem getItemByName(String auctionItemId) {
        for (AuctionItem item : auctionItems.values()) {
            if (item.getAuctionItemId().equals(auctionItemId)) {
                return item;
            }
        }
        return null;
    }
    public class InvalidBidException extends Exception {
        public InvalidBidException(String message) {
            super(message);
        }
    }
}


package server;
import java.util.List;

public class BidValidator {
    private static final double UnlimitedCreditLimit = Double.MAX_VALUE;
	public static final int BID_INCREMENT = 1;
	public static boolean isBidValid(AuctionItem item, Bid bid, List<Bid> allBids) {
    	

        if (bid == null) {
            return false;
        }
        
        if (bid.getAmount() <= item.getReservePrice()) {
            return false;
        }
        
        if (bid.getCreditLimit() == UnlimitedCreditLimit && bid.getAmount() > bid.getCreditLimit()) {
            return false;
        }
        
        if (bid.isBlocked()) {
            return false;
        }
        
        if (allBids.contains(bid)) {
            return false;
        }
        if (!bid.isValidBid(bid.getAmount())) {
            return false;
        }
        
        return true;
    }
    
}


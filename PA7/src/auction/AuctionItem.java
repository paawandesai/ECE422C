package auction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class AuctionItem {
	Integer auctionItemId;
	String name;
    String description;
    String startPrice;
    String highestBid;
    boolean closed;
    private boolean sold;
    int delay = 300000;
    private Timer timer;
    private String highestBidderId;
    

	public AuctionItem(Integer auctionItemId, String name, String description, String startPrice) {
	    this.auctionItemId = auctionItemId;
	    this.name = name;
	    this.description = description;
	    this.startPrice = startPrice;
	    this.sold = false;
	    this.timer = new Timer(300000, new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	        sold = true;
	        closed = true;
	      }
	    });
	    timer.start();
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
    public String getHighestBidder() {
    	return highestBidderId;
    }
    public synchronized boolean bid(String customerId, double bidAmount) {
        if (closed() || bidAmount <= Integer.parseInt(customerId)) {
          return false;
        }
        highestBid = String.valueOf(bidAmount);
        highestBidderId = customerId;
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
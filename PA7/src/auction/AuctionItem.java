package auction;


public class AuctionItem {
	Integer auctionItemId;
	String name;
    String description;
    double startPrice;

	public AuctionItem(Integer auctionItemId, String name, String description, double startPrice) {
	    this.auctionItemId = auctionItemId;
	    this.name = name;
	    this.description = description;
	    this.startPrice = startPrice;
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
    public double getStartPrice() {
        return startPrice;
    }
    
}
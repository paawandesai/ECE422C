package auction;

import java.util.List;

enum MessageType {
	GET_AUCTION_ITEMS,
	SEND_AUCTION_ITEMS
}
class Message {
	MessageType messageType;
	String type;
	String input;
	int number;
	public String itemId;
	public double bidAmount;
	public int customerId;
	public static final String updateAuctionItems = "UPDATE_AUCTION_ITEMS";
	private List<AuctionItem> auctionItems;


	protected Message() {
		this.type = "";
		this.input = "";
		this.number = 0;
		System.out.println("client-side message created");
	}
	
	public Message(MessageType type) {
		this.messageType = type;
	}
	public Message(MessageType type, ArrayList<AuctionItem> auctionItems) {
		this.messageType = type;
		this.auctionItems = auctionItems;
	}
	
	public List<AuctionItem> getAuctionItems() {
		return auctionItems;
	}
	public String getType() {
		return type;
	}
	
}
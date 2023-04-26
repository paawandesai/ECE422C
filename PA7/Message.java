package auction;

import java.util.List;

enum MessageType {
	GET_AUCTION_ITEMS,
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
	public Message(String type, String content) {
		this.type = type;
		this.input = content;
	}
	public Message(String type, String content, List<AuctionItem> auctionItems) {
		this.type = type;
		this.input = content;
		this.auctionItems = auctionItems;
	}
	

	protected Message(String type, String input, int number) {
		this.type = type;
		this.input = input;
		this.number = number;
		System.out.println("client-side message created");
	}
	public Message(String auctionItem, double bidAmount) {
		this.itemId = String.valueOf(auctionItem);

	}
	public List<AuctionItem> getAuctionItems() {
		return auctionItems;
	}
	public String getType() {
		return type;
	}
	
}
/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Paawan Desai>
* <pkd397>
* <17140>
* Spring 2023
* Slip Day Used: 1
*/
package auction;

import java.util.ArrayList;
import java.util.List;

enum MessageType {
	GET_AUCTION_ITEMS,
	SEND_AUCTION_ITEMS,
	UPDATE_AUCTION_BID,
}
class Message {
	MessageType messageType;
	String jsonAuctionItems;
	String type;
	String input;
	int number;
	public String itemId;
	public double bidAmount;
	public int customerId;
	static MessageType updateAuctionItems = MessageType.SEND_AUCTION_ITEMS;
	static MessageType getAuctionItems = MessageType.GET_AUCTION_ITEMS;
	static MessageType updateAuctionBid = MessageType.UPDATE_AUCTION_BID;
	private List<AuctionItem> auctionItems;
	AuctionItem auctionItem;
	List<AuctionItem> auctionItemsList;

	public Message(MessageType type) {
		this.messageType = type;
	}
	public Message(MessageType type, String json) {
		this.messageType = type;
		this.jsonAuctionItems = json;
	}
	public Message(MessageType type, AuctionItem auctionItem) {
		this.messageType = type;
		this.auctionItem = auctionItem;
	}
	public Message(MessageType type, List<AuctionItem> auctionItemsList) {
		this.messageType = type;
		this.auctionItemsList = auctionItemsList;
	}
	public List<AuctionItem> getAuctionItems() {
		return auctionItems;
	}
	public String getType() {
		return type;
	}
	
}
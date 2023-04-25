package auction;

class Message {
	  String type;
	  String input;
	  int number;
	  public String itemId;
	  public double bidAmount;
	  public int customerId;

	  protected Message() {
	    this.type = "";
	    this.input = "";
	    this.number = 0;
	    System.out.println("client-side message created");
	  }
	  protected Message(String input) {
		    this.input = "";
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
	}
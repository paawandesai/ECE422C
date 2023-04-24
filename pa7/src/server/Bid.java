package server;

public class Bid implements Comparable<Bid> {
    private double amount;
    private boolean blocked;
    private double creditLimit;
	private String bidderName;
	private String itemId;

    public Bid(double amount, double creditLimit) {
        this.amount = amount;
        this.blocked = false;
        this.creditLimit = creditLimit;
    }
    public Bid(String bidderName, double amount) {
        this.bidderName = bidderName;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
    	this.itemId = itemId;
    }
    public String getBidderName() {
    	return bidderName;
    }
    public void setBidderName(String bidderName) {
    	this.bidderName = bidderName;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public boolean isValidBid(double currentBid) {
        if (blocked) {
            return false;
        }
        if (creditLimit != 0 && amount + currentBid > creditLimit) {
            return false;
        }
        return true;
    }

    public boolean isValidBid(double currentBid, double reservePrice) {
        if (!isValidBid(currentBid)) {
            return false;
        }
        if (amount <= currentBid) {
            return false;
        }
        if (amount < reservePrice) {
            return false;
        }
        return true;
    }

    public String toString() {
        return String.format("Bid [amount=%.2f, blocked=%b, creditLimit=%.2f]", amount, blocked, creditLimit);
    }

    @Override
    public int compareTo(Bid otherBid) {
        return Double.compare(this.getAmount(), otherBid.getAmount());
    }
}



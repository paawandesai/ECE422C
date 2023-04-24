package other;

public class AuctionConstants {
    // Constants
    public static final int SERVER_PORT = 5000;
    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final int MAX_ITEMS = 5;
    public static final int MAX_CUSTOMERS = 5;
    public static final int AUCTION_DURATION_SECONDS = 60;
    public static final double UnlimitedCreditLimit = Double.MAX_VALUE;

    // Utility Methods
    public static boolean isValidBid(double bidAmount, double currentHighestBid) {
        return bidAmount > currentHighestBid;
    }

    public static String formatBid(double bidAmount) {
        return String.format("$%.2f", bidAmount);
    }

    public static String formatTimeRemaining(long secondsRemaining) {
        long minutes = secondsRemaining / 60;
        long seconds = secondsRemaining % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
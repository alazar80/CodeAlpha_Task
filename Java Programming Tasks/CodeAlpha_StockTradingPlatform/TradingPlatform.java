import java.util.*;

public class TradingPlatform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, Stock> market = new HashMap<>();
        Portfolio portfolio = new Portfolio();

        // Predefined stocks
        market.put("AAPL", new Stock("AAPL", 180));
        market.put("TESLA", new Stock("TESLA", 250));
        market.put("AMAZON", new Stock("AMAZON", 140));
        market.put("GOOGLE", new Stock("GOOGLE", 135));

        boolean running = true;

        while (running) {
            System.out.println("\n💼 Stock Trading Platform");
            System.out.println("1. View Market");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n📊 Market Prices:");
                    for (String symbol : market.keySet()) {
                        System.out.println(symbol + ": ₹" + market.get(symbol).getPrice());
                    }
                    break;

                case 2:
                    System.out.print("Enter stock symbol to buy: ");
                    String buySymbol = sc.next().toUpperCase();
                    if (market.containsKey(buySymbol)) {
                        System.out.print("Enter quantity: ");
                        int qty = sc.nextInt();
                        portfolio.buyStock(buySymbol, qty);
                        System.out.println("✅ Bought " + qty + " shares of " + buySymbol);
                    } else {
                        System.out.println("❌ Stock not found.");
                    }
                    break;

                case 3:
                    System.out.print("Enter stock symbol to sell: ");
                    String sellSymbol = sc.next().toUpperCase();
                    if (market.containsKey(sellSymbol)) {
                        System.out.print("Enter quantity: ");
                        int qty = sc.nextInt();
                        portfolio.sellStock(sellSymbol, qty);
                    } else {
                        System.out.println("❌ Stock not found.");
                    }
                    break;

                case 4:
                    portfolio.displayPortfolio(market);
                    break;

                case 5:
                    running = false;
                    System.out.println("👋 Exiting... Goodbye!");
                    break;

                default:
                    System.out.println("⚠️ Invalid choice.");
            }
        }

        sc.close();
    }
}

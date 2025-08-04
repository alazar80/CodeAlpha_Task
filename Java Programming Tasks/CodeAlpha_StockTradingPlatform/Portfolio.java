import java.util.HashMap;

public class Portfolio {
    private HashMap<String, Integer> holdings = new HashMap<>();

    public void buyStock(String symbol, int quantity) {
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + quantity);
    }

    public void sellStock(String symbol, int quantity) {
        if (holdings.containsKey(symbol) && holdings.get(symbol) >= quantity) {
            holdings.put(symbol, holdings.get(symbol) - quantity);
        } else {
            System.out.println("❌ Not enough shares to sell.");
        }
    }

    public void displayPortfolio(HashMap<String, Stock> market) {
        System.out.println("\n📈 Portfolio Summary:");
        double totalValue = 0;
        for (String symbol : holdings.keySet()) {
            int qty = holdings.get(symbol);
            double price = market.get(symbol).getPrice();
            double value = qty * price;
            System.out.println(symbol + ": " + qty + " shares × ₹" + price + " = ₹" + value);
            totalValue += value;
        }
        System.out.println("💰 Total Value: ₹" + totalValue);
    }
}

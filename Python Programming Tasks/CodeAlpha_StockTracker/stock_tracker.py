stock_prices = {
    "AAPL": 180,
    "TSLA": 250,
    "GOOGL": 135,
    "AMZN": 140,
    "MSFT": 310
}

total_investment = 0
portfolio = {}

print("📈 Enter stock quantities (0 to skip):")
for stock, price in stock_prices.items():
    try:
        qty = int(input(f"How many shares of {stock} (₹{price}/share)? "))
        if qty > 0:
            portfolio[stock] = qty
            total_investment += qty * price
    except ValueError:
        print("⚠️ Please enter a valid number.")

print("\n🧾 Portfolio Summary:")
for stock, qty in portfolio.items():
    print(f"{stock}: {qty} shares × ₹{stock_prices[stock]} = ₹{qty * stock_prices[stock]}")

print(f"\n💰 Total Investment: ₹{total_investment}")

with open("portfolio_summary.txt", "w") as file:
    file.write("Portfolio Summary:\n")
    for stock, qty in portfolio.items():
        file.write(f"{stock}: {qty} shares × ₹{stock_prices[stock]} = ₹{qty * stock_prices[stock]}\n")
    file.write(f"\nTotal Investment: ₹{total_investment}")

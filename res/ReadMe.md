# Stocks

### This project provides the following main functionalities:

- Displays a menu stating all the functions that a user can perform and response from methods
  as a result. It also accepts the choice of method to be executed as an input and process the
  requests accordingly.
- Creates a portfolio. The user can choose to create a portfolio and add any stocks along with
  as much quantity of it as they like. The user can choose to create the portfolios using one of the two methods:
  1. Using text-based CLI or
  2. Upload a _<<portfolio_name>>_.csv file that contains the stocks and its quantity along
     with the date of creation of the portfolio.
- Get the valuation of the portfolio. The value of the portfolio is determined by the
  sum of the value of each stock in the portfolio. The value of every stock is the product
  of its closing price on a particular date by its quantity. Our program allows the client
  to get the latest valuation, or the valuation on a custom date. We support
  getting the valuation from any dates between `2020-12-31` to `2022-11-01`. By default,
  the valuation is done based on the closing prince of the stock on a certain date.
  If the date entered by the user is of a weekend or of a federal holiday, then the value of the
  portfolio is calculated based on the closing prices of the latest day when the stock market was
  open.
- Gives the composition of the portfolio. The composition of the portfolio tells us about the
  stocks present in the portfolio, along with their respective quantity.
- After a portfolio has been created by the user, it is automatically saved in a csv format in
  `files/stock` directory.



| Corporation                      | Ticker |
|----------------------------------|--------|
| Apple                            | AAPL   |
| Amazon                           | AMZN   |
| Bank of America Corp.            | BAC    |
| Berkshire Hathaway Inc. Class B  | BRK.B  |
| Best Buy Co. Inc.                | BBY    |
| Boston Properties Inc.           | BXP    |
| Chevron Corporation              | CVX    |
| Coca-Cola Company                | KO     |
| Copart Inc.                      | CPRT   |
| Costco Wholesale Corporation     | COST   |
| Domino's Pizza Inc.              | DPZ    |
| Ebay                             | EBAY   |
| Expedia Group Inc.               | EXPE   |
| Federal Realty Investment Trust  | FRT    |
| Google                           | GOOG   |
| Hasbro Inc.                      | HAS    |
| Hershey Company                  | HSY    |
| Home Depot Inc.                  | HD     |
| HP Inc.                          | HPQ    |
| Johnson & Johnson                | JNJ    |
| JPMorgan Chase & Co.             | JPM    |
| Kellogg Company                  | K      |
| Meta                             | META   |
| MGM Resorts International        | MGM    |
| Monster Beverage Corporation     | MNST   |
| Microsoft                        | MSFT   |
| Motorola Solutions Inc.          | MSI    |
| Netflix                          | NFLX   |
| Nvidia                           | NVDA   |
| PepsiCo Inc.                     | PEP    |
| Pfizer Inc.                      | PEF    |
| Procter & Gamble Company         | PG     |
| Ralph Lauren Corporation Class A | RL     |
| Ulta Beauty Inc.                 | ULTA   |
| Visa Inc. Class A                | V      |
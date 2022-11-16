# Stocks

### This project provides the following main functionalities:

- Displays a menu stating all the functions that a user can perform and response from methods
  as a result. It also accepts the choice of method to be executed as an input and process the
  requests accordingly.


- Creates a portfolio. The user can choose to create a portfolio and add any stocks along with
  as much quantity of it as they like. The user can choose to create the portfolios using one 
  of the two methods:
  1. Using text-based CLI or
  2. Upload a _<<portfolio_name>>_.csv file that contains the stocks and its quantity along
     with the date of creation of the portfolio.


- Get the valuation of the portfolio. The value of the portfolio is determined by the
  sum of the value of each stock in the portfolio. The value of every stock is the product
  of its closing price on a particular date by its quantity. Our program allows the client
  to get the latest valuation, or the valuation on a custom date. We support
  getting the valuation from any dates between `2015-01-01` to `2022-11-02`. Date chosen after the 
  latest date will give the valuation from `2022-11-02`. By default,
  the valuation is done based on the closing price of the stock on a certain date.
  If the date entered by the user is of a weekend or of a federal holiday, then the value of the
  portfolio is calculated based on the closing prices of the latest day when the stock market was
  open.


- Gives the composition of the portfolio. The composition of the portfolio tells us about the
  stocks present in the portfolio, along with their respective quantity.


- After a portfolio has been created by the user, it is automatically saved in a csv format in
  `files/stocks` directory.


## Features specific to Flexible Portfolio

- User can buy stocks (provided that their data is accessible by the AlphaVantage API)
  on any date as provided by the user. 


- Sell any stocks that is present in the portfolio. There are however, some constrains while selling the stock in portfolio.
  The user cannot sell more stocks than the available quantity in the portfolio. For example, if the portfolio  
  has 100 shares of Google, selling 101 shares of the same stocks is not possible. Furthermore, we have restricted
  selling stocks before it has already been sold once. For instance, if you have sold 10 shares of google on 10th of January 2020,
  you can sell those share again only on or after the last sold date (i.e. 10 Jan 2020). Selling share of google on 
  9th Jan, 2020 or earlier would now be prohibited. 


- For every transaction, user can now charge a commission fee of their choice that gets added and 
  stored in the cost basis of the portfolio if the transaction goes through successfully.


- Everytime a transaction (buy/sell) is successfully executed, the cost basis of the portfolio is updated for the date of transaction. 
  


- User can now view the performance of the portfolio throughout its creation, or between the dates specifies by the user.

## Features specific to Inflexible Portfolio

_**For now, a flexible portfolio supports the use of only the following stocks listed below. If the user 
choose a share of stock from outside this list, they'll get an error.**_

| SR no. | Corporation                             | Ticker |
|--------|-----------------------------------------|--------|
| 1      | Apple                                   | AAPL   |
| 2      | Amazon                                  | AMZN   |
| 3      | Bank of America Corp.                   | BAC    |
| 4      | Berkshire Hathaway Inc. Class B         | BRK.B  |
| 5      | Best Buy Co. Inc.                       | BBY    |
| 6      | Boston Properties Inc.                  | BXP    |
| 7      | Cardinal Health Inc.                    | CAH    |
| 8      | Chevron Corporation                     | CVX    |
| 9      | Coca-Cola Company                       | KO     |
| 10     | Copart Inc.                             | CPRT   |
| 11     | Costco Wholesale Corporation            | COST   |
| 12     | Delta Air Lines Inc.                    | DAL    |
| 13     | Domino's Pizza Inc.                     | DPZ    |
| 14     | Ebay                                    | EBAY   |
| 15     | Expedia Group Inc.                      | EXPE   |
| 16     | Federal Realty Investment Trust         | FRT    |
| 17     | FedEx Corporation                       | FDX    |
| 18     | Fortune Brands Home & Security Inc.     | FBHS   |
| 19     | Genuine Parts Company                   | GPC    |
| 20     | Google                                  | GOOG   |
| 21     | Hasbro Inc.                             | HAS    |
| 22     | Hershey Company                         | HSY    |
| 23     | Home Depot Inc.                         | HD     |
| 24     | HP Inc.                                 | HPQ    |
| 25     | International Flavors & Fragrances Inc. | IFF    |
| 26     | Johnson & Johnson                       | JNJ    |
| 27     | JPMorgan Chase & Co.                    | JPM    |
| 28     | Kellogg Company                         | K      |
| 29     | Lumen Technologies Inc.                 | LUMN   |
| 30     | Meta                                    | META   |
| 31     | MGM Resorts International               | MGM    |
| 32     | Monster Beverage Corporation            | MNST   |
| 33     | Microsoft                               | MSFT   |
| 34     | Motorola Solutions Inc.                 | MSI    |
| 35     | Nasdaq Inc.                             | NDAQ   |
| 36     | Netflix                                 | NFLX   |
| 37     | Nvidia                                  | NVDA   |
| 38     | PepsiCo Inc.                            | PEP    |
| 39     | Pfizer Inc.                             | PEF    |
| 40     | Procter & Gamble Company                | PG     |
| 41     | Qorvo Inc.                              | QRVO   |
| 42     | Ralph Lauren Corporation Class A        | RL     |
| 43     | Sealed Air Corporation                  | SEE    |
| 44     | Southwest Airlines Co.                  | LUV    |
| 45     | Tyson Foods Inc. Class A                | TSN    |
| 46     | Ulta Beauty Inc.                        | ULTA   |
| 47     | United Rentals Inc.                     | URI    |
| 48     | Visa Inc. Class A                       | V      |
| 49     | Whirlpool Corporation                   | WHR    |
| 50     | Zions Bancorporation N.A.               | ZION   |
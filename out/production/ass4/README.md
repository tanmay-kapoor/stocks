# Stocks

### This project provides the following main functionalities:

- Gives user the option to either work with a flexible or inflexible portfolio.


- Displays a menu stating all the functions that a user can perform and response from methods
  as a result. It also accepts the choice of method to be executed as an input and process the
  requests accordingly.


- Creates a portfolio. The user can choose to create a portfolio and add any stocks along with
  as much quantity of it as they like. The user can choose to create the portfolios using one 
  of the two methods:
  1. Using text-based CLI or
  2. Upload a _<<portfolio_name>>_.csv file that contains the stocks and its quantity.


- Get the valuation of the portfolio. The value of the portfolio is determined by the
  sum of the value of each stock in the portfolio. The value of every stock is the product
  of its closing price on a particular date by its quantity. Our program allows the client
  to get the latest valuation, or the valuation on a custom date. We support
  getting the valuation from any dates provided that the data for the stocks is available in the AlphaVantage API.  By default,
  the valuation is done based on the closing price of the stock on a certain date.
  If the date entered by the user is of a weekend or of a federal holiday, then the value of the
  portfolio is calculated based on the closing prices of the latest day when the stock market was
  open.


- Gives the composition of the portfolio. The composition of the portfolio tells us about the
  stocks present in the portfolio, along with their respective quantity. User can also wish to 
  see the weightage of each company in the portfolio based on percentage.


- After a portfolio has been created by the user, it is automatically saved in a csv format in
  `files/stocks` directory.


## Features specific to Flexible Portfolio

- User can buy stocks (provided that their data is accessible by the AlphaVantage API) on any date as provided by the user. 


- Sell any stocks that is present in the portfolio. There are however, some constrains while
selling the stock in portfolio. The user cannot sell more stocks than the available quantity in the portfolio. 
For example, if the portfolio has 100 shares of Google, selling 101 shares of the same stocks is not possible. Furthermore, we have restricted
selling stocks before it has already been sold once. For instance, if you have sold 10 shares of google on 10th of January 2020,
you can sell those share again only on or after the last sold date (i.e. 10 Jan 2020). Selling share of google on 
9th Jan, 2020 or earlier would now be prohibited. 


- For every transaction, user can now charge a commission fee of their choice that gets added and 
stored in the cost basis of the portfolio if the transaction goes through successfully.


- Everytime a transaction (buy/sell) is successfully executed, the cost basis of the portfolio
  is updated for the date of transaction. The updated cost basis is also reflected on the future
  dates since we allow some operation such as buy, and sell in specific cases to be out of 
  chronological order. If user tries to get costBasis for a date when there were no stocks in the portfolio, The requested value would be 0.
  Requesting cost basis for future dates not allowed by the program.
  

- User can now view the performance of the portfolio between the dates specified by the user. 
  User cannot choose future dates as nor price data would exist for those requests.


## Features specific to Inflexible Portfolio

This type of portfolio is immutable. 

A flexible portfolio can only buy shares while it is created. It is incapable of buying  or 
selling shares later after the portfolio has been created. It does not track cost basis or shows 
performance of the portfolio over time for now.

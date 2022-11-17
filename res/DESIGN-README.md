# Design changes from previous iteration of the project.

### View Changes

**No changes in the existing package structure.**

Added `MainMenu`, `MainMenuImpl` and `StockPortfolioFelxible` to accommodate the workings of both
flexible and inflexible portfolio. Find more details about these classes below.

### Model Changes

1. In `AbstractPortfolio`, Instead of storing a map of `Details` object of each stock, we now
   store `Log` object of each stock.

2. Added `StockPortfolioFlexible`, `Log` and `Txn` to better streamline the features of different
   type of portfolio.
   Find more details about these classes below.

### Controller Changes

1. Added ```GenericController``` whose ```start()``` method is called by the ```ProgramRunner```.
   This controller is sued to accept from the user the portfolio he/she wants to work with and calls
   that controller's ```start()``` method.

2. Added ```SpecificController``` interface that is implemented by ```StockControllerInflexible```
   and ```StockControllerFlexible``` classes. These classes are used to perform different actions
   for the same options depending on whether the user wants to work with Flexible/Inflexible
   portfolio.

# Design of the current project

## The design of the program is divided into 3 segments, namely, model, view, and controller.

### View

*The view is what the end user of the program interacts with.
It includes displaying the menu or any text based outputs produced by the application along with
accepting requests from the user's choice in the form of an input.
It is also responsible for displaying the resultant data as per the request of the client.
It also the accepts csv file path and sends it to the controller to process the portfolio
content present in them.*

Every aspect of code related to the view segment is in the `view` package.
The structure of the view in the project folder is as follows:

1. `Menu` (Interface) that states the methods to be implemented by any class that extends
   the Menu class. The method signatures in it lists the functions that are going to be used to
   display the menu to the user.


2. `AbstractMenu` (abstract class) that will be used for providing views that will be
   common to other menu classes. This is done because in the future we may include menu for
   crypto/mutual fund portfolio which will share common methods.


3. `StockMenuInflexible` (concrete class) is meant to specifically deal with menu while interacting
   with
   user about inflexible stock portfolio.


4. `StockMenuFlexible` (concrete class) is meant to specifically deal with menu while interacting
   with
   user about flexible stock portfolio.


5. `MainMenu` interface that state the methods to be implemented in classes that implement it.


6. `MainMenuImpl` which can be used for choosing either to work with `Flexible` or `Inflexible`.

### Model

*The model is where the actual crux of code’s working lies. It is responsible for creation of
data to be saved in the portfolio object. It dictates the working of API which are going to be
used to fetch pertinent data relating to a particular stock. It lays down the skeleton for the
Portfolio object that we use to store all the relevant data relating to the portfolio created
by the user.*

Every aspect of code related to the view segment is in the `models` package.
The structure of the model in the project folder is as follows:

1. `api` package which contains:
    - `ShareAPI` (Interface) to get details about any type of share as and when requested by the
      user. We use it so that the program is open for extensions in the future. In later updates
      of the program, it may be required to include a portfolio for crypto, mutual funds, etc.,
      thus requiring the possible usage of multiple third party APIs.

    - `AlphaVantage` (Class) uses the AlphaVantage API to fetch data of any stock requested by
      the client to be added to their portfolio.

    - `StockAPI` A class that checks if a ticker symbol is supported by our program.
      If yes, then fetch the necessary data as per the users request.

    - `supported_stocs` directory, that contains all the stocks that are supported by the program.
      This directory stores the csv files of individual stock's data. This is done to make the
      application independent of any third party api. These are the only stocks accessible by
      the `StockAPI`


2. `portfolio` package which contains:
    - `Portfolio` (Interface) that states the methods that are expected from any kind of portfolio.
      We make this an interface so that it is scalable for future upgrades. For instance, we may be
      required to implement crypto, forex, or any other king of portfolio. The portfolio supports
      addition of fractional shares, but the controller decides whether to allow whole or  
      fractional shares

    - `StockPortfolioInflexible` (Class) that is an extension of Portfolio class. This class
      specifically
      deals with the portfolio that stores shares supported by our program.

    - `StockPortfolioFlexible` (Class) that is an extension of Portfolio class. This class
      specifically
      deals with the portfolio that stores shares supported out APIs. This type of portfolio has the
      ability to sell stocks, store cost basis,
      and get the performance of the portfolio over a time period.

    - `Txn` (Enum)  to restrict types of transactions to only Buy or Sell. In the future, the
      transaction types like
      lending, borrowing could be accommodated if required.

    - `Composition` (Enum) Lists the type of compositions that can be requested by the client.
      For now, we only allow getting composition based on contents or percent weightage of stocks.


3. `Details` (Class) that stores the quantity and the date of purchase of a particular share.


4. `Log` (Class) that stores the `Details` and `lastSoldDate` of a particular share in the
   portfolio.


### Controller

*The controller is the part of the program that has been designed to act as a delegator
between the model and the client. It tells the view how to interact with the client (as in
what kind of menu and choices to be provided to the client). It also makes sure that the
input from the users are valid by performing necessary validation checks. It then sends the
input form the user to the model for processing of the data. Finally, it gets the response from the
model and relays it to the view to be shown to the client.*

Every aspect of code related to the view segment is in the `controller` package.
The structure of the controller in the project folder is as follows:

1. `Controller` (Interface) Defines functions to be executed for deciding which specific controller
   to be called.


2. `GenericController` (concrete class) Implements the `Controller` interface and is used to decide
   which specific controller to call. It is used to determine which type pof portfolio the user
   wants to work with.


3. `SpecificController` (Interface) An interface that state all the methods that are supposed to be
   implemented by all controllers of the program. A `SpecificController` object is meant to be
   used to produce a view to the client, take requests from them, perform data manipulations/cleanup
   on the inputs and delegate necessary functionality to the model of the program.


4. `AbstractController` (abstract class) An abstract controller that implements methods that
   are supposed to be common between multiple types of controllers across the program.


5. `StockControllerFlexible` (concrete class) The StockControllerFlexible implements methods that
   are meant to be executed while specifically working with flexible portfolios related to stock
   data and `AlphaVantageAPI`. Flexible portfolios have the ability to Buy/Sell shares, get value of
   portfolio, get cost basis of the portfolio as well as see the portfolio performance all on
   past/present dates.


6. `StockControllerInflexible` (concrete class) The StockControllerInflexible implements methods
   that are meant to
   be executed while working specifically for stock related data on inflexible portfolios
   and `AlphaVantageAPI`. It also consists of
   methods for creating stock portfolios. It has limited functionality and cannot work with
   past/future dates.


7. `Function` (enum) states the feature to be executed by the program.


8. `FileType` (enum) states the purpose of the file being worked on.




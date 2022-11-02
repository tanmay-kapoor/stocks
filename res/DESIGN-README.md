# Design of the project


## The design of the program is divided into 3 major segments, namely, the model, the controller, and the view. 

### View
*The view is what the end user of the program interacts with. 
It includes displaying the menu or any text based outputs produced by the application along with acceptation requests from the user in the form of the choice response. 
It is also responsible for displaying the resultant data as per the request of the client. It also the accepts csv file path and sends them to the controller to process the portfolio content present in them.*

Every aspect of code related to the view segment is in the `view` package.
The structure of the view in the project folder is as follows:

1. `Menu` (Interface) that states the methods to be implemented by any class that extends the Menu class. The method signatures in it lists the functions that are going to be used to display the menu to the user.

2. `AbstractMenu` (abstract class) that is that will be used for providing views that will be common to other menu classes. This is done because in the future we may include menu for crypto/mutual fund portfolio which will share common methods.

3. `StockMenu` (concrete class) is meant to specifically deal with menu while interacting with user about stock portfolio.


### Model
*The model is where the actual crux of code’s working lies. It is responsible for creation of data to be saved in the portfolio object. It dictates the working of API which are going to be used to fetch pertinent data relating to a particular stock. It lays down the skeleton for the Portfolio object that we use to store all the relevant data relating to the portfolio created by the user.*

Every aspect of code related to the view segment is in the `models` package.
The structure of the model in the project folder is as follows: 

1. `api` package which contains:
   - `ShareAPI` (Interface) to get details about any type of share as and when requested by the user. We use it so that the program is open for extensions in the future. In later updates of the program, it may be required to include a portfolio for crypto, mutual funds, etc., thus requiring the possible usage of multiple third party APIs.

   - `AlphaVantage` (Class) uses the AlphaVantage API to fetch data of any stock requested by the client to be added to their portfolio.

2. `portfolio` package which contains:
   - `Portfolio` (Interface) that states the methods that are expected from any kind of portfolio. We make this an interface so that it is scalable for future upgrades. For instance, we may be required to implement crypto, forex, or any other king of portfolio.

   - `StockPortfolio` (Class) that is an extension of Portfolio class. This class specifically deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.

### Controller
Every aspect of code related to the view segment is in the `models` package.
The structure of the model in the project folder is as follows: 





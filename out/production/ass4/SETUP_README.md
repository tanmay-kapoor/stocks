#  Project setup guide

Please follow the steps below to run the program:

1. Open the project root directory in the terminal. 


2. type `cd res` and press ENTER. you should see a file with .jar extension in this directory. 
   This is the file that you'll be using to interact with the program.


3. type `java -jar ass4.jar` to run the program.


4. After completing all the previous steps successfully you should see a menu printed 
   in the command line interface. Please refer to the `README.md` to know the features provided
   by the program.

_If you wish to not use the jar file, you can also run the program from the `ProgramRunner` class
in the `src` folder._


### _Initial inputs for the program._

Follow the steps mentioned below to add 3 stocks to the portfolio.

1. Start the program using the jar file as demonstrated above.
2. Select Flexible portfolio option on the menu.
3. Select the option to create a portfolio and choose to create it through the interface. Choose any name you like other than "qwerty". This is because we have already provided a portfolio named "qwerty" in our submission.
4. Add 5 shares `GOOG` to add to your stock initially. Select date of purchase as `2014-04-05`. Select any quantity as 5 and enter commission fee as 10. 
5. Now let us go back to the main menu and buy shares of 2 more companies. Choose the Buy/Sell (option 4) on the menu and then select the buy option. Enter the name of the portfolio you created. Stock bought would be added to this portfolio.
6. This time buy 10 shares of `AAPL` on `2014-04-01` with commission fee of 5.
7. Repeat step 5 and 6, but instead of Apple, enter `MSFT` as the stock to purchase. Buy 25 shares on `2022-05-01` with a commission fee of 0.


Follow the steps mentioned below to query the portfolio's value and cost basis on 2 different dates.

1. Check the valuation of the portfolio by selecting 
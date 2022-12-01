#  Project setup guide

drive link: https://drive.google.com/drive/folders/1G9TxuIn4zU6YGdgk4RzeVmpG36JfWsuE?usp=sharing


- Download the two jar files from the external_libs folder from the above Google Drive link. 
- Also watch the vide in the drive that shows how to add these downloaded libraries onto your project.

Please follow the steps below to run the program:

1. Open the project root directory in the terminal. 


2. type `cd res` and press ENTER. You should see a file with .jar extension in this directory. 
   This is the file that you'll be using to interact with the program.


3. type `java -jar ass4.jar` to run the program.


4. After completing all the previous steps successfully you should see a menu printed 
   in the command line interface. Please refer to the `README.md` to know the features provided
   by the program.

_If you wish to not use the jar file, you can also run the program from the `ProgramRunner` class
in the `src` folder._

For further issues, view the videos in the link below for additional help. 
link: https://drive.google.com/drive/folders/1G9TxuIn4zU6YGdgk4RzeVmpG36JfWsuE?usp=sharing



## _Initial inputs for the program [If using txt based interface]._

Follow the steps mentioned below to add 3 stocks to the portfolio.

1. Start the program using the jar file as demonstrated above.
2. Select Flexible portfolio option on the menu.
3. Select the option to create a portfolio and choose to create it through the interface. 
4. Add 5 shares `GOOG` to your stock initially. Select date of purchase as `2014-04-05`. Select quantity as 5 and enter commission fee as 10. 
5. Now let us go back to the main menu and buy shares of 2 more companies. Choose the Buy/Sell (option 4) on the menu and then select the buy option. Enter the name of the portfolio you created. Shares bought would be added to this portfolio.
6. This time buy 10 shares of `MSFT` on `2014-04-01` with commission fee of 5.
7. Repeat step 5 and 6, but instead of Apple, enter `AAPL` as the stock to purchase. Buy 25 shares on `2022-05-01` with a commission fee of 0.


Follow the steps mentioned below to query the portfolio's value on 2 different dates.

1. Check the valuation of the portfolio by selecting Option `3` of Flexible portfolio's main menu. Enter name of the portfolio that you just created.
2. Choose `Today` option to get the portfolio's value as of the current date.
3. For other dates repeat `step 1` and instead of choosing today's date, select the `Custom Date` option.
4. Choose the date `2012-04-11`. You should see the value : `0.00`. This is because all stocks have been bought after this date.
5. Repeat the same process for custom date and this time, enter `2022-01-01`. You should get the value of `$17831.15`.

Follow the steps mentioned below to query the portfolio's cost basis on 2 different dates.

1. Check the cost of the portfolio by selecting Option `6` of Flexible portfolio.
2. Enter the name of the portfolio that you created. 
3. Choose `Today` option to get the portfolio's cost basis till the current date. The cost value should be `$7086.15`.
4. For other dates repeat `step 1` and instead of choosing today's date, select the `Custom Date` option.
5. Choose the date `2017-10-10`. You should see the cost basis value : `$3144.90`.
6. Repeat the same process for custom date and this time, enter `2022-07-12`. You should get the cost basis value of `$7086.15`.

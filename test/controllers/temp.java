//  private class MockStockControllerGui extends FeaturesImpl {
//
//    private MockStockControllerGui(ShareApi api, String path) {
//      super(api, path);
//    }
//
//    @Override
//    protected Portfolio createPortfolioObject(String portfolioName) {
//      log.append("Portfolio name : ").append(portfolioName).append("\n");
//      return new StockControllerFlexibleGuiTest.MockStockPortfolio(log);
//    }
//
//    @Override
//    protected LocalDate getDate(String d) {
//      log.append("Inside getDate : ").append(d).append("\n");
//      if (d.equals("")) {
//        return LocalDate.now();
//      }
//      return LocalDate.parse(d);
//    }
//
//    @Override
//    protected double getCommissionFee(String commission) {
//      log.append("Inside getCommissionFee : ").append(commission).append("\n");
//      if (commission.equals("")) {
//        commission = "0";
//      }
//      return Double.parseDouble(commission);
//    }
//
//    @Override
//    protected void sellStockIfAllowed(String portfolioName, String ticker, String quant, String d, String commission) {
//      log.append("Inside sellStockIfAllowed : ").append(portfolioName).append(ticker)
//              .append(quant).append(d).append(commission).append("\n");
//
//      try {
//        Portfolio portfolio = findPortfolio(portfolioName);
//        ticker = ticker.toUpperCase();
//        double quantity = Double.parseDouble(quant);
//        double commissionFee = Double.parseDouble(commission);
//        LocalDate date = LocalDate.parse(d);
//        Map<String, Log> portfolioComposition = portfolio.getComposition();
//        if (!portfolioComposition.containsKey(ticker)) {
//          menu.printMessage(ticker + " is not in this portfolio");
//        } else {
//          Details details = new Details(quantity, date);
//          portfolio.sell(ticker, details, commissionFee);
//          portfolio.savePortfolio();
//          menu.successMessage(ticker, details, Txn.Sell);
//        }
//
//      } catch (NumberFormatException e) {
//        menu.errorMessage("Invalid format for 1 or more fields");
//      } catch (IllegalArgumentException e) {
//        menu.errorMessage(e.getMessage());
//      } catch (DateTimeParseException e) {
//        menu.errorMessage("Invalid Date format");
//      }
//    }
//
//    @Override
//    protected Report getPortfolioPerformanceIfAllowed(String portfolioName, String f, String t) {
//      log.append("Inside getPortfolioPerformanceIfAllowed : ").append(portfolioName)
//              .append(f).append(t).append("\n");
//      portfolio.getPortfolioPerformance(LocalDate.parse(f), LocalDate.parse((t)));
//      return null;
//    }
//
//    @Override
//    protected double getCostBasisIfAllowed(String portfolioName, String date) {
//      log.append("Inside getCostBasisIfAllowed : ").append(portfolioName).append(date);
//      return findPortfolio(portfolioName).getCostBasis(LocalDate.parse(date));
//    }
//
//    private boolean satisfiesWeightageTotal(double val) {
//      log.append("Inside satisfiesWeightageTotal\n");
//      return totalWeightage - val >= 0.0;
//    }
//
//    @Override
//    protected void addTickerToStrategyIfAllowed(String ticker, String weightage) {
//      log.append("Inside addTickerToStrategyIfAllowed").append(ticker).append(weightage);
//      try {
//        ticker = ticker.toUpperCase();
//        if (!api.isTickerPresent(ticker)) {
//          api.getShareDetails(ticker, LocalDate.now());
//        }
//        double w = Double.parseDouble(weightage);
//        if (w <= 0) {
//          menu.printMessage("Weightage of a ticker must be > 0");
//        } else if (!satisfiesWeightageTotal(w)) {
//          menu.printMessage("Total weightage should be < 100%");
//        } else {
//          if (!stocksWeightage.containsKey(ticker)) {
//            stocksWeightage.put(ticker, w);
//          } else {
//            stocksWeightage.put(ticker, stocksWeightage.get(ticker) + w);
//          }
//          totalWeightage -= w;
//
//          if (totalWeightage != 0) {
//            menu.printMessage("Successfully added ticker to strategy");
//          } else {
//            menu.printMessage("100% weightage completed");
//          }
//        }
//      } catch (NumberFormatException e) {
//        menu.errorMessage("Invalid format for 1 or more fields");
//      } catch (IllegalArgumentException e) {
//        menu.errorMessage("Invalid ticker");
//      }
//    }
//
//    @Override
//    protected void saveDcaIfAllowed(String portfolioName, String strategyName, String amt, String f, String t, String interval, String commission) {
//      log.append("Inside saveDcaIfAllowed").append(portfolioName).append(strategyName).append(amt).append(f).append(t).append(interval).append(commission);
//
//      try {
//        Portfolio portfolio;
//        if (allPortfolios.contains(portfolioName)) {
//          portfolio = findPortfolio(portfolioName);
//        } else {
//          portfolio = this.portfolio;
//        }
//        double amount = Double.parseDouble(amt);
//        LocalDate from = LocalDate.parse(f);
//        LocalDate to = !t.equals("") ? LocalDate.parse(t) : LocalDate.parse("2100-12-31");
//
//        if (from.compareTo(to) > 0) {
//          menu.printMessage("Start date should be before end date");
//        } else {
//          TimeLine timeline = new TimeLine(from, to);
//          int intervalVal = Integer.parseInt(interval);
//          if (intervalVal < 1) {
//            menu.printMessage("Interval should be at least 1 day");
//          } else {
//            double commissionFee = Double.parseDouble(commission);
//            doDca(portfolio, strategyName, amount, stocksWeightage,
//                    timeline, intervalVal, commissionFee);
//          }
//        }
//      } catch (NumberFormatException e) {
//        menu.errorMessage("Invalid format for 1 or more fields");
//      } catch (DateTimeParseException e) {
//        menu.errorMessage("Invalid date format");
//      } catch (IllegalArgumentException e) {
//        menu.errorMessage(e.getMessage());
//      }
//    }
//
//    private void doDca(Portfolio portfolio, String strategyName, double amount,
//                       Map<String, Double> stocksWeightage, TimeLine timeline,
//                       int interval, double commission) {
//      log.append("Inside doDca");
//
//      if (this.totalWeightage != 0) {
//        menu.printMessage("Total weightage is not 100%");
//      } else {
//        try {
//          Dca dca = new Dca(amount, stocksWeightage, timeline, interval, commission);
//          portfolio.doDca(strategyName, dca);
//          menu.printMessage("Successfully created strategy.");
//        } catch (IllegalArgumentException e) {
//          menu.errorMessage(e.getMessage());
//        }
//      }
//    }
//  }
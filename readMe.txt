****  DSE - dream Stock exchange

1) StockExOrderMatchEngineApplication -- spring boot application configured to run in server.port = 1902
         , can be modified in app properties.
         -- if required use Mvn Run configuration ----[ spring-boot:run -f pom.xml ]

2) TestRunnerRestClient controller , places test orders while start up for 2 securities ABC.XM && XYZ.XM

3) Following is the flow
    Order request flow in this order
       # OrderRequestController:bookOrder   --- http://localhost:1902/orderApi/bookOrder/VOD.L/SELL/99/14.9
       # OrderRequestService:processOrder [place the order into order engine]
       # OrderProcessingEngine:placeOrder [ saves the order into in memory store
                                            | notify the quote service when buy/sell order gets top of the queue ]
       # QuoteService:updateQuote  [ stores the ask & bid price to store     http://localhost:1902/quoteApi/getAllQuotes
                                            | notify the listeners when ask < bid , OrderProcessingEngine is one the listiner
                                            | Idea was quote to has the responsiblity of storing quote and invoke the
                                                 listeners when a execution match is found ]
       # OrderProcessingEngine:executeOrder [creates trades for matching buy and sell order
                           | Creates partial order  when (buy quanity - sell quanity) ! = 0]
       # TradeService:createTrade [creates trade and places them in store http://localhost:1902/tradeApi/getTrades]


4) url to get current snap shot  of a security book http://localhost:1902/orderApi/getOrderBook/ABC.XM/4

5) Idea is every security has seperate book , so they can be processed in parallel.
   Also book has two queues , buyer and seller , so they can be work in parallel.
   When a order is ready to execute , add to orders to buyer and seller is paused.


Once the app is Up , you can play with these end points and curl

http://localhost:1902/orderApi/getOrderBook/ABC.XM/4
http://localhost:1902/orderApi/bookOrder/VOD.L/SELL/99/14.9
http://localhost:1902/quoteApi/getAllQuotes
http://localhost:1902/tradeApi/getTrades

# this is a prototype
#TODO - Test coverage
#TODO - comments are documentation
#TODO - logging is not adequate
#TODO - load testing
#TODO - immutable work not fully one




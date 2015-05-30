# tweet-map
A heat map visualization of where people tweet.

This is a web application that depicts tweets in a heat map as they are being collected in near real time. The application utilizes AJAX, sending GET request to update for every X seconds as the streamer listens to and saves tweets into DynamoDB.

The front end includes search funcationality where you may choose to depict tweets by keyword, language, start date, end date, and any combinations of the mentioned. The front end before conducting the search also validates the input date, and only processes valid input.

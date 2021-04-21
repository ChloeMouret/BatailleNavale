# BatailleNavale

![](https://github.com/ChloeMouret/BatailleNavale/blob/master/picture/screen.png)

This project is a web implementation of the famous Naval Battle game. It relies on [JAVA Spark](https://sparkjava.com/documentation) for the web framework & websockets for the realtime updates 
with the backend.

The application is hosted on Heroku at the following [address](https://touche-coule.herokuapp.com/) if you want to test the game (sorry for non-french users).

![](https://github.com/ChloeMouret/BatailleNavale/blob/master/picture/screen2.png)

## External features

This project uses [notify.js](https://notifyjs.jpillora.com/) to notify other players of the game changes.

## How to play

The master branch is deployed on heroku at [this link](https://touche-coule.herokuapp.com/). It is not persistent yet, so you will need to avoid refreshing the page. 
You just need to create a game, share the id with the other player, and both choose your boats position. Once this is done, the game will run between different pages. 
The trading system is not implemented yet, and the game lacks a bit of information (notifications), but it is coming soon.


# Entity Utils

A papermc plugin that adds various quality of life features for entities.  

# Structure 

EntityUtils is broken into two main packages: an entity package and a utils package (pun intended!).  
The entity package contains the code for creating, managing, and modifying entities, while the utils package
contains more general utilities that most entities need, like a packet listener, data classes, math utilities, 
and custom event classes.  In a future update the math utilities should be replaced with [MathUtils] (https://github.com/Yoursole1/MathUtils), 
a more polished version created for more than just this project.  

# Features

The main feature is an player-model NPC system.  This system includes managing static NPCs, teleporting, making the NPC headtrack, 
setting skins (with any combination of skin layers), and making the NPC walk, complete with custom pathfinding and animations.  
The pathfinding is done with a slightly modified version of the A* algorithm, which works very well since minecraft is already seperated into nodes (blocks).  The pathfinding code is totally seperate from minecraft though, so in theory it could be moved to another java project successfully.  

Another feature is the hologram system, currently living on the hologram-board branch.  This was created by [@MarvelousAnything](https://www.github.com/MarvelousAnything), and makes scoreboards that self align and update using an event based system.  It is not finished yet though.  

# Getting Started

Clone this repository and move the code into your project (no need to put it in a jar, I really don't care if you use it decompiled). Copy the code from the main class here into your plugins main class, making sure that all the event registration stuff is good to go. Now just make an instance of the static or animated PlayerNpc class you're good to go!


## Badges

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

## Run Locally

Clone the project

```bash
  git clone https://github.com/Yoursole1/EntityUtils.git
```

Go to the project directory

```bash
  cd EntityUtils
```

Build jar

```bash
  ./gradlew build
```

Start the paper server

```bash
  ./gradlew :runServer
```


## Authors

- [@Yoursole1](https://www.github.com/Yoursole1)
- [@MarvelousAnything](https://www.github.com/MarvelousAnything)


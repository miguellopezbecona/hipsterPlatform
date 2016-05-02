## What is this project about?

I made this project for my Bachelor's Thesis. I have already finished it, so you are free to contribute to the repository. The application consists in a web application where you can visualize the execution of classic search algorithms. It is composed by a server-client model. The features are divided in the following way:

#### Main features

* Client
    * Visualizes graphs (done with [D3.js](https://github.com/mbostock/d3)).
    * Selects options such as search algorithms, layout algorithms, graph to work with...
    * Zoom in, zoom out and graph centering.
    * Grid-like graph generator.
* Server
    * Executes algorithms (done with Hipster, described later). Algorithms included:
        * DFS: Depth-First-Search.
        * BFS: Breadth-First-Search.
        * Dijkstra.
        * Bellman-Ford.
        * A star (A*).
    * Saves graphs loaded by the user
    * Provides a REST web service which applies a layout algorithm (done mostly with JUNG) to the graph which you are working with

#### Hipster project
![Hipster logo](https://raw.githubusercontent.com/miguellopezbecona/hipsterPlatform/master/img/hipsterLogo.png)

The graph-search component of this application is [Hipster](https://github.com/citiususc/hipster), which gives part of the name of this project. It was created and maintained by [Pablo Rodríguez Mier](https://github.com/pablormier) and [Adrián González Sieira](https://github.com/gonzalezsieira).

The aim of Hipster is to provide an easy to use yet powerful and flexible type-safe Java library for heuristic search. Hipster relies on a flexible model with generic operators that allow you to reuse and change the behavior of the algorithms very easily. Algorithms are also implemented in an iterative way, avoiding recursion. This has many benefits: full control over the search, access to the internals at runtime or a better and clear scale-out for large search spaces using the heap memory.

## Getting started

This app is [deployed in Heroku](http://hipster-platform.herokuapp.com/). If you want to execute it locally, you need to have installed Maven and Java. After this, open a command-line interpreter (such as bash), go to the source code folder, and execute this:  
	mvn package && mvn exec:java

The app will be ready when there isn't new output and the last thing printed looks like:  
	+- sun.misc.Launcher$ExtClassLoader@3449a8

So, after this, you can access to the app in:  
<http://localhost:8080>

## License

This software is licensed under the Apache 2 license.

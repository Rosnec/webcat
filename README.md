# webcat

CSC365 Assignment 1: A website categorization program. Maintains a database of
websites in pre-defined categories, Physics, Programming Languages, and
Baseball Teams. The user inputs a URL, and the program displays which category
it fits best, as well as the closest URL match. Uses a Chi-squared test to
calculate the difference in word frequencies between the user input page, and
the pre-defined page, and uses the page with the lowest Chi-squared value. For
matching categories, takes the average of each site in the category.

Also implements a barely functioning binary search tree, based on Clojure's
PersistentTreeMap.

## Usage

Run with Leiningen using

    $ lein run

or using the provided jar with

    $ java -jar webcat-1.0.0-standalone.jar

## License

Copyright © 2014 Dan Wysocki

Distributed under the GNU General Public License version 3

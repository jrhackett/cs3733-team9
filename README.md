# CapraNav

CapraNav is a navigation system for the Worcester Polytechnic Institute campus. It provides detailed maps of the campus and of several buildings. The program provides turn-by-turn directions as well as a graphically drawn path on the maps.

We also developed a MapBuilder tool too speed up the process of adding nodes and edges to maps. It is very intuitive to use; you can add nodes and edges to maps by clicking directly on the maps.

## History

CapraNav was created for CS 3733 Software Engineering at Worcester Polytechnic Institute during B term 2015. 

## Implementation

CapraNav uses the A* algorithm to find the shortest path between locations on campus. The UI is done in Java FX. The graph is stored locally in JSON files, parsed into a HashMap using GSON. Email functionality is done using the JavaX Mail API. CapraSync (the "Get Involed" pane) parses XML data from OrgSync's public RSS feed.

## Installation

Download our repo, open the "FinalProduct" folder. Within that are all of the program files. capra.exe / capra.jar is CapraNav, devtool.jar is the MapBuilder tool.

## Usage

On opening CapraNav for the first time, you will be prompted to try the tutorial. After that, you can reopen the tool tips by clicking the question mark in the menu bar.

## Credits

#####Project Manager and Scrum Master: 
Greg Tighe
#####Product Owner: 
Mike Giancola
#####Lead Software Engineer: 
Jake Hackett
#####UX/UI: 
Charlie Lovering
#####Test Engineer/Document Specialist: 
Henry Wheeler-Mackta

#####Other members: 
Kurt Bugbee

Josh Friscia

Tucker Martin

Anthony Ratte

#####Coach/Adviser:
Nilesh Patel
#####Instructor:
Wilson Wong


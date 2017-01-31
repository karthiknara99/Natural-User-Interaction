This is a command line offline Recognition Engine of the NUI pipeline using the $P algorithm.

Place pdollar.exe and PDollarGestureRecognizer.dll in a folder and navigate on cmd to that folder and then run the application.
eg:
pdollar –t C:\Users\..\arrowhead.txt : Adds the gesture file to the list of gesture templates
pdollar -r : Clears the templates
pdollar C:\Users\..\arrowhead_event.txt : Prints the name of gestures as they are recognized from the event stream

Note:
1. Please provide full path name where the file exists
2. Please ensure there are no spaces in the path name

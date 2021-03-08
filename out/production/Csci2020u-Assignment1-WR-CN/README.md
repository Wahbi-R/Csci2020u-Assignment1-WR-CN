This is assignment 1 for CSCI2020U. 

Group Members: Wahbi Raihan (100751913) & Chau Nguyen (100747411)

This is a Spam Detector that checks a file or a directory/folder and searches 
through every file in a folder called Ham and Spam and makes a list of the occurences.
It then compares the two lists and finds common words and tells you whether a selected folder
has spam or not. (The reason we don't round our probabilities here is due to the fact that all our probabilities would end up as zero, the function still exists and can be used easily)

The interface has been improved so that now the Accuracy and Precision is bold, colours have been added
as well to the background, textboxes and text making it look much more aesthetically pleasing.

step-by-step information on how one can successfully clone and run your
application

Step 1: open the github repository: https://github.com/Wahbi-Raihan/Csci2020u-Assignment1-WR-CN
Step 2: click on code and copy the web URL
Step 3: open cmd or Linux Terminal and type in git clone "web URL"
Step 4: open the folder in Intellij (Recommended)
Step 5: Under the project tab, open External Libraries and right click then choose "Open Library Settings"
Step 6: Add the proper SDK version(15.0.2 as newest version) and the required libraries in Global Libraries.
Step 7: Select Apply then OK.
Step 8: On the tool bar, under Run, select Edit Configuration
Step 9: Under Templates, add Application, choose the same java version as your SDK version.
Step 10: Type in the name of the main class.
Step 11: Under Modify options, choose Add VM Options, copy the absolute path to the lib folder in your java version
and fill it in this line --module-path "lib path here" --add-modules javafx.controls,javafx.fxml
Step 12: Hit Apply then OK.
Your program should be able to run now.
Step 13: Under Run, choose run, then run Main
Step 14: When the directory chooser interface shows up, choose the folder you want to calculate the spam probability then select OK


References:
https://www.youtube.com/watch?v=FLkOX4Eez6o&list=PL6gx4Cwl9DGBzfXLWLSYVy8EbTdpGbUIG (Overall JavaFX Guides)
https://learn.ontariotechu.ca/courses/11612/modules (CSCI2020U module page for info & help)
https://www.rapidtables.com/web/css/css-color.html#blue (CSS Colours)
https://howtodoinjava.com/java/collections/arraylist/iterate-through-objects/ (Java ArrayList)
# SnakesAndLadders
![GitHub Logo](illustration.png)
Format: ![Screenshot of the game](url)

Wrote this ages ago in the second year. Back then I knew nothing about multithreading or serialisation. The year before that we were introduced to C, and teaching of Java was lacking to say the least - I spent a week trying to fiogure out how to deallocate memory :D
This was the first application I ever made that actually did something. 

# Repo Contents
There is a jar file, you can use it to try the game. THere is also a pdf which was my school report, it explains the game in detail. 
There is a bug somewhere in the threading code, that causes the player figure's visual position to drift from it's actual place.
That is probably related to the ridiculous way in which animation is implemented: I created a thread per figure on the board, and animate them by moving the figure a little, then sleeping the thread, then moving the figure a little, etc. 


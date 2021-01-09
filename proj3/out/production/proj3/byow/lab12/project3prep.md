# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: My implementation of hexagon didn't include the position of the hexagon in the overall map (upperleft for example). 
I learned to plan more for the Hexagon class.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: Tessellating hexagons  is like randomly generating some space on the map (rooms, hallways, etc).

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: I would first write a room class.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: The tiles are different for the two. They are similar in the sense that the player can walk through them and they can
extend a Space class.

# LinkState
My design uses heap and its bubbleUp, sinkBottom, and other methods typically found in it to find the shortest path from a source node to a destination node. It also uses static classes edge and graph to determine the links.
Since a graph contains V vetrices and E edges, the time complexities are as follows with the heap:
-Extracting a vertex from a heap is O(logN).
-Extracting V vertices from the heap is O(VlogN).
-Decrease the distance of a vertex is O(logN).
-For E edges, the decrease in distance will at most be called O(ElogN) times.
-Overall, the complexity is O(ElogV) to find the shortest path from a source to a destination.

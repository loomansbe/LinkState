import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.IntStream;

public class LinkState {

    static class Node {
        int vertex;
        float distance;
        String pathName = "";

        public void addPath(String path) {
            this.pathName = this.pathName.concat(path);
        }

        public void replacePath(String path) {
            this.pathName = path;
        }

        public String getPath() {
            return pathName;
        }
    }

    static class Edge {
        int sourceNode;
        int destinationNode;
        float edgeWeight;

        public Edge(int sourceNode, int destinationNode, float edgeWeight) {
            this.sourceNode = sourceNode;
            this.destinationNode = destinationNode;
            this.edgeWeight = edgeWeight;
        }
    }

    static class Heap {
        int size;
        int numVertices;
        Node[] heap;
        int[] heapIndexes;

        public Heap(int numVertices) {
            this.numVertices = numVertices;
            heap = new Node[numVertices + 1];
            heapIndexes = new int[numVertices];
            size = 0;
            heap[0] = new Node();
            heap[0].vertex = 0;
            heap[0].distance = Float.MIN_VALUE;
        }

        public void add(Node added) {
            size++;
            int index = size;
            heap[index] = added;
            heapIndexes[added.vertex] = index;
            bubbleTop(index);
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public void bubbleTop(int index) {
            int parIndex = index/2;
            int curIndex = index;
            while(curIndex > 0 && heap[parIndex].distance > heap[curIndex].distance) {
                Node curNode = heap[curIndex];
                Node parNode = heap[parIndex];
                heapIndexes[curNode.vertex] = parIndex;
                heapIndexes[parNode.vertex] = curIndex;
                swap(curIndex, parIndex);
                curIndex = parIndex;
                parIndex = parIndex/2;
            }
        }

        public void swap(int current, int swapped) {
            Node temp = heap[current];
            heap[current] = heap[swapped];
            heap[swapped] = temp;
        }

        public void sinkBottom(int index) {
            int minimum = index;
            int leftIndex = 2 * index;
            int rightIndex = 2 * index + 1;
            if(leftIndex < size && heap[minimum].distance > heap[leftIndex].distance)
                minimum = leftIndex;
            if(rightIndex < size && heap[minimum].distance > heap[rightIndex].distance)
                minimum = rightIndex;
            if(minimum != index) {
                Node minimumNode = heap[minimum];
                Node swapped = heap[index];
                heapIndexes[minimumNode.vertex] = index;
                heapIndexes[swapped.vertex] = minimum;
                swap(index, minimum);
                sinkBottom(minimum);
            }
        }

        public Node getMinimum() {
            Node minimum = heap[1];
            Node finalNode = heap[size];
            heapIndexes[finalNode.vertex] = 1;
            heap[1] = finalNode;
            heap[size--] = null;
            sinkBottom(1);
            return minimum;
        }
    }

    static class Graph {
        LinkedList<Edge>[] graph;
        int vertices;

        Graph(int vertices) {
            this.vertices = vertices;
            graph = new LinkedList[vertices];
            IntStream.range(0, vertices).forEach(i -> graph[i] = new LinkedList<>());
        }

        public void createLink(int sourceNode, int destinationNode, float edgeWeight) {
            //One way path to start
            Edge edge = new Edge(sourceNode, destinationNode, edgeWeight);
            graph[sourceNode].add(edge);
            //Bidirectional path always
            edge = new Edge(destinationNode, sourceNode, edgeWeight);
            graph[destinationNode].add(edge);
        }

        public void findMinimumPaths(int sourceNode) {
            boolean[] checkLink = new boolean[vertices];
            float INFINITY = Float.MAX_VALUE;

            Node[] nodes = new Node[vertices];
            for(int i = 0; i < vertices; i++) {
                nodes[i] = new Node();
                nodes[i].vertex = i;
                nodes[i].distance = INFINITY;
                nodes[i].addPath(sourceNode + "");
            }
            //The source node has no distance to travel to reach itself
            nodes[sourceNode].distance = 0;

            Heap heap = new Heap(vertices);
            for(int i = 0; i < vertices; i++)
                heap.add(nodes[i]);

            while(!heap.isEmpty()) {
                Node minimumNode = heap.getMinimum();
                int minimumVertex = minimumNode.vertex;
                checkLink[minimumNode.vertex] = true;

                LinkedList<Edge> list = graph[minimumVertex];
                for(int i = 0; i < list.size(); i++) {
                    Edge edge = list.get(i);
                    int destinationNode = edge.destinationNode;
                    if(checkLink[destinationNode] == false) {
                        float newDistance = nodes[minimumVertex].distance + edge.edgeWeight;
                        float currentDistance = nodes[destinationNode].distance;
                        if(currentDistance > newDistance) {
                            decreaseDistance(heap, newDistance, destinationNode);
                            nodes[destinationNode].distance = newDistance;
                            nodes[destinationNode].replacePath(nodes[minimumVertex].pathName + "->" + nodes[destinationNode].vertex);
                        }
                    }
                }
            }

            showResults(nodes, sourceNode);
        }

        public void showResults(Node [] results, int sourceNode) {
            for(int i = 0; i < vertices; i++) {
                if(i != sourceNode)
                    System.out.println("shortest path to node " + i + " is " + results[i].getPath() + " with cost " +
                            results[i].distance);
            }
        }

        public void decreaseDistance(Heap heap, float newDistance, int vertex) {
            int index = heap.heapIndexes[vertex];
            Node node = heap.heap[index];
            node.distance = newDistance;
            heap.bubbleTop(index);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if(args.length >= 2)
        {
            File file = new File(args[0]);
            int sourceVertex = Integer.parseInt(args[1]);
            Scanner scanner = new Scanner(file);
            int numVertices = scanner.nextInt();
            int source;
            int destination;
            float cost;
            Graph graph = new Graph(numVertices);
            while(scanner.hasNext())
            {
                source = scanner.nextInt();
                destination = scanner.nextInt();
                cost = scanner.nextFloat();
                graph.createLink(source, destination, cost);
            }
            graph.findMinimumPaths(sourceVertex);
        }
        else
            System.out.println("Not enough parameters provided! " +
                    "A File input and a number of vertices are required!");
    }
}

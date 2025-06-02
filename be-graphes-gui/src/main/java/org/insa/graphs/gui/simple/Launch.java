package org.insa.graphs.gui.simple;

//import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.BinaryPathReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.io.PathReader;

import java.util.Random;

public class Launch {

    /**
     * Create a new Drawing inside a JFrame an return it.
     *
     * @return The created drawing.
     * @throws Exception if something wrong happens when creating the graph.
     */
    public static Drawing createDrawing() throws Exception {
        BasicDrawing basicDrawing = new BasicDrawing();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("BE Graphes - Launch");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                frame.setSize(new Dimension(800, 600));
                frame.setContentPane(basicDrawing);
                frame.validate();
            }
        });
        return basicDrawing;
    }

    public static boolean AssertEquals (double a, double b) throws NotIdenticPaths{
        if (a - b > 0.0001) throw new NotIdenticPaths("EXECUTION ERROR : Not identic paths !");
        return true;
    }

    public static void Compare_Dijkstra_Path(ShortestPathData data){
        //###############################
        // EXECUTION DE L'ALGORITHME
        //###############################
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        ShortestPathSolution resultat_dijkstra = dijkstra.run();

        Path path = Path.createShortestPathFromNodes(data.getGraph(),resultat_dijkstra.getPath().getNodesFromPath());
        double Cout;
        if (data.getMode() == Mode.LENGTH){
            Cout = path.getLength();
        }else{
            Cout = path.getMinimumTravelTime();
        }

        //###############################
        // VERIFICATION 
        //###############################
        try {
            if (data.getMode() == Mode.LENGTH){
                AssertEquals(resultat_dijkstra.getPath().getLength(),Cout);
                System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getLength() + " m.");
                System.out.println("--- Chemin trouvé par la classe Path :  " + Cout + " m.");
            }else{
                AssertEquals(resultat_dijkstra.getPath().getMinimumTravelTime(),Cout);
                System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getMinimumTravelTime() + " min.");
                System.out.println("--- Chemin trouvé par la classe Path :  " + Cout + " min.");
            }
            System.out.println("--- SUCCESS");
        }catch(NotIdenticPaths e){
            System.out.println(e);
        }
    }

    public static void Compare_Dijkstra_Bellman(ShortestPathData data){
        //###############################
        // EXECUTION DE L'ALGORITHME
        //###############################
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        ShortestPathSolution resultat_dijkstra = dijkstra.run();
        BellmanFordAlgorithm bellman = new BellmanFordAlgorithm(data);
        ShortestPathSolution resultat_bellman = bellman.run();

        //###############################
        // VERIFICATION 
        //###############################
        try {
            if (data.getMode() == Mode.LENGTH){
                AssertEquals(resultat_dijkstra.getPath().getLength(),resultat_bellman.getPath().getLength());
                System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getLength() + " m.");
                System.out.println("--- Chemin trouvé par Bellman-Ford :    " + resultat_bellman.getPath().getLength() + " m.");
            }else{
                AssertEquals(resultat_dijkstra.getPath().getMinimumTravelTime(),resultat_bellman.getPath().getMinimumTravelTime());
                System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getMinimumTravelTime() + " min.");
                System.out.println("--- Chemin trouvé par Bellman-Ford :    " + resultat_bellman.getPath().getMinimumTravelTime() + " min.");
            }
            System.out.println("--- SUCCESS");
        }catch(NotIdenticPaths e){
            System.out.println(e);
        }
    }

    public static void Test_Infeasible(ShortestPathData data){
        //###############################
        // EXECUTION DE L'ALGORITHME
        //###############################
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        ShortestPathSolution resultat_dijkstra = dijkstra.run();

        //###############################
        // VERIFICATION 
        //###############################
        if (resultat_dijkstra.getStatus() == Status.INFEASIBLE){
            System.out.println("Dijkstra : Aucun chemin n'a été trouvé ");
            System.out.println("SUCCESS");
        }else{
            System.out.println("FAIL");
        }
    }

    public static void Check(ShortestPathData data, Drawing draw) throws Exception{
        //###############################
        // EXECUTION DES ALGORITHMES
        //###############################
        System.out.println("Execution des 2 algorithmes...");
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        BellmanFordAlgorithm bellman = new BellmanFordAlgorithm(data);

        ShortestPathSolution resultat_dijkstra = dijkstra.run();
        ShortestPathSolution resultat_bellman = bellman.run();

        //###############################
        // TRACE DES RESULTATS
        //###############################
        System.out.println("Représentations des solutions trouvées sur le graphe...");
        Graph graph = data.getGraph();
        // create the drawing

        draw.drawGraph(graph);
        // draw the path on the drawing
        draw.drawPath(resultat_dijkstra.getPath(),false);
        draw.drawPath(resultat_bellman.getPath(),false);

        //###############################
        // VERIFICATION 
        //###############################
        System.out.println("Vérifications de l'égalité entre les solutions trouvées...");
        try {
            AssertEquals(resultat_dijkstra.getPath().getLength(),resultat_bellman.getPath().getLength());
            System.out.println("Chemin trouvé par Bellman : " + resultat_bellman.getPath().getLength() + " km.");
            System.out.println("Chemin trouvé par Dijkstra : " + resultat_dijkstra.getPath().getLength() + " km.");
            System.out.println("Les 3 algorithmes renvoient des résultats cohérents et similaires !");
        }catch(NotIdenticPaths e){
            System.out.println(e);
        }
}

    public static void main(String[] args) throws Exception {

        // visit these directory to see the list of available files on commetud.
        String mapName ;
        Graph graph;
        Node originNode ;
        Node destinationNode ;
        ShortestPathData data;
        
        System.out.println("\n[Tests infaisables] ------------------------------");
        mapName ="/home/eloi/Bureau/cartes/bretagne.mapgr";
        try (GraphReader reader = new BinaryGraphReader(new DataInputStream(
            new BufferedInputStream(new FileInputStream(mapName))))) {

        graph = reader.read();
        }
        originNode = graph.get(372251);     //île de Belle-Île
        destinationNode = graph.get(41656); //Ville de Rennes
        data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(0));
        System.out.println("Test 1 : Belle-île --> Rennes");
        Test_Infeasible(data);

        originNode = graph.get(28673);      //île de Groix
        destinationNode = graph.get(86314); //Vile de Quimper
        data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(0));
        System.out.println("Test 2 : Groix --> Quimper");
        Test_Infeasible(data);






        System.out.println("\n[Tests chemins nuls] ------------------------------");
        originNode = graph.get(28673);      //île de Groix
        destinationNode = graph.get(28673); //île de Groix
        data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(0));
        System.out.println("Test 1 : Rennes --> Rennes en distance");
        Compare_Dijkstra_Path(data);






        System.out.println("\n[Tests chemins courts] ------------------------------");
        mapName ="/home/eloi/Bureau/cartes/paris.mapgr";
        try (GraphReader reader = new BinaryGraphReader(new DataInputStream(
            new BufferedInputStream(new FileInputStream(mapName))))) {

        graph = reader.read();
        }
        originNode = graph.get(12620);
        destinationNode = graph.get(39307);
        data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(0));
        System.out.println("Test 1 : Paris");
        Compare_Dijkstra_Path(data);
        Compare_Dijkstra_Bellman(data);

        originNode = graph.get(12620);
        destinationNode = graph.get(39307);
        data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(0));
        System.out.println("Test 1 : Toulouse");
        Compare_Dijkstra_Path(data);
        Compare_Dijkstra_Bellman(data);






        /* System.out.println("VAGUE DE TEST : PARIS, SHORTEST ------------------------------");
        for (int i = 0; i<3;i++){
            System.out.println("TEST N°" + i + ":");
            Random r= new Random();
            int bound = graph.getNodes().size();
            originNode = graph.get(r.nextInt(bound));
            destinationNode = graph.get(r.nextInt(bound));
            data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(0));
            Compare_Dijkstra_Path(data);
        }

        System.out.println("VAGUE DE TEST : PARIS, FASTEST ------------------------------");
        for (int i = 0; i<3;i++){
            System.out.println("TEST N°" + i + ":");
            Random r= new Random();
            int bound = graph.getNodes().size();
            originNode = graph.get(r.nextInt(bound));
            destinationNode = graph.get(r.nextInt(bound));
            data = new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(1));
            Compare_Dijkstra_Path(data); 
        } */
        
    }





}

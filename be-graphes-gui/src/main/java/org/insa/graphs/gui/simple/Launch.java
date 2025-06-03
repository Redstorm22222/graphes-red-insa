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

import java.util.List;
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

    public static void AssertEquals (double a, double b) throws FailTest{
        if (a - b > 0.0001) throw new FailTest("EXECUTION ERROR : Not identic paths !");
    }

    public static void AssertValid (Path path) throws FailTest{
        if (!path.isValid()) throw new FailTest("EXECUTION ERROR : Path invalid !");
    }

    public static void Compare_Dijkstra_Path(ShortestPathData data) throws FailTest{
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
        AssertValid(resultat_dijkstra.getPath());
        if (data.getMode() == Mode.LENGTH){
            AssertEquals(resultat_dijkstra.getPath().getLength(),Cout);
            System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getLength() + " m.");
            System.out.println("--- Chemin trouvé par la classe Path :  " + Cout + " m.");
        }else{
            AssertEquals(resultat_dijkstra.getPath().getMinimumTravelTime(),Cout);
            System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getMinimumTravelTime() + " sec.");
            System.out.println("--- Chemin trouvé par la classe Path :  " + Cout + " sec.");
        }
    }

    public static void Compare_Dijkstra_Bellman(ShortestPathData data) throws FailTest{
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
        AssertValid(resultat_dijkstra.getPath());
        if (data.getMode() == Mode.LENGTH){
            AssertEquals(resultat_dijkstra.getPath().getLength(),resultat_bellman.getPath().getLength());
            System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getLength() + " m.");
            System.out.println("--- Chemin trouvé par Bellman-Ford :    " + resultat_bellman.getPath().getLength() + " m.");
        }else{
            AssertEquals(resultat_dijkstra.getPath().getMinimumTravelTime(),resultat_bellman.getPath().getMinimumTravelTime());
            System.out.println("--- Chemin trouvé par Dijkstra :        " + resultat_dijkstra.getPath().getMinimumTravelTime() + " sec.");
            System.out.println("--- Chemin trouvé par Bellman-Ford :    " + resultat_bellman.getPath().getMinimumTravelTime() + " sec.");
        }
    }

    public static void Test_Infeasible(ShortestPathData data) throws FailTest{
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
        }else{
            throw new FailTest("INFEASIBLE TESTS ERROR");
        }
    }

    public static void Wave_Short_Test(int [] TabTests,String map, int arcinspector) throws Exception {
        for (int i = 0; i < TabTests.length-1 ; i++){
            ShortestPathData data = BuildTest(TabTests[i], TabTests[i+1], map, arcinspector); 
            Compare_Dijkstra_Path(data);
            Compare_Dijkstra_Bellman(data);
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
        }catch(FailTest e){
            System.out.println(e);
        }
}

    public static ShortestPathData BuildTest(int originNode_id, int destinationNode_id, String map, int arcinspector) throws Exception {
        Graph graph;
        String full_mapName ="/home/eloi/Bureau/cartes/" + map + ".mapgr";
        try (GraphReader reader = new BinaryGraphReader(new DataInputStream(
            new BufferedInputStream(new FileInputStream(full_mapName))))) {

        graph = reader.read();
        }
        Node originNode = graph.get(originNode_id);
        Node destinationNode = graph.get(destinationNode_id); 
        return new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(arcinspector));
    }

    public static ShortestPathData BuildRandomTest(String map, int arcinspector) throws Exception {
        Graph graph;
        String full_mapName ="/home/eloi/Bureau/cartes/" + map + ".mapgr";
        try (GraphReader reader = new BinaryGraphReader(new DataInputStream(
            new BufferedInputStream(new FileInputStream(full_mapName))))) {

        graph = reader.read();
        }
        Random r= new Random();
        int bound = graph.getNodes().size();
        Node originNode = graph.get(r.nextInt(bound));
        Node destinationNode = graph.get(r.nextInt(bound)); 
        return new ShortestPathData(graph, originNode, destinationNode, ArcInspectorFactory.getAllFilters().get(arcinspector));
    }

    /*
    public static void runFullTest(Graph graph, int originId, int destinationId, Drawing draw) throws Exception {
        Node origin = graph.get(originId);
        Node destination = graph.get(destinationId);
        List<Mode> modes = List.of(Mode.LENGTH, Mode.TIME);
        for (Mode mode : modes) {
            ShortestPathData data = new ShortestPathData(
                    graph,
                    origin,
                    destination,
                    // On choisit l’ArcInspector en fonction du Mode :
                    //  -> si Mode.LENGTH, on prend l’inspecteur “distance” (index 0)
                    //  -> si Mode.TIME, on prend l’inspecteur “temps” (index 1)
                    ArcInspectorFactory.getAllFilters().get(
                        (mode == Mode.LENGTH) ? 0 : 1
                    )
            );
            System.out.println("\n===== Mode de test : " + mode + " =====");
            if (!origin.equals(destination)) {
                System.out.println("→ Test d’infaisable (origin ≠ dest)");
                Test_Infeasible(data);
            } else {
                System.out.println("→ Test de chemin nul (origin == dest)");
                Compare_Dijkstra_Path(data);
            }
            System.out.println("→ Vérif. Dijkstra Path pour le coût (" + mode + ")");
            Compare_Dijkstra_Path(data);
            System.out.println("→ Vérif. Dijkstra Bellman‐Ford (" + mode + ")");
            Compare_Dijkstra_Bellman(data);
            if (draw != null) {
                System.out.println("→ Trace des chemins trouvés dans le Drawing…");
                draw.drawGraph(graph);
                ShortestPathSolution solDij = new DijkstraAlgorithm(data).run();
                ShortestPathSolution solBel = new BellmanFordAlgorithm(data).run();
                draw.drawPath(solDij.getPath(), false);
                draw.drawPath(solBel.getPath(), false);
            }
        }
    }
     */

    public static void main(String[] args) throws Exception, FailTest {

        ShortestPathData data;
        
        System.out.println("################################################################");
        System.out.println("####### TESTS INFAISABLES ######################################");
        System.out.println("################################################################\n");
        System.out.println("Test 1 : BRETAGNE : Belle-île --> Rennes");
        
        try {
            //île de Belle-Île
            //Ville de Rennes
            data = BuildTest(372251, 41656, "bretagne", 0); 
            Test_Infeasible(data);

            //île de Groix
            //Vile de Quimper
            System.out.println("Test 2 : BRETAGNE : Groix --> Quimper");
            data = BuildTest(28673, 86314, "bretagne", 0); 
            Test_Infeasible(data);

            //île de Marie-Galante
            //île de La Désirade
            System.out.println("Test 3 : GUADELOUPE : Marie-Galante --> La Désirade");
            data = BuildTest(4232, 12748, "guadeloupe", 0); 
            Test_Infeasible(data);

            //TODO : 
            /* //Corse
            //France métropolitaine
            System.out.println("Test 3 : FRANCE METROPOLITAINE : Corse --> Marseille");
            data = BuildTest(4232, 12748, "guadeloupe", 0); 
            Test_Infeasible(data); */

            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS INFAISABLES");
        }




        System.out.println("\n");
        System.out.println("################################################################");
        System.out.println("####### TESTS DE CHEMINS NULS ##################################");
        System.out.println("################################################################\n");
        try {
            //île de Groix
            //île de Groix
            System.out.println("Test 1 : Rennes --> Rennes en distance");
            data = BuildTest(28673, 28673, "bretagne", 0); 
            Compare_Dijkstra_Path(data);


            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS CHEMINS NULS");
        }




        System.out.println("\n");
        System.out.println("################################################################");
        System.out.println("####### TESTS DE CHEMINS COURTS ################################");
        System.out.println("################################################################\n");
        //for (int i = 0; i<3;i++){
        //    System.out.println("TEST N°" + i + ":");
        //    data = BuildRandomTest("paris", 0); 
        //    Compare_Dijkstra_Path(data);
        //}
        try {
            System.out.println("Vague 1 : Plus courts chemins à Paris en voiture");
            int [] TestsParis = {   18542,31357,
                                    9947,6776,
                                    12620,39307};
            Wave_Short_Test(TestsParis,"paris",0);
            System.out.println("Vague 2 : Chemins les plus rapides à Toulouse en voiture");
            int [] TestsToulouse = {8755,2212,
                                    13507,23549,
                                    4143,1271};
            Wave_Short_Test(TestsToulouse,"toulouse",2);
            System.out.println("Vague 3 : Être le plus rapide à Bordeaux à pied");
            int [] TestsBordeaux = {653,6459,
                                    5248,14956,
                                    15132,12216};
            Wave_Short_Test(TestsBordeaux,"bordeaux",2);


            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS CHEMINS COURTS");
        }
        
        


        System.out.println("\n");
        System.out.println("################################################################");
        System.out.println("####### TESTS DE CHEMINS LONGS #################################");
        System.out.println("################################################################\n");
        System.out.println("Test 1 : Rennes --> Brest en distance");
        try{
            data = BuildTest(41675, 75642, "bretagne", 0); 
            Compare_Dijkstra_Path(data);
            System.out.println("Test 2 : Port-Au-Prince --> Higûhey en temps");
            data = BuildTest(83370, 201656, "bretagne", 2); 
            Compare_Dijkstra_Path(data);

            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS CHEMINS COURTS");
        }
        }

}

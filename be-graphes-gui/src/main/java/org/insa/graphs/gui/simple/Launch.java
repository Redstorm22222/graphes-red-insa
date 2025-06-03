package org.insa.graphs.gui.simple;

//import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
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
import org.insa.graphs.model.io.GraphReader;

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

    public static String formatDuration(long totalSeconds) {
        long days = totalSeconds / 86400;
        long rem = totalSeconds % 86400;
        long hours = rem / 3600;
        rem %= 3600;
        long minutes = rem / 60;
        long seconds = rem % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" j ");
        }
        if (hours > 0) {
            sb.append(hours).append(" h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" min ");
        }
        sb.append(seconds).append(" sec");
        return sb.toString().trim();
    }

    public static String formatDistance(long meters) {
        if (meters < 1000) {
            return meters + " m";
        } else {
            long km = meters / 1000;
            long rem = meters % 1000;
            if (rem == 0) {
                return km + " km";
            } else {
                return km + " km " + rem + " m";
            }
        }
    }

    public static void AffichageDuration(String NomAlgo, double duration){
        long cost = (long) Math.round(duration);
        String formatedCost = formatDuration(cost);
        System.out.println("--- Chemin trouvé par " + NomAlgo +  " :  " + formatedCost);
    }

    public static void AffichageDistance(String NomAlgo, double distance){
        long cost = (long) Math.round(distance);
        String formatedCost = formatDistance(cost);
        System.out.println("--- Chemin trouvé par " + NomAlgo +  " :  " + formatedCost);
    }

    public static void AssertEquals (double a, double b) throws FailTest{
        if (a - b > 0.0001) throw new FailTest("EXECUTION ERROR : Not identic paths !");
    }

    public static void AssertValid (Path path) throws FailTest{
        if (!path.isValid()) throw new FailTest("EXECUTION ERROR : Path invalid !");
    }

    public static ShortestPathSolution ResultAlgo(ShortestPathData data, int algoId){
        //Call for Dijkstra
        if (algoId == 0){
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
            return dijkstra.run();
        //Call for AStar
        }else{
            AStarAlgorithm astar = new AStarAlgorithm(data);
            return astar.run();
        }
    }

    public static void Compare_Algo_Path(ShortestPathData data, int algoId) throws FailTest{
        //###############################
        // EXECUTION DE L'ALGORITHME
        //###############################
        ShortestPathSolution resultat = ResultAlgo(data, algoId);
        String NameAlgo;
        if (algoId == 0){NameAlgo = "Dijkstra      ";}else{NameAlgo = "A-Star        ";}
        
        Path path = Path.createShortestPathFromNodes(data.getGraph(),resultat.getPath().getNodesFromPath());
        double Cout;

        //###############################
        // VERIFICATION 
        //###############################
        AssertValid(resultat.getPath());
        if (data.getMode() == Mode.LENGTH){
            Cout = path.getLength();
            AssertEquals(resultat.getPath().getLength(),Cout);
            AffichageDistance(NameAlgo, resultat.getPath().getLength());
            AffichageDistance("la classe Path", Cout);
        }else{
            Cout = path.getMinimumTravelTime();
            AssertEquals(resultat.getPath().getMinimumTravelTime(),Cout);
            AffichageDuration(NameAlgo,resultat.getPath().getMinimumTravelTime());
            AffichageDuration("la classe Path",Cout);
        }
    }

    public static void Compare_Algo_Bellman(ShortestPathData data, int algoId) throws FailTest{
        //###############################
        // EXECUTION DE L'ALGORITHME
        //###############################
        ShortestPathSolution resultat = ResultAlgo(data, algoId);
        String NameAlgo;
        if (algoId == 0){NameAlgo = "Dijkstra      ";}else{NameAlgo = "A-Star        ";}

        BellmanFordAlgorithm bellman = new BellmanFordAlgorithm(data);
        ShortestPathSolution resultat_bellman = bellman.run();

        //###############################
        // VERIFICATION 
        //###############################
        AssertValid(resultat.getPath());
        if (data.getMode() == Mode.LENGTH){
            AssertEquals(resultat.getPath().getLength(),resultat_bellman.getPath().getLength());
            AffichageDistance(NameAlgo, resultat.getPath().getLength());
            AffichageDistance("Bellman-Ford  ", resultat_bellman.getPath().getLength());
        }else{
            AssertEquals(resultat.getPath().getMinimumTravelTime(),resultat_bellman.getPath().getMinimumTravelTime());
            AffichageDuration(NameAlgo,resultat.getPath().getMinimumTravelTime());
            AffichageDuration("Bellman-Ford  ",resultat_bellman.getPath().getMinimumTravelTime());
        }
    }

    public static void Compare_Dijkstra_AStar(ShortestPathData data)throws FailTest {
        //###############################
        // EXECUTION DES ALGORITHMES
        //###############################

        //instantiate time variables 
        long startTime ;
        long endTime ;
        long executionTime ;
        
        System.out.println("Construction des tests...");
        //this takes a lot of time so let's do it before running the algorithms
        AStarAlgorithm astar = new AStarAlgorithm(data);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);

        System.out.println("Dijkstra ready to start");
        //Running Dijkstra algorithm first
        startTime = System.nanoTime();
        ShortestPathSolution resultat_dijkstra = dijkstra.run();
        endTime = System.nanoTime();
        executionTime = (endTime - startTime) / 1000000;
        System.out.println("Dijkstra finished in "+ executionTime + "ms");

        System.out.println("A-Star ready to start");
        //Then run A-star algorithm
        startTime = System.nanoTime();
        ShortestPathSolution resultat_astar = astar.run();
        endTime = System.nanoTime();
        executionTime = (endTime - startTime) / 1000000;
        System.out.println("Dijkstra finished in "+ executionTime + "ms");

        //###############################
        // VERIFICATION 
        //###############################
        AssertValid(resultat_dijkstra.getPath());
        AssertValid(resultat_astar.getPath());
        if (data.getMode() == Mode.LENGTH){
            AssertEquals(resultat_dijkstra.getPath().getLength(),resultat_astar.getPath().getLength());
            AffichageDistance("Dijkstra  ", resultat_dijkstra.getPath().getLength());
            AffichageDistance("A-Star    ", resultat_astar.getPath().getLength());
        }else{
            AssertEquals(resultat_dijkstra.getPath().getMinimumTravelTime(),resultat_astar.getPath().getMinimumTravelTime());
            AffichageDuration("Dijkstra  ",resultat_dijkstra.getPath().getMinimumTravelTime());
            AffichageDuration("A-Star    ",resultat_astar.getPath().getMinimumTravelTime());
        }
        
    }

    public static void Test_Infeasible(ShortestPathData data, int algoId) throws FailTest{
        //###############################
        // EXECUTION DE L'ALGORITHME
        //###############################
        ShortestPathSolution resultat = ResultAlgo(data, algoId);
        String NameAlgo;
        if (algoId == 0){NameAlgo = "Dijkstra ";}else{NameAlgo = "A-Star   ";}

        //###############################
        // VERIFICATION 
        //###############################
        if (resultat.getStatus() == Status.INFEASIBLE){
            System.out.println(NameAlgo + ": Aucun chemin n'a été trouvé ");
        }else{
            throw new FailTest("INFEASIBLE TESTS ERROR");
        }
    }

    public static void Wave_Short_Test(int algoId, int [] TabTests,String map, int arcinspector) throws Exception {
        for (int i = 0; i < TabTests.length-1 ; i++){
            ShortestPathData data = BuildTest(TabTests[i], TabTests[i+1], map, arcinspector); 
            Compare_Algo_Path   (data, algoId);
            Compare_Algo_Bellman(data, algoId);
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
        
        try {
            //île de Belle-Île
            //Ville de Rennes
            System.out.println("Test 1 : BRETAGNE : Belle-île --> Rennes -------------------");
            data = BuildTest(372251, 41656, "bretagne", 0); 
            Test_Infeasible(data,0);
            Test_Infeasible(data,1);

            //île de Groix
            //Vile de Quimper
            System.out.println("Test 2 : BRETAGNE : Groix --> Quimper ----------------------");
            data = BuildTest(28673, 86314, "bretagne", 1); 
            Test_Infeasible(data,0);
            Test_Infeasible(data,1);

            //île de Marie-Galante
            //île de La Désirade
            System.out.println("Test 3 : GUADELOUPE : Marie-Galante --> La Désirade --------");
            data = BuildTest(4232, 12748, "guadeloupe", 2); 
            Test_Infeasible(data,0);
            Test_Infeasible(data,1);

            //Corse
            //Marseille
            System.out.println("Test 4 : FRANCE METROPOLITAINE : Corse --> Marseille ------");
            data = BuildTest(824699, 402019, "france", 0); 
            Test_Infeasible(data,0);
            Test_Infeasible(data,1);


            System.out.println("\n✅ All tests validated");
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
            Compare_Algo_Path(data,0);
            Compare_Algo_Path(data,1);


            System.out.println("\n✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS CHEMINS NULS");
        }




        System.out.println("\n");
        System.out.println("################################################################");
        System.out.println("####### TESTS DE CHEMINS COURTS ################################");
        System.out.println("################################################################\n");
        //Tentative de randomisation abandonnée en raison de la non connexité des cartes
        //Bien que le code proposé soit correct, il nous est apparu que cette méthode de 
        //test n'était pas viable dans notre cas
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
            Wave_Short_Test(0,TestsParis,"paris",0);
            Wave_Short_Test(1,TestsParis,"paris",0);
            System.out.println("Vague 2 : Chemins les plus rapides à Toulouse en voiture");
            int [] TestsToulouse = {8755,2212,
                                    13507,23549,
                                    4143,1271};
            Wave_Short_Test(0,TestsToulouse,"toulouse",2);
            Wave_Short_Test(1,TestsToulouse,"toulouse",2);
            System.out.println("Vague 3 : Être le plus rapide à Bordeaux à pied");
            int [] TestsBordeaux = {653,6459,
                                    5248,14956,
                                    15132,12216};
            Wave_Short_Test(0,TestsBordeaux,"bordeaux",2);
            Wave_Short_Test(1,TestsBordeaux,"bordeaux",2);


            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS CHEMINS COURTS");
        }
        
        


        System.out.println("\n");
        System.out.println("################################################################");
        System.out.println("####### TESTS DE CHEMINS LONGS #################################");
        System.out.println("################################################################\n");

        try{
            System.out.println("Test 1 : Rennes --> Brest en distance");
            data = BuildTest(41675, 75642, "bretagne", 0); 
            Compare_Algo_Path(data,0);
            Compare_Algo_Path(data,1);
            System.out.println("Test 2 : Port-Au-Prince --> Higûhey en temps");
            data = BuildTest(83370, 201656, "bretagne", 2); 
            Compare_Algo_Path(data,0);
            Compare_Algo_Path(data,1);

            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : TESTS CHEMINS LONGS");
        }





        System.out.println("\n");
        System.out.println("################################################################");
        System.out.println("####### COMPARAISON DIJKSTRA / ASTAR SUR LONGS CHEMINS #########");
        System.out.println("################################################################\n");
        try{
            System.out.println("Test 1 : Bayonne --> Perpignan en distance");
            data = BuildTest(6380012, 9118804, "france", 0); 
            Compare_Dijkstra_AStar(data);

            //heap space error

            System.out.println("Test 2 : Bordeaux --> Marseille en temps");
            data = BuildTest(5563985, 1760667, "france", 2); 
            Compare_Dijkstra_AStar(data);
            System.out.println("Test 3 : Toulon --> Annecy à pied en temps");
            data = BuildTest(5563985, 1760667, "france", 3); 
            Compare_Dijkstra_AStar(data);
            
            
            
            

            System.out.println("✅ All tests validated");
        } catch (FailTest e) {
            System.out.println("❌ FAIL : COMPARAISONS DIJKSTRA / ASTAR");
        }


        }




        
}

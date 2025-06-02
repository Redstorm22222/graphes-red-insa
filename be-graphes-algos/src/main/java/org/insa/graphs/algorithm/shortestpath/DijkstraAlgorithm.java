package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.LayoutStyle;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.shortestpath.Label;

import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;


public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }


    @Override
    protected ShortestPathSolution doRun() {
        //System.out.println("----- Début algorithme de Dijkstra -----");        

        //############################
        //DÉCLARATIONS DES VARIABLES
        //############################

        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        final Graph graph = data.getGraph();
        final int nbNodes = graph.size();
        Node origin_node = data.getOrigin();
        Node destination_node = data.getDestination();
        int origin_id = origin_node.getId();
        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;
        // algorithm variables
        Label[] labels = new Label[nbNodes];
        BinaryHeap tasDij = new BinaryHeap<Label>();

        //#############################
        //INITIALISATION DU PROGRAMME
        //#############################
        //System.out.println(">   Initialisation...");

        labels[origin_id] = new Label(origin_node, false, 0, null);
        tasDij.insert(labels[origin_id]);
        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(origin_node);

        //#############################
        //ALGORITHME
        //#############################
        //System.out.println(">   Execution...");

        while (!tasDij.isEmpty()){
            Label current_label = (Label) tasDij.deleteMin();       //Current Label
            Node  current_node  = current_label.GetSommetCourant(); //Current Node

            current_label.SetMarque(true);
            //If we have found the destination, no need to continue
            if (current_node == destination_node){
                //System.out.println(">   Solution trouvée ! break !");
                break;
            }

            List<Arc> successors = current_node.getSuccessors(); //liste de tous les successeurs de x

            for (int i = 0; i < successors.size(); i++){
                Arc  next_arc  = successors.get(i);         //i-th exiting arc of current_label
                Node next_node = next_arc.getDestination(); //Next Node
                int  next_id   = next_node.getId();         //ID of the next node

                //If we visit this node for the first time
                if (labels[next_id] == null){
                    labels[next_id] = new Label(next_node, false, Double.POSITIVE_INFINITY, next_arc);
                    notifyNodeReached(next_node);
                }

                Label next_label = labels[next_id]; //Next Label
                double new_cost = current_label.GetCost() + data.getCost(next_arc);
                if (!next_label.GetMarque()){
                    if (next_label.GetCost() > new_cost){
                        try {
                            
                            tasDij.remove(next_label);

                        } catch(ElementNotFoundException e) {

                            //nothing to do

                        }
                        next_label.SetCost(new_cost);
                        next_label.SetPere(next_arc);
                        tasDij.insert(next_label); //Update
                    }
                }
            }            
        }
        //System.out.println("> Construction de la solution...");

        
        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()] == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());
            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].GetPere();

            while (arc != null) {
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].GetPere();
            }
            

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL,
                    new Path(graph, arcs));
        }
        // when the algorithm terminates, return the solution that has been found
        return solution;
    }

}

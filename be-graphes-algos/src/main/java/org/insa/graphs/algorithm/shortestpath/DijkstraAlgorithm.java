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

import java.lang.Math;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    //TODO : utiliser la méthode getCost de l'arcinspector dans data (data.arcinspector.getcost)

    @Override
    protected ShortestPathSolution doRun() {

        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        final Graph graph = data.getGraph();
        final int nbNodes = graph.size();

        // Initialize array of predecessors.
        Arc[] predecessorArcs = new Arc[nbNodes];

        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        Label[] distances = new Label[nbNodes];
        BinaryHeap tasDij = new BinaryHeap<Label>();

        //INITIALISATION
        System.out.println("################ INITIALISATION DU TABLEAU DISTANCES ################");
        for (int i=0; i<nbNodes; i++){
            distances[i] = new Label(graph.get(i), false, 0, null);
            if (i != data.getOrigin().getId()) {

                distances[i].SetCost(Double.POSITIVE_INFINITY);
            }
            distances[i].Afficher();
            tasDij.insert(distances[i]);
            
        }
        

        //ALGORITHM ITSELF
        System.out.println("################ DÉBUT DE L'ALGORITHME ################");
        int cpt = 0;//compteur pour suivre l'avancée de la boucle
        while (!tasDij.isEmpty()){
            Label ori = (Label) tasDij.deleteMin(); //sommet courant
            System.out.println("ITÉRATION N°"+cpt);
            System.out.println("|--- ID du sommet actuel : " + ori.GetSommetCourant().getId());
            ori.SetMarque(true);
            distances[ori.GetSommetCourant().getId()].SetMarque(true);

            List<Arc> successors = ori.GetSommetCourant().getSuccessors();

            for (int i = 0; i < successors.size(); i++){

                Label y = distances[successors.get(i).getDestination().getId()]; //y = chaque successeur

                if (!y.GetMarque()){
                    if (y.GetCost() > ori.GetCost() + data.getCost(successors.get(i))){
                        tasDij.remove(y);
                        y.SetCost(ori.GetCost() + data.getCost(successors.get(i)));
                        y.SetPere(successors.get(i));
                        tasDij.insert(y); //Update
                    }

                    //y.SetCost(Math.min(y.GetCost(), solutionCost+successors.get(i).getLength()));

                }
            }            
            cpt +=1;

        }
        System.out.println("################ SORTIE DE LA BOUCLE ################");
        
        
// Destination has no predecessor, the solution is infeasible...
        if (distances[data.getDestination().getId()].GetPere() == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = distances[data.getDestination().getId()].GetPere();
            while (arc != null) {
                arcs.add(arc);
                arc = distances[arc.getOrigin().getId()].GetPere();
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

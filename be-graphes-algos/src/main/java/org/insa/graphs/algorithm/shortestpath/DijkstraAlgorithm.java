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
        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        Label[] labels = new Label[nbNodes];
        BinaryHeap tasDij = new BinaryHeap<Label>();

        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(data.getOrigin());

        //INITIALISATION
        System.out.println("################ INITIALISATION DU TABLEAU DISTANCES ################");
        /* for (int i=0; i<nbNodes; i++){
            labels[i] = new Label(graph.get(i), false, 0, null);
            if (i != data.getOrigin().getId()) {

                labels[i].SetCost(Double.POSITIVE_INFINITY);
            }
            labels[i].Afficher();
        } */
        
        labels[data.getOrigin().getId()] = new Label(data.getOrigin(), false, 0, null);
        tasDij.insert(labels[data.getOrigin().getId()]);

        //ALGORITHM ITSELF
        System.out.println("################ DÉBUT DE L'ALGORITHME ################");
        int cpt = 0;//compteur pour suivre l'avancée de la boucle
        while (!tasDij.isEmpty()){
            Label x = (Label) tasDij.deleteMin(); //sommet courant
            System.out.println("ITÉRATION N°"+cpt);
            System.out.println("|--- ID du sommet actuel : " + x.GetSommetCourant().getId());
            x.SetMarque(true);
            if (x.GetSommetCourant() == data.getDestination()){
                break;
            }

            //distances[x.GetSommetCourant().getId()].SetMarque(true);

            List<Arc> successors = x.GetSommetCourant().getSuccessors(); //liste de tous les successeurs de x

            for (int i = 0; i < successors.size(); i++){
                if (labels[successors.get(i).getDestination().getId()] == null){
                    labels[successors.get(i).getDestination().getId()] = new Label(successors.get(i).getDestination(), false, Double.POSITIVE_INFINITY, successors.get(i));
                }
                Label y = labels[successors.get(i).getDestination().getId()];
                if (!y.GetMarque()){
                    if (y.GetCost() > x.GetCost() + data.getCost(successors.get(i))){
                        if (Double.isInfinite(y.GetCost())
                            && Double.isFinite(x.GetCost() + data.getCost(successors.get(i))))
                        {
                            notifyNodeReached(y.GetSommetCourant());
                        }
                        try {
                            
                            tasDij.remove(y);

                        } catch(ElementNotFoundException e) {

                            //nothing

                        }
                        y.SetCost(x.GetCost() + data.getCost(successors.get(i)));
                        y.SetPere(successors.get(i));
                        tasDij.insert(y); //Update
                    }
                }
            }            
            cpt +=1;
        }
        System.out.println("################ SORTIE DE LA BOUCLE ################");

        
        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()] == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());
            System.out.println("doody1");
            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].GetPere();
            System.out.println("doody2");

            while (arc != null) {
                arcs.add(arc);
                System.out.println(arc);
                arc = labels[arc.getOrigin().getId()].GetPere();
            }
            

            // Reverse the path...
            Collections.reverse(arcs);
            System.out.println("doody4");

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL,
                    new Path(graph, arcs));
        }
        // when the algorithm terminates, return the solution that has been found
        return solution;
    }

}

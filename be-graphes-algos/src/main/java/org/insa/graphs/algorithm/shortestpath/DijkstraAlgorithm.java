package org.insa.graphs.algorithm.shortestpath;

import java.util.Arrays;
import java.util.List;

import javax.swing.LayoutStyle;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;

import org.insa.graphs.algorithm.shortestpath.Label;

import org.insa.graphs.algorithm.utils.BinaryHeap;

import java.lang.Math;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {

        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        final Graph graph = data.getGraph();
        final int nbNodes = graph.size();

        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        // variable that will contain the final cost of the solution

        double solutionCost = 0;

        // TODO: implement the Dijkstra algorithm

        Label[] distances = new Label[nbNodes];
        BinaryHeap tasDij = new BinaryHeap<Label>();

        //INITIALISATION

        for (int i=0; i<nbNodes; i++){

            if (i == data.getOrigin().getId()) {

                distances[i].SetCost(0);
        
            } else {

                distances[i].SetCost(Double.POSITIVE_INFINITY);
                
            }
            distances[i].SetSommetCourant(graph.get(i));
            distances[i].SetSommetPere(null);
            distances[i].SetMarque(false);
            tasDij.insert(distances[i]);
            
        }

        //ALGORITHM ITSELF

        while (!tasDij.isEmpty()){

            Label ori = (Label) tasDij.deleteMin();
            ori.SetMarque(true);
            distances[ori.GetSommetCourant().getId()].SetMarque(true);

            List<Arc> successors = ori.GetSommetCourant().getSuccessors();

            for (int i = 0; i < successors.size(); i++){

                Label y = distances[successors.get(i).getDestination().getId()];
                if (!y.GetMarque()){

                    if (y.GetCost() > solutionCost+successors.get(i).getLength()){

                        tasDij.remove(y);
                        y.SetCost(solutionCost+successors.get(i).getLength());
                        tasDij.insert(y); //Update
                        
                    }

                    y.SetCost(Math.min(y.GetCost(), solutionCost+successors.get(i).getLength()));

                }

            }            

        }


        // when the algorithm terminates, return the solution that has been found
        return solution;
    }

}

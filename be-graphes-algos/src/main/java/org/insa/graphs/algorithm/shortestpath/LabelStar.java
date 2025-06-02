package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label{

    private double coutVolOiseau;

    public LabelStar(Node sommetCourant, Boolean marque, 
                        double currentCoutMin, Arc Pere, double coutVolOiseau){
        super(sommetCourant, marque, currentCoutMin, Pere);
        this.coutVolOiseau = coutVolOiseau;
    }

    @Override
    public double getTotalCost(){
        return this.GetCost() + this.coutVolOiseau;
    } 
}

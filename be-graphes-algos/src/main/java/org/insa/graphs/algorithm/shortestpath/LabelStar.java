package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractInputData;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label{

    private ShortestPathData data;

    public LabelStar(Node sommetCourant, Boolean marque, 
                        double currentCoutMin, Arc Pere, ShortestPathData data){
        super(sommetCourant, marque, currentCoutMin, Pere);
        this.data = data;
    }

    /* @Override
    public double getTotalCost(){
        return this.GetCost() + this.coutVolOiseau;
    } */ 

    @Override
    public double getTotalCost() {
        //Mode shortest
        if (this.data.getMode() == AbstractInputData.Mode.LENGTH) {
            return this.GetCost() + this.GetSommetCourant().getPoint().distanceTo(this.data.getDestination().getPoint());
        }else{
        //Mode fastest
            double speed = this.data.getGraph().getGraphInformation().getMaximumSpeed() / 3.6;
            return this.GetCost() + this.GetSommetCourant().getPoint().distanceTo(this.data.getDestination().getPoint()) / speed;
        }
        
    }
}

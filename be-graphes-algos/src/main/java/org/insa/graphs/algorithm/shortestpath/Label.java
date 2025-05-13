package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label> {
    private Node sommetCourant;
    private boolean marque;
    private double currentCoutMin;
    private Arc Pere;

    public Label(Node sommetCourant, Boolean marque, double currentCoutMin, Arc Pere){
        this.sommetCourant = sommetCourant;
        this.marque = marque;
        this.currentCoutMin = currentCoutMin;
        this.Pere = Pere;
    }

    public double GetCost(){
        return this.currentCoutMin;
    }

    public void SetCost(double newCost){
        this.currentCoutMin = newCost;
    }

    public Node GetSommetCourant(){
        return this.sommetCourant;
    }

    public void SetSommetCourant(Node newSommC){
        this.sommetCourant = newSommC;
    }

    public Arc GetPere(){
        return this.Pere;
    }

    public void SetPere(Arc newSommP){
        this.Pere = newSommP;
    }

    public boolean GetMarque(){
        return this.marque;
    }

    public void SetMarque(boolean newBool){
        this.marque = newBool;
    }

    public void Associer(Node node){
        this.sommetCourant = node;
    }

    public void Afficher(){
        if (Pere == null){
        System.out.println("ID = " + sommetCourant.getId()+ "|" + "etat = " + marque + "|" + "coût actuel minimal = " 
        + currentCoutMin+ "|" + "Arc : null" );

        }else{
        System.out.println("ID = " + sommetCourant.getId()+ "|" + "etat = " + marque + "|" + "coût actuel minimal = " 
        + currentCoutMin+ "|" + "Arc : " + Pere.getOrigin().getId() + "-->" + Pere.getDestination().getId());

        }
    }
    

    @Override
    public int compareTo(Label arg0) {
        if (this.currentCoutMin < arg0.currentCoutMin){

            return -1;

        } else if (this.currentCoutMin == arg0.currentCoutMin) {

            return 0;

        } else {

            return 1;
        }
    }
}


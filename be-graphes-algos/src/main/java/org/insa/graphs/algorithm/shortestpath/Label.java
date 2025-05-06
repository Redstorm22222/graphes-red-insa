package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label implements Comparable<Label> {
    private Node sommetCourant;
    private boolean marque;
    private double currentCoutMin;
    private Node sommetPere;

    public Label(Node sommetCourant, Boolean marque, double currentCoutMin, Node sommetPere){
        this.sommetCourant = sommetCourant;
        this.marque = marque;
        this.currentCoutMin = currentCoutMin;
        this.sommetPere = sommetPere;
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

    public Node GetSommetPere(){
        return this.sommetPere;
    }

    public void SetSommetPere(Node newSommP){
        this.sommetPere = newSommP;
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


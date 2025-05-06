package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label {
    private Node sommetCourant;
    private boolean marque;
    private int currentCoutMin;
    private Node sommetPere;

    public Label(Node sommetCourant, Boolean marque, int currentCoutMin, Node sommetPere){
        this.sommetCourant = sommetCourant;
        this.marque = marque;
        this.currentCoutMin = currentCoutMin;
        this.sommetPere = sommetPere;
    }

    public int GetCost(){
        return this.currentCoutMin;
    }

    public Node GetSommetCourant(){
        return this.sommetCourant;
    }

    public Node GetSommetPere(){
        return this.sommetPere;
    }

    public boolean GetMarque(){
        return this.marque;
    }

    public void Associer(Node node){
        this.sommetCourant = node;
    }
}


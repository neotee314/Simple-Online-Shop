package com.neotee.ecommercesystem.solution.storageunit.domain;

public class RemovalPlan {
    private final int fromStock;
    private final int fromReserved;

    public RemovalPlan(int fromStock, int fromReserved) {
        this.fromStock = fromStock;
        this.fromReserved = fromReserved;
    }


}


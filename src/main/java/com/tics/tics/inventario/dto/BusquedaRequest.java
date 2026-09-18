package com.tics.tics.inventario.dto;

public class BusquedaRequest {
    private String query;

    public BusquedaRequest() {}

    public BusquedaRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
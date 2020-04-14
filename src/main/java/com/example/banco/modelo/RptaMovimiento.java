package com.example.banco.modelo;

import java.util.List;

public class RptaMovimiento {
    
    private int codigo_error;
    private String msj_error;
    private List<Movimiento> lstMovimientos;

    public RptaMovimiento() { }

    public RptaMovimiento(int codigo_error, String msj_error, List<Movimiento> lstMovimientos) {
        this.codigo_error = codigo_error;
        this.msj_error = msj_error;
        this.lstMovimientos = lstMovimientos;
    }

    public List<Movimiento> getLstMovimientos() {
        return lstMovimientos;
    }

    public void setLstMovimientos(List<Movimiento> lstMovimientos) {
        this.lstMovimientos = lstMovimientos;
    }

    public int getCodigo_error() {
        return codigo_error;
    }

    public void setCodigo_error(int codigo_error) {
        this.codigo_error = codigo_error;
    }

    public String getMsj_error() {
        return msj_error;
    }

    public void setMsj_error(String msj_error) {
        this.msj_error = msj_error;
    }
    
    
}
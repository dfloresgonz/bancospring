package com.example.banco.dao;

import com.example.banco.modelo.Movimiento;
import com.example.banco.modelo.RptaMovimiento;

public interface IDao {
    RptaMovimiento realizarTransferencia(Movimiento movi);
    RptaMovimiento getMovimientos(int idClienteLogeado);
    int existeCliente(int idCliente);
    String getNroCuentaByCliente(int idCliente);
}
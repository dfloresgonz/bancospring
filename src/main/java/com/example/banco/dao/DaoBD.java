package com.example.banco.dao;

import com.example.banco.modelo.Movimiento;
import com.example.banco.modelo.RptaMovimiento;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("daobd")
public class DaoBD implements IDao{
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public RptaMovimiento realizarTransferencia(Movimiento movi) {
        RptaMovimiento rpta = new RptaMovimiento();
        try {
            System.err.println("realizarTransferencia....");
            String sql = "call realizar_trasnferencia(?, ?, ?, ?, ?, ?)";
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            CallableStatement callableSt = connection.prepareCall(sql);
            callableSt.setInt(1, movi.getIdCliente());   // ID del que hace depo
            callableSt.setInt(2, movi.getIdClienteOtro()); // ID que recibe
            callableSt.setString(3, movi.getNrCuenta());  // Cuenta del que recibe
            callableSt.setDouble(4, movi.getMonto());
            
            callableSt.registerOutParameter(5, Types.INTEGER);
            callableSt.registerOutParameter(6, Types.VARCHAR);
            //Call Stored Procedure
            callableSt.executeUpdate();
            rpta.setCodigo_error(callableSt.getInt(5));
            rpta.setMsj_error(callableSt.getString(6));
            System.err.println("retorno: "+rpta.getCodigo_error()+" -- "+rpta.getMsj_error());
        } catch (Exception e) {
            e.printStackTrace();
            rpta.setCodigo_error(-1);
            rpta.setMsj_error(e.getMessage());
        }
        return rpta;
    }

    @Override
    public RptaMovimiento getMovimientos(int idClienteLogeado) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int existeCliente(int idCliente) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNroCuentaByCliente(int idCliente) {
        try {
            System.err.println("ejecutando....");
            String sql = "call get_nro_cuenta_by_cliente(?, ?)";
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            CallableStatement callableSt = connection.prepareCall(sql);
            callableSt.setInt(1, idCliente);
            callableSt.registerOutParameter(2, Types.VARCHAR);
            //Call Stored Procedure
            callableSt.executeUpdate();
            System.err.println("retorno: "+callableSt.getString(2));
            return callableSt.getString(2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Double getMontoDepositoHoyByCliente(int idCliente) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCantDepositosHoyByCliente(int idCliente) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getSaldoByCliente(int idCliente) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
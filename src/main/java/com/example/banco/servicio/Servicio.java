package com.example.banco.servicio;

import com.example.banco.dao.Dao;
import com.example.banco.dao.DaoBD;
import com.example.banco.modelo.Movimiento;
import com.example.banco.modelo.RptaMovimiento;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class Servicio {
    
    private final DaoBD dao;
    
    public Servicio(@Qualifier("daobd") DaoBD __dao) {
        this.dao = __dao;
    }
    
    /*
    private final Dao dao;
    
    public Servicio(@Qualifier("bancodao") Dao __dao) {
        this.dao = __dao;
    }
    
    */
    
    public RptaMovimiento realizarTransferencia_PorBD(Movimiento movi) {
        return dao.realizarTransferencia(movi);
    }
    
    public RptaMovimiento realizarTransferencia(Movimiento movi) {
        //validacion 1
        RptaMovimiento rpta = new RptaMovimiento();
        ////
        if(movi.getMonto() <= 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El monto tiene que ser un número positivo.");
            return rpta;
        }
        int existe = dao.existeCliente(movi.getIdCliente());
        if(existe == 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente con el id "+movi.getIdCliente()+" no existe.");
            return rpta;
        }
        // REVISAR QUE NO EXCEDA EL MONTO DE DEPOSITO DIARIO
        Double depositoHoy = dao.getMontoDepositoHoyByCliente(movi.getIdCliente());
        if(depositoHoy + movi.getMonto() > 1500) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente supera el límite de depósito del día, intente un monto menor "+ (1500 - depositoHoy) );
            return rpta;
        }
        int cantDepositosHoy = dao.getCantDepositosHoyByCliente(movi.getIdCliente());
        if(cantDepositosHoy + 1 >= 5) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente ha llegado al límite de transferencias diarias.");
            return rpta;
        }
        //validacion 2
        existe = dao.existeCliente(movi.getIdClienteOtro());
        if(existe == 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente con el id "+movi.getIdClienteOtro()+" no existe.");
            return rpta;
        }
        String nroCuenta = dao.getNroCuentaByCliente(movi.getIdCliente());
        if(nroCuenta.equals(movi.getNrCuenta())) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("No se puede depositar a si mismo");
            return rpta;
        }
        // REVISAR SI TIENE EL SALDO SUFICIENTE
        double monto_actual = dao.getSaldoByCliente(movi.getIdCliente());
        System.err.println("SALDO DEL QUE DEPOSITA: "+monto_actual);
        if(movi.getMonto() > monto_actual) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente no tiene el saldo suficiente");
            return rpta;
        }
        movi.setMontoActualHaceDeposito(monto_actual);
        // ACTUALIZAR EL SALDO DEL CLIENE QUE RECIBIO EL DEPOSITO
        monto_actual = dao.getSaldoByCliente(movi.getIdClienteOtro());
        movi.setMontoActualReciboDeposito(monto_actual);
        // Pasaron todas las validaciones recien llamas al DAO
        return dao.realizarTransferencia(movi);
    }
    
    public RptaMovimiento getMovimientos(int idClienteLogeado) {
        return dao.getMovimientos(idClienteLogeado);
    }
    
    public String getNroCuentaByCliente(int idCliente) {
        return dao.getNroCuentaByCliente(idCliente);
    }
}
package com.example.banco.servicio;

import com.example.banco.dao.Dao;
import com.example.banco.modelo.Movimiento;
import com.example.banco.modelo.RptaMovimiento;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class Servicio {
    
    private final Dao dao;
    
    public Servicio(@Qualifier("bancodao") Dao __dao) {
        this.dao = __dao;
    }
    
    public RptaMovimiento realizarTransferencia(Movimiento movi) {
        //validacion 1
        
        //validacion 2
        
        // Pasaron todas las validaciones recien llamas al DAO
        return dao.realizarTransferencia(movi);
    }
    
    public RptaMovimiento getMovimientos(int idClienteLogeado) {
        return dao.getMovimientos(idClienteLogeado);
    }
    
    public int existeCliente(int idCliente) {
        return dao.existeCliente(idCliente);
    }
}
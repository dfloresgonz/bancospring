package com.example.banco.dao;

import com.example.banco.modelo.Movimiento;
import com.example.banco.modelo.RptaMovimiento;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("bancodao")
public class Dao implements IDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public RptaMovimiento realizarTransferencia(Movimiento movi) {
        RptaMovimiento rpta = new RptaMovimiento();
        ////
        int existe = existeCliente(movi.getIdCliente());
        if(existe == 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente con el id "+movi.getIdCliente()+" no existe.");
            return rpta;
        }
        existe = existeCliente(movi.getIdClienteOtro());
        if(existe == 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente con el id "+movi.getIdClienteOtro()+" no existe.");
            return rpta;
        }
        ///
        String nroCuenta = getNroCuentaByCliente(movi.getIdCliente());
        if(nroCuenta.equals(movi.getNrCuenta())) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("No se puede depositar a si mismo");
            return rpta;
        }
        if(movi.getMonto() <= 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El monto tiene que ser un número positivo.");
            return rpta;
        }
        // REVISAR SI TIENE EL SALDO SUFICIENTE
        double monto_actual = getSaldoByCliente(movi.getIdCliente());
        System.err.println("SALDO DEL QUE DEPOSITA: "+monto_actual);
        if(movi.getMonto() > monto_actual) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente no tiene el saldo suficiente");
            return rpta;
        }
        String sql = "INSERT INTO movimiento (id_cliente, nro_cuenta, tipo_movi, monto) VALUES (?, ?, ?, ?)";
        // INGRESO
        jdbcTemplate.update(
                sql,
                movi.getIdCliente(),// Id quien hizo el deposito
                movi.getNrCuenta(), // Cuenta del beneficiado
                movi.getTipoMovi(), // EGR
                movi.getMonto()
        );
        // EGRESO
        jdbcTemplate.update(
                sql,
                movi.getIdClienteOtro(), // Id del beneficiado
                movi.getNroCuentaOtro(), // Cuenta del que hizo el deposito
                "ING",
                movi.getMonto()
        );
        // ACTUALIZAR EL SALDO DEL CLIENTE QUE HACE EL DEPOSITO
        sql = "UPDATE cliente SET saldo = ? WHERE id_cliente = ?";
        jdbcTemplate.update(
                sql,
                monto_actual - movi.getMonto(),
                movi.getIdCliente()// Id quien hizo el deposito
        );
        // ACTUALIZAR EL SALDO DEL CLIENE QUE RECIBIO EL DEPOSITO
        monto_actual = getSaldoByCliente(movi.getIdClienteOtro());
        sql = "UPDATE cliente SET saldo = ? WHERE id_cliente = ?";
        jdbcTemplate.update(
                sql,
                monto_actual + movi.getMonto(),
                movi.getIdClienteOtro()// Id quien hizo el deposito
        );
        //
        rpta.setCodigo_error(0);
        rpta.setMsj_error("El depósito fue realizado correctamente");
        return rpta;
    }

    @Override
    public RptaMovimiento getMovimientos(int idClienteLogeado) {
        RptaMovimiento rpta = new RptaMovimiento();
        int existe = existeCliente(idClienteLogeado);
        if(existe == 0) {
            rpta.setCodigo_error(1);
            rpta.setMsj_error("El cliente con el id "+idClienteLogeado+" no existe.");
            return rpta;
        }
        String sql = "SELECT c1.nomb_cliente AS usuario_logeado,\n" +
                     "       m.tipo_movi,\n" +
                     "       m.monto,\n" +
                     "       c2.nomb_cliente AS nomb_cliente_otro,\n" +
                     "       c2.nro_cuenta AS nro_cuenta_otro, \n" +
                     "       CASE WHEN m.tipo_movi = 'EGR' THEN 'rojo' ELSE 'verde' END AS color\n" +
                     "  FROM movimiento m,\n" +
                     "       cliente c1,\n" +
                     "       cliente c2\n" +
                     " WHERE m.id_cliente = c1.id_cliente\n" +
                     "   AND m.nro_cuenta = c2.nro_cuenta\n" +
                     "   AND c1.id_cliente = ?";
        List<Movimiento> lstMovi = jdbcTemplate.query(sql,
                new Object[]{idClienteLogeado},
                (rs, rNum) -> 
                        new Movimiento(0,
                                0,
                                null,
                                rs.getString("nro_cuenta_otro"),
                                rs.getString("tipo_movi"),
                                rs.getDouble("monto"),
                                rs.getString("color"),
                                rs.getString("nomb_cliente_otro"),
                                rs.getString("usuario_logeado")
                        )
        );
        rpta.setCodigo_error(0);
        rpta.setMsj_error("Lista de movimientos correcta");
        rpta.setLstMovimientos(lstMovi);
        return rpta;
    }

    @Override
    public int existeCliente(int idCliente) {
        String sql = "SELECT COUNT(1) AS existe FROM cliente WHERE id_cliente = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { idCliente }, Integer.class );
    }

    @Override
    public String getNroCuentaByCliente(int idCliente) {
        String sql = "SELECT nro_cuenta FROM cliente WHERE id_cliente = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { idCliente }, String.class);
    }
    
    public Double getSaldoByCliente(int idCliente) {
        String sql = "SELECT saldo FROM cliente WHERE id_cliente = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { idCliente }, Double.class);
    }
}
DELIMITER $$
CREATE PROCEDURE get_nro_cuenta_by_cliente(
    IN _p_id_cliente INT,
    OUT _p_nro_cuenta VARCHAR(5)
)
BEGIN
    SELECT c.nro_cuenta
      INTO _p_nro_cuenta
      FROM cliente c
     WHERE c.id_cliente = _p_id_cliente;
END
END$$
DELIMITER ;

-- source ruta.sql
-- call get_nro_cuenta_by_cliente(1, @cuenta);
-- select @cuenta;
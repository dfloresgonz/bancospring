DELIMITER $$
DROP PROCEDURE IF EXISTS realizar_trasnferencia;
CREATE PROCEDURE realizar_trasnferencia(
    IN _p_id_cliente_hace_depo INT,
    IN _p_id_cliente_reci_depo INT,
    IN _p_nro_cuenta_reci_depo VARCHAR(5),
    IN _p_monto                NUMERIC(6,2),
    OUT _p_cod_error           INT,
    OUT _p_msj_error           VARCHAR(100)
)
this_proc:BEGIN
    DECLARE _v_existe INT;
    DECLARE _v_monto_depositado NUMERIC;
    DECLARE _v_cnt_depos INT;
    DECLARE _v_nro_cuenta VARCHAR(5);
    DECLARE _v_saldo_hace NUMERIC;
    DECLARE _v_saldo_reci NUMERIC;
    --
    IF _p_monto <= 0 THEN
        SET _p_cod_error = 1;
        SET _p_msj_error = 'El monto tiene que ser un número positivo.';
        LEAVE this_proc;
    END IF;
    --
    SELECT COUNT(1)
      INTO _v_existe
      FROM cliente c
     WHERE c.id_cliente = _p_id_cliente_hace_depo;
    --
    IF _v_existe = 0 THEN
        SET _p_cod_error = 2;
        SET _p_msj_error = CONCAT('El cliente con el id ', _p_id_cliente_hace_depo, ' no existe.');
        LEAVE this_proc;
    END IF;
    --
    SELECT COALESCE(SUM(m.monto), 0)
      INTO _v_monto_depositado
      FROM movimiento m
     WHERE m.id_cliente = _p_id_cliente_hace_depo
       AND m.tipo_movi  = 'EGR'
       AND DATE(m.fecha_movi) = CURRENT_DATE
    ;
    IF ( _v_monto_depositado + _p_monto > 1500) THEN
        SET _p_cod_error = 3;
        SET _p_msj_error = 'El cliente supera el límite de depósito del día.';
        LEAVE this_proc;
    END IF;
    --
    SELECT COUNT(1)
      INTO _v_cnt_depos
      FROM movimiento m
     WHERE m.id_cliente = _p_id_cliente_hace_depo
       AND m.tipo_movi = 'EGR'
       AND DATE(m.fecha_movi) = CURRENT_DATE
    ;
    IF _v_cnt_depos + 1 >= 5 THEN
        SET _p_cod_error = 4;
        SET _p_msj_error = 'El cliente ha llegado al límite de transferencias diarias.';
        LEAVE this_proc;
    END IF;
    --
    SELECT COUNT(1)
      INTO _v_existe
      FROM cliente c
     WHERE c.id_cliente = _p_id_cliente_reci_depo;
    --
    IF _v_existe = 0 THEN
        SET _p_cod_error = 5;
        SET _p_msj_error = CONCAT('El cliente con el id ', _p_id_cliente_reci_depo, ' no existe.');
        LEAVE this_proc;
    END IF;
    --
    SELECT nro_cuenta
      INTO _v_nro_cuenta
      FROM cliente
     WHERE id_cliente = _p_id_cliente_hace_depo;
    IF _v_nro_cuenta = _p_nro_cuenta_reci_depo THEN
        SET _p_cod_error = 6;
        SET _p_msj_error = 'No se puede depositar a si mismo.';
        LEAVE this_proc;
    END IF;
    --
    SELECT saldo
      INTO _v_saldo_hace
      FROM cliente
     WHERE id_cliente = _p_id_cliente_hace_depo;
    IF _p_monto > _v_saldo_hace THEN
        SET _p_cod_error = 7;
        SET _p_msj_error = 'El cliente no tiene el saldo suficiente.';
        LEAVE this_proc;
    END IF;
    --
    --
    SELECT saldo
      INTO _v_saldo_reci
      FROM cliente
     WHERE id_cliente = _p_id_cliente_reci_depo;
    --
    INSERT INTO movimiento (id_cliente, nro_cuenta, tipo_movi, monto)
        VALUES (_p_id_cliente_hace_depo, _p_nro_cuenta_reci_depo, 'EGR', _p_monto);
    INSERT INTO movimiento (id_cliente, nro_cuenta, tipo_movi, monto)
        VALUES (_p_id_cliente_reci_depo, _v_nro_cuenta, 'ING', _p_monto);
    UPDATE cliente SET saldo = ( _v_saldo_hace - _p_monto )
     WHERE id_cliente = _p_id_cliente_hace_depo;
    UPDATE cliente SET saldo = ( _v_saldo_reci + _p_monto )
     WHERE id_cliente = _p_id_cliente_reci_depo;
    SET _p_cod_error = 0;
    SET _p_msj_error = 'El depósito fue realizado correctamente.';
END$$
DELIMITER ;
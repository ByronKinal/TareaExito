drop database if exists cajeroDB2;
create database cajeroDB2;
use cajeroDB2;

create table Usuarios (
	idUsuario int auto_increment,
	nombreUsuario varchar(64) not null,
	correoUsuario varchar(128) not null,
	contraseñaUsuario varchar(128) not null,
    tipo enum('Empleado', 'Admin'),
    constraint pk_usuarios primary key (idUsuario)
);

create table Productos(
	idProducto int auto_increment,
	nombreProducto varchar(64) not null,
	precioProducto decimal(10,2) not null,
	stockProducto int not null,
    codigoBarras varchar(256),
    constraint pk_productos primary key (idProducto) 
);

create table Compras(
	idCompra int auto_increment,
    estadoCompra enum('Pendiente','Completada','Cancelada'),
    estadoPago enum('Pendiente', 'Pagado')default'Pendiente',
    fechaCompra datetime default now(),
    constraint pk_compras primary key (idCompra)
);

create table DetalleCompras(
	idCompra int not null,
    idProducto int not null,
    cantidad int not null,
    subtotal decimal(10,2) not null,
    constraint pk_detallecompras primary key (idCompra, idProducto),
    constraint fk_detalle_compras_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade,
    constraint fk_detalle_compras_productos foreign key (idProducto)
		references Productos(idProducto) on delete cascade
);

create table Pagos(
	idPago int auto_increment,
    fecha datetime default now(),
	metodoPago enum('Efectivo','Tarjeta','Transferencia')default"Efectivo",
	cantidad  decimal(10,2) not null,
    idCompra int not null,
    constraint pk_pagos primary key (idPago),
    constraint fk_pagos_compras foreign key (idCompra) 
		references Compras (idCompra)
);

create table Facturas(
	idFactura int auto_increment,
    fecha datetime default now(),
    total decimal(10,2) not null,
    metodoPago enum('Efectivo','Tarjeta','Transferencia')default"Efectivo",
	idCompra int not null,
	constraint pk_facturas primary key (idFactura),
	constraint fk_facturas_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade
);



-- -----------------------------------------------------------------------------------------------------------CRUD--------------------------------------------------------------------------------------------------------------------
-- CREATE
DELIMITER $$ 
create procedure sp_AgregarUsuario(
		in p_nombreUsuario varchar(200),
		in p_correoUsuario varchar(200),
		in p_contraseñaUsuario varchar(200),
		in p_tipo enum ('Cliente','Empleado','Admin'))
	begin
		insert into Usuarios(nombreUsuario, correoUsuario, contraseñaUsuario, tipo)
		values(p_nombreUsuario, p_correoUsuario,p_contraseñaUsuario, p_tipo);
	end;
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarUsuario()
    begin
		select 
        idUsuario as ID,
        nombreUsuario as USUARIO,
        correoUsuario as CORREO,
        contraseñaUsuario as CONTRASEÑA,
        tipo as TIPO 
        from Usuarios;
    end;
$$
DELIMITER ;

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarUsuario(
		in p_idUsuario int,
		in p_nombreUsuario varchar(200),
		in p_correoUsuario varchar(200),
		in p_contraseñaUsuario varchar(200),
		in p_tipo enum ('Cliente','Empleado','Admin'))
	begin
		update Usuarios
			set
				nombreUsuario = p_nombreUsuario,
				correoUsuario = p_correoUsuario,
				contraseñaUsuario = p_contraseñaUsuario,
				tipo = p_tipo
            where 
				p_idUsuario = idUsuario;
		
	end;
$$
DELIMITER ;


-- DELETE
DELIMITER $$
create procedure sp_EliminarUsuario(in p_idUsuario int)
    begin
		delete 
        from Usuarios
			where idUsuario = p_idUsuario;
    end 
$$
DELIMITER ;
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD PRODUCTOS

-- CREATE
DELIMITER $$
create procedure sp_agregarProducto(
    in p_nombreProducto varchar (200),
    in p_precioProducto decimal(10,2),
    in p_stockProducto int,
    in p_codigoBarras varchar(200))
	begin
		insert into Productos(
        nombreProducto,precioProducto,stockProducto,codigoBarras)
        values(p_nombreProducto,p_precioProducto,p_stockProducto,p_codigoBarras);
    end
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarProductos()
    begin
		select 
        idProducto as ID,
        nombreProducto as PRODUCTO,
        precioProducto as PRECIO,
        stockProducto as STOCK,
        codigoBarras as 'CODIGO DE BARRAS'
        from Productos;
    end;
$$
DELIMITER ;

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarProducto(
		in p_idProducto int,
		in p_nombreProducto varchar(200),
		in p_precioProducto decimal(10,2),
		in p_stockProducto int,
		in p_codigoBarras varchar (200))
	begin
		update Productos
			set
				nombreProducto = p_nombreProducto,
				precioProducto = p_precioProducto,
				stockProducto = p_stockProducto,
				codigoBarras = p_codigoBarras
            where 
				p_idProducto = idProducto;
		
	end;
DELIMITER ;

-- DELETE
DELIMITER $$
create procedure sp_EliminarProducto(in p_idProducto int)
    begin
		delete 
        from Productos
			where idProducto = p_idProducto;
    end 
$$
DELIMITER ;


-- CRUD COMPRAS

-- CREATE
DELIMITER $$
create procedure sp_agregarCompra(
    in p_estadoCompra enum ('Pendiente','Completada','Cancelada'),
    in p_estadoPago enum ('Pendiente','Pagado'))
	begin
		insert into Compras(
        estadoCompra,estadoPago)
        values(p_estadoCompra,p_estadoPago);
    end
$$
DELIMITER ;


-- READ
DELIMITER $$
CREATE PROCEDURE sp_ListarCompras()
BEGIN
    SELECT 
        c.idCompra AS COMPRA,
        (SELECT SUM(subtotal) FROM DetalleCompras WHERE idCompra = c.idCompra) * 1.12 AS TOTAL,
        c.estadoCompra AS ESTADO_COMPRA,
        c.estadoPago AS ESTADO_PAGO,
        c.fechaCompra AS FECHA
    FROM Compras c;
END$$
DELIMITER ;

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarCompras(
		in p_idCompra int,
		in p_estadoCompra enum('Pendiente','Completada','Cancelada'),
		in p_estadoPago enum('Pendiente', 'Pagado'))
	begin
		update Compras
			set
                estadoCompra = p_estadoCompra,
				estadoPago = p_estadoPago
            where 
				p_idCompra = idCompra;
		
	end;
$$
DELIMITER ;

-- DELETE
DELIMITER $$
create procedure sp_EliminarCompras(in p_idCompras int)
    begin
		delete 
        from Compras
			where idCompra = p_idCompras;
    end 
$$
DELIMITER ;

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD DETALLECOMPRAS

-- CREATE
DELIMITER $$
create procedure sp_agregarDetalleCompra	(
	in p_idCompra int,
    in p_idProducto int,
    in p_cantidad int)
	begin
		insert into DetalleCompras(
        idCompra,idProducto,cantidad)
        values(p_idCompra,p_idProducto,p_cantidad);
    end
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarDetalleCompras()
    begin
		select 
        idCompra as COMPRA,
        idProducto as PRODUCTO,
        cantidad as CANTIDAD,
        subtotal as SUBTOTAL
        from DetalleCompras;
    end;
$$
DELIMITER ;

-- UPDATE
DELIMITER $$
CREATE PROCEDURE sp_ActualizarDetalleCompraSimple(
    IN p_idCompra INT,
    IN p_idProducto INT,
    IN p_cantidad INT
)
BEGIN
    UPDATE DetalleCompras 
    SET 
        cantidad = p_cantidad,
        subtotal = (SELECT precioProducto FROM Productos WHERE idProducto = p_idProducto) * p_cantidad
    WHERE 
        idCompra = p_idCompra AND 
        idProducto = p_idProducto;
END$$
DELIMITER ;

-- DELETE
DELIMITER $$
create procedure sp_EliminarDetalleCompras(in p_idCompras int)
    begin
		delete 
        from DetalleCompras
			where idCompra = p_idCompras;
    end 
$$
DELIMITER ;

 DELIMITER $$
CREATE PROCEDURE sp_ReporteVentas(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE)
BEGIN
    SELECT 
        c.idCompra,
        u.nombreUsuario AS cliente,
        COUNT(dc.idProducto) AS productos,
        SUM(dc.subtotal) AS total,
        c.fechaCompra
    FROM Compras c

    JOIN DetalleCompras dc ON c.idCompra = dc.idCompra
    WHERE c.fechaCompra BETWEEN p_fechaInicio AND p_fechaFin
    AND c.estadoCompra = 'Completada'
    GROUP BY c.idCompra;
END$$
DELIMITER ;
 
 -- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD PAGOS

-- CREATE
DELIMITER $$
create procedure sp_AgregarPagos(
    in p_cantidad decimal(10,2),
    in p_idCompra int,
    out p_idPago int)
begin
    insert into Pagos( cantidad, idCompra)
    values( p_cantidad, p_idCompra);
    
    set p_idPago = LAST_INSERT_ID();
end$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarPagos()
    begin
		select 
        idPago as PAGO,
        fecha as FECHA,
        cantidad as CANTIDAD,
        idCompra as COMPRA
        from Pagos;
    end;
$$
DELIMITER ;

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarPagos(
		in p_idPago int,
		in p_cantidad decimal(10,2),
		in p_idCompra int)
	begin
		if not exists 
        (select 1 from Compras where idCompra = p_idCompra) 
        then 
        signal sqlstate '45000' 
        set message_text = 'La compra no existe';
        else
		update Pagos
			set
                cantidad = p_cantidad,
				idCompra = p_idCompra
            where 
				idPago = p_idPago;
		end if;
	end;
$$
DELIMITER ;

-- DELETE
DELIMITER $$
create procedure sp_EliminarPagos(in p_idPagos int)
    begin
		delete 
        from Pagos
			where idPago = p_idPagos;
    end 
$$
DELIMITER ;
 
 -- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD FACTURAS

-- CREATE
DELIMITER $$
create procedure sp_AgregarFactura(
     in p_idCompra int)
 	begin
		insert into Facturas(
        idCompra)
        values(p_idCompra);
    end
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarFactura()
    begin
		select 
        idFactura as FACTURA,
        fecha as FECHA,
        total as TOTAL,
        idCompra as COMPRA
        from Facturas;
    end;
$$
DELIMITER ;

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarFactura(
		in p_idFactura int,
		in p_total decimal(10,2),
		in p_idCompra int)
        begin
		update Facturas
			set
                total = p_total,
				metodoPago = p_metodoPago,
                idCompra = p_idCompra
                where 
				p_idFactura = idFactura;
	end;
$$
DELIMITER ;


-- DELETE
DELIMITER $$
create procedure sp_EliminarFacturas(in p_idFactura int)
    begin
		delete 
        from Facturas
			where idFactura = p_idFactura;
    end 
$$
DELIMITER ;
 -- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 
-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



delimiter $$
create trigger tr_CalcularSubTotal_Before_Insert
before insert on DetalleCompras
for each row
begin
    declare precio decimal(10,2);
    
    select precioProducto into precio from Productos where idProducto = new.idProducto;
    
    set new.subtotal = precio * new.cantidad;
end$$
delimiter ;

delimiter $$
create trigger tr_CalcularTotal_Before_Insert
before insert on Facturas
for each row
begin
    declare total_compra decimal(10,2);
    
    select sum(subtotal) into total_compra 
    from detallecompras 
    where idCompra = new.idCompra;
    
    set new.total = total_compra;
end$$
delimiter ;
call sp_AgregarUsuario("Byron Pineda","b","b","Admin");
call sp_AgregarUsuario("Alvaro Calderon","a","a","Admin");
call sp_ListarUsuario();

call sp_AgregarProducto('Maus','200.00',30,'10001');
call sp_AgregarProducto('Teclado','150.00',30,'10002');
call sp_AgregarProducto('Audifonos con cable','30.00',30,'10003');
call sp_AgregarProducto('Audifonos sin cable','120.00',30,'10004');
call sp_AgregarProducto('Cascos','200.00',20,'10005');
call sp_AgregarProducto('Carnasa de Telefono','30',30,'10006');
call sp_AgregarProducto('Cable HDMI','60',30,'10007');
call sp_AgregarProducto('Cable USB','15',30,'10008');
call sp_AgregarProducto('Cargador Tipo c','150',30,'10009');
call sp_ListarProductos();


-- Trigger para verificar stock antes de insertar un detalle de compra
DELIMITER $$
CREATE TRIGGER tr_VerificarStock_Before_Insert
BEFORE INSERT ON DetalleCompras
FOR EACH ROW
BEGIN
    DECLARE stock_actual INT;
    
    -- Obtener el stock actual del producto
    SELECT stockProducto INTO stock_actual 
    FROM Productos 
    WHERE idProducto = NEW.idProducto;
    
    -- Verificar si hay suficiente stock
    IF stock_actual < NEW.cantidad THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'No hay suficiente stock para este producto';
    END IF;
END$$
DELIMITER ;

-- Trigger para actualizar el stock después de insertar un detalle de compra
DELIMITER $$
CREATE TRIGGER tr_ActualizarStock_After_Insert
AFTER INSERT ON DetalleCompras
FOR EACH ROW
BEGIN
    -- Restar la cantidad comprada del stock disponible
    UPDATE Productos 
    SET stockProducto = stockProducto - NEW.cantidad
    WHERE idProducto = NEW.idProducto;
END$$
DELIMITER ;


-- Trigger para devolver el stock cuando se elimina un detalle de compra
DELIMITER $$
CREATE TRIGGER tr_DevolverStock_After_Delete
AFTER DELETE ON DetalleCompras
FOR EACH ROW
BEGIN
    -- Devolver la cantidad al stock cuando se elimina un detalle de compra
    UPDATE Productos 
    SET stockProducto = stockProducto + OLD.cantidad
    WHERE idProducto = OLD.idProducto;
END$$
DELIMITER ;


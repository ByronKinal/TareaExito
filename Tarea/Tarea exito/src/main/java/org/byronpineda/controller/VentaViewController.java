/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.byronpineda.controller;

import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.byronpineda.database.Conexion;
import org.byronpineda.model.Factura;
import org.byronpineda.model.Producto;
import org.byronpineda.report.Report;

import org.byronpineda.system.Main;

/**
 * FXML Controller class
 *
 * @author byron 
 */
public class VentaViewController implements Initializable {
    @FXML
    private ComboBox<Producto> cmbProductos;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtSubtotal, txtTotal;

    
    private Main principal;
    private int idCompraActual;
    private double subtotal;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colProducto;
    @FXML
    private TableColumn<Producto, Double> colPrecioUnitario;
    @FXML
    private TableColumn<Factura, Integer> colCantidad;
    @FXML
    private TableColumn<Factura, Double> colSubtotal;

    private ObservableList<Producto> listaProductos;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarProductosComboBox();
        setFormatoColumnaModelo();
        cargarDatos();
    }

    private void cargarProductosComboBox(){
        ObservableList<Producto> listaProducto = FXCollections.observableArrayList();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("{call sp_ListarProductos()}");
                    ResultSet resultado = enunciado.executeQuery();
                    while (resultado.next()) {
                    listaProducto.add(new Producto(
                        resultado.getInt(1),
                        resultado.getString(2),
                        resultado.getDouble(3),
                        resultado.getInt(4),
                        resultado.getString(5)));
                }
        } catch (SQLException e) {
            System.out.println("error  mula");
            e.printStackTrace();
        }
        cmbProductos.setItems(listaProducto);
    }
        

    private void crearNuevaCompra() {
        try {
            if (idCompraActual == 0) {
                Connection conexion = Conexion.getInstancia().getConexion();
                Statement stmt = conexion.createStatement();

                stmt.executeUpdate("INSERT INTO Compras(estadoCompra, estadoPago) VALUES ('Pendiente', 'Pendiente')",
                        Statement.RETURN_GENERATED_KEYS);

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idCompraActual = generatedKeys.getInt(1);
                    System.out.println("Nueva compra creada con ID: " + idCompraActual);
                } else {
                    mostrarAlerta("No se pudo obtener el ID de la nueva compra");
                }

                generatedKeys.close();
                stmt.close();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al crear nueva compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setFormatoColumnaModelo() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    public void cargarDatos() {
        listaProductos = FXCollections.observableArrayList(listarProductosEnFactura());
        tablaProductos.setItems(listaProductos);
        if (!listaProductos.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
        }
    }



    public ArrayList<Producto> listarProductosEnFactura() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT p.idProducto, p.codigoBarras, p.nombreProducto, p.precioProducto, "
                    + "dc.cantidad, dc.subtotal "
                    + "FROM DetalleCompras dc "
                    + "JOIN Productos p ON dc.idProducto = p.idProducto "
                    + "WHERE dc.idCompra = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idCompraActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("codigoBarras"),
                        rs.getDouble("precioProducto")
                );
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setSubtotal(rs.getDouble("subtotal"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar productos de la factura: " + e.getMessage());
        }
        
        return productos;
    }

    public void agregarProducto() {
        try {
            crearNuevaCompra();
            Producto productoSeleccionado = cmbProductos.getValue();
            
            if (productoSeleccionado != null) {
                int idProducto = productoSeleccionado.getIdProducto();
                int cantidad = Integer.parseInt(txtCantidad.getText());

                Connection conexion = Conexion.getInstancia().getConexion();

                String sqlVerificar = "SELECT cantidad FROM DetalleCompras WHERE idCompra = ? AND idProducto = ?";
                PreparedStatement stmtVerificar = conexion.prepareStatement(sqlVerificar);
                stmtVerificar.setInt(1, idCompraActual);
                stmtVerificar.setInt(2, idProducto);
                ResultSet rsVerificar = stmtVerificar.executeQuery();

                if (rsVerificar.next()) {
                    String callSP = "{call sp_ActualizarDetalleCompraSimple(?, ?, ?)}";
                    CallableStatement stmtActualizar = conexion.prepareCall(callSP);
                    stmtActualizar.setInt(1, idCompraActual);
                    stmtActualizar.setInt(2, idProducto);    
                    stmtActualizar.setInt(3, cantidad);    
                    stmtActualizar.execute();
                } else {
                    CallableStatement procedimiento = conexion.prepareCall("{call sp_agregarDetalleCompra(?, ?, ?)}");
                    procedimiento.setInt(1, idCompraActual);
                    procedimiento.setInt(2, idProducto);
                    procedimiento.setInt(3, cantidad);
                    procedimiento.execute();
                }

                limpiarTexto();
                cargarDatos();
                actualizarTotales();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al agregar detalle de compra: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número válido");
        }
    }

@FXML
public void eliminarProducto() {
    Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
    if (productoSeleccionado != null) { 
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "DELETE FROM DetalleCompras WHERE idCompra = ? AND idProducto = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idCompraActual);
            stmt.setInt(2, productoSeleccionado.getIdProducto());
            stmt.executeUpdate();

            cargarDatos();
            actualizarTotales(); 
        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar producto: " + e.getMessage());
        }
    } else {
        mostrarAlerta("Seleccione un producto de la tabla para eliminar");
    }
}

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void limpiarTexto() {
        cmbProductos.getSelectionModel().clearSelection();
        txtCantidad.clear();
        cmbProductos.requestFocus();
    }
    
    @FXML
    private void FinalizarPedido() {
    subtotal = 0; 
    for (Producto producto : listaProductos) {
        subtotal += producto.getSubtotal();
    }
    pagar();
      principal.getMenuView();
}



public void setPrincipal(Main principal) {
    this.principal = principal;
}

private void actualizarTotales() {
    subtotal = 0;
    for (Producto producto : listaProductos) {
        subtotal += producto.getSubtotal();
    }
    mostrarDatos();
}

private void mostrarDatos() {
    if (txtSubtotal != null) {
        txtSubtotal.setText(String.format("%.2f", subtotal));
    }
    if (txtTotal != null) {
        double total = subtotal * 1.12; 
        txtTotal.setText(String.format("%.2f", total));
    }
}

private void pagar() {
    try {
        Connection conexion = Conexion.getInstancia().getConexion();
        double total = subtotal * 1.12; 
        
        CallableStatement procedimientoFactura = conexion.prepareCall("{call sp_AgregarFactura(?)}");
        procedimientoFactura.setInt(1, idCompraActual);
        procedimientoFactura.execute();

        Statement stmtFactura = conexion.createStatement();
        ResultSet rsFactura = stmtFactura.executeQuery("SELECT MAX(idFactura) FROM Facturas");
        int idFactura = rsFactura.next() ? rsFactura.getInt(1) : 0;

        Statement stmtUpdate = conexion.createStatement();
        stmtUpdate.executeUpdate("UPDATE Compras SET estadoCompra = 'Completada', estadoPago = 'Pagado' WHERE idCompra = " + idCompraActual);

        imprimirReporte(idCompraActual);
        
        idCompraActual = 0;
        listaProductos.clear();
        tablaProductos.getItems().clear();
        cmbProductos.getSelectionModel().clearSelection();
        limpiarTexto();
        actualizarTotales();
        
    } catch (SQLException e) {
        mostrarAlerta("Error al registrar pago: " + e.getMessage());
        e.printStackTrace();
    }
}

private Map<String, Object> parametros;

private InputStream cargarReporte(String urlReporte) {
    InputStream reporte = null;
    try {
        reporte = Main.class.getResourceAsStream(urlReporte);
        if (reporte == null) {
            throw new Exception("No se pudo cargar el reporte: " + urlReporte);
        }
    } catch (Exception e) {
        System.out.println("Error al cargar Reporte " + urlReporte + ": " + e);
        mostrarAlerta("Error al cargar el reporte: " + e.getMessage());
    }
    return reporte;
}

private void imprimirReporte(int idCompra) {
    Connection conexion = Conexion.getInstancia().getConexion();
    parametros = new HashMap<String, Object>();

    try {
        String url = "/report/";
        parametros.put("idCompra", idCompra);
        parametros.put("url", getClass().getResource(url).toString());
        
        Report.generarReporte(conexion, parametros, cargarReporte("/report/Factura.jasper"));
        Report.mostrarReporte();
    } catch (Exception e) {
        System.out.println("error");
    }

}
 
}
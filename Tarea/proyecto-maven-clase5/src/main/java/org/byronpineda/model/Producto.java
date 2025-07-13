package org.byronpineda.model;

/**
 *
 * @author Wilson Florian
 */
public class Producto {
    private int idProducto;
    private String nombreProducto;
    private double precioProducto;
    private int stockProducto;
    private String codigoBarras;
    private int cantidad;
    private double subtotal;

    public Producto() {
    }

    public Producto(int idProducto, String nombreProducto, double precioProducto, int stockProducto, String codigoBarras) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.stockProducto = stockProducto;
        this.codigoBarras = codigoBarras;
    }

    public Producto(int idProducto, String nombreProducto, String codigoBarras, double precioProducto) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.codigoBarras = codigoBarras;
        this.precioProducto = precioProducto;
    }

    public Producto(String nombreProducto, double precioProducto, int stockProducto) {
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.stockProducto = stockProducto;
    }

    public int getStockProducto() {
        return stockProducto;
    }

    public void setStockProducto(int stockProducto) {
        this.stockProducto = stockProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
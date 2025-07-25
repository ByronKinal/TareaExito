/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.byronpineda.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import org.byronpineda.system.Main;

/**
 * FXML Controller class
 *
 * @author Byron Pineda
 */
public class MenuController implements Initializable {

 
    private Main principal;
 
    @FXML private Button btnCompras;
    @FXML private Button btnProductos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
        public void clickManejadorEventos(ActionEvent evento) {
        if (evento.getSource() == btnCompras) {
            principal.getVentaView();
            
        }else if (evento.getSource() == btnProductos) {
            principal.getProductoView();
        }   
    }   
}

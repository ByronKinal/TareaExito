package org.byronpineda.system;

import java.io.InputStream;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.byronpineda.controller.InicioController;
import org.byronpineda.controller.MenuController;
import org.byronpineda.controller.ProductoController;
import org.byronpineda.controller.VentaViewController;


/**
 *
 * @author Byron Pineda
 */
public class Main extends Application {

    private Stage escenarioPrincipal;
    private Scene siguienteEscena;
    private static String URL = "/view/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("Tienda Exitante");
        Image icono = new javafx.scene.image.Image("/imagen/Logo.png");
        escenarioPrincipal.getIcons().add(icono);
        getLoginView();
        escenarioPrincipal.show();
    }

    public Initializable cambiarEscena(String fxml) throws Exception {
        Initializable interfazDeCambio = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivoFXML = Main.class.getResourceAsStream(URL + fxml);

        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL + fxml));

        siguienteEscena = new Scene(cargadorFXML.load(archivoFXML));
        escenarioPrincipal.setScene(siguienteEscena);
        escenarioPrincipal.sizeToScene();

        interfazDeCambio = cargadorFXML.getController();
        return interfazDeCambio;
    }

    public void getLoginView() {
        try {
            InicioController control
                    = (InicioController) cambiarEscena("InicioView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al login: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getMenuView() {
        try {
            MenuController control
                    = (MenuController) cambiarEscena("MenuView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al Menu: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getProductoView() {
        try {
            ProductoController control
                    = (ProductoController) cambiarEscena("ProductoView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al Producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getVentaView() {
        try {
            VentaViewController control
                    = (VentaViewController) cambiarEscena("VentaView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al Venta: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}


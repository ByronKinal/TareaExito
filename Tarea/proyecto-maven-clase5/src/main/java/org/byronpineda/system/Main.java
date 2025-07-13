package org.byronpineda.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 *
 * @author informatica
 */
//import org.json.JSONObject;
public class Main extends Application {

    @Override
    public void start(Stage escenario) throws Exception {
       FXMLLoader cargadorFXML = new FXMLLoader(getClass().getResource("/view/InicioView.fxml"));
       Parent raiz = cargadorFXML.load();
       
       Scene escena = new Scene(raiz);
       
       escenario.setScene(escena);
       escenario.show();
    }

    public static void main(String[] args) {
//        System.out.println("Hello World!");
//        JSONObject persona = new JSONObject();
//        
//        persona.put("nombre", "Byron");
//        persona.put("apellido", "Pineda");
//        persona.put("edad", 17);
//        persona.put("valido", true);
//        
//        System.out.println("Este es el contenido de json");
//        System.out.println(persona.toString(4));
        launch(args);
     }
}

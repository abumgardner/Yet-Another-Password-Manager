/*
 * YAPMApp.java
 */

package yapm;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class YAPMApp extends SingleFrameApplication implements Application.ExitListener  {

    YAPMView tView;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        addExitListener(this);
        tView = new YAPMView(this);
        show(tView);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of YAPMApp
     */
    public static YAPMApp getApplication() {
        return Application.getInstance(YAPMApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(YAPMApp.class, args);
    }

    public boolean canExit(EventObject e) {
         Object source = (e != null) ? e.getSource() : null;
         Component owner = (source instanceof Component) ? (Component)source : null;
         int option = JOptionPane.showConfirmDialog(owner, "Really Exit?");
         return option == JOptionPane.YES_OPTION;
    }

    public void willExit(EventObject e) {
        tView.getDAO().destroy();
        System.out.println("DAO Destroyed");
    }


    

}

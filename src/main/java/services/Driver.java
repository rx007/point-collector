package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import services.card.Card;
import services.infoseek.Infoseek;
import services.kuji.Kuji;
import services.websearch.Websearch;

public class Driver {

    private static final Logger LOGGER        = Logger.getLogger(Driver.class);

    private static String       versionInfo   = "Rakuten Kuji Automator v0.0.4\nReleased 2015-09-10, updated 2016-12-27\nCreated by Tang Yen.";

    private static String       disclaimer    = "Tang Yen (the developer) provides the software below \"as is,\" and you use the software at your own risk.\nthe developer makes no warranties as to performance, merchantability, fitness for a particular purpose, or any other warranties whether expressed or implied.\nNo oral or written communication from or information provided by the developer shall create a warranty.\nUnder no circumstances shall the developer be liable for direct, indirect, special, incidental, or consequential damages resulting from the use, misuse, or inability to use this software, even if the developer has been advised of the possibility of such damages.\n\n";

    private static List<Thread> threads;

    // add or remove services to this list to kick off parallelism
    private static List<Class>  pointServices = new ArrayList<Class>(Arrays.asList(new Class[] { Kuji.class, Card.class, Websearch.class }));

    static {
        DOMConfigurator.configure("log4j.xml");
    }

    private Driver() {

    }

    public static void main(String[] args) throws Exception {

        if (args.length == 2) {
            LoginDialog.setUsername(args[0]);
            LoginDialog.setPassword(args[1]);
        }
        else {
            // invoke login GUI. Return on error or cancel.
            if (init() == null)
                return;
        }

        welcomeMessage();

        threads = new ArrayList<Thread>();
        for (Class c : pointServices) {
            threads.add(executeThread(c));
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Infoseek should be done last (has dependency on previous threads)
        Infoseek infoSeek = new Infoseek();
        infoSeek.run();
    }

    /**
     * 
     * @param c thread class
     * @return
     */
    private static Thread executeThread(Class c) {

        LOGGER.info(String.format("Starting thread with class: %s", c.getName()));
        Object ob;
        try {
            ob = c.newInstance();
            Thread thread = new Thread((Runnable) ob);
            thread.start();
            return thread;
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * Prints welcome message, including current version info. Also specifies additional options user has set from login
     * GUI.
     * 
     * @param login
     * @throws Exception
     */
    public static void welcomeMessage() {

        System.out.println(disclaimer);
        LOGGER.info(versionInfo);
        LOGGER.info("Logged in as: " + LoginDialog.getUsername());
    }

    /**
     * Initializes the login GUI, where user can enter login credentials and specify additional options.
     * 
     * @return
     */
    public static LoginDialog init() {

        // take account information, as well as additional options
        LoginDialog dialog = new LoginDialog();
        dialog.showDialog();
        dialog.dispose();

        if (dialog.isCanceled()) {
            return null;
        }

        return dialog;
    }
}

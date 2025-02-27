package ppp;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setHeadless(false);
        app.run(args);

        
        SwingUtilities.invokeLater(() -> {
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu popup = new PopupMenu();
            Image image = null;
            try {
                image = ImageIO.read(Application.class.getResourceAsStream("/icon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            TrayIcon icon = new TrayIcon(image, "Pdf Print Proxy", popup);
            icon.setImageAutoSize(true);
/*
            MenuItem item1 = new MenuItem("Hello");
            item1.addActionListener(e -> {
                System.out.println("Hello world!!");
            });
*/
            MenuItem item2 = new MenuItem("Exit");
            item2.addActionListener(e -> {
                tray.remove(icon);
                System.exit(0);
            });
  //          popup.add(item1);
            popup.add(item2);

            try {
                tray.add(icon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        });
    }
}

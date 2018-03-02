/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author jabar
 */
public class AppServletContextListener implements ServletContextListener {

    private static ArrayList<FileParser> items;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("ServletContextListener destroyed");
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        SQLConnector sql = new SQLConnector();
        //sql.createDatabase();
        items = new ArrayList<>();
        if (!(new intro11equiz().createTable())) {
            System.out.println("Table Created");
            String path = "C:\\selftest\\selftest11e";
            try {
                List<File> files = Files.walk(Paths.get(path))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                files.forEach((File file) -> {
                    new FileParser(file.getAbsolutePath()).start();
                });
            } catch (IOException ex) {
            }
            items.forEach((item) -> {
                item.start();
            });
            int total = items.size();
            int terminated = 0;
            do {
                try {
                    terminated = items.stream().filter((FileParser item) -> item.getState().toString().equals("TERMINATED")).map((_item) -> 1).reduce(terminated, Integer::sum);
                    if (terminated < total) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                }
            } while (terminated < total);
        } else {
            System.out.println("Table Creation Failed!");
        }
    }
}

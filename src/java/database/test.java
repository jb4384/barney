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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jabar
 */
public class test {

    private static ArrayList<FileParser> items;

    public static void main(String args[]) {
        SQLConnector sql = new SQLConnector();
        System.out.println("entered build Database");
        //sql.createDatabase();
        ArrayList<FileParser> items = new ArrayList<>();
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
            System.out.println("Table creation Complete");
        } else {
            System.out.println("Table Creation Failed!");
        }        
    }

}

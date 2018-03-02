/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ajax;

import database.FileParser;
import database.SQLConnector;
import database.intro11equiz;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jabar
 */
public class functions extends HttpServlet {

    PrintWriter out;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("text/html;charset=UTF-8");
            out = response.getWriter();
            
            String call = request.getParameter("method");
            String data = Optional.ofNullable(request.getParameter("data")).orElse("");
            String chapterNo = Optional.ofNullable(request.getParameter("chapterNo")).orElse("");
            String questionNo = Optional.ofNullable(request.getParameter("questionNo")).orElse("");
            
            Method method = functions.class.getDeclaredMethod(call, new Class[]{String.class, String.class, String.class});
            method.invoke(this, chapterNo, questionNo, data);
        } catch (NoSuchMethodException ex) {
            System.out.println(ex.getLocalizedMessage());
            out.print("{error:'Invalid Method specified.'}");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
             System.out.println(ex.getLocalizedMessage());
        } 

    }
    
    private String buildDatabase(String chapterNo, String questionNo, String data){
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
            return "Table creation Complete";
        } else {
            return "Table Creation Failed!";
        }        
    }
    
    private void getQuestionsList(String chapterNo, String questionNo, String data){
        HashMap<String, String> list = new database.SQLConnector().getQuestionsList();
        JsonObjectBuilder item = Json.createObjectBuilder();
        JsonArrayBuilder items = Json.createArrayBuilder();
        list.forEach((k, v) -> {
            item.add("chapter",k);
            item.add("question", v);
            items.add(item);
        });
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("result", items);
        JsonObject result = builder.build();
        StringWriter sw = new StringWriter();
        
        JsonWriter writer = Json.createWriter(sw);
        writer.writeObject(result);
        
        out.println(sw.toString());
    }

    protected void buildJsonArray(ArrayList<String> list) {
        JsonArrayBuilder items = Json.createArrayBuilder();
        list.forEach((object) -> {
            items.add(object);
        });
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("result", items);
        JsonObject result = builder.build();
        StringWriter sw = new StringWriter();
        try (JsonWriter writer = Json.createWriter(sw)) {
            writer.writeObject(result);
        }
        out.println(sw.toString());
    }

    protected void buildJsonObject(HashMap<String, String> list) {
        JsonObjectBuilder items = Json.createObjectBuilder();
        list.forEach((k, v) -> {
            items.add(k, v);
        });
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("result", items);
        JsonObject result = builder.build();
        StringWriter sw = new StringWriter();
        
        JsonWriter writer = Json.createWriter(sw);
        writer.writeObject(result);
        
        out.println(sw.toString());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

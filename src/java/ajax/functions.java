/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ajax;

import database.intro11e;
import database.intro11equiz;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;
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
    
    private void saveAnswer(String chapterNo, String questionNo, String data){
        intro11e ans = new intro11e(chapterNo, questionNo, data);
        intro11equiz qst = new intro11equiz(chapterNo, questionNo);
        String Correct = (qst.getAnswerKey().equalsIgnoreCase(data)) ? "1" : "0";
        System.out.println(qst.getAnswerKey());
        System.out.println(data);
        System.out.println(Correct);
        ans.setIsCorrect(Correct);
        ans.insert();
        JsonObjectBuilder item = Json.createObjectBuilder();
        item.add("result", Correct);
        item.add("key", qst.getAnswerKey().toUpperCase());
        item.add("hint", qst.getHint());
        JsonObject result = item.build();
        StringWriter sw = new StringWriter();
        
        JsonWriter writer = Json.createWriter(sw);
        writer.writeObject(result);
        
        out.println(sw.toString());
    }
    
    private void getQuestionsList(String chapterNo, String questionNo, String data){
        TreeMap<Integer, String> list = new database.SQLConnector().getQuestionsList();
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

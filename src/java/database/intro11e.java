/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author jabar
 */
public class intro11e {
//These variables will exist in all table accessores

    private final String tableName = "intro11e";

//These variables are specific to this table
    private String chapterNo;
    private String questionNo;
    private String isCorrect;
    private String hostname;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
    private String username;

    public intro11e() {
        setDefaults();
        createTable();
    }

    public intro11e(String chapterNo, String questionNo, String answer) {
        setDefaults();
        createTable();
        this.chapterNo = chapterNo;
        this.questionNo = questionNo;
        setAnswer(answer);
    }

    private void setDefaults() {
        chapterNo = "";
        questionNo = "";
        isCorrect = "0";
        hostname = "";
        answerA = "0";
        answerB = "0";
        answerC = "0";
        answerD = "0";
        answerE = "0";
        username = "";
    }

    public final boolean createTable() {
        SQLConnector sql = new SQLConnector();
        Boolean exists = true;
        if (!sql.tableExists(tableName)) {
            HashMap<String, String> columns = new HashMap<>();
            columns.put("chapterNo", "int(11)");
            columns.put("questionNo", "int(11)");
            columns.put("isCorrect", "bit(1) default 0");
            columns.put("time", "timestamp default current_timestamp");
            columns.put("hostname", "varchar(100)");
            columns.put("answerA", "bit(1) default 0");
            columns.put("answerB", "bit(1) default 0");
            columns.put("answerC", "bit(1) default 0");
            columns.put("answerD", "bit(1) default 0");
            columns.put("answerE", "bit(1) default 0");
            columns.put("username", "varchar(100)");

            Boolean sc = sql.createTable(columns, tableName, "chapterNo,questionNo");
            System.out.println("create table status: " + sc);
            System.out.println("create table error: " + sql.getError());
            exists = false;
        }
        return exists;
    }

    private void setAnswer(String data) {
        try {
            Method method = intro11e.class.getMethod("setAnswer" + data.toUpperCase(), String.class);
            method.invoke(this, 1);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
    }

    public void insert() {
        SQLConnector sql = new SQLConnector();
        String SQL_INSERT = "replace into intro11e ("
                + "chapterNo,questionNo,isCorrect,hostname,answerA,answerB,answerC,answerD,answerE,username)"
                + "values (?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection con = sql.getConnection();
            PreparedStatement statement = con.prepareStatement(SQL_INSERT);
            statement.setString(1, chapterNo);
            statement.setString(2, questionNo);
            statement.setInt(3, Integer.parseInt(isCorrect));
            statement.setString(4, hostname);
            statement.setInt(5, Integer.parseInt(answerA));
            statement.setInt(6, Integer.parseInt(answerB));
            statement.setInt(7, Integer.parseInt(answerC));
            statement.setInt(8, Integer.parseInt(answerD));
            statement.setInt(9, Integer.parseInt(answerE));
            statement.setString(10, username);
            System.out.println(statement.toString());
            statement.execute();
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public String getTableName() {
        return tableName;
    }

    public String getChapterNo() {
        return chapterNo;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public String getIsCorrect() {
        return isCorrect;
    }

    public String getHostname() {
        return hostname;
    }

    public String getAnswerA() {
        return answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public String getAnswerE() {
        return answerE;
    }

    public String getUsername() {
        return username;
    }

    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public void setIsCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public void setAnswerE(String answerE) {
        this.answerE = answerE;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

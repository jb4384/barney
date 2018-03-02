/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author jabar
 */
public class FileParser extends Thread {

    private int chapter;
    private Boolean nextQ = false;
    private int at = 0;
    private String fileName;
    private String error;
    List<intro11equiz> list;
    intro11equiz qz;

    public FileParser(String fileName) {
        error = "";
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            this.fileName = fileName;
        } else {
            error = "Invalid file!";
        }
    }

    @Override
    public void run() {
        list = new ArrayList<>();
        qz = new intro11equiz();
        //Stream that filters blank lines and Section Headers
        try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.ISO_8859_1)
                .filter(line -> !line.startsWith("Section"))
                .filter(line -> !line.isEmpty())) {
            lines.forEach((String line) -> {
                //line = line.replaceAll("\t", " ");
                line = line.replaceAll("[\\u2018\\u2019]", "'")
                            .replaceAll("[\\u201C\\u201D]", "\"");
                if (nextQ) {
                    //Build first line of question
                    addQuestion(line);
                } else {
                    if (line.startsWith("Chapter")) {
                        //Get current chapter
                        String ch = line.split("Chapter")[1].trim();
                        ch = ch.split(" ")[0];
                        //String.valueOf(line.charAt("Chapter".length())).trim();
                        chapter = Integer.parseInt(ch);
                        nextQ = true;
                    } else if (line.startsWith("#")) {
                        //# signifies a new question
                        list.add(qz);
                        qz = new intro11equiz();
                        nextQ = true;
                    } else if (line.startsWith("a.") || line.startsWith("A.")
                            || line.startsWith("b.") || line.startsWith("B.")
                            || line.startsWith("c.") || line.startsWith("C.")
                            || line.startsWith("d.") || line.startsWith("D.")
                            || line.startsWith("e.") || line.startsWith("E.")) {
                        //find the answer options to the current question
                        String q = line.split("\\.")[0];
                        String value = line.substring(q.length() + 1).trim();
                        qz.setAnswer(q, value);
                    } else if (line.startsWith("Key:") || line.startsWith("key:")) {
                        //find the key to the current question
                        line = line.replaceAll("key", "Key");
                        String[] valArr = line.split("Key:");
                        String value = valArr[1];
                        String key = value;
                        String hint = "";
                        if (value.contains(" ")) {
                            key = value.split(" ")[0];
                            hint = value.substring(key.length()).trim();
                        }
                        qz.setAnswerKey(key);
                        qz.setHint(hint);
                    } else {
                        //all other lines become part of a question
                        String question = qz.getQuestion();
                        question += "\n"+line;
                        qz.setQuestion(question);
                        //addQuestion(line);
                    }
                }
            });
            list.add(qz);
            insert();
        } catch (IOException e) {
            error = e.getMessage();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * add a question to the hashMap
     *
     * @param newVal
     */
    private void addQuestion(String line) {
        qz.setChapterNo(Integer.toString(chapter));
        at += 1;
        qz.setQuestionNo(Integer.toString(at));
        String key = line.split("\\.")[0];
        String value = line.substring(key.length() + 1).trim();
        String question = qz.getQuestion();
        question += value;
        qz.setQuestion(question);
        nextQ = false;
    }

    private void insert() {
        SQLConnector sql = new SQLConnector();
        String SQL_INSERT = "replace into intro11equiz ("
                + "chapterNo,questionNo,question,choiceA,choiceB,choiceC,choiceD,choiceE,answerKey,hint)"
                + "values (?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection con = sql.getConnection();
            int i = 0;
            PreparedStatement statement = con.prepareStatement(SQL_INSERT);
            con.setAutoCommit(false);

            for (intro11equiz qst : list) {
                statement.setString(1, qst.getChapterNo());
                statement.setString(2, qst.getQuestionNo());
                statement.setString(3, qst.getQuestion());
                statement.setString(4, qst.getChoiceA());
                statement.setString(5, qst.getChoiceB());
                statement.setString(6, qst.getChoiceC());
                statement.setString(7, qst.getChoiceD());
                statement.setString(8, qst.getChoiceE());
                statement.setString(9, qst.getAnswerKey());
                statement.setString(10, qst.getHint());
                statement.addBatch();
                i++;
                if (i == list.size()) {
                     statement.executeBatch(); // Execute every 1000 items.
                }
                
            }
            
            con.commit();
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}

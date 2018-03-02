/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static javax.lang.model.SourceVersion.isKeyword;

/**
 *
 * @author Josh Barney This class contains the table information for the
 * database connection format
 */
public class intro11equiz implements java.io.Serializable {
//These variables will exist in all table accessores

    private final String tableName = "intro11equiz";
    //private final HashMap<String, String> columns;        

//These variables are specific to this table
    private String chapterNo;
    private String questionNo;
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String choiceE;
    private String answerKey;
    private String hint;
    private String firstLine;
    private String remainder;
    private String error;

    /**
     * Create and build sql table, if it doesn't exist
     */
    public intro11equiz() {
        setDefaults();
    }

    public intro11equiz(String chapterNo, String questionNo) {
        setDefaults();
        getItem(chapterNo, questionNo);
    }

    private void getItem(String chapterNo, String questionNo) {
        HashMap<String, String> result;
        chapterNo = chapterNo.replaceAll("\\D+", "");
        questionNo = questionNo.replaceAll("\\D+", "");
        if (chapterNo.isEmpty() || questionNo.isEmpty()) {
            result = new SQLConnector().getRandomItem(tableName);
        } else {
            HashMap<String, String> criteria = new HashMap<>();
            criteria.put("chapterNo", chapterNo);
            criteria.put("questionNo", questionNo);
            result = new SQLConnector().loadItem(tableName, criteria);
            if (result.isEmpty()){
                criteria.remove("questionNo");
                result = new SQLConnector().loadItem(tableName, criteria);
                if (result.isEmpty()){
                    error = "Chapter # " + chapterNo;
                } else {
                    error = "Question # " + questionNo;                    
                }
                result.clear();
            }
        }
        result.forEach((key, value) -> {
            try {
                String setter = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
                Method method = intro11equiz.class.getMethod(setter, String.class);
                method.invoke(this, value);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            }
        });
    }

    private void setDefaults() {
        chapterNo = "0";
        questionNo = "0";
        question = "";
        choiceA = "";
        choiceB = "";
        choiceC = "";
        choiceD = "";
        choiceE = "";
        answerKey = "";
        hint = "";
        remainder = "";
    }

    public void buildItem() {
        getItem(chapterNo, questionNo);
        if (question.isEmpty()) {
            firstLine = "Invalid Selection: " + error;
        } else {
            firstLine = question.split("\n")[0];
            remainder = "";
            String rest = question.substring(firstLine.length());
            if (!rest.isEmpty()) {
                remainder = "<pre><code class='java'>" + rest + "</code></pre>";
            }
        }
    }

    public boolean createTable() {
        SQLConnector sql = new SQLConnector();
        Boolean exists = true;
        if (!sql.tableExists(tableName)) {
            HashMap<String, String> columns = new HashMap<>();
            columns.put("chapterNo", "int(11)");
            columns.put("questionNo", "int(11)");
            columns.put("question", "text");
            columns.put("choiceA", "varchar(1000)");
            columns.put("choiceB", "varchar(1000)");
            columns.put("choiceC", "varchar(1000)");
            columns.put("choiceD", "varchar(1000)");
            columns.put("choiceE", "varchar(1000)");
            columns.put("answerKey", "varchar(5)");
            columns.put("hint", "text");

            Boolean sc = sql.createTable(columns, tableName, "chapterNo,questionNo");
            System.out.println("create table status: " + sc);
            System.out.println("create table error: " + sql.getError());
            exists = false;
        }
        return exists;
    }

    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public void setChoiceE(String choiceE) {
        this.choiceE = choiceE;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getChapterNo() {
        return chapterNo;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public String getQuestion() {
        return question;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public String getChoiceE() {
        return choiceE;
    }

    public String getAnswerKey() {
        return escapeHTML(answerKey);
    }

    public String getHint() {
        return escapeHTML(hint);
    }

    public void setAnswer(String key, String value) {
        try {
            Method method = intro11equiz.class.getMethod("setChoice" + key.toUpperCase(), String.class);
            method.invoke(this, value);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
    }

    public String getFirstLine() {
        return escapeHTML(firstLine);
    }

    public String getRemainder() {
        return (remainder.isEmpty()) ? "" : remainder;
    }

    public String getOption(String value) {
        String type = (answerKey.length() > 1) ? "checkbox" : "radio";
        String option;
        switch (value) {
            case "A":
                option = choiceA;
                break;
            case "B":
                option = choiceB;
                break;
            case "C":
                option = choiceC;
                break;
            case "D":
                option = choiceD;
                break;
            case "E":
                option = choiceE;
                break;
            default:
                return "";
        }
        if (option.isEmpty()) {
            return "";
        }
        String line = "<div id='choicemargin'>"
                + "<input type='"
                + type
                + "' value='"
                + value
                + "' name='QA12'> <span id='choicelabel'>"
                + value
                + ".</span> <span id='choicestatement'>"
                + escapeHTML(option)
                + "</span><br></div>";
        return line;
    }

    private static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    @Override
    public String toString() {
        String info = "";
        info += "Chapter  #: " + chapterNo + "\n";
        info += "Question #: " + questionNo + "\n";
        info += "Question  : " + question + "\n";
        info += "Choice A  : " + choiceA + "\n";
        info += "Choice B  : " + choiceB + "\n";
        info += "Choice C  : " + choiceC + "\n";
        info += "Choice D  : " + choiceD + "\n";
        info += "Choice E  : " + choiceE + "\n";
        info += "Answer Key: " + answerKey + "\n";
        info += "Hint      : " + hint;
        return info;
    }

}

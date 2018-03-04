<%-- 
    Document   : questionList
    Created on : Mar 3, 2018, 9:10:38 PM
    Author     : jabar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <jsp:useBean id="qst" class="database.intro11equiz" scope="session">
    </jsp:useBean>

    <label>Chapter</label>
    <select id="chapterSelect" />
    <label>Question</label>
    <select id="questionSelect" />

    <script>
        var questionList;
        $(function () {
            $.post('ajax/functions?method=getQuestionsList&chapterNo=' + chapterNo + '&questionNo=' + questionNo + '&data=' + info, function (data) {
                console.log(data);
                obj = $.parseJSON(data);
                questionList = obj.result;
                var s1 = $("#chapterSelect");
                var l2 = $('<label />')
                var s2 = $("questionSelect");
                $.each(questionList, function (i, ele) {
                    item = ele.chapter;
                    $('<option />', {value: item, text: item}).appendTo(s1);
                });
                chp = questionlist[]
            }).fail(function (error) {
                alert(error.responseJSON);
            });
        });
    </script>

</html>

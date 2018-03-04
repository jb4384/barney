<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html>
<html>

    <head>
        <title>Multiple-Choice Question by Joshua Barney</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
        <link rel="stylesheet" href="css/googlecode.css">
        <link rel="stylesheet" href="css/modal.css">
        <!--<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/styles/default.min.css">-->
        <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/highlight.min.js"></script>
        <script defer src="https://use.fontawesome.com/releases/v5.0.8/js/all.js"></script>
        <script>hljs.initHighlightingOnLoad();</script>
        <style type="text/css">
            body {
                font-family: "Courier New", sans-serif;
                font-size: 100%;
                color: black
            }

            pre { 
                margin: 0px;
                width: 100%;
                word-wrap: break-word;
                overflow-x: hidden;
            }

            #question {
                font-family: "Courier New", Courier, Verdana, Helvetica, sans-serif;
                font-size: 100%;
                color: black;
                margin-left: 0.5em
            }

            #questionstatement {
                font-family: Times New Roman, monospace, Courier, Verdana, Helvetica, sans-serif;
                font-size: 100%;
                color: black;
                margin-left: 1.8em;
                margin-right: 0.5em;
            }

            #choices {
                font-family: Times New Roman, Helvetica, sans-serif;
                font-size: 100%;
                color: #8000f0;
                color: black;
                margin-left: 25.0pt;
                margin-left: 0.5em;
                margin-bottom: 0.5em;
            }

            #choicemargin {
                font-family: Times New Roman, Helvetica, sans-serif;
                font-size: 100%;
                display: inline-flex;
            }

            #choicestatement {
                font-family: Times New Roman, Helvetica, sans-serif;
                font-size: 100%;
                color: black;
                margin-left: 0.5em;
                margin-bottom: 0.5em;
                margin-right: 0.5em;
            }

            .buttonSet {
                margin-top: 10px; 
                margin-left: 5px;
                border: 0px; 
                font-family: Helvetica, monospace; 
                font-size: 85%;
                background-color: rgba(0, 128, 0, 0.7); 
                border-radius: 0px;
                color:black;
            }

            .correct {
                color: green;
            }

            .incorrect {
                color: red;
            }

            #h3style {
                color: white;
                font-family: Helvetica, sans-serif;
                font-size: 100%;
                border-color: #6193cb;
                text-align: center;
                background-color: #6193cb;
            }
        </style>
        <script>
            var questionList;
            $(function () {
                $("#submitButton").click(function () {
                    var chapterNo = $('#chapterNo').val();
                    var questionNo = $('#questionNo').val();
                    var info = '';
                    $('input[name="QA12"]:checked').each(function () {
                        info += this.value;
                    });
                    if (info == "") {
                        $('#modalContent').html("").append("<div style='color:red;'>You must make a selection</div>");
                        $("#myModal").css("display", "block");
                    } else {
                        $.post('ajax/functions?method=saveAnswer&chapterNo=' + chapterNo + '&questionNo=' + questionNo + '&data=' + info, function (data) {
                            $("#againButton").toggle();
                            $("#submitButton").toggle();
                            obj = $.parseJSON(data);
                            var correct = "correct";
                            var check = "check";
                            console.log(obj.result);
                            if (obj.result == 0) {
                                correct = "incorrect";
                                check = "times";
                            }
                            var item = $('<span />').addClass(correct)
                                    .text('Your answer ' + info + ' is ' + correct)
                                    .append('<i class="fas fa-' + check + ' fa-2x" aria-hidden="true"></i>');
                            $('#modalContent').html("").append(item).append('<br>');
                            var item = $('<div />');
                            item.attr({id: 'a1', style: 'color: green'})
                                    .text('Click here to show the correct answer and an explanation')
                                    .on('click', function () {
                                        $(this).text("The correct answer is " + obj.key);
                                        if (obj.hint != "") {
                                            $(this).append("<div style = 'color: purple; font-family: Times New Roman;'> Explanation:  " + obj.hint);
                                        }
                                    });
                            $('#modalContent').append(item);
                            $("#myModal").css("display", "block");
                        }).fail(function (error) {
                            alert(error.responseJSON);
                        });
                    }
                });
                $("#choiceButton").click(function () {
                    var chapterNo = $('#chapterNo').val();
                    var questionNo = $('#questionNo').val();
                    var info = '';
                    $.post('ajax/functions?method=getQuestionsList&chapterNo=' + chapterNo + '&questionNo=' + questionNo + '&data=' + info, function (data) {
                        obj = $.parseJSON(data);
                        questionList = obj.result;
                        var l1 = $('<label />')
                        l1.attr('for', 'chapterSelect')
                                .text('Chapter: ');
                        var s1 = $('<select />');
                        s1.attr('name', 'chapterSelect');
                        $.each(questionList, function (i, ele) {
                            item = ele.chapter;
                            $('<option />', {value: item, text: item}).appendTo(s1);
                        });
                        $('#modalContent').html('').append(l1).append(s1);
                        s1.on("change", function () {
                            chapterSelectChange();
                        });
                        var l2 = $('<label />')
                        l2.attr('for', 'choiceSelect')
                                .attr('style', 'margin-left:25px;')
                                .text('Question: ');
                        var s2 = $('<select />');
                        s2.attr('name', 'questionSelect');
                        $('#modalContent').append(l2).append(s2);
                        buildQuestionSelect(1);
                        var input = $('<input>');
                        input.attr({
                            type: 'button',
                            class: 'buttonSet',
                            id: 'selectedButton',
                            value: 'Get Question',
                        }).on('click', function () {
                            getSelectedQuestion();
                        });
                        $('#modalContent').append('<br>').append(input);
                        $("#myModal").css("display", "block");
                    }).fail(function (error) {
                        alert(error.responseJSON);
                    });
                });
                $("#againButton").click(function () {
                    var chapterNo = $('#chapterNo').val();
                    var questionNo = $('#questionNo').val();
                    getNextQuestion(chapterNo, questionNo);
                });
                $("#randomButton").click(function () {
                    getNextQuestion();
                });
            }
            );
            function buildQuestionSelect(chapter) {
                item = questionList[chapter - 1];
                s = $('[name="questionSelect"]');
                s.empty();
                var array = item.question.split(',');
                $.each(array, function (index, item) {
                    $('<option />', {value: item, text: item}).appendTo(s);
                });
            }

            function chapterSelectChange() {
                chapter = $('[name="chapterSelect"]').val();
                buildQuestionSelect(chapter);
            }

            function getSelectedQuestion() {
                chapter = $('[name="chapterSelect"]').val();
                question = $('[name="questionSelect"]').val();
                getNextQuestion(chapter, question);
            }

            function getNextQuestion(chapter = "", question = "") {
                url = "OneQuestion.jsp";
                if (chapter != "" && question != "") {
                    url += "?chapterNo=" + chapter + "&questionNo=" + question;
                }
                window.location.replace(url);
            }

        </script>
    </head>

    <body>
        <%
            String chapterNo = (request.getParameterMap().containsKey("chapterNo")) ? request.getParameter("chapterNo") : "";
            String questionNo = (request.getParameterMap().containsKey("questionNo")) ? request.getParameter("questionNo") : "";
            String title = (request.getParameterMap().containsKey("title")) ? request.getParameter("title") : "";
        %>
        <jsp:useBean id="qst" class="database.intro11equiz" scope="page">
            <jsp:setProperty name="qst" property="chapterNo" value="<%=chapterNo%>"/>                     
            <jsp:setProperty name="qst" property="questionNo" value="<%=questionNo%>"/> 
            <% qst.buildItem();
                if (title.isEmpty()) {
                    title = "Ch: " + qst.getChapterNo() + " - Qst: " + qst.getQuestionNo();
                }
            %>
        </jsp:useBean>
        <h3 id="h3style" style="max-width: 620px; margin: 0 auto;">Multiple-Choice Question <%=title%></h3>
        <div style="max-width: 620px; margin: 0 auto; border: 1px solid #f6912f; font-weight: normal ">
            <form>
                <div id="question">
                    <div id="questionstatement"> 
                        <%= qst.getFirstLine()%>
                        <%= qst.getRemainder()%>
                    </div>
                    <br/>
                    <div id="choices">
                        <%= qst.getOption("A")%><br>
                        <%= qst.getOption("B")%><br>
                        <%= qst.getOption("C")%><br>
                        <%= qst.getOption("D")%><br>
                        <%= qst.getOption("E")%><br>
                    </div>

                    <div style="text-align: left; margin-right: 1em; ">
                        <input type="button" class="buttonSet" id="submitButton" value="Check My Answer">
                        <input type="button" class="buttonSet" id="againButton" value="Try Again" style="display:none;">
                        <input type="button" class="buttonSet" id="choiceButton" value="Select Question">
                        <input type="button" class="buttonSet" id="randomButton" value="Random Question">

                        <!-- The Modal -->
                        <div id="myModal" class="modal">

                            <!-- Modal content -->
                            <div class="modal-content">
                                <span class="close">&times;</span>
                                <div id="modalContent">
                                </div>
                            </div>

                        </div>
                        <script src="js/modal.js"></script>
                        <input type="hidden" id="chapterNo" value="<%= qst.getChapterNo()%>">
                        <input type="hidden" id="questionNo" value="<%= qst.getQuestionNo()%>">
                    </div>
            </form>
        </div>
    </body>

</html>

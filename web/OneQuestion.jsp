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
        <script>hljs.initHighlightingOnLoad();</script>
        <style type="text/css">
            body {
                font-family: "Courier New", sans-serif;
                font-size: 100%;
                color: black
            }

            pre { margin: 0px;
                  width: 100%;}

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
                    $.post('ajax/functions?method=getQuestionsList&chapterNo=' + chapterNo + '&questionNo=' + questionNo + '&data=' + info, function (data) {

                    }).fail(function (error) {
                        alert(error.responseJSON);
                    });
                });
                $("#choiceButton").click(function () {
                    var chapterNo = $('#chapterNo').val();
                    var questionNo = $('#questionNo').val();
                    var info = '';
                    $.post('ajax/functions?method=getQuestionsList&chapterNo=' + chapterNo + '&questionNo=' + questionNo + '&data=' + info, function (data) {
                        console.log(data);
                        questionList = $.parseJSON(data);
                        sortResults('chapter',true);
                        var l = $('<label />')
                        l.attr('for', 'choiceSelect')
                                .text('Chapter: ');
                        var s = $('<select />');
                        s.attr('id', 'choiceSelect');
                        $.each(questionList.result, function (i, ele) {
                            item = ele.chapter;
                            $('<option />', {value: item, text: item}).appendTo(s);
                        });
                        $('#modalContent').html('').append(l).append(s);
                        $("#myModal").css("display", "block");
                    }).fail(function (error) {
                        alert(error.responseJSON);
                    });
                });
            });
            
            function sortResults(prop, asc) {
                questionList = questionList.sort(function (a, b) {
                    if (asc) {
                        return (a[prop] > b[prop]) ? 1 : ((a[prop] < b[prop]) ? -1 : 0);
                    } else {
                        return (b[prop] > a[prop]) ? 1 : ((b[prop] < a[prop]) ? -1 : 0);
                    }
                });
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
                        <!-- Trigger/Open The Modal -->
                        <input type="button" class="buttonSet" id="choiceButton" value="Select Question">

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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="../../includes.jsp" %>
</head>
<body id="page-top" data-spy="scroll" data-target=".navbar-fixed-top">
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand page-scroll" href="${pageContext.request.contextPath}/">Robostorm Name Tag Auto
                Printing</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a data-toggle="tab" href="#printersTab">Printers</a></li>
                <li><a data-toggle="tab" href="#nameTagsTab">Name Tags</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="main container tab-content">
    <div id="printersTab" class="tab-pane fade in active">
        <table id="printers" class="table table-striped table-bordered">
            <tr>
                <td><h3>Printers</h3></td>
                <td colspan="5" class="editButtonContainer">
                    <button id="editPrinters" class="btn btn-primary">Edit</button>
                </td>
            </tr>

            <%--<tr class="form-inline">
                <td>
                    <label class="control-label">Name</label>
                    <input class="form-control" value="Test" size="1" readonly/>
                </td>
                <td>
                    <label class="control-label">IP</label>
                    <input class="form-control" value="127.0.0.1" size="5" readonly/>
                </td>
            </tr>--%>
        </table>
    </div>
    <div id="nameTagsTab" class="tab-pane fade">

    </div>
</div>
<script src="${pageContext.request.contextPath}/js/manger.js"></script>
</body>
</html>

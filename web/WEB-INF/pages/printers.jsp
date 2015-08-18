<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Printers</title>
    <%@ include file="/includes.jsp" %>
    <link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet"/>
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
                <li><a href="${pageContext.request.contextPath}/ntap/manager#printersTab">Back</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container main">
    <form:form method="post" action="${pageContext.request.contextPath}/ntap/manager/printers"
               modelAttribute="printerWrapper" enctype="multipart/form-data">
        <table class="table table-striped">
            <thead>
            <tr>
                <th><label class="control-label">Name</label></th>
                <th><label class="control-label">Printing</label></th>
                <th><label class="control-label">Active</label></th>
                <th><label class="control-label">IP</label></th>
                <th><label class="control-label">Port</label></th>
                <th><label class="control-label">API-Key</label></th>
                <th><label class="control-label">Config File</label></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="printer" items="${printerWrapper.printers}" varStatus="status">
                <tr class="form-inline">
                    <form:hidden path="printers[${status.index}].id" value="${printer.id}"/>
                    <td>
                        <form:input path="printers[${status.index}].name" cssClass="form-control"/>
                    </td>
                    <td>
                        <form:checkbox path="printers[${status.index}].printing"/>
                    </td>
                    <td>
                        <form:checkbox path="printers[${status.index}].active"/>
                    </td>
                    <td>
                        <form:input path="printers[${status.index}].ip" cssClass="form-control"/>
                    </td>
                    <td>
                        <form:input path="printers[${status.index}].port" cssClass="form-control"/>
                    </td>
                    <td>
                        <form:input path="printers[${status.index}].apiKey" cssClass="form-control"/>
                    </td>
                    <td>
                        <div class="input-group">
                            <form:input path="printers[${status.index}].configFile" cssClass="form-control"/>
                            <%--<span class="input-group-btn">--%>
                                <input type="file" class="upload" name="files[${status.index}]">
                            <%--</span>--%>
                        </div>
                    </td>
                    <td>
                        <input type="button" class="btn btn-danger delete" value="Delete"/>
                        <form:hidden path="deleted[${status.index}]" value="${false}"/>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="8" class="text-center"><input id="addPrinter" type="button" class="btn btn-info"
                                                           value="Add Printer"/></td>
            </tr>
            <tr>
                <td colspan="8"><input type="submit" value="Save" class="btn btn-success"/></td>
            </tr>
            </tbody>
        </table>
        <script>var num = ${printerWrapper.printers.size()};</script>
    </form:form>
</div>
<script src="${pageContext.request.contextPath}/js/editor.js"></script>
</body>
</html>

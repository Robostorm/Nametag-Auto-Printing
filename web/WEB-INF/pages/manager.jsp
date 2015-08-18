<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Manager</title>
    <%@ include file="../../includes.jsp" %>
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
                <li><a data-toggle="tab" href="#nameTagsTab">Name Tags</a></li>
                <li><a data-toggle="tab" href="#printersTab">Printers</a></li>
                <li><a data-toggle="tab" href="#printServerTab">Print Server</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a id="reload">Reload</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="main container tab-content">
    <div id="nameTagsTab" class="tab-pane fade in active">
        <form:form modelAttribute="nameTagWrapper">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th><label class="control-label">Name</label></th>
                    <th><label class="control-label">STL</label></th>
                    <th><label class="control-label">GCode</label></th>
                    <th><label class="control-label">Printing</label></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="printer" items="${nameTagWrapper.nameTags}" varStatus="status">
                    <tr class="form-inline">
                        <td>
                            <form:input path="nameTags[${status.index}].name" cssClass="form-control" readonly="true"/>
                        </td>
                        <td>
                            <form:input path="nameTags[${status.index}].stl" cssClass="form-control" readonly="true"/>
                        </td>
                        <td>
                            <form:input path="nameTags[${status.index}].gcode" cssClass="form-control" readonly="true"/>
                        </td>
                        <td>
                            <form:checkbox path="nameTags[${status.index}].printing" onclick="return false"/>
                        </td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="6"><a href="${pageContext.request.contextPath}/ntap/manager/nameTags"
                                       class="btn btn-success">Edit</a></td>
                </tr>
                </tbody>
            </table>
        </form:form>
    </div>
    <div id="printersTab" class="tab-pane fade in">
        <form:form modelAttribute="printerWrapper">
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
                        <td>
                            <form:input path="printers[${status.index}].name" cssClass="form-control" readonly="true"/>
                        </td>
                        <td>
                            <form:checkbox path="printers[${status.index}].printing" onclick="return false"/>
                        </td>
                        <td>
                            <form:checkbox path="printers[${status.index}].active" onclick="return false"/>
                        </td>
                        <td>
                            <form:input path="printers[${status.index}].ip" cssClass="form-control" readonly="true"/>
                        </td>
                        <td>
                            <form:input path="printers[${status.index}].port" cssClass="form-control" readonly="true"/>
                        </td>
                        <td>
                            <form:input path="printers[${status.index}].apiKey" cssClass="form-control"
                                        readonly="true"/>
                        </td>
                        <td>
                            <form:input path="printers[${status.index}].configFile" cssClass="form-control"
                                        readonly="true"/>
                        </td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="6"><a href="${pageContext.request.contextPath}/ntap/manager/printers"
                                       class="btn btn-success">Edit</a></td>
                </tr>
                </tbody>
            </table>
        </form:form>
    </div>
    <div id="printServerTab" class="tab-pane fade in">
        <div id="server">
            <form:form modelAttribute="printServerStatus">
                <div class="input-group">
                    <span class="input-group-addon">Server Status</span>
                    <form:input path="status" cssClass="form-control" readonly="true"/>
                </div>
                <div id="spacer"></div>
                <div class="input-group">
                    <span class="input-group-addon">Server Action</span>
                    <form:input path="action" cssClass="form-control" readonly="true"/>
                </div>
            </form:form>
            <input id="start" type="button" class="btn btn-success" value="Start"/>
            <input id="stop" type="button" class="btn btn-danger" value="Stop"/>
            <button id="refresh" type="button" class="btn btn-info" aria-label="Refresh">
                <span class="glyphicon glyphicon-repeat" aria-hidden="true"></span> Refresh
            </button>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/manger.js"></script>
</body>
</html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="/includes.jsp" %>
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
                <li><a href="${pageContext.request.contextPath}/ntap/manager">Back</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container main">
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
            </tbody>
        </table>
    </form:form>
</div>
</body>
</html>

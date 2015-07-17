<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="/includes.jsp" %>
</head>
<body id="page-top" data-spy="scroll" data-target=".navbar-fixed-top">
<div class="container main">
    <form:form method="post" action="/ntap/editPrinter" modelAttribute="wrapper">
        <table class="table">
            <tbody>
            <c:forEach var="printer" items="${wrapper.printers}" varStatus="status">
                <tr class="form-inline">
                    <form:hidden path="printers[${status.index}].id" value="${printer.id}"/>
                    <td>
                        <label class="control-label">Name</label>
                        <form:input path="printers[${status.index}].name" cssClass="form-control"/>
                    </td>
                    <td>
                        <label class="control-label">IP</label>
                        <form:input path="printers[${status.index}].ip" cssClass="form-control"/>
                    </td>
                    <td>
                        <label class="control-label">Port</label>
                        <form:input path="printers[${status.index}].port" cssClass="form-control"/>
                    </td>
                    <td>
                        <label class="control-label">API-Key</label>
                        <form:input path="printers[${status.index}].apiKey" cssClass="form-control"/>
                    </td>
                    <td>
                        <label class="control-label">Printing</label>
                        <form:checkbox path="printers[${status.index}].printing"/>
                    </td>
                    <td>
                        <label class="control-label">Active</label>
                        <form:checkbox path="printers[${status.index}].active"/>
                    </td>
                    <form:hidden path="printers[${status.index}].configFile" value="${printer.configFile}"/>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="6"><input type="submit" value="Save" class="btn btn-success"/></td>
            </tr>
            </tbody>
        </table>
    </form:form>
</div>
</body>
</html>

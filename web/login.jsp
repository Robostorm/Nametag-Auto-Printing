<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="includes.jsp" %>
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
                <li><a class="page-scroll" href="${pageContext.request.contextPath}/">Back</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container main">
    <form action="${pageContext.request.contextPath}/ntap/login" method="post" class="form-group text-left">
        <label class="control-label">Please Login:</label>
        <div class="input-group">
            <input type="text" class="form-control" name="password" placeholder="Password..."/>
            <span class="input-group-btn">
                <input type="submit" value="Login" class="btn btn-default"/>
            </span>
        </div>
    </form>
</div>
</body>
</html>

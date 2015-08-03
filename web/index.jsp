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
                <li><a class="page-scroll" href="#form">Get A Name Tag</a></li>
            </ul>
<%--            <ul class="nav navbar-nav navbar-right">
                <li><a href="${pageContext.request.contextPath}/login.jsp">Manager</a></li>
            </ul>--%>
        </div>
    </div>
</nav>

<div class="container main">
    <h1>Robostorm's Name Tag Auto Printing</h1>

    <p class="lead">Enter your name to get a 3D printed name tag</p>
    <section id="form">
        <div class="jumbotron">
            <img id="preview-image" src="assets/blank.png" alt="Name Tag Image"/>

            <form id="getNameTag" onsubmit="return false">
                <div class="form-group">
                    <label for="name" class="form-label">Enter your name</label>
                    <input type="text" class="form-control form-input" id="name" name="name"
                           placeholder="Enter your name"/>
                </div>
                <button type="button" class="btn btn-info" id="preview">Preview</button>
                <button type="button" class="btn btn-success" id="submit">Submit</button>
            </form>
        </div>
    </section>
</div>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
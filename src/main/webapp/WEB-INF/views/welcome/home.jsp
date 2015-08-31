<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
    href="${f:h(pageContext.request.contextPath)}/resources/app/css/styles.css">
</head>
<body>
    <div id="wrapper">
    	<c:if test="${f:h(isPasswordExpired)}">
    		Your password has expired. Please change.
    	</c:if>
    
        <h1>Hello world!</h1>
        
        <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)}</p>
        
        <c:if test="${f:h(lastLoginDate != null)}">
        	<p>Last login date is ${f:h(lastLoginDate)}.</p>
        </c:if>
        
        <form:form action="${f:h(pageContext.request.contextPath)}/logout">
        	<button>Logout</button>
        </form:form>
        
        <form:form action="${f:h(pageContext.request.contextPath)}/account">
        	<button>Account Information</button>
        </form:form>
        
        <sec:authorize url="/unlock">
	        <form:form action="${f:h(pageContext.request.contextPath)}/unlock?form">
	        	<button>Unlock Account</button>
	        </form:form>
	    </sec:authorize>
    </div>
</body>
</html>

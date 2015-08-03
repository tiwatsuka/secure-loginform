<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<body>
    <div id="wrapper">
    	<c:if test="${isPasswordExpired}">
    		Your password has expired. Please change.
    	</c:if>
    
        <h1>Hello world!</h1>
        
        <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)}</p>
        <p>Last login date is ${f:h(lastLoginDate)}.</p>
        
        <form:form action="${pageContext.request.contextPath}/logout">
        	<button>Logout</button>
        </form:form>
        
        <form:form action="${pageContext.request.contextPath}/password?form">
        	<button>Change Password</button>
        </form:form>
    </div>
</body>
</html>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Password Reissue URL is generated</title>
<link rel="stylesheet" 
	href="${pageContext.request.contextPath}/resources/app/css/styles.css" />
</head>
<body>
	<div id="wrapper">
		<h1>Your Password Reissue URL was successfully generated.</h1>
		The URL was sent to your registered E-mail address.<br/>
		Please access the URL and enter the password shown below.
		<h3>Password : ${f:h(password)}</h3>
	</div>
</body>
</html>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Unlock Account</title>
<link rel="stylesheet" 
	href="${f:h(pageContext.request.contextPath)}/resources/app/css/styles.css" />
</head>
<body>
	<div id="wrapper">
		<h1>Unlock Account</h1>
		<form:form action="${pageContext.request.contextPath}/unlock"
			method="POST" modelAttribute="unlockForm">
			<table>
				<tr>
					<th>Username</th>
					<td>
						<form:input path="username" value="${f:h(form.username)}"/>
					</td>
				</tr>
			</table>
			
			<input type="submit" value="Unlock" />
		</form:form>
	</div>
</body>
</html>

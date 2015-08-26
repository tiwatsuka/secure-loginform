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
		<t:messagesPanel />
		<form:form action="${pageContext.request.contextPath}/unlock"
			method="POST" modelAttribute="unlockForm">
			<table>
				<tr>
					<th>
						<form:label path="username" cssErrorClass="error-label">Username</form:label>
					</th>
					<td><form:input path="username" value="${f:h(form.username)}" cssErrorClass="error-input"/></td>
					<td><form:errors path="username" cssClass="error-messages"/></td>
				</tr>
			</table>
			
			<input type="submit" value="Unlock" />
		</form:form>
	</div>
</body>
</html>

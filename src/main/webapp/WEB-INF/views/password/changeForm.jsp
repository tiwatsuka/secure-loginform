<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Change Password</title>
<link rel="stylesheet" 
	href="${pageContext.request.contextPath}/resources/app/css/styles.css" />
</head>
<body>
	<div id="wrapper">
		<h1>Change Password</h1>
		<form:form action="${pageContext.request.contextPath}/password"
			method="POST" modelAttribute="passwordForm">
			<table>
				<tr>
					<th>Username</th>
					<td>
						${f:h(account.username)}
						<form:hidden path="username" value="${f:h(account.username)}"/>
					</td>
				</tr>
				<tr>
					<th>Old password</th>
					<td><form:password path="oldPassword" /><form:errors path="oldPassword"/></td>
				</tr>
				<tr>
					<th>New password</th>
					<td><form:password path="newPassword" /><form:errors path="newPassword"/></td>
				</tr>
				<tr>
					<th>New password(Confirm)</th>
					<td><form:password path="confirmNewPassword" /><form:errors path="confirmNewPassword"/></td>
				</tr>
			</table>
			
			<input type="submit" value="Change password" />
		</form:form>
	</div>
</body>
</html>
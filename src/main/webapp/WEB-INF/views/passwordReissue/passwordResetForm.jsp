<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Reset Password</title>
<link rel="stylesheet" 
	href="${pageContext.request.contextPath}/resources/app/css/styles.css" />
</head>
<body>
	<div id="wrapper">
		<h1>Reset Password</h1>
		<t:messagesPanel />
		<form:form action="${pageContext.request.contextPath}/reissue/resetPassword"
			method="POST" modelAttribute="passwordResetForm">
			<table>
				<tr>
					<th>Username</th>
					<td>
						${f:h(passwordResetForm.username)}
						<form:hidden path="username" value="${f:h(passwordResetorm.username)}"/>
					</td>
				</tr>
				<form:hidden path="token" value="${f:h(passwordResetForm.token)}"/>
				<tr>
					<th>Secret</th>
					<td><form:password path="secret" /><form:errors path="secret"/></td>
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
			
			<input type="submit" value="Reset password" />
		</form:form>
	</div>
</body>
</html>
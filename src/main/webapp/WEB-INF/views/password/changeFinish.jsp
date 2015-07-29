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
		<form>
			<table>
				<tr>
					<th>Username</th>
					<td>${f:h(account.username)}</td>
				</tr>
				<tr>
					<th>Old password</th>
					<td><input type="password" /></td>
				</tr>
				<tr>
					<th>New password</th>
					<td><input type="password" /></td>
				</tr>
				<tr>
					<th>New password(Confirm)</th>
					<td><input type="password" /></td>
				</tr>
			</table>
			
			<input type="submit" value="Change password" />
		</form>
	</div>
</body>
</html>
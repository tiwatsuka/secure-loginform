<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reissue Password</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<body>
	<div id="wrapper">
		<h1>Reissue password</h1>
		<form:form action="${pageContext.request.contextPath}/reissue"
			method="Post" modelAttribute="reissueForm">
			<table>
				<tr>
					<th>Username</th>
					<td><form:password path="username" /></td>
				</tr>
			</table>
			<input type="submit" value="Reissue password" />
		</form:form>
	</div>
</body>
</html>
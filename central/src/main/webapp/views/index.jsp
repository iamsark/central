<%@ page session="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>

	<head>
		
		<meta charset="utf-8">	
		<meta name="HandheldFriendly" content="True">
		<meta name="MobileOptimized" content="320">
		<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"/>

		<title>Lever Edge Central App - Sign In</title>
	
		<link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favico.png">		
		<link type="text/css" href="<%=request.getContextPath()%>/assets/css/leca-signin.css" rel="stylesheet">
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/libs/jquery-3.5.1.min.js"></script>
		
		<script type="text/javascript">
		
			$(document).on("submit", "#leca-central-login-form", function(e) {
				
				let _payload = {},
					proceed = true,
					username = $("#leca_username"),
					password = $("#leca_password");
				
				if (username.val() == "") {
					proceed = false;
					username.closest(".leca-input-wrapper").next().show();	
				}				
				if (password.val() == "") {
					proceed = false;
					password.closest(".leca-input-wrapper").next().show();
				}
				
				_payload["username"] = username.val();
				_payload["password"] = password.val();
				
				if (proceed) {
					// Prepare Ajax object
					var param = {  
						type       : "POST",  
						data       : {leca_request_body: JSON.stringify({action: "", entity: "", task: "", page: 0, payload: _payload, data_type: "json", content_type: "application/json; charset=utf-8"})},   
						dataType   : "text",
						contentType: "/json; charset=utf-8",
						url        : "<%=request.getContextPath()%>/user/signin",
						beforeSend : function() {},
						success    : function(data) { console.log("/central"+ data);
							location.href = "/central"+ data;
						},
						error      : function( jqXHR, textStatus, errorThrown ) {}
					};
					
					// Boom.........
					$.ajax( param );
				}
				
				e.preventDefault();
			});
		
		</script>
		
	</head>

	<body class="signin">
	
		<div class="leca-master-container">
		
			<div class="leca-login-form-container">
			
				<div class="leca-left-part">
					
					<div class="leca-brand-logo-container">
						<img src="<%=request.getContextPath()%>/assets/img/logo.png" />
					</div>
					
				</div>
				<div class="leca-right-part">
				
					<form action="" method="POST" id="leca-central-login-form">
					
						<h3>Sign in</h3>
					
						<div class="leca-form-row">
							<div class="leca-input-wrapper">
								<span><i class="material-icons">person</i></span>
								<input type="text" id="leca_username" name="leoc_username" />
							</div>							
							<div class="leca-validation-message">Please enter your user name</div>
						</div>
						<div class="leca-form-row">
							<div class="leca-input-wrapper">
								<span><i class="material-icons">lock</i></span>
								<input type="password" id="leca_password" name="leoc_password" />
							</div>
							
							<div class="leca-validation-message">Please enter your password</div>
						</div>						
						<div class=leca-form-row>
							<button type="submit" class="btn btn-primary btn-login" >Login</button>
						</div>
					
					</form>
				
				</div>
			
			</div>
		
		</div>

	</body>
	
</html>
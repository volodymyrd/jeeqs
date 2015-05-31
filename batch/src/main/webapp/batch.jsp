<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Batch start page</title>
<script src="js/jquery-2.1.3.min.js"></script>
</head>
<body>
	<button id="startjobId">Start</button>

	<script>
		$(function() {
			$("#startjobId").on('click', function(event) {
				$.get("batch/hello", function(data) {
					console.log(data);
				});
			});
		});
	</script>
</body>
</html>
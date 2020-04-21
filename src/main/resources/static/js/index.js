$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// set csrf token to request header before AJAX
	// var token = $("meta[name='_csrf']").attr("content");
	// var header = $("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e, xhr, options) {
	// 	xhr.setRequestHeader(header, token);
	// });

	// get title and content
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	// AJAX
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function(data) {
			data = $.parseJSON(data);
			// display message in hint body
			$("#hintBody").text(data.msg);
			// show hint body
			$("#hintModal").modal("show");
			// hide hint body after 2 seconds
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// refresh if success
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	)
}
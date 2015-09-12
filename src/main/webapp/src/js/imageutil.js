/**
 * 
 */
function createImageIndex(){
	$("#info").html("正在创建索引...");
	$.ajax({
		type:"POST",
		url:"createImageIndex",
		success : function(obj) {
			if(obj){
				$("#info").html("索引创建成功");
			}else{
				$("#info").html("索引创建失败");
			}
		},
		async : false
	});
}
function copyImages(){
	$("#info").html("正在拷贝...");
	$.ajax({
		type:"POST",
		url:"copyImages",
		success : function(obj) {
			$("#info").html("拷贝成功"+obj);
		},
		async : false
	});
}

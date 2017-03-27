//点击分页，显示遮罩层
function pageChange(){
	$(".eXtremeTable img[title=上一页]").bind("click",function(){
		showMask();	
	});
	$(".eXtremeTable img[title=下一页]").bind("click",function(){
		showMask();	
	});
	$(".eXtremeTable img[title=第一页]").bind("click",function(){
		showMask();	
	});
	$(".eXtremeTable img[title=最后页]").bind("click",function(){
		showMask();	
	});
	$(".eXtremeTable select[name=ec_rd]").bind("change",function(){
		showMask();	
	});
	$(".eXtremeTable img[title=搜索]").bind("change",function(){
		showMask();	
	});
	$(".eXtremeTable img[title=清除]").bind("change",function(){
		showMask();	
	});
	
}

function retTest(){
	alert("retTest");
	return "retTest";
}

function roleOptionMove(){
//移到右边
	$('#add').click(function() {
	//获取选中的选项，删除并追加给对方
		$('#allRole option:selected').appendTo('#userRole');
	});
	//移到左边
	$('#remove').click(function() {
		$('#userRole option:selected').appendTo('#allRole');
	});
	//全部移到右边
	$('#add_all').click(function() {
		//获取全部的选项,删除并追加给对方
		$('#allRole option').appendTo('#userRole');
	});
	//全部移到左边
	$('#remove_all').click(function() {
		$('#userRole option').appendTo('#allRole');
	});
	//双击选项
	$('#allRole').dblclick(function(){ //绑定双击事件
		//获取全部的选项,删除并追加给对方
		$("option:selected",this).appendTo('#userRole'); //追加给对方
	});
	//双击选项
	$('#userRole').dblclick(function(){
	   $("option:selected",this).appendTo('#allRole');
	});
}


function myclose(){
	//alert("myclose");
	  $("#notice").hide();
}


var newMask;
var tipsDiv;
var docEle = function()  
{
  return document.getElementById(arguments[0]) || false;
};
function hideMask(){
	document.body.removeChild(document.getElementById("tipsDiv"));
	document.body.removeChild(document.getElementById("mask"));
}
function showMask(){
	openNewDiv('newDiv'); 
	//document.body.removeChild(newMask);
	//document.body.removeChild(tipsDiv);
	//setTimeout("document.body.removeChild(newMask);",3000);
	//setTimeout("new function(){document.body.removeChild(tipsDiv);}",2000);
}

function openNewDiv(_id)  
{
	var m = "mask";
	  if (docEle(_id)) document.body.removeChild(docEle(_id));
	  if (docEle(m)) document.body.removeChild(docEle(m));
	  //mask遮罩层
	  newMask = document.createElement("div");
	  newMask.id = m;
	  newMask.style.position = "absolute";
	  newMask.style.zIndex = "1";
	  _scrollWidth = Math.max(document.documentElement.scrollWidth,document.documentElement.scrollWidth);
	  _scrollHeight = Math.max(document.documentElement.scrollHeight,document.documentElement.scrollHeight);
	  newMask.style.width = (_scrollWidth-2) + "px";
	  newMask.style.height = (_scrollHeight-2) + "px";
	  newMask.style.top = "0px";
	  newMask.style.left = "0px";
	  //#010101
	  newMask.style.background = "#010101";
	  newMask.style.filter = "alpha(opacity=50)";
	  newMask.style.opacity = "0.70";
	  tipsDiv = document.createElement("div");
	  tipsDiv.id = "tipsDiv";
	  tipsDiv.style.fontSize = "18px";
	  tipsDiv.style.top = document.documentElement.scrollTop+200+ "px";
	  tipsDiv.style.left = (document.documentElement.scrollLeft + document.body.clientWidth/2)-100 + "px";
	  tipsDiv.style.position = "absolute";
	  tipsDiv.style.zIndex = "9999";
	  tipsDiv.style.weight = "800";
	  tipsDiv.style.width = "150px";
	  tipsDiv.style.fontWeight = "bold";
	  tipsDiv.style.border = "1px solid #fff";
	 // tipsDiv.style.backgroundColor = "#010101";
	  tipsDiv.style.lineHeight = "50px";
	  tipsDiv.style.fontFamily = "微软雅黑";
	  //tipsDiv.style.filter = "alpha(opacity=50)";
	  tipsDiv.style.align = "center";
	  //tipsDiv.style.margin = "300px 100px 100 100px";
	  //tipsDiv.innerHTML = "正在查询，请稍候......";
	  
	  tipsDiv.innerHTML = "<div style='width:150px' align='center'><img style='width:50px' src='images/sys/waiting1.gif'></div><div style='width:150px;font-size:12px;color:#fff' align='center''>正在处理，请稍候...</div>";
	  
	  document.body.appendChild(newMask);
	  document.body.appendChild(tipsDiv);
}

function findframe(value,sorttag){
	theframe = parent[value];
	theframe.location.href=sorttag;
}

function getFileName(filePath){
	var fileName = filePath.split("\\");
	//alert(fileName[fileName.length-1]);
	return fileName[fileName.length-1];
}



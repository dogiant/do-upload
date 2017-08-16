<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>图片文件上传文档</title>
		<meta name="robots" content="nofollow">
		<meta name="robots" content="noarchive">
        <meta http-equiv="pragma" content="no-cache">
        <meta http-equiv="cache-control" content="no-cache">
        <meta http-equiv="expires" content="0">
        <link type="text/css" rel="stylesheet" href="/assets/css/imgareaselect-default.css"/>
        <script type="text/javascript" src="/assets/scripts/jquery.min.js"></script>
        <script type="text/javascript" src="/assets/scripts/jquery.form.js"></script>
        <script type="text/javascript" src="/assets/scripts/ajaxfileupload.js"></script>
		<script type="text/javascript" src="/assets/scripts/jquery.imgareaselect.pack.js"></script>
    </head>

    <body>
		<div id="content" class="container">
		<h1>Image Upload Documentation</h1>
		<p class="revision">
		<!-- 
		二〇一七年八月十五日
		 -->
		</p>
		<ol>
			 <li><a href="#introduction">简介</a></li>
			 <li><a href="#basic-usage">基本用法</a></li>
			 <li><a href="#options">字段选项</a></li>
			 <li><a href="#img-area-select">图片区域剪裁</a></li>
		</ol>
		<a name="introduction"></a>
		<h2>简介</h2>
		<p>
		 图片文件上传项目，支持图片剪裁，水印等。
		</p>
		<hr/>
		<a name="basic-usage"></a>
		<h2>基本用法</h2>
		<div>
		<p>${errors }</p>
		<form action="upload" enctype="multipart/form-data" method="post">
        	返回类型 :<input type="text" name="returnType" placeholder="json" /> returnType(json|url)<br/>
        	返回地址 :<input type="text" name="returnUrl" /> returnUrl(http://aa.com/aa.do)当type=url时有效<br/>
        	使用方通道标识: <input type="text" name="channel" /> channel(avatar|pic|...) 使用方通道标识<br/>
        	是否生成缩略图: <input type="text" name="genThumbnails" /> genThumbnails(true|false) 图片生成缩略图<br/>
        	尺寸描述: <input type="text" name="sizes" /> sizes(width,height,suffix) 例如：100,100,_100_square<br/>
        	水印: <input type="text" name="waterMark" /> waterMark(true|false) <br/>
        	水印文件路径: <input type="text" name="waterMarkPath" /> waterMarkPath(/data/upload/files/watermark/watermark.png) <br/>
            选择图片: <input type="file" name="uploads" /> <br/>
            　　　　　 <input type="file" name="uploads" /> <br/>
           <input type="submit" value="上传" />
        </form>

		</div>
		<hr/>
		<a name="options"></a>
		<h2>字段选项</h2>
		<table class="list">
		 <thead>
		  <tr>
		   <th>Option</th>
		   <th>Description</th>
		  </tr>
		 </thead>
		 <tbody>
		  <tr>
		   <td>
		    <code>returnType</code>
		   </td>
		   <td>
		    返回类型：json格式 或者 url跳转.
		   </td>
		  </tr>
		  <tr>
		   <td><code>returnUrl</code></td>
		   <td>
		    返回地址
		   </td>
		  </tr>
		  <tr>
		   <td><code>channel</code></td>
		   <td>
		    使用方通道标识
		   </td>
		  </tr>
		  <tr>
		   <td><code>genThumbnails</code></td>
		   <td>
		    是否生成缩略图
		   </td>
		  </tr>
		  <tr>
		   <td><code>sizes</code></td>
		   <td>
		   尺寸描述， 例如：100,100,_100_square
		   </td>
		  </tr>
		  <tr>
		   <td><code>waterMark</code></td>
		   <td>
		   水印
		   </td>
		  </tr>
		  <tr>
		   <td><code>waterMarkPath</code></td>
		   <td>
		    水印文件路径
		   </td>
		  </tr>
		 </tbody>
		</table>
		<hr/>
		<a name="img-area-select"></a>
		<h2>图片区域剪裁</h2>
		<div>
			<input type="hidden" name="x1" value="0" />  
	    	<input type="hidden" name="y1" value="0" />  
			<input type="hidden" name="x2" value="100" />  
			<input type="hidden" name="y2" value="100" /> 
			<form id="uploadAjaxForm" action="upload" enctype="multipart/form-data" method="post">
				<input id="uploadAjax" name="uploads" type="file" onchange="uploadAjaxSubmit(this);"/>  
			</form>
			<div id="facediv" style="display:none;z-index:100;">  
		        <img id="face" />  
	        </div>
	        <div id="faceCropDiv" style="display:none;z-index:100;">
	        	<img id="faceCrop"/>
	        </div>
        </div>
		</div>
		<hr/>
		<script type="text/javascript">
		var fileHost = "${fileHost}";
		function uploadAjaxSubmit(o) {
			var ajaxForm = $('#uploadAjaxForm'), $file = $(o).clone();
			//ajaxForm.append($file);
			var options = {
				dataType : "json",
				data : {returnType:"json","channel":"avatar",upload:$file.val()},
				beforeSubmit : function() {
					//alert("开始上传");
				},
				success : function(data) {
					if (data.success) {
						$("#facediv").css({"display":"block"});
						$("#face").attr("src", fileHost + data['fileName'] );
						$('<div><img src="' + fileHost + data['fileName'] + '" style="position: relative;" /><div>')
					        .css({
					            float: 'left',
					            position: 'relative',
					            overflow: 'hidden',
					            width: '150px',
					            height: '150px',
					            padding:'0,10px,0,10px'
					        }).insertAfter($('#face'));
						
						$('<button id="btnSubmit">提交</button>')
				        .click(function (){
				        	cutImage(data['fileName']);
				        }).insertAfter($('#facediv'));
						$(".imgareaselect-outer").show();
						$(".imgareaselect-selection").parent().show();
					    $('#face').imgAreaSelect({
							maxWidth: 500, maxHeight: 500,
							minWidth: 63, minHeight:63,
							x1:100,y1:100,x2:250,y2:250,
					        aspectRatio: '1:1', 
							onSelectChange: function (img, selection) {
								var scaleX = 150 / (selection.width || 1);
								var scaleY = 150 / (selection.height || 1);
							  	var w = img.width;
							  	var h = img.height;
								$('#face + div > img').css({
									width: Math.round(scaleX * img.width) + 'px',
									height: Math.round(scaleY * img.height) + 'px',
									marginLeft: '-' + Math.round(scaleX * selection.x1) + 'px',
									marginTop: '-' + Math.round(scaleY * selection.y1) + 'px'
								});
							},
							onSelectEnd: function (img, selection) {
								$('input[name="x1"]').val(selection.x1);
								$('input[name="y1"]').val(selection.y1);
								$('input[name="x2"]').val(selection.x2);
								$('input[name="y2"]').val(selection.y2);
							} 
						});
					}
				},
				error : function(data) {

				}
			};
			ajaxForm.ajaxSubmit(options);
			return false;
		}

		function cutImage(path) {
			$.ajax( {
				type : "POST",
				url:"upload",
				dateType:"jsonp",
				data:{"x1":$('input[name="x1"]').val(),
				"x2":$('input[name="x2"]').val(),
				"y1":$('input[name="y1"]').val(),
				"y2":$('input[name="y2"]').val(),
				"channel":"avatar","imgAreaSelect":true,"imgPath":path},
				success : function(data) {
					if(data.success){
						//alert("头像剪裁成功:" + data.fileName);
						$("#facediv").css({"display":"none"});
						$("#face").css({"display":"none"});
						$("#facediv").remove();
						$(".imgareaselect-outer").hide();
						$(".imgareaselect-selection").parent().hide();
						$("#faceCropDiv").css({"display":"block"});
						$("#faceCrop").attr("src", fileHost + data['fileName'] );
					}else{
						alert("头像剪裁失败");
					}
				},
				error:function(data) {
					
				}
			});
		}
		</script>
    </body>
</html>
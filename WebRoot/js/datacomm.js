// JavaScript Document
function login(form){
	if(form.isValid()){
		form.submit({
			waitTitle:'提示',
			waitMsg:'正在验证用户信息，请稍候...',
			success:function(currentForm,action){
				var location;
				/*if ((navigator.userAgent.indexOf('MSIE') >= 0 || (navigator.userAgent.indexOf('.NET')) >= 0)&& (navigator.userAgent.indexOf('Opera') < 0)) {//ie
					location = 'pages/main/main.jsp';
				}
				else{
					location = 'pages/main/main.jsp';
				}*/
				location = 'pages/main/main.jsp';
				//alert(action.result.errorCode);
				if(action.result.errorCode == '00'){//验证通过
					window.location=location+'?consbReqId='+new Date().getTime();
				}else{//验证失败
//					Ext.Msg.show({title:'提示',msg:action.result.msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
					msgAlert(action.result.errorMsg);
				}
			},
			failure:function(form,action){
				//alert("tttttt");
//						Ext.Msg.alert('提示',action.result.msg+"|"+action.failureType);
//				Ext.Msg.show({title:'提示',msg:action.result.msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
				msgAlert(action.result.errorMsg);
			}
		});
	}
}
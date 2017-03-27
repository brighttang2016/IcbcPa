// JavaScript Document

Ext.application({
	name:'login',
	launch:function(){
		//alert("userId:"+userId);
		var form = '';
		var loginForm = Ext.create('Ext.form.Panel',{
			id:'loginFormId',
			defaultType:'textfield',
			labelAlign:'right',
			title:'用户登录',
			renderTo:Ext.getBody(),
			margin:'200 0 0 0',
			frame:true,
			width:250,//width配置项有效
			url:'loginCheck.html',
			method:'POST',
			header:{
				'Content-Type':'application/json',
				charset:'UTF-8'
			},
			draggable:true,
			defaults:{
//				width:250,
				labelWidth:50,
				allowBlank:false,
				blankText:'不能为空',
				labelAlign:'right',
				msgTarget:'side',
				autoFitErrors:false
			},
			items:[{
				fieldLabel:'用户id',
				value:userId,
				name:'userId',
				id:'userId',
				value:userIdLogin,
				readOnly:true,
				blankText:'请输入用户id'
			},{
				xtype:'hidden',
				fieldLabel:'用户id',
				value:'1',
				name:'loginTag',
				blankText:'请输入用户id'
			}]/*,
			buttons:[{
				text:'登录',
				handler:function() {
					form = loginForm.getForm();
					login(form);
				}
			}]*/
		});
		browserInfo = navigator.userAgent.toLowerCase();
		loginForm.center();
		loginForm.getForm().findField('userId').focus(true,true);
		document.onkeydown = getKeyCode;
		function getKeyCode(e){
			if(browserInfo.indexOf('msie') > 0)
				keyCode = event.keyCode;
			else
				keyCode = e.which;
			if(keyCode == 13){
				login(loginForm.getForm());
			}
		}
		
		function login(){
			form = loginForm.getForm();
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
						//location = 'pages/main/main.jsp';
						location = "toMain.html";
						if(action.result.validate == 1){//验证通过
							window.location=location+'?consbReqId='+new Date().getTime();
						}else{//验证失败
							Ext.Msg.show({title:'提示',msg:action.result.errorMsg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
							setTimeout(function(){
								parent.window.close();
							},3000);
						}
					},
					failure:function(form,action){
//						Ext.Msg.alert('提示',action.result.msg+"|"+action.failureType);
						Ext.Msg.show({title:'提示',msg:action.result.msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
					}
				});
			}
		}
		
		setTimeout(login,1000);
	}
});


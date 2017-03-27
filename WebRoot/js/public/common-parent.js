Ext.define('updFormPanelPrt',{
			extend:'Ext.form.Panel',
			id:'updFormPanel',
			buttonAlign:'center',
			fieldDefaults:{
				margin:'2 0 0 0',
				labelAlign:'right'
			},
//			url:'roleRouter.html',
//			method:'post',
			resizable:false,
			buttons:[{
				text:'保存',
				handler:handleSave
			},{
				text:'取消',
				handler:handlerCancle
			}]
		});
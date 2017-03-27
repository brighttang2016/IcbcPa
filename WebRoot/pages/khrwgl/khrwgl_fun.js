
/**
 * 获取表单面板对象
 * @returns {updFormPanelPrt}
 */
function getAddFp(){
	udpFormPanelAdd = new updFormPanelPrt({
		id:'udpFormPanelAdd',
		height:200,
		width:300,
//		border:false,
//		baseCls:'borderClass',
		items:[new queryTextField({
			columnWidth:.5,
//			frame:true,
			items:[tran_code]
		}),{
			baseCls:'my-panel-no-border',
			width:'100%',
			layout:{
				type:'hbox',
				pack:'center'
			},
			items:[new inputTextField({
				baseCls:'borderClass',
				border:false,
				margin:'5 0 5 0',
				name:'zq',
				fieldLabel:'考核周期',
				emptyText:'请录入考核周期',
				readOnly:false
			})]
		},{
			baseCls:'my-panel-no-border',
			layout:{
				type:'hbox',
				pack:'center'
			},
			items:[new queryDateInput({
				baseCls:'borderClass',
				margin:'5 0 5 0',
				name:'timeStart',
				fieldLabel:'起始日期',
				readOnly:false
			})]
		},{
			baseCls:'my-panel-no-border',
			layout:{
				type:'hbox',
				pack:'center'
			},
			items:[new queryDateInput({
				baseCls:'borderClass',
				margin:'5 0 5 0',
				name:'timeEnd',
				fieldLabel:'终止日期',
				readOnly:false
			})]
		}],
		buttons:[{
			//baseCls:'borderClass',
			text:'保存',
			handler:function(){
				comm_dir("param_khrw.html","gridStoreId","udpFormPanelAdd");
			}
		},{
			//baseCls:'borderClass',
			text:'取消',
			handler:handlerCancle
		}]
	});
	return udpFormPanelAdd;
}

//展示新增弹窗
function showAdd(){
	var tempPanel = getAddFp();
	var tempForm = tempPanel.getForm();
	tempForm.reset();
//			Ext.getCmp('tx_code').setValue('3011');
//	alert(tempForm.findField('user_name'));
//	tempForm.findField('userId').readOnly = false;
	tempForm.findField('tx_code').setValue('10000077');
	tempPanel.doLayout();
//	updFormPanel = udpFormPanelAdd;
//	winAdd = new win({
	winAdd = Ext.create('win',{
		title:'创建考核周期',
		width:450,
		height:180,
		items:[tempPanel]
	});
	winAdd.show();
}
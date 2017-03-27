
/**
 * 记录修改
 */
function recordUpd(myGrid){
	var records = myGrid.getSelectionModel().getSelection();
	if(records.length != 1){
		msgAlert("请选择一条记录");
	}else{
		var record = records[0];
	}
	return record;
}

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
			xtype:'hidden',
			name:'bl_id',
			fieldLabel:'考核比例编号'
		},{
			baseCls:'my-panel-no-border',
			layout:'column',
			items:[new queryTextField({
				columnWidth:.5,
				items:[new inputTextField({
					name:'nr_id',
					fieldLabel:'考核内容编号',
					readOnly:true
				})]
			}),new queryTextField({
				columnWidth:.5,
				items:[new inputTextField({
					name:'nr_name',
					fieldLabel:'考核内容名称',
					readOnly:true
				})]
			})]
		},{
			baseCls:'my-panel-no-border',
			layout:'column',
			items:[new queryTextField({
				columnWidth:.5,
				items:[new textFieldDcml({
					name:'min',
					fieldLabel:'下限',
					readOnly:false
				})]
			}),new queryTextField({
				columnWidth:.5,
				items:[new textFieldDcml({
					name:'max',
					fieldLabel:'上限',
					readOnly:false
				})]
			})]
		}],
		buttons:[{
			//baseCls:'borderClass',
			text:'保存',
			handler:function(){
				comm_dir("zbzbManage.html","gridStoreId","udpFormPanelAdd");
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
function showAdd(myGrid){
	var tempPanel = getAddFp();
	var tempForm = tempPanel.getForm();
	tempForm.reset();
//			Ext.getCmp('tx_code').setValue('3011');
//	alert(tempForm.findField('user_name'));
//	tempForm.findField('userId').readOnly = false;
	tempForm.findField('tx_code').setValue('20000015');
	tempPanel.doLayout();
	try{
		tempForm.loadRecord(recordUpd(myGrid));
	}catch(e){
		return;
	}
	
//	updFormPanel = udpFormPanelAdd;
//	winAdd = new win({
	winAdd = Ext.create('win',{
		title:'编辑',
		width:600,
		height:180,
		items:[tempPanel]
	});
	winAdd.show();
}
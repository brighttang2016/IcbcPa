

//删除
function handleDel(rec,url,storeId){
//	var formTemp  = Ext.getCmp(formId).getForm();
//	formTemp.loadRecord(rec);
//	formTemp.findField('tx_code').setValue('10013');
	Ext.Msg.confirm('提示信息','确认删除吗？',function(btn){
		   if(btn == 'yes'){
			   //comm_update(url,storeId,formId);
			   var paramColl = new Ext.util.MixedCollection();//集合
			   paramColl.add("ssId", rec.get("ssId"));
			   paramColl.add("tx_code", "30023");
			   var paramJson = Ext.decode(Ext.encode(paramColl)).map;
			   comm_ajax(url,storeId,paramJson);
		   }   
	   });
}
function handleUpdate(rec,storeId,formId){
	var tempPanel = Ext.getCmp(formId);
	var formTemp  = tempPanel.getForm();
	formTemp.loadRecord(rec);
	formTemp.findField('tx_code').setValue('30041');
	winUpd = Ext.create('win',{
		title:'编辑',
		width:600,
		height:150,
		items:[tempPanel]});
	winUpd.show();
}
//展示新增弹窗
function showAdd(formId){
	var tempPanel = Ext.getCmp(formId);
	var tempForm = tempPanel.getForm();
	tempForm.reset();
//			Ext.getCmp('tx_code').setValue('3011');
//	alert(tempForm.findField('user_name'));
//	tempForm.findField('userId').readOnly = false;
	tempForm.findField('tx_code').setValue('30021');
	tempPanel.doLayout();
//	updFormPanel = udpFormPanelAdd;
//	winAdd = new win({
	winAdd = Ext.create('win',{
		title:'新增',
		width:600,
		height:150,
		items:[tempPanel]
	});
	winAdd.show();
}
//		增加、修改处理结束----------------------------



	
		
		
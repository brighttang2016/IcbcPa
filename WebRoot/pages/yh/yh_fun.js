
function handleSave(url,storeId,formId){
	var role_auth;
//			var records = treePanel.getChecked();
	var records = Ext.getCmp('treePanelId').getChecked();
	idArr = [];
	Ext.Array.each(records,function(rec){
		if(rec.get('leaf'))
			idArr.push(rec.get('id'));
	});
	for(var i = 0;i < idArr.length;i++){
		if(i == 0)
			role_auth = idArr[i];
		else
			role_auth += "|"+idArr[i];
	}
	if(idArr.length > 0){
		Ext.getCmp(formId).getForm().findField('role_auth').setValue(role_auth);
		//handlerSave('roleRouter.html','myStoreId');
		comm_update(url,storeId,formId);
	}else{
		noticeShow("角色权限不能为空");
	}
}

//删除
function handleDel(rec,url,storeId){
//	var formTemp  = Ext.getCmp(formId).getForm();
//	formTemp.loadRecord(rec);
//	formTemp.findField('tx_code').setValue('10013');
	Ext.Msg.confirm('提示信息','确认删除吗？',function(btn){
		   if(btn == 'yes'){
			   //comm_update(url,storeId,formId);
			   var paramColl = new Ext.util.MixedCollection();//集合
			   paramColl.add("userId", rec.get("userId"));
			   paramColl.add("tx_code", "10013");
			   var paramJson = Ext.decode(Ext.encode(paramColl)).map;
			   var params = {tranData:Ext.encode(paramJson)};
			   comm_ajax(url,storeId,params);
		   }   
	   });
}
function handleUpdate(rec,storeId,formId,comboJbjx){
	var tempPanel = Ext.getCmp(formId);
	
	var formTemp  = tempPanel.getForm();
	formTemp.loadRecord(rec);//第一次加载（所属岗位下拉框赋值）
	setTimeout(function(){
		comboJbjx.getStore().reload();
		formTemp.loadRecord(rec);//第二次加载(绩效考核岗位赋值)
	},1000);
	
	formTemp.findField('tx_code').setValue('10012');
//	formTemp.findField('orgDeptComboUpd').setValue(rec.get("deptName"));
//	formTemp.findField('orgInfoUpd').setRawValue(rec.get("depName"));
	formTemp.findField('tpUpdId').setRawValue(rec.get("depName"));
	setTimeout(function(){
		Ext.Ajax.request({
			type:'ajax',
			url:'getUserRoleInfo.html',
			params:{
				userId:rec.get("userId")
			},
			success:function(resp,opts){
				var strData=resp.responseText;
				//alert(strData);
				var jsonObject = Ext.JSON.decode(strData);
				var jsonRowArray = jsonObject.rows;
				//alert("jsonRow:"+jsonRow[0].role_id);
				var valueArray = new Array();
				for(var i = 0;i < jsonRowArray.length;i++){
					//alert(jsonRowArray[i].role_id);
					valueArray.push(jsonRowArray[i].role_id);
				}
				formTemp.findField('selFormAddId').setValue(valueArray);
//				winUpd = new win({
				/*winUpd = Ext.create('win',{
					title:'编辑',
					closeAction:'hide',
					width:600,
					height:430,
					items:[tempPanel]});
				winUpd.show();*/
			},
			failure:function(resp,opts){
			}
		});
	},1000);
	winUpd = Ext.create('win',{
		title:'编辑',
		closeAction:'hide',
		width:600,
		height:430,
		items:[tempPanel]});
	winUpd.show();
	
}
//展示新增弹窗
function showAdd(formId){
	var tempPanel = Ext.getCmp(formId);
	var tempForm = tempPanel.getForm();
	tempForm.reset();
	tempForm.findField('userId').readOnly = false;
	tempForm.findField('tx_code').setValue('10011');
	tempPanel.doLayout();
	winAdd = Ext.create('win',{
		title:'新增',
		width:600,
		closeAction:'hide',
		height:430,
		items:[tempPanel]
	});
	winAdd.show();
}
//		增加、修改处理结束----------------------------



	
		
		
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
function handleDel(rec){
	
	updFormPanel.getForm().loadRecord(rec);
	updFormPanel.getForm().findField('tx_code').setValue('10023');
	Ext.Msg.confirm('提示信息','确认删除吗？',function(btn){
		   if(btn == 'yes'){
//					   handlerSave('roleRouter.html','myStoreId');
			   comm_update("roleRouter.html","myStoreId","updFormPanelId");
		   }   
	   });
}
function handleUpdate(rec,storeId,updFormPanel){
	updFormPanel.getForm().loadRecord(rec);
	updFormPanel.getForm().findField('tx_code').setValue('10022');
	Ext.data.StoreManager.lookup(storeId).reload();
//			treeStore.reload();
//			Ext.getCmp('tx_code').setValue('3012');
//	winUpd = new win({items:[updFormPanel]});
	winUpd = Ext.create('win',{
		items:[updFormPanel],
		closeAction:'hide'//若使用destroy，js.js 310行（updForm = Ext.getCmp('updFormPanelId').getForm();）抛出异常：Uncaught TypeError: Cannot call method 'getForm' of undefined   2016-02-03
	});
	winUpd.title="编辑",
//			updFormPanel.getForm().findField('user_id').hide();
	winUpd.show();
}
function showAdd(treeStoreId){
	updFormPanel.getForm().reset();
	updFormPanel.getForm().findField('tx_code').setValue('10021');
	updFormPanel.getForm().findField('role_id').setValue('-1');//-1 没有角色id
	//treeStore.load();
	Ext.data.StoreManager.lookup("treeStoreId").load();
	updFormPanel.doLayout();
	winAdd = Ext.create('win',{
		items:[updFormPanel],
		closeAction:'hide'
	});
	//winAdd = new win({items:[updFormPanel]});
	winAdd.title="添加";
	winAdd.show();
}
//		增加、修改处理结束----------------------------

var allNodes = [];
function findChild(record){
	record.eachChild(function(record){
		allNodes.push(record);
		findChild(record);
	});
}
		//根据当前节点及兄弟节点，递归设置父节点状态
function setParentNode(allNodes,record){
	var parentRecord;
//			alert("tttt"+record.get('checked'));
	parentChecked = false;
	//判断record父节点状态
	//算法一：只要有一个子节点选中，则向上递归选中所有父级节点，当所有子代节点置为不选中状态时，父级节点才不选中
	for(var i = 0;i < allNodes.length;i++){
		tempRecord = allNodes[i];
		if(tempRecord.get('parent_id') == record.get('parent_id')){
//					parentRecord.set('checked',true);
			if(tempRecord.get('checked') == true){
				parentChecked = true;
			}
		}
	}
	//设置record父节点状态
	for(var i = 0;i < allNodes.length;i++){
		tempRecord = allNodes[i];
		if(tempRecord.get('id') == record.get('parent_id')){
			parentRecord = tempRecord;
			parentRecord.set('checked',parentChecked);
		}
	}
	try{
		setParentNode(allNodes,parentRecord);//防止js抛set方法不存在异常
	}catch(e){}
	
	/*
	 * 算法二：当子节点全选中后，父节点才显示选中，只要其中一个子节点取消选中，父节点则置为没选中状态
	for(var i = 0;i < allNodes.length;i++){
		tempRecord = allNodes[i];
		if(tempRecord.get('id') == record.get('parent_id')){
			parentRecord = tempRecord;
			parentRecord.set('checked',true);
			
		}
	}
	for(var i = 0;i < allNodes.length;i++){
		tempRecord = allNodes[i];
		if(tempRecord.get('parent_id') == record.get('parent_id')){
			if(!tempRecord.get('checked')){
				try{
					parentRecord.set('checked',false);//防止js抛set方法不存在异常
				}catch(e){}
			}	
		}
	}
	try{
		setParentNode(allNodes,parentRecord);//防止js抛set方法不存在异常
	}catch(e){}
	*/
}
//根据父节点状态，递归设置子节点checked状态
function setChildNode(record,flag){
		record.eachChild(function(record){
			record.set('checked',flag);
			setChildNode(record,flag);
		});
}
//		treePanel.on('checkchange',function(record,flag){
		
		

/**
 * 查看详细信息框架窗口
 * @param baseInfo 基本信息fieldset
 * @param pointInfo 积分信息fieldset
 */
function showPopWin(baseInfo,pointInfo,wglHisInfo,jbHisInfo,xfInfo,ctInfo){
	userInfoPanel = Ext.create('Ext.tab.Panel', {
		id:'userInfoPanelId',
	    margin:'1 1 1 1',
	    border:true,
	    //renderTo: document.body,
	    defaults:{
	    	border:false,
	    	layout:{
	    		type:'hbox',
	    		pack:'center'
	    	}
	    },
	    items: [{
	        title: '个人基本信息',
	        items:[baseInfo]
	    },{
	        title: '用户积分历史信息',
	        items:[pointInfo]
	    },{
	        title: '工资等级档次历史记录',
	        items:[wglHisInfo]
	    },{
	        title: '岗位层级历史变更记录',
	        items:[jbHisInfo]
	    },{
	        title: '学分信息',
	        items:[xfInfo]
	    },{
	        title: '资格信息',
	        items:[ctInfo]
	    }]
	});
	
	winAdd = Ext.create('Ext.window.Window',{
		title:'用户详细信息',
		width:'90%',
		height:'100%',
		constrain:true,
//		autoScroll:false,
		bodyStyle:'background:#ffffff',
		layout:{
			type:'fit',
		},
		modal:true,
//		maximized:true,//默认最大化窗口
		maximizable:true,
		items:[userInfoPanel]
	});
	winAdd.show();
}

//显示用户详细信息
function showDetail(grid){
	var sm = grid.getSelectionModel();
	var records = sm.getSelection();
	var record = null;
	var userId;
	if(records.length == 0){
		msgAlert("操作错误:请选择一条记录");
	}else if(records.length >= 2){
		msgAlert("操作错误:仅能选择一条记录");
	}else if(records.length == 1){
		var record = records[0];
		var userId = record.get("userId");
		var recordCol = new Ext.util.MixedCollection();
		recordCol.add("userid",record.get("userId"));
		recordCol.add("name",record.get("name"));
		recordCol.add("orgname",record.get("orgName"));
		recordCol.add("depname",record.get("depName"));
		recordCol.add("sex",record.get("sex"));
		recordCol.add("mail",record.get("mail"));
		recordCol.add("begindate",record.get("beginDate"));
		recordCol.add("propertyname",record.get("propertyName"));
		recordCol.add("jbname",record.get("jbName"));
//		alert(Ext.encode(recordCol));
		var recordParse = Ext.decode(Ext.encode(recordCol)).map;//record转json对象（record类似javabean，属性需用get(属性名称)获取,转json后，通过“.”来获取）
		var baseInfo = userBasicInfo(recordParse);//用户基本信息（userBasicInfo函数入参类型：json对象）
		var pointInfo = userPointHisInfo(userId);//用户积分历史信息
		var wglHisInfo = userWglHisInfo(userId);//工资等级档次历史信息
		var jbHisInfo = userJbHisInfo(userId);//岗位历史变更记录
		var xfInfo = userxfInfo(userId);//用户学分记录
		var ctInfo = userCtInfo(userId);//用户以获资格
		showPopWin(baseInfo,pointInfo,wglHisInfo,jbHisInfo,xfInfo,ctInfo);
	}
}

/*function handleSave(url,storeId,formId){
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
}*/

//删除
/*function handleDel(rec,url,storeId){
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
			   comm_ajax(url,storeId,paramJson);
		   }   
	   });
}*/
/*function handleUpdate(rec,storeId,formId){
//	alert(rec.get("deptId"));
	var tempPanel = Ext.getCmp(formId);
	var formTemp  = tempPanel.getForm();
	formTemp.loadRecord(rec);
	formTemp.findField('tx_code').setValue('10012');
//	formTemp.findField('orgDeptComboUpd').setValue(rec.get("deptName"));
	formTemp.findField('orgInfoUpd').setRawValue(rec.get("depName"));
	
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
//			winUpd = new win({
			winUpd = Ext.create('win',{
				title:'编辑',
				width:600,
				height:430,
				items:[tempPanel]});
			winUpd.show();
		},
		failure:function(resp,opts){
		}
	});
}*/
//展示新增弹窗
/*function showAdd(formId){
	var tempPanel = Ext.getCmp(formId);
	var tempForm = tempPanel.getForm();
	tempForm.reset();
//			Ext.getCmp('tx_code').setValue('3011');
//	alert(tempForm.findField('user_name'));
	tempForm.findField('userId').readOnly = false;
	tempForm.findField('tx_code').setValue('10011');
	tempPanel.doLayout();
//	updFormPanel = udpFormPanelAdd;
//	winAdd = new win({
	winAdd = Ext.create('win',{
		title:'新增',
		width:600,
		height:430,
		items:[tempPanel]
	});
	winAdd.show();
}*/
//增加、修改处理结束----------------------------



	
		
		
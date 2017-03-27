/**
 * 叶子节点后动判断（若不用leaf属性）
 * @param node
 * @returns
 */
function hasChild(node){
	var childNum = 0;
	node.eachChild(function(record){
		childNum++;
	});
	return childNum == 0 ? false:true;
}

/**
 * 表单初始化
 * @param formId
 * @param record
 * @param treeStoreId
 */
function formInit(formId,record,treeStoreId){
	FormUtil.disableForm(formId);
	formTemp = Ext.getCmp(formId).getForm();
	if(record.get("pId") == "-1")
		record.set("pName","根节点");
	formTemp.loadRecord(record);
	setTimeout(function(){
		tempCombo = Ext.getCmp("addType");
		tempCombo.setValue("100");
	},100);
	orgId = getLatestOrgId(getNode(treeStoreId,record.get("menuId")),treeStoreId);//获取当前操作节点最接近机构id
	Ext.getCmp("orgId").setValue(orgId);
}



function handleSave(url,storeId,formId){
	comm_update(url,"",formId);//tree需要特殊的局部加载，故不需要使用公用的ajax提交后自动加载store，故store传“”
	storeTemp = Ext.data.StoreManager.lookup(storeId);
	var node =storeTemp.getNodeById(formTemp.findField('pId').getValue()); 
	var parentNode = node.parentNode;
	if(node.get('leaf'))//当前添加节点的节点如果是叶子节点，那么在此节点下添加节点后，应刷新本节点的上层节点，否则，添加后，本节点由于是叶子节点，导致无法刷新
		storeTemp.load({node:parentNode});
	else
		storeTemp.load({node:node});
//	storeTemp.reload();//直接reload树，会抛出异常：Uncaught TypeError: Cannot read property 'internalId' of undefined。故采用更为合理的局部节点刷新 （2015-06-25 brighttang）
	FormUtil.disableForm(formId);
}

//删除节点通信
function comm_update_jg(url,storeId,formId,menuId){
	//alert("数据通讯comm_update"+url+"|"+storeId+"|"+formId);
	var paramJson = getParam(formId);
	var childNum = 0;
	node = getNode(storeId,menuId);
	parentNode = node.parentNode;
	parentNode.eachChild(function(record){
		childNum++;
	});
	storeTemp = Ext.data.StoreManager.lookup(storeId);
	if(Ext.getCmp(formId).getForm().isValid()){
		Ext.Ajax.request({
			url:url,
			params:paramJson,
			success:function(resp,opts){
				//var currStore = Ext.data.StoreManager.lookup(storeId);
				if(childNum >= 2){
//					msgAlert("刷新："+parentNode.get("menuId"));
					storeTemp.load({node:parentNode});
				}else{
					
//					msgAlert("刷新："+parentNode.parentNode.get("menuId"));
					storeTemp.load({node:parentNode.parentNode});
				}
			},
			failure:function(resp,opts){
			}
		});
		handlerCancle();
	}else{
		msgAlert("表单非法,无法提交");
	}
	return;
}
//删除
function handleDel(formId,record,storeId){
	
	var disArr = [];
	var hideArr = [];
	var showArr = [];
	formTemp = Ext.getCmp("updFormPanelId").getForm();
	FormUtil.enableForm("updFormPanelId", disArr,hideArr,showArr);
	record.set("tx_code","10033");
	formTemp.loadRecord(recordNow);
//	formTemp.findField("tx_code").setValue("10033");
	formTemp.findField("menuType").setValue(menuType);
	menuId = record.get("menuId");
	nodeNow = getNode(storeId,menuId);
	if(record.get("leaf")){
		Ext.Msg.confirm('提示信息','确认删除吗？',function(btn){
			   if(btn == 'yes'){
				   comm_update_jg("orgManage.html",storeId,"updFormPanelId",menuId);
			   }   
		});
	}else{
		msgAlert("无法删除，请提前删除子机构或子部门");
	}
	FormUtil.disableForm(formId);
}
function handleUpdate(formId,record,menuType){
	var disArr = [];
	var hideArr = [];
	var showArr = [];
	hideArr.push("orgId");
	hideArr.push("pId");
	hideArr.push("pName");
	hideArr.push("addType");
	hideArr.push("menuType");
	disArr.push("menuId");
	formTemp = Ext.getCmp(formId).getForm();
	FormUtil.enableForm(formId, disArr,hideArr,showArr);
	record.set("tx_code","10032");
	formTemp.loadRecord(record);
	
//	formTemp.findField("tx_code").setValue("10032");
	
//	formTemp = Ext.getCmp(formId).getForm();
	
	
	formTemp.findField("menuType").setValue(menuType);
}
function showAdd(formId,recordNow){
	var hideArr = [];//隐藏组件
	var showArr = [];//显示组件
	var disArr = [];//设置组件不可用
	showArr.push("pId");
	showArr.push("pName");
	showArr.push("addType");
	showArr.push("menuId");
	showArr.push("menuName");
	disArr.push("pId");
	disArr.push("pName");
	hideArr.push("orgId");
	hideArr.push("menuType");
	formTemp = Ext.getCmp(formId).getForm();
	recordNow.set("tx_code","10031");
	recordNow.set("pId",recordNow.get("menuId"));
	recordNow.set("pName",recordNow.get("menuName"));
	formInit("updFormPanelId",recordNow,"treeStoreId");
	formTemp.findField("menuId").setValue("");
	formTemp.findField("menuName").setValue("");
	FormUtil.enableForm(formId, disArr,hideArr,showArr);
}


		
		
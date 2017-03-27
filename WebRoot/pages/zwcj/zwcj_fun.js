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
	var ctComboStore = Ext.data.StoreManager.lookup("ctComboStoreId");
	var dtComboStore = Ext.data.StoreManager.lookup("dtComboStoreId");
	var wgComboStore = Ext.data.StoreManager.lookup("wgComboStoreId");
	var wlComboStore = Ext.data.StoreManager.lookup("wlComboStoreId");
	
	ctComboStore.load();
	dtComboStore.load();
	wgComboStore.load();
	wlComboStore.load();
	var disArr = [];
	var hideArr = [];
	var showArr = [];
	hideArr.push("pId");
	hideArr.push("pName");
	if(record.get("leaf") == false){//非叶子节点（岗位序列）
		showArr.push("pId");
		showArr.push("pName");
		showArr.push("menuId");
		showArr.push("menuName");
		hideArr.push("ctId");
		hideArr.push("dtId");
		hideArr.push("mGrade");
		hideArr.push("sGrade");
		hideArr.push("sLevel");
		hideArr.push("seqNum");
	}else{//叶子节点（岗位）
		showArr.push("menuId");
		showArr.push("menuName");
		showArr.push("ctId");
		showArr.push("dtId");
		showArr.push("mGrade");
		showArr.push("sGrade");
		showArr.push("sLevel");
		showArr.push("seqNum");
	}
	FormUtil.disableFormShowCtrl(formId,showArr,hideArr);
	formTemp = Ext.getCmp(formId).getForm();
	if(record.get("pId") == "-1")
		record.set("pName","根节点");
	formTemp.loadRecord(record);
}

//添加节点通信
function comm_add_tree(url,storeId,formId){
	paramJson = getParam(formId);
	var node = getNode(storeId,paramJson.pId);
	var parentNode = node.parentNode;
	var storeTemp = Ext.data.StoreManager.lookup(storeId);
	if(Ext.getCmp(formId).getForm().isValid()){
		Ext.Ajax.request({
			url:url,
			params:paramJson,
			success:function(resp,eOpts){
				if(node.get('leaf')){
					storeTemp.load({node:parentNode});
				}else{
					storeTemp.load({node:node});
				}
			},
			failure:function(resp,eOpts){
			}
		});
		FormUtil.disableForm(formId);
	}else{
		msgAlert("表单非法,无法提交!");
	}
	return;
}
//删除节点通信
function comm_del_tree(url,storeId,formId,menuId){
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
				if(childNum >= 2){
					storeTemp.load({node:parentNode});
				}else{
					storeTemp.load({node:parentNode.parentNode});
				};
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
	record.set("tx_code","10043");
	formTemp.loadRecord(recordNow);
//	formTemp.findField("menuType").setValue(menuType);
	menuId = record.get("menuId");
	nodeNow = getNode(storeId,menuId);
	if(record.get("leaf")){
		Ext.Msg.confirm('提示信息','确认删除吗？',function(btn){
			   if(btn == 'yes'){
				   comm_del_tree("jobManage.html",storeId,"updFormPanelId",menuId);
			   }   
		});
	}else{
		msgAlert("无法删除，请提前删除子节点");
	}
	FormUtil.disableForm(formId);
}
function handleUpdate(formId,record,menuType){
	var disArr = [];
	var hideArr = [];
	var showArr = [];
	disArr.push("menuId");
	formTemp = Ext.getCmp(formId).getForm();
	FormUtil.enableForm(formId, disArr,hideArr,showArr);
	record.set("tx_code","10042");
	formTemp.loadRecord(record);
}
//添加岗位(叶子节点)
function showAddJob(formId,recordNow){
	var hideArr = [];//隐藏组件
	var showArr = [];//显示组件
	var disArr = [];//设置组件不可用
	//新增节点，显示父节点信息功参考
	disArr.push("pId");
	disArr.push("pName");
	
	showArr.push("pId");
	showArr.push("pName");
	hideArr.push("menuId");
//	showArr.push("menuId");
	showArr.push("menuName");
	showArr.push("ctId");
	showArr.push("dtId");
	showArr.push("mGrade");
	showArr.push("sGrade");
	showArr.push("sLevel");
	showArr.push("seqNum");
	formTemp = Ext.getCmp(formId).getForm();
	recordNow.set("tx_code","10041");
	recordNow.set("pId",recordNow.get("menuId"));
	recordNow.set("pName",recordNow.get("menuName"));
	formInit("updFormPanelId",recordNow,"treeStoreId");
	formTemp.findField("menuId").setValue("");
	formTemp.findField("menuName").setValue("");
	FormUtil.enableForm(formId, disArr,hideArr,showArr);
}
//添加序列(存在子节点)
function showAddSeq(formId,recordNow){
	var hideArr = [];//隐藏组件
	var showArr = [];//显示组件
	var disArr = [];//设置组件不可用
	//新增节点，显示父节点信息功参考
	disArr.push("pId");
	disArr.push("pName");
	
	showArr.push("pId");
	showArr.push("pName");
//	showArr.push("menuId");
	
	showArr.push("menuName");
//	showArr.push("seqNum");
	hideArr.push("menuId");
	hideArr.push("ctId");
	hideArr.push("dtId");
	hideArr.push("mGrade");
	hideArr.push("sGrade");
	hideArr.push("sLevel");
	hideArr.push("seqNum");
	formTemp = Ext.getCmp(formId).getForm();
	recordNow.set("tx_code","10041");
	recordNow.set("pId",recordNow.get("menuId"));
	recordNow.set("pName",recordNow.get("menuName"));
	formInit("updFormPanelId",recordNow,"treeStoreId");
	formTemp.findField("menuId").setValue("");
	formTemp.findField("menuName").setValue("");
	FormUtil.enableForm(formId, disArr,hideArr,showArr);
}


		
		
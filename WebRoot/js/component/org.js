var leafTag;
var pId;
var pName;
var menuId;
var menuName;
var menuType;

/*****************下拉框父类*************/
Ext.define("orgDeptCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'机构部门',
//    forceSelection:true,
    emptyText:'选择机构部门',
    name:'orgDeptComboQuery',
//    id:'orgInfoQuery',
    margin:'0 0 4 0 ',
//    store: states,
    queryMode:'local',
    displayField:'menuName',
    valueField:'menuId',
    renderTo:Ext.getBody(),
//    width:350,
    width:280,
//    maxHeight: 300,  
    labelAlign:'right',
//    allowBlank:true,
//    blankText:'请选择机构部门',
    emptyText:'选择机构部门',
    msgTarget:'under',
    autoFitErrors:false,
    layout:'fit',
    editable:false,
    listConfig:{
    	minWidth:250
    }
});

/**
 * 定制机构下拉树treePicker类
 */
Ext.define('orgTreePicker',{
	extend:'Ext.ux.TreePicker',
	// id:'sysTree',
    xtype:'treepicker',
    fieldLabel:'机构部门:',
    labelAlign:'right',
    name:'childSys',
    displayField:'menuName',
    valueField: 'menuId',
    minPickerHeight: 100, //最小高度，不设置的话有时候下拉会出问题
    minWidth:330,
   // width:280,
    allowBlank:false,
    msgTarget:'under',
    autoFitErrors:false,
    editable: true, //启用编辑，主要是为了清空当前的选择项
    enableKeyEvents: true, //激活键盘事件
//    store: tree_store_query,
    listeners:{
      keyup:function(){ //鼠标弹起时清空所有的值
       //Ext.getCmp("sysTree").setValue("");
      }
    }/*,
    root: {
          text: '根节点',
          id: 'root',
          expanded: true
    }*/
});


/*****************下拉框子类：查询*************/
Ext.define("orgDeptComboQuery",{
	extend:'orgDeptCombo',
    tpl: "<div style='height:296px;border:0px solid red;overflow:hidden' id='treeId'></div>",
    fieldLabel:'机构部门',
    emptyText:'选择机构部门',
    allowBlank:true,
    allowText:'请选择机构部门'
});

/*****************下拉框子类：更新*************/
Ext.define("orgDeptComboUpd",{
	extend:'orgDeptCombo',
    tpl: "<div style='height:296px;border:0px solid red' id='treeIdUpd'></div>"
});

//机构部门组件
function orgCmponent(){
	this.treeUpd;
	//查询区机构部门tree panel
	this.getTreeQuery=function(){
		var tree_store = Ext.create('Ext.data.TreeStore',{
			storeId:'treeStoreId',
			model:'orgInfo',
			nodeParam:'menuTreeId',
			proxy:{
				type:'ajax',
//				url: 'orgManage.html?tx_code=10034',
				url: 'orgTreeQuery.html',
				reader:new Ext.data.JsonReader({
					root:'rows'
				})
//						reader:'json'//reader必须为json
			},
			folderSort: true,
			autoLoad:false/*,
			root:{
				name:'根节点',
				id:'-1',
				expanded: true//默认展开根节点
			}*/
		});
		//机构部门下拉树（查询面板）
		var tree = Ext.create('Ext.tree.TreePanel',{
			rootVisible:false,
			border:false,
			height:298,//设置高度，会在展开时出现水平滚动条，然后瞬间滚动条又消失，故不设置高度
			singleExpand:true,
			margin:'-2 0 0 0',
			autoScroll:true,
			store:'treeStoreId',
			displayField:'menuName'
		});
		return tree;
	};
	
	//更新弹窗 tree panel
	this.getTreeUpd=function(){
		//机构部门store,点击上级，查询下级（jg.js使用）（用户更行弹窗）
		var tree_store_upd = Ext.create('Ext.data.TreeStore',{
			storeId:'treeStoreUpdId',
			model:'orgInfo',
			nodeParam:'menuTreeId',
			proxy:{
				type:'ajax',
//				url: 'orgManage.html?tx_code=10034',
				url: 'orgTreeQuery.html',
				reader:new Ext.data.JsonReader({
					root:'rows'
				})
//						reader:'json'//reader必须为json
			},
			folderSort: true,
			autoLoad:false/*,
			root:{
				name:'根节点',
				id:'-1',
				expanded: true//默认展开根节点
			}*/
		});

		//机构部门下拉树（修改面板）
		var treeUpd = Ext.create('Ext.tree.TreePanel',{
			rootVisible:false,
			border:false,
			height:296,
			singleExpand:true,
			margin:'-2 0 0 0',
			autoScroll:false,
			store:'treeStoreUpdId',
			displayField:'menuName'
		});
//		this.treeUpd = treeUpd;
		return treeUpd;
	};
	//查询区 机构部门下拉框（机构部门tree 容器）
	this.getOrgInfoQuery=function(treeQuery){
		var orgInfoQuery = new orgDeptComboQuery();
		var queryOrgComTag = 0;
		var storeTemp = treeQuery.getStore();
		orgInfoQuery.on('expand',function(){  
//			Ext.data.StoreManager.lookup('treeStoreId').reload();
			
//			console.log(storeTemp.getRootNode());
//			console.log(storeTemp.getRootNode().childNodes);
//			console.log(storeTemp.getRootNode().getPath());
//			console.log(storeTemp.getRootNode().getDepth());
//			console.log(storeTemp.getRootNode().hasChildNodes());
//			console.log(storeTemp.getRootNode().isLeaf());
//			console.log(storeTemp.getRootNode().text);
			var childArr = storeTemp.getRootNode().childNodes;
//			console.log(childArr);
//			console.log(childArr.length);
			for(var i = 0;i < childArr.length;i++){
				nodeTemp = childArr[i];
//				console.log(nodeTemp);
			}
			nodeTemp0 = childArr[0];
			if(queryOrgComTag == 0){
				setTimeout(function(){
					storeTemp.reload();
					setTimeout(function(){treeQuery.render('treeId');},10);
					
				},100);//解决偶发性查询机构树无法加载完成的问题20151108  
			}
			queryOrgComTag = 1;
		}); 
		storeTemp.on('beforeLoad',function(){
			Ext.apply(storeTemp.proxy.extraParams,{
				leafTag:leafTag,
				pId:pId,
				pName:pName,
				menuId:menuId,
				menuType:menuType,
				menuName:menuName
			});
		});
		return orgInfoQuery;
	};
	//更新弹窗 机构部门下拉框（机构部门tree 容器）
	this.getOrgInfoUpd=function(treeUpd){
		var orgInfoUpd = new orgDeptComboUpd({
			id:'orgInfoUpd'
		});
		var storeTemp = treeUpd.getStore();
		var updOrgComTag = 0;//机构树面板已渲染标识（1：已渲染 0：为渲染）
		orgInfoUpd.on('expand',function(){ 
			if(updOrgComTag == 0){
				setTimeout(function(){storeTemp.reload();
				treeUpd.render('treeIdUpd');  
				},100);//解决偶发性查询机构树无法加载完成的问题20151108
			}
			updOrgComTag = 1;
		});
		storeTemp.on('beforeLoad',function(){
			Ext.apply(storeTemp.proxy.extraParams,{
				leafTag:leafTag,
				pId:pId,
				pName:pName,
				menuId:menuId,
				menuType:menuType,
				menuName:menuName
			});
		});
		return orgInfoUpd;
	};
	
	this.treePickerQuery = function(){
		var tree_store_query = Ext.create('Ext.data.TreeStore',{
			storeId:'treeStoreId_query',
			model:'orgInfo',
			nodeParam:'menuTreeId',
			proxy:{
				type:'ajax',
//				url: 'orgManage.html?tx_code=10034',
				url: 'orgTreeQuery.html',
				reader:new Ext.data.JsonReader({
					root:'rows'
				})
//						reader:'json'//reader必须为json
			},
			folderSort: true,
			autoLoad:false/*,
			root:{
				name:'根节点',
				id:'-1',
				expanded: true//默认展开根节点
			}*/
		});
		tree_store_query.on('beforeLoad',function(){
			Ext.apply(tree_store_query.proxy.extraParams,{
				leafTag:leafTag,
				pId:pId,
				pName:pName,
				menuId:menuId,
				menuType:menuType,
				menuName:menuName
			});
		});
		tree_store_query.reload();
		var tpQuery = Ext.create('orgTreePicker',{
		   allowBlank:true,
		   id:'tpQueryId',
		   store: tree_store_query,
		   emptyText:'选择机构部门'
		});
		return tpQuery;
	};
	
	
	this.treePickerUpd = function(){
		var tree_store_upd = Ext.create('Ext.data.TreeStore',{
			storeId:'treeStoreId_upd',
			model:'orgInfo',
			nodeParam:'menuTreeId',
			proxy:{
				type:'ajax',
//				url: 'orgManage.html?tx_code=10034',
				url: 'orgTreeQuery.html',
				reader:new Ext.data.JsonReader({
					root:'rows'
				})
//						reader:'json'//reader必须为json
			},
			folderSort: true,
			autoLoad:false/*,
			root:{
				name:'根节点',
				id:'-1',
				expanded: true//默认展开根节点
			}*/
		});
		tree_store_upd.on('beforeLoad',function(){
			Ext.apply(tree_store_upd.proxy.extraParams,{
				leafTag:leafTag,
				pId:pId,
				pName:pName,
				menuId:menuId,
				menuType:menuType,
				menuName:menuName
			});
		});
		tree_store_upd.reload();
		var tpUpd = Ext.create('orgTreePicker',{
		   	allowBlank:false,
		   	id:'tpUpdId',
			editable:false,
			msgTarget:'under',
			store: tree_store_upd,
			emptyText:'选择机构部门',
			blankText:'选择机构部门'
		});
		return tpUpd;
	};
	
	/**
	 * treePicker下拉树select事件
	 */
	this.tpOnSelect = function(tpQuery,checkForm){
		//treePicker下拉树select事件
		tpQuery.on('select',function(){
			var tpValue = tpQuery.getValue();//对应valueField:'menuId'
//			var tpRawValue = tpQuery.getRawValue();//对应displayField:'menuName'
//			var pNode = tpQuery.previousNode();
//			alert(tpValue);
//			alert(tpRawValue);
//			console.log(pNode);
			var tpStore = tpQuery.getStore();
			var node = tpStore.getNodeById(tpValue);//当前选中节点
//			console.log(node);
			var orgArr = new Array();
//			node = getNode("treeStoreId",record.get("menuId"));//初始节点
			getUpOrgAll(node,orgArr);//所有上级机构部门历史记录写入orgArr
			var queryStr = getQueryStr(orgArr);//所有上级机构部门历史记录
			Ext.getCmp("queryStrId").setText(queryStr);//设置机构选择历史记录
//			console.log(queryStr);
			var orgId = getLatestOrgId(node);//获取当前操作节点最接近机构id
//			console.log(orgId);
			checkForm.getForm().findField("orgIdQuery").setValue(orgId);//查询机构
			checkForm.getForm().findField("deptIdQuery").setValue(tpValue);//查询部门
		});
	};
}


/**
 * 机构点击事件执行函数
 * @param orgTreePanel 机构部门下拉框面板
 * @param orgInfoQuery 机构部门下拉框
 * @param checkForm 查询表单
 */
function orgTreeClick(orgTreePanel,orgInfoQuery,checkForm){
	orgTreePanel.on('itemclick',function(view,record,item,index,e,obj){
		orgInfoQuery.setValue(record.get("menuId"));
		orgInfoQuery.setRawValue(record.get("menuName"));
//		alert(getNode("treeStoreId",record.get("menuId")));
//		orgId = getLatestOrgId(getNode("treeStoreId",record.get("menuId")),"treeStoreId");//获取当前操作节点最接近机构id
		var orgId = getLatestOrgId(getNode("treeStoreId",record.get("menuId")));//获取当前操作节点最接近机构id
		orgInfoQuery.collapse();
		checkForm.getForm().findField("orgIdQuery").setValue(orgId);
		checkForm.getForm().findField("deptIdQuery").setValue(record.get("menuId"));
		
		var orgArr = new Array();
		node = getNode("treeStoreId",record.get("menuId"));//初始节点
		getUpOrgAll(node,orgArr);
		var queryStr = getQueryStr(orgArr);
		Ext.getCmp("queryStrId").setText(queryStr);
	});
}











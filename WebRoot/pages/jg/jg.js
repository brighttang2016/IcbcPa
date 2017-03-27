// JavaScript Document
var height;
var updFormPanel;
var tableStore;
var win;
var checkForm;
var role_id = 0;
var pageSize = 10;
var leafTag;
var pId;
var pName;
var menuId;
var menuName;
var menuType;
var recordNow;
var orgId;
Ext.Loader.setConfig({
	enable:true,
	paths:{
		'App.js':'pages/js'
	}
});

Ext.application({
	name:'roleInfo',
	launch:function(){
		Ext.Loader.require("js.comm");
		var states = Ext.create('Ext.data.Store', {
		    fields: ['typeId', 'typeName'],
		    data : [
		        {"typeId":"100", "typeName":"添加机构"},
		        {"typeId":"200", "typeName":"添加部门"}
		    ]
		});
		var addTypeCombo = Ext.create('Ext.form.ComboBox', {
		    fieldLabel:'添加类型',
		    forceSelection:true,
		    emptyText:'选择添加类型',
		    name:'addType',
		    id:'addType',
		    margin:'0 0 4 0 ',
		    store: states,
		    queryMode:'local',
		    displayField:'typeName',
		    valueField:'typeId',
		    renderTo:Ext.getBody(),
		    width:280,
		    labelAlign:'right',
		    allowBlank:false,
		    blankText:'请选择添加类型',
		    msgTarget:'side',
		    editable:false,
		    autoFitErrors:false
		});
		updFormPanel = new Ext.form.Panel({
			frame:true,
			baseCls:'my-panel-no-border',
//			height:'100%',
			buttonAlign:'center',
			id:'updFormPanelId',
			items:[tran_code,new inputTextField({
				allowBlank:true,
				//xtype:'hidden',
				name:'menuType',
				id:'menuType',
				fieldLabel:'节点类型'
			}),new inputTextField({
				allowBlank:true,
				//xtype:'hidden',
				name:'orgId',
				id:'orgId',
				fieldLabel:'所属机构号'
			}),new inputTextField({
				allowBlank:true,
				name:'pId',
				id:'pId',
				fieldLabel:'上级编号'
			}),new inputTextField({
				allowBlank:true,
				name:'pName',
				id:'pName',
				fieldLabel:'上级名称'
			}),addTypeCombo,
			new inputTextField({
//				cls:'hide',
				name:'menuId',
				id:'menuId',
				fieldLabel:'编号'
			}),new inputTextField({
				name:'menuName',
				id:'menuName',
				fieldLabel:'名称'
			})],
			buttons:[{
				text:'保存',
				id:'btnSave',
				handler:function(){
//					comm_update("orgManage.html","treeStoreId","updFormPanelId");
					handleSave("orgManage.html","treeStoreId","updFormPanelId");
				}
			}/*,{
				text:'取消',
				buttonAlign:'center',
				id:'btnCancel',
				handler:handlerCancle
			}*/]
		});
		
		
		/*菜单menu*/
		Ext.create('Ext.menu.Menu',{
			id:'rightMenuId',
			float:true,
			
			items:[{
				text:'添加',
//				cls:'menuVCenter',
				icon:'images/ext/add.gif',
				handler:function(){
					showAdd("updFormPanelId",recordNow);
				}
			},{	
				text:'编辑',
				icon:'images/ext/edit.png',
				handler:function(){
					setTimeout(function(){
						tempCombo = Ext.getCmp("addType");
						tempCombo.setValue("200");
					},100);
					handleUpdate("updFormPanelId",recordNow,menuType);
				}
			},{
				text:'删除',
				icon:'images/ext/delete.gif',
				handler:function(){
					addTypeCombo.setValue("100");
					handleDel("updFormPanelId",recordNow,"treeStoreId");
				}
			},{
				text:'刷新',
//				icon:'images/ext/delete.gif',
				iconCls:'Arrowrefresh',
				handler:function(){
					addTypeCombo.setValue("100");
					nodeRefresh(recordNow,"treeStoreId");
				}
			}]
		});
		
		//获得TreePanel实例
//		var tree = Ext.create('Ext.tree.Panel',{
		var tree = Ext.create('Ext.tree.TreePanel',{
//			title:'TreeGrid',
			rootVisible:false,
			border:false,
//			baseCls:'my-panel-no-border',
			singleExpand:true,
			margin:'-2 0 0 0',
			height:'100%',
//			autoScroll:false,
			store:tree_store,
			displayField:'menuName'
		});
		var tableViewport = Ext.create('Ext.container.Viewport',{
			layout:'border',
			renderTo:Ext.getBody(),
//			resizable:true,
			defaults:{
//				frame:true,//viewport的frame配置项，必须在defaults中配置才会生效(就是这里引起中间部分出现双层边框)
//				border:true
			},
			items:[
			{
				region:"west",
				layout:'fit',
				collapsible:true,
//				margins:'0 2 0 0',
				title:'机构部门',
				width:350,
				height:"100%",
				split: true,
				autoScroll:true,
				items:[tree]
			},{
				id:'center',
				title:'机构信息',
				frame:true,
				layout:'fit',
				region:"center",
				items:[updFormPanel]
			}]
		});
		FormUtil.disableForm("updFormPanelId");
	
		tree_store.on('beforeLoad',function(){
			//alert(menuName);
			Ext.apply(tree_store.proxy.extraParams,{
				leafTag:leafTag,
				pId:pId,
				pName:pName,
				menuId:menuId,
				menuType:menuType,
				menuName:menuName
			});
		});
		tree.on('itemcontextmenu',function(menutree,record,items,index,e){
			FormUtil.disableForm("updFormPanelId");
			leafTag = record.get("leaf");
			pId = record.get("pId");//当前添加子节点的节点的父节点
			pName = record.get("pName");
			menuId = record.get("menuId");
			menuName = record.get("menuName");
			menuType = record.get("menuType");
			recordNow = record;
			//alert("itemcontextmenu menuName:"+menuName);
			e.preventDefault();
			e.stopEvent();
			Ext.getCmp("rightMenuId").showAt(e.getXY());
			formInit("updFormPanelId",record,"treeStoreId");
			//msgAlert("得到最近机构："+orgId);
		});
		tree.on('itemselectchange',function(record,selected ,eOpts ){
			FormUtil.disableForm("updFormPanelId");
		});
		tree.on('itemclick',function(view,record,item,index,e,obj){
			formInit("updFormPanelId",record,"treeStoreId");
		});
	}	
});


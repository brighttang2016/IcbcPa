// JavaScript Document
var height;
var updFormPanel;
var tableStore;
var win;
var checkForm;
var role_id = 0;
var pageSize = 10;

Ext.Loader.setConfig({
	enable:true,
	paths:{
		'App.js':'pages/js'
		/*,
		'App.public':'js/public'*/
	}
});
//Ext.require([
//'Ext.data.*',
//'Ext.util.*',
//'Ext.view.View']);


//Ext.Loader.require("App.js.js_before");
//Ext.Loader.require("App.js.js_after");
//Ext.Loader.require("App.js.test");
Ext.application({
	name:'roleInfo',
	launch:function(){
//		Ext.Loader.require("App.js.js_before");
		Ext.Loader.require("App.js.js_fun");
		Ext.Loader.require("js.comm");
		checkForm = Ext.create('Ext.form.Panel',{
			buttonAlign:'center',
			id:'checkForm',
			title:'查询区',
			frame:true,
			border:false,
			defaults:{
				labelWidth:50,
				labelAlign:'right',
				msgTarget:'side'
			},
			items:[{
				layout:'column',
				baseCls : 'my-panel-no-border',
				frame:true,
				border:false,
				items:[new queryTextField({
					columnWidth:.5,
					layout:{
						type:'hbox',
						pack:'center'
					},
					items:[new queryTextInput({
						fieldLabel:'角色名',
						name:'role_name'
					})/*{
						xtype:'textfield',
						width:230,
						fieldLabel:'角色名',
						labelAlign:'right',
						name:'role_name'
					}*/]
				}),new queryTextField({
					columnWidth:.5,
					items:[new queryTextInput({
						fieldLabel:'角色描述',
						name:'role_desc'
					})/*{
						xtype:'textfield',
						width:230,
						fieldLabel:'角色描述',
						labelAlign:'right',
						name:'role_desc'
					}*/]
				})]
			}],
			buttons:[{
				text:'查询',
				handler:function(){
					comm_query('myStoreId','checkForm','10024');
				}
			},{
				text:'清空',
				handler:function(){
//					checkForm.getForm().reset();
					Ext.getCmp('checkForm').getForm().reset();
				}
			}]
		});
		
		
		Ext.regModel('tableModel',{
			fields:['role_id','role_name','role_desc']
		});
		tableStore = Ext.create('Ext.data.Store',{
			storeId:'myStoreId',
			autoLoad:{start:0,limit:pageSize},
			model:'tableModel',
			width:'100%',
			height:'100%',
			pageSize:pageSize,
			proxy:{
				type:'ajax',
				url:'roleRouter.html',
				reader:new Ext.data.JsonReader({
					root:'rows',
					totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
				})
			}
		});
		tableStore.on('beforeLoad',function(){
			Ext.apply(tableStore.proxy.extraParams,{
				tranData:Ext.encode({"tx_code":"10024"})
			});
		});
		
		/*tableStore.on('beforeLoad',function(){
			checkForm = Ext.getCmp('checkForm').getForm();
			Ext.apply(tableStore.proxy.extraParams,{
				role_name:checkForm.findField('role_name').getValue(),
				role_desc:checkForm.findField('role_desc').getValue(),
				tx_code:'10024'//角色查询交易
				role_name:checkForm.findField('role_name').getValue(),
				role_desc:checkForm.findField('role_desc').getValue(),
				txCode:'10024'
			});
		});*/
		
		
		
		//列
		var myColumns = [ 
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
//			{header:'角色id',dataIndex:'role_id',width:100,sortable:true,align:'center'},
			{header:'角色名',dataIndex:'role_name',width:150,sortable:true,align:'center'},
			{header:'描述信息',dataIndex:'role_desc',width:400,sortable:false,align:'center'},
			{header:'修改',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/edit.png',
					handler:function(grid,rowIndex,colIndex){
						var rec = grid.getStore().getAt(rowIndex);
						handleUpdate(rec,"treeStoreId",updFormPanel);
					}
				}]
			},{header:'删除',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/delete.gif',
					handler:function(var1,var2,var3){//var1:当前Ext.grid.Panel对象，var2:当前操作行，var3:当前操作列
						var rec = var1.getStore().getAt(var2);
						handleDel(rec);
					}
				}]
			}
		];
		
		
//		增加、修改处理开始----------------------------
		//增加、修改面板
		Ext.regModel('menuInfo',{
			fields:['id','menu_text','count','parent_id']
		});
		var treeStore = Ext.create('Ext.data.TreeStore',{
			storeId:'treeStoreId',
			model:'menuInfo',
			nodeParam:'treeId',
			proxy:{
				type:'ajax',
				url:'getMenuItemAll.html?role_id='+role_id,
//				url:'Menu_getMenuItemAll.action',
				reader:'json'//reader必须为json，后台返回json对象数组
			},
			folderSort: true,
			root:{
				name:'根节点',
				id:'-1',
				expanded: true//默认展开根节点
			}
		});
		
		var treePanel = Ext.create('Ext.tree.Panel',{
			id:'treePanelId',
			title:'权限选择',
//			width:300,
			rootVisible:false,
//			singleExpand:true,
			height:261,
			autoScroll:true,
			store:treeStore,
			border:false,
			margin:'5 0 0 0',
			displayField:'menu_text',
		    valueField: 'menu_id',
//			useArrows: true,//导航条前使用箭头
//			layout:'fit',
			/*columns:[{//分列树
				xtype:'treecolumn',
				text:'请为角色分配权限',
//				dataIndex:'name',
				dataIndex:'menu_text',
				width:'100%',
				sortable:true
			}]*/
		});
		
//		updFormPanel = new Ext.form.Panel({
		updFormPanel = Ext.create('Ext.form.Panel',{
			id:'updFormPanelId',
			items:[tran_code,{
				xtype:'hidden',
				name:'role_auth'
			},{
				xtype:'hidden',
				name:'role_id',
				fieldLabel:'角色id',
				readOnly:false
			},new inputTextField({
				name:'role_name',
				margin:'5 0 5 0 ',
				fieldLabel:'角色名称'
			}),new inputTextField({
				name:'role_desc',
				fieldLabel:'角色描述'
			}),treePanel],
			buttons:[{
				text:'保存',
				id:'btnSave',
				handler:function(){
					handleSave("roleRouter.html","myStoreId","updFormPanelId");
				}
//				handler:handleSave
			},{
				text:'取消',
				id:'btnCancel',
				handler:handlerCancle
			}]
		});
		
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			title:'系统角色信息',
			scroll:true,
			border:true,
//			layout:'fit',
			width:'100%',
			height:'100%',
			viewConfig:{
//				forceFit:true,
//				stripeRows:true
			},
			store:tableStore,
			features:[{
				ftype:'summary'
			},{
				ftype:'summary'
			}],
			columns:myColumns,
			selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),
			tbar:[
					{
						text:'添加',
						cls:'x-btn-text-icon',
						icon:'images/ext/add.gif',
						handler:function(){
							showAdd("treeStoreId");
						}
					}
				],
			bbar:[{//分页（dockedItems显示在表格顶端）
				id:'pagingBar',
				xtype:'pagingtoolbar',
				store:tableStore,
				displayInfo:true,
				border:false,
				items:['每页显示',sizeCombo,'条']
			}]
		});
		
		var tableViewport = Ext.create('Ext.container.Viewport',{
			layout:'border',
			renderTo:Ext.getBody(),
//			resizable:true,
			defaults:{
//				frame:true,//viewport的frame配置项，必须在defaults中配置才会生效(就是这里引起中间部分出现双层边框)
//				border:true
			},
			items:[{
				region:'north',
				collapsible:false,
				split:true,
//				xtype:'panel',
				layout:'fit',
//				height:100,
				border:false,
				items:[checkForm],
				margins:'0 0 4 0'
			},{
				region:'center',
				collapsible:false,
				layout:'fit',
				border:false,
				items:[myGrid],
				margins:'0 0 4 0'
			}]
		});
		
		
		treeStore.on('beforeLoad',function(){
			updForm = Ext.getCmp('updFormPanelId').getForm();
			role_id = updForm.findField('role_id').getValue();
			var proxy = treeStore.getProxy();
			proxy.url='getMenuItemAll.html?role_id='+role_id,
			treeStore.setProxy(proxy);
			Ext.apply(tableStore.proxy.extraParams,{
				role_id:role_id
			});
		});
		
		
		sizeCombo.on('select',function(combo){
			var pageSize = parseInt(combo.getValue());
			var pagingToolBar = Ext.getCmp('pagingBar');
			pagingToolBar.pageSize = pageSize;
			tableStore.pageSize = pageSize;
			tableStore.loadPage(1);
		});
		
		Ext.getCmp('treePanelId').on('checkchange',function(record,flag){
			allNodes = [];
			record.eachChild(function(record){
				record.set('checked',flag);
				setChildNode(record,flag);
			});
			var root = treePanel.getRootNode();
			findChild(root);
			setParentNode(allNodes,record);
		});
		
		/*Ext.getCmp("btnSave").on('click',function(){
			handleSave();
		});*/
		
	}	
});


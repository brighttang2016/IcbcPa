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
		updFormPanel = new Ext.form.Panel({
			frame:true,
			baseCls:'my-panel-no-border',
//			height:'100%',
			buttonAlign:'center',
			id:'updFormPanelId',
			items:[tran_code,new inputTextField({
				allowBlank:true,
				//xtype:'hidden',
				name:'pId',
				id:'pId',
				fieldLabel:'父级id'
			}),new inputTextField({
				allowBlank:true,
				//xtype:'hidden',
				name:'pName',
				id:'pName',
				fieldLabel:'父级名称'
			}),new inputTextField({
				allowBlank:true,
				//xtype:'hidden',
				name:'menuId',
				id:'menuId',
				fieldLabel:'id'
			}),new inputTextField({
				allowBlank:true,
				//xtype:'hidden',
				name:'menuName',
				id:'menuName',
				fieldLabel:'名称'
			}),new ctCombo({
				allowBlank:true,
				id:'ctId',
				name:'ctId'
			}),new dtCombo({
				allowBlan:true,
				id:'dtId',
				name:'dtId'
			}),new wgCombo({
				allowBlank:true,
				name:'mGrade',
				id:'mGrade',
				fieldLabel:'最高工资等级'
			}),new wgCombo({
				allowBlank:true,
				name:'sGrade',
				id:'sGrade',
				fieldLabel:'起始工资等级'
			}),/*new wlCombo({
				allowBlank:true,
				name:'sLevel',
				id:'sLevel',
				fieldLabel:'起始工资档次'
			}),*/
			/**
			 * 由于工资档次id：wl_id是通过序列生成，在每次初始化时，都会改变，
			 * 故，若采用下拉框形式设置，当工资薪点对照表初始化后,wl_id全部改变，需要重新设置所有岗位的其实工资档次，
			 * 此时直接录入起始工资档次为较优方案20150728
			 */
			,new textFieldInt({
				allowBlank:true,
				name:'sLevel',
				id:'sLevel',
				fieldLabel:'起始工资档次'
			}),
			/*,new textFieldInt({
				allowBlank:true,
				name:'mGrade',
				id:'mGrade',
				fieldLabel:'最高工资等级'
			}),new textFieldInt({
				allowBlank:true,
				name:'sGrade',
				id:'sGrade',
				fieldLabel:'起始工资等级'
			}),new textFieldInt({
				allowBlank:true,
				name:'sLevel',
				id:'sLevel',
				fieldLabel:'起始工资档次'
			}),*/new textFieldInt({
				allowBlank:true,
				name:'seqNum',
				id:'seqNum',
				fieldLabel:'顺序号'
			})],
			buttons:[{
				text:'保存',
				id:'btnSave',
				handler:function(){
//					handleSave("jobManage.html","jobTreeStoreId","updFormPanelId");
					comm_add_tree('jobManage.html','jobTreeStoreId','updFormPanelId');
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
				text:'添加类别或序列',
				icon:'images/ext/add.gif',
				handler:function(){
					showAddSeq("updFormPanelId",recordNow);
				}
			},{
				text:'添加岗位',
				icon:'images/ext/add1.gif',
				handler:function(){
					showAddJob("updFormPanelId",recordNow);
				}
			},{	
				text:'编辑',
				icon:'images/ext/edit.png',
				handler:function(){
					/*setTimeout(function(){
						tempCombo = Ext.getCmp("addType");
						tempCombo.setValue("200");
					},100);*/
					handleUpdate("updFormPanelId",recordNow,menuType);
				}
			},{
				text:'删除',
				icon:'images/ext/delete.gif',
				handler:function(){
//					addTypeCombo.setValue("100");
					handleDel("updFormPanelId",recordNow,"jobTreeStoreId");
				}
			}]
		});
		
		//获得TreePanel实例
		var jobTreeStore = Ext.data.StoreManager.lookup("jobTreeStoreId");
		var tree = Ext.create('Ext.tree.TreePanel',{
//			title:'TreeGrid',
			rootVisible:false,
			border:false,
//			baseCls:'my-panel-no-border',
			singleExpand:true,
			margin:'-2 0 2 0',
			height:'100%',
//			autoScroll:false,
			store:jobTreeStore,
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
				title:'岗位序列职务层级',
				width:250,
				height:"100%",
				split: true,
				autoScroll:true,
				items:[tree]
			},{
				id:'center',
				title:'详细信息',
				frame:true,
				layout:'fit',
				region:"center",
				items:[updFormPanel]
			}]
		});
		FormUtil.disableForm("updFormPanelId");
	
		//beforeexpand事件在load事件之前发生（expand事件在load事件时候反生，故不能监听expand事件赋值），在load树之前，给树需要传递的参数赋值20150723。
		jobTreeStore.on("beforeexpand",function(node,eOpts){
			menuId = node.get("menuId");
			menuName = node.get("menuName");
		});
		jobTreeStore.on('beforeLoad',function(){
			Ext.apply(jobTreeStore.proxy.extraParams,{
//				leafTag:leafTag,
//				pId:pId,
//				pName:pName,
				menuId:menuId,
//				menuType:menuType,
				menuName:menuName
			});
		});
		//itemcontextmenu：Fires when an item is right clicked.
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
//			FormUtil.disableForm("updFormPanelId");
		});
		tree.on('itemclick',function(view,record,item,index,e,obj){
			formInit("updFormPanelId",record,"treeStoreId");
		});
	}	
});


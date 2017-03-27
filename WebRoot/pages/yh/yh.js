// JavaScript Document
var height;
var updFormPanel;
//var tableStore;
var win;
var checkForm;
var winUpd;
var winAdd;
var checkForm;
var role_id = 0;

var leafTag;
var pId;
var pName;
var menuId;
var menuName;
var menuType;
var recordNow;
var orgId;
var udpFormPanelAdd;
//var orgInfoUpd;//机构部门下拉框（用户更新）
Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'js/ext');
Ext.require([
 	'Ext.form.Panel',
	'Ext.ux.form.MultiSelect',
	'Ext.ux.form.ItemSelector',
	'Ext.tip.QuickTipManager',
	'Ext.ux.ajax.JsonSimlet',
	'Ext.ux.ajax.SimManager',
	'Ext.ux.ajax.DataSimlet',
	'Ext.ux.ajax.SimXhr',
	'Ext.ux.ajax.Simlet'
]);
Ext.Loader.setConfig({
	enable:true,
	paths:{
		'App.js':'pages/js',
		'App.ux':'lib'
	}
});
Ext.Loader.setPath('com.nt.cmp','js/component');

Ext.application({
	name:'roleInfo',
	launch:function(){
		dwnIframeInit();
		Ext.Loader.require("js.comm");
		Ext.Loader.setPath('com.nantian.common','js/public');
		
		MessagePush.onPageLoad(top.varpool.userId);//注册监听
		
		var orgCmp = new orgCmponent();//机构组件对象
		var treeUpd = orgCmp.getTreeUpd();//更新树panel
		var orgInfoUpd = orgCmp.getOrgInfoUpd(treeUpd); //更新panel
		var treeQuery = orgCmp.getTreeQuery();//查询树panel
		var orgInfoQuery = orgCmp.getOrgInfoQuery(treeQuery);//查询树panel
		var tpQuery = orgCmp.treePickerQuery();//查询区panel机构下拉树
		
		var tpUpd = orgCmp.treePickerUpd();//用户编辑弹窗机构下拉树
		/*********查询开始**********/
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
				items:[{
					xtype:'hidden',
					name:'orgId',
					id:'orgIdQuery'
				},{
					xtype:'hidden',
					name:'deptId',
					id:'deptIdQuery'
				},/*new queryTextField({
					columnWidth:.5,
					items:[orgInfoQuery]
				})*/new queryTextField({
					columnWidth:.5,
					items:[tpQuery]
				}),new queryTextField({
					columnWidth:.5,
					items:[new queryTextInput({
						fieldLabel:'用户号',
						name:'userIdQuery'
					})]
				})]
			},{
				layout:'column',
				frame:true,
				baseCls:'my-panel-no-border',
				items:[{
					frame:true,
//					border:false,
					baseCls:'my-panel-no-border',
					columnWidth:.5,
					layout:{
						type:'hbox',
						pack:'center'
					},
					items:[{
						xtype:'label',
						text:'【机构部门:】',
						id:'queryStrId'
					}]
				}]
			}],
			buttons:[{
				text:'查询',
				handler:function(){
					comm_query('gridStoreId','checkForm','10014');
				}
			},{
				text:'清空',
				handler:function(){
//					checkForm.getForm().reset();
					Ext.getCmp('checkForm').getForm().reset();
					Ext.getCmp('queryStrId').setText('【机构部门:】');
				}
			}]
		});
		/*********查询结束**********/
		
		/*********显示开始**********/
		//列
		var myColumns = [ 
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
			{header:'员工id',dataIndex:'userId',width:100,sortable:true,align:'center'},
			{header:'姓名',dataIndex:'name',width:100,sortable:false,align:'center'},
			{header:'机构名称',dataIndex:'orgName',width:130,sortable:false,align:'center'},
			{header:'部门名称',dataIndex:'depName',width:220,sortable:false,align:'center'},
			{header:'绩效考核岗位大类',dataIndex:'jbjxPname',width:150,sortable:false,align:'center'},
			{header:'绩效考核岗位小类',dataIndex:'jbjxName',width:150,sortable:false,align:'center'},		
			{header:'修改',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/edit.png',
					handler:function(grid,rowIndex,colIndex){
						comboJbjxp.getStore().reload();
						var rec = grid.getStore().getAt(rowIndex);
//						console.log(rec);
						handleUpdate(rec,"gridStoreId","udpFormPanelAdd",comboJbjx);
					}
				}]
			},{header:'删除',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/delete.gif',
					handler:function(var1,var2,var3){//var1:当前Ext.grid.Panel对象，var2:当前操作行，var3:当前操作列
						var rec = var1.getStore().getAt(var2);
						handleDel(rec,"userManage.html","gridStoreId");
					}
				}]
			}
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			title:'用户信息',
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
			/*selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),*/
			tbar:[
					{
						text:'添加',
						cls:'x-btn-text-icon',
						icon:'images/ext/add.gif',
						handler:function(){
							showAdd("udpFormPanelAdd");
						}
					},{
						text:'批量导入',
						//overCls:'jxCalcClass',
						iconCls:'Pagewhiteexcel',
						handler:function(){
							fileUpload("10015","用户批量导入");
						}
					},{
						text:'下载用户导入模版',
						iconCls:'Packagedown',
						handler:function(){
							Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=10015";
//							downloadIframe.src = "downLoad.html?tx_code=20000001";
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
		/*********显示结束**********/
		
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
		
		/************更新开始************/
	/*	var valueArray = new Array();
		valueArray.push("01");*/
		var selFormAdd = new selFormPanelPrt({
			items:[{
	        	columnWidth:.99,
	        	baseCls:'my-panel-no-border',
	        	layout: {   
	        		type: 'hbox',    
	        		align: 'middle ',    
	        		pack: 'center'
	        	},
	        	items:[{
	        		 xtype: 'itemselector',
	 	            name: 'roleSelect',
	 	            id: 'selFormAddId',
	 	            labelAlign:'right',
//	 	            fieldLabel: '角色',
	 	            labelWidth:140,
	 	            width:450,
	 	            height:170,
	 	            imagePath: 'images/ext',
	 	            store: roleStore,
//	 	            displayField: 'text',
//	 	            valueField: 'value',
	 	            displayField:'role_name',
	 	            valueField:'role_id',
	 	          //  value: valueArray,
	 	            allowBlank: false,
	 	            blankText:'请选择角色',
	 	            autoFitErrors:false,
	 	            msgTarget: 'side',
	 	            fromTitle: '可选角色',
	 	            toTitle: '具备角色'
		        }]
	        }]
		});
		
		try{
			udpFormPanelAdd.removeAll();
		}catch(e){}
		
		var jobInfoUpd = new jobComboUpd({
			id:'jbName',
			name:'jbName'
		});
		//岗位类别对象
		/*var jbCatCombo =Ext.create('jbCatCombo',{
			name:'jbCat'
		});*/
	/*	var jbjxpCombo =Ext.create('jbjxpCombo',{
			name:'jbjx_pid'
		});
		*/
		
		var updJobComTag = 0;//岗位树面板已渲染标识（1：已渲染 0：为渲染）
		var menuId = 0;
		var menuName = 0;
		//职务层级store
//		var jobTreeStore = Ext.data.StoreManager.lookup("jobTreeStoreId");
		
		
		var jbjxCmp = new jbjxComponent();
		var comboJbjxp = jbjxCmp.getJbjxpCombo();
		var comboJbjx = jbjxCmp.getJbjxCombo();
//		console.log(comboJbjxp);
//		console.log(comboJbjx);
		
		
		//岗位层级true
	/*	var jobTreePanel = Ext.create('Ext.tree.TreePanel',{
//			title:'TreeGrid',
			rootVisible:false,
			border:false,
			height:200,
//			baseCls:'my-panel-no-border',
			singleExpand:true,
			margin:'-2 0 0 0',
//			height:'100%',
//			layout:'fit',
			autoScroll:false,
			store:jobTreeStore,
			displayField:'menuName'
		});*/
		
		//alert(treeUpd);
		
		/*******************岗位下拉结束*/
		udpFormPanelAdd = new updFormPanelPrt({
			id:'udpFormPanelAdd',
			items:[{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[new queryTextField({
					columnWidth:.5,
					frame:true,
					items:[tran_code]
				})]
			},{
				xtype:'hidden',
				name:'orgId',
				id:'orgIdUpd',
				fieldLabel:'所属机构'
			},{
				xtype:'hidden',
				name:'deptId',
				id:'deptIdUpd',
				fieldLabel:'所属部门'
			},{
				xtype:'hidden',
				name:'jbId',
				id:'jbIdUpd',
				fieldLabel:'所属岗位'
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
//				frame:true,
				items:[new queryTextField({
					columnWidth:.5,
					layout: {   
						type: 'hbox',       
						pack: 'left'
					},
					items:[new inputTextField({
						name:'userId',
						fieldLabel:'用户号',
						readOnly:false,
						regex:/^[0-9]{10}$/,
						regexText:'人力资源编码限定10位数字,若不足10位，请在最前端加0补全'
					})]
				}),new queryTextField({
					columnWidth:.5,
					items:[new inputTextField({
						name:'name',
						fieldLabel:'姓名'
					})]
				})]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[new queryTextField({
					columnWidth:.5,
//					items:[orgInfoUpd]
					items:[tpUpd]
				}),new queryTextField({
					columnWidth:.5,
					items:[comboJbjxp]
				})]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[new queryTextField({
					columnWidth:.5,
					layout: {   
						type: 'hbox',       
						pack: 'left'
					},
					items:[comboJbjx]
				})]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[selFormAdd]
			}],
			buttons:[{
				text:'保存',
				handler:function(){
//					handleSave("userManage.html","gridStoreId","udpFormPanelAdd");
					//comm_update("userManage.html","gridStoreId","udpFormPanelAdd");
					comm_dir("userManage.html","gridStoreId","udpFormPanelAdd");
				}
			},{
				text:'取消',
				handler:handlerCancle
			}]
		});
		/************更新结束************/
		
		/************事件区域开始************/
		sizeCombo.on('select',function(combo){
			//alert("select");
			var pageSize = parseInt(combo.getValue());
			var pagingToolBar = Ext.getCmp('pagingBar');
			pagingToolBar.pageSize = pageSize;
			tableStore.pageSize = pageSize;
			tableStore.loadPage(1);
		});
		
		//机构点击事件执行函数
		orgTreeClick(treeQuery,orgInfoQuery,checkForm);
		
		treeUpd.on('itemclick',function(view,record,item,index,e,obj){
			orgInfoUpd.setValue(record.get("menuId"));
			orgInfoUpd.setRawValue(record.get("menuName"));
			//alert(getNode("treeStoreId",record.get("menuId")));
//			orgId = getLatestOrgId(getNode("treeStoreId",record.get("menuId")),"treeStoreId");//获取当前操作节点最接近机构id
			orgId = getLatestOrgId(getNode("treeStoreUpdId",record.get("menuId")),"treeStoreUpdId");//获取当前操作节点最接近机构id
			orgInfoUpd.collapse();
			udpFormPanelAdd.getForm().findField("orgIdUpd").setValue(orgId);
			udpFormPanelAdd.getForm().findField("deptIdUpd").setValue(record.get("menuId"));
		});
		
		//编辑弹窗机构下拉树select事件
		tpUpd.on('select',function(){
			var tpValue = tpUpd.getValue();
			var tpStore = tpUpd.getStore();
			var node = tpStore.getNodeById(tpValue);
			var orgId = getLatestOrgId(node);//获取当前操作节点最接近机构id
//			console.log(tpValue);
//			console.log(orgId);
			udpFormPanelAdd.getForm().findField("orgIdUpd").setValue(orgId);
			udpFormPanelAdd.getForm().findField("deptIdUpd").setValue(tpValue);
		});
		
		//alert(new orgCmp());
		/*jobTreeStore.on('beforeexpand',function(node,eOpts){
			menuId = node.get("menuId");
			menuName = node.get("menuName");
		});
		jobTreeStore.on("beforeLoad",function(){
			Ext.apply(jobTreeStore.proxy.extraParams,{
				menuId:menuId,
				menuName:menuName
			});
		});*/
		//岗位层级tree面板
		/*jobTreePanel.on('itemclick',function(view,record,item,index,e,obj){
			if(record.get("leaf")){//选择的是岗位层级叶子
//				jobInfoUpd.setValue(record.get("menuId"));
				jobInfoUpd.setRawValue(record.get("menuName"));
				jobInfoUpd.collapse();
				udpFormPanelAdd.getForm().findField("jbId").setValue(record.get("menuId"));
			}else{
				msgAlert("请选择最终的岗位层级!");
			}
		});*/
		
		//岗位类别展开
		/*jbCatCombo.on('change',function(){
			var jbCatId = jbCatCombo.getValue();
			jobTreeStore.on('beforeLoad',function(){
				Ext.apply(jobTreeStore.proxy.extraParams,{
					pId:jbCatId
				});
			});
			//jobTreeStore.load();
			//alert(jbCatId);
		});*/
		
		/*
		jobInfoUpd.on('expand',function(){  
			jobTreeStore.load();
			if(jbCatCombo.getValue() == null){
				msgAlert("请选择岗位类别");
			}else{
				if(updJobComTag == 0){
					jobTreePanel.render('jobTreeDiv');  
				}
				updJobComTag = 1;
			}
	    }); 
		*/
		comboJbjx.on('expand',function(){
			if(comboJbjxp.getValue() == null){
				msgAlert("请选择绩效考核所属岗位类别");
			}else{
				comboJbjx.getStore().reload();
			}
		});
		
		var comboJbjxStore = comboJbjx.getStore();
		comboJbjxp.on('change',function(){
			comboJbjx.clearValue();
		});
		comboJbjxStore.on('beforeload',function(){
			Ext.apply(comboJbjxStore.proxy.extraParams,{
				jbjx_pid:comboJbjxp.getValue()
			});
		});
		
		
		orgCmp.tpOnSelect(tpQuery,checkForm);//treePicker下拉树select事件
		/************事件区域结束************/
//		var tableStore = tableStore;
		
		setTimeout(function(){
//			roleStore.reload();
			tableStore.reload();
		},100);
		setTimeout(function(){
			roleStore.reload();
		},500);
	}	
});


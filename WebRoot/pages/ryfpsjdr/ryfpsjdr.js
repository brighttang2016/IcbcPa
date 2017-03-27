// JavaScript Document

dwr.engine.setActiveReverseAjax(true);
dwr.engine.setNotifyServerOnPageUnload(true);

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
		'App.js':'pages/js'
	}
});


Ext.application({
	name:'roleInfo',
	launch:function(){
		Ext.Loader.require("js.comm");
		MessagePush.onPageLoad(userId);//注册监听
		/*********查询开始**********/
		var orgCmp =  new orgCmponent();
		var orgTreePanel = orgCmp.getTreeQuery();
		var orgInfoQuery = orgCmp.getOrgInfoQuery(orgTreePanel);
		
		//考核周期
		var zqCmp = new zqComponent();
		var zqCombo = zqCmp.getZqCombo();
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
				},new queryTextField({
					columnWidth:.5,
					items:[orgInfoQuery]
				}),new queryTextField({
					columnWidth:.5,
					items:[zqCombo]
				})]
			},{
				layout:'column',
				frame:true,
				baseCls:'my-panel-no-border',
				items:[{
					frame:true,
					border:true,
					baseCls:'my-panel-no-border',
					columnWidth:.5,
					layout:{
						type:'hbox',
						pack:'center'
					},
					items:[{
						xtype:'label',
						text:'【机构部门:】',
						id:'queryStrId',
					}]
				}]
			}],
			buttons:[{
				text:'查询',
				handler:function(){
					comm_query('gridStoreId','checkForm');
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
//		'bl_zhkhjl_ywl','bl_zhkhjl_bzcp','bl_zhkhjl_zhts','bl_zhkhjl_dx',
//      'bl_wdkhjl_ywl','bl_wdkhjl_bzcp','bl_wdkhjl_zhts','bl_wdkhjl_dx'
		var myColumns = [ 
//		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
		    {header:'机构号',dataIndex:'orgid',width:110,align:'left',locked:true},
            {header:'机构名称',dataIndex:'orgname',width:170,align:'left',locked:true},
            {header:'考核周期',dataIndex:'zq',width:80,align:'left',locked:true},
            {text:'柜员考核比例',
            	columns:[{text:'支行考核比例',
			            		columns:[{header:'业务量',dataIndex:'bl_zhgy_ywl',width:80,align:'left'},
			            		         {header:'标准产品',dataIndex:'bl_zhgy_bzcp',width:80,align:'left'},
			            		         {header:'特色业务',dataIndex:'bl_zhgy_zhts',width:80,align:'left'},
			            		         {header:'定性',dataIndex:'bl_zhgy_dx',width:80,align:'left'}
			            		]
            	         },
            		     {text:'网点考核比例',
            		        	 columns:[{header:'业务量',dataIndex:'bl_wdgy_ywl',width:80,align:'left'},
            		        	          {header:'标准产品',dataIndex:'bl_wdgy_bzcp',width:80,align:'left'},
            		        	          {header:'特色业务',dataIndex:'bl_wdgy_zhts',width:80,align:'left'},
            		        	          {header:'定性',dataIndex:'bl_wdgy_dx',width:80,align:'left'}
            		        	          ]
            	         }
			     ]
            },
            {text:'客户经理考核比例',
            	columns:[{text:'支行考核比例',
			            		columns:[{header:'标准产品',dataIndex:'bl_zhkhjl_bzcp',width:80,align:'left'},
			            		         {header:'特色业务',dataIndex:'bl_zhkhjl_zhts',width:80,align:'left'},
			            		         {header:'定性',dataIndex:'bl_zhkhjl_dx',width:80,align:'left'}
			            		]
            	         },
            		     {text:'网点考核比例',
            		        	 columns:[{header:'标准产品',dataIndex:'bl_wdkhjl_bzcp',width:80,align:'left'},
            		        	          {header:'特色业务',dataIndex:'bl_wdkhjl_zhts',width:80,align:'left'},
            		        	          {header:'定性',dataIndex:'bl_wdkhjl_dx',width:80,align:'left'}
            		        	          ]
            	         }
			     ]
            }];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			rowLines:true,
			title:'人员总包分配比例信息表',
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
//			selModel:Ext.create('Ext.selection.CheckboxModel',{
//				checkOnly:false//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
//			}),
			tbar:[{
				text:'导入人员总包分配比例批量文件',
				cls:'x-btn-text-icon',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("10000068","导入人员总包分配比例批量文件");
				}
			},{
				text:'下载批量模版',
				iconCls:'Packagedown',
				handler:function(){
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=10000068";
				}
			}],
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
		
		sizeCombo.on('select',function(combo){
			var pageSize = parseInt(combo.getValue());
			var pagingToolBar = Ext.getCmp('pagingBar');
			pagingToolBar.pageSize = pageSize;
			tableStore.pageSize = pageSize;
			tableStore.loadPage(1);
		});
		
		orgTreePanel.on('itemclick',function(view,record,item,index,e,obj){
			orgInfoQuery.setValue(record.get("menuId"));
			orgInfoQuery.setRawValue(record.get("menuName"));
			//alert(getNode("treeStoreId",record.get("menuId")));
			orgId = getLatestOrgId(getNode("treeStoreId",record.get("menuId")),"treeStoreId");//获取当前操作节点最接近机构id
//			alert("orgId:"+orgId);
			orgInfoQuery.collapse();
			checkForm.getForm().findField("orgIdQuery").setValue(orgId);
			checkForm.getForm().findField("deptIdQuery").setValue(record.get("menuId"));
			
			var orgArr = new Array();
			node = getNode("treeStoreId",record.get("menuId"));//初始节点
			getUpOrgAll(node,orgArr);
			var queryStr = getQueryStr(orgArr);
			Ext.getCmp("queryStrId").setText(queryStr);
		});
		setTimeout(function(){tableStore.reload();},1000);//tableStore属性autoLoad:true，可能存在加载不成功的情况，故采用延时加载解决该问题。2015-11-10
	}	
});


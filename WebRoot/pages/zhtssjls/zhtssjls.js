// JavaScript Document
/*var height;
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
var udpFormPanelAdd;*/
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
		//机构部门下拉树（查询面板）
		var orgCmp = new orgCmponent();//机构组件对象
		var tree = orgCmp.getTreeQuery();//机构树对象
		var orgInfoQuery = orgCmp.getOrgInfoQuery(tree);;//机构下拉框对象
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
					items:[new queryTextInput({
						fieldLabel:'用户号',
						name:'userId'
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
				}
			}]
		});
		/*********查询结束**********/
		
		/*********显示开始**********/
		//列
//		var myColumns = new Array();
		var myColumns = [{header:'',dataIndex:'userId',width:110,sortable:false,align:'center',locked:true}];//列数组必须初始化一列为locked：true,否则加载完的表格中的列属性locked:true将无效  2015-11-08
		var sizeCombo = new sizeComboPrt();
		tableStore.on('load',function(store,records,successful,eOpts){
			var records;
			var record;
			//获取records值测试2015-11-06
			//方法一：
			/*records = tableStore.data;
			record = records.get(0);
			userId = record.get("userId");
			record = records[0];*/
			//方法二：
			/*record = records[0];
			alert(record.get("userId"));*/
			try{
				record = records[0];
			//	console.log(records);
			//	console.log(record);
			}catch(e){}
			

//			alert(record.raw['dj']);
//			alert(Ext.JSON.encode(record.getData()));
			var jsonArray = new Array();
			if(record != undefined){//后台有list数据返回
				for(key in record.getData()){
					//alert(key+"|"+record.getData()[key]);
					var coll = new Ext.util.MixedCollection();
//					var colName = key.toLowerCase()+"_colname";
					var colName = key+"_colname";
					var reg = new RegExp('col');
					if(!reg.test(key)){
					//	console.log(key);
					//	console.log(colName);
						coll.add('locked',true);
					}else{
						coll.add('locked',false);
					}
					coll.add('header',record.raw[colName]);
					coll.add('dataIndex',key);
					coll.add('width',110);
					coll.add('sortable',false);
					coll.add('align','center');
//					myColumns.push(coll.map);
					if(record.raw[colName] != '' && record.raw[colName]!= null && record.raw[colName]!= undefined)
						jsonArray.push(coll.map);
				}
			}
			/*console.log(myColumns);
			var coll = new Ext.util.MixedCollection();
			coll.add('key1', 'val1');
			coll.add('key2', 'val2');
			coll.add('key3', 'val3');
			console.log(coll);
			console.log(coll.map);
			console.log(jsonArray);*/
			myGrid.reconfigure(tableStore,jsonArray);
		});
		/*tableStore.load({
			scope:this,
			callback: function(records, operation, success) {
//		        console.log(records);
		        alert(records);
		    }
		});*/
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			columnLines: true,
			title:'往期特色产品考核业绩展示',
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
			/*features:[{
				ftype:'summary'
			},{
				ftype:'summary'
			}],*/
			columns:myColumns,
			/*selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),*/
			/*tbar:[{
				text:'上传支行特色产品考核基础数据批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30122","上传支行特色产品考核基础数据批量文件");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				html:'<iframe id="myiframe"></iframe>',
				handler:function(){
//					document.location = "downLoad.html?txCode=30062";//会导致【MessagePush.onPageLoad(userId);】已注册的后台监听失效
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30122";
				}
			}],*/
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
		
		tree.on('itemclick',function(view,record,item,index,e,obj){
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
	}	
});


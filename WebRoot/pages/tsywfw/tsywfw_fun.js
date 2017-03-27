
//设置编辑表格
var cellEditing = Ext.create('Ext.grid.plugin.CellEditing',{
		clicksToEdit:1
});




/**
 * 用户基本信息
 * @return 用户基本信息fieldset
 */
function userBasicInfo(record){
	var basePanel = Ext.create('Ext.form.Panel',{
		id:"basePanelId",
//		baseCls:'my-panel-no-border',
		buttonAlign:'center',
		fieldDefaults:{
			margin:'4 0 3 0',
			labelAlign:'right',
			allowBlank:false,
			blankText:'该项为必输项,若无数据请填写"无"',
			msgTarget:'side',
			maxLength:25,
			maxLengthText:'数据超长,限定20字以内'
		},
		resizable:false,
		defaults:{
//			labelWidth:50,
			labelAlign:'right',
//			msgTarget:'side',
		},
		items:[{
			layout:'column',
			baseCls:'my-panel-no-border',
			margin:'2 0 2 0',
			items:[{
				columnWidth:.5,
				xtype:'displayfield',
				name:'orgid',
				fieldLabel:'机构号',
				value:record.orgid/*,
				style:'border:1px solid red',*/
			},{
				columnWidth:.5,
				xtype:'displayfield',
				fieldLabel:'机构名称',
				name:'orgname',
				value:record.orgname,
				border:true/*,
				style:'border:1px solid red',*/
			}]
		},{
			layout:'column',
			baseCls:'my-panel-no-border',
			items:[{
				columnWidth:.5,
				xtype:'displayfield',
				fieldLabel:'序号',
				name:'seq_num',
				value:record.seq_num/*,
				style:'border:1px solid red',*/
			},{
				columnWidth:.5,
				xtype:'displayfield',
				fieldLabel:'名称',
				name:'zb_name',
				value:record.zb_name,
				border:true/*,
				style:'border:1px solid red',*/
			}]
		},{
			layout:'column',
			baseCls:'my-panel-no-border',
			items:[{
				columnWidth:.5,
				xtype:'textfield',
				fieldLabel:'机构设定积分',
				name:'org_point',
				value:record.org_point,
				regex:/(^0{1}$)|(^[-]*[0]{1}\.{1}[0-9]{0,2}$)|(^[-]*[1-9]{1,}[0-9]*\.?[0-9]{0,2}$)/,
				msgTarget:'side',
				width:280,
				allowBlank:false,
				autoFitErrors:false,
				regexText:'数据非法,限定整数或两位小数',
				border:true/*,
				style:'border:1px solid red',*/
			
			}]
		}],
		buttons:[{
			text:'保存',
			handler:function(){
				var paramJson = getParam("basePanelId");
				paramJson.txCode = "30152";
			//	console.log(paramJson);
				comm_json("zbfwEdit.html","gridStoreId",Ext.encode(paramJson),"basePanelId");
			}
		},{
			text:'取消',
			handler:handlerCancle
		}]
	});
	return basePanel;
}


/**
 * 查看详细信息框架窗口
 * @param baseInfo 基本信息fieldset
 * @param pointInfo 积分信息fieldset
 */
function showPopWin(baseInfo){
	winAdd = Ext.create('Ext.window.Window',{
//		frame:true,
		title:'编辑',
		width:600,
		height:180,
		constrain:true,
//		autoScroll:false,
		layout:{
			type:'fit',
		},
		modal:true,
		maximizable:true,
		items:[baseInfo]
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
		//var userId = record.get("orgid");
		//'orgid','orgname','org_point','seq_num','zb_name','unit','point'
		var recordCol = new Ext.util.MixedCollection();
		recordCol.add("orgid",record.get("orgid"));
		recordCol.add("orgname",record.get("orgname"));
		recordCol.add("org_point",record.get("org_point"));
		recordCol.add("seq_num",record.get("seq_num"));
		recordCol.add("zb_name",record.get("zb_name"));
//		alert(Ext.encode(recordCol));
		var recordParse = Ext.decode(Ext.encode(recordCol)).map;//record转json对象（record类似javabean，属性需用get(属性名称)获取,转json后，通过“.”来获取）
		var baseInfo = userBasicInfo(recordParse);//用户基本信息（userBasicInfo函数入参类型：json对象）
		showPopWin(baseInfo);
	}
}

/**
 * 获取弹窗对象
 * @param title 弹窗标题
 * @param editGrid 弹窗内表格对象
 * @returns 弹窗对象
 */
function getWinAdd(title,editGrid){
	 var winAdd = Ext.create('Ext.window.Window',{
			constrain:true,
			layout:'fit',
			title:title,
			maximizable:true,
			width:800,
			height:400,
			modal:true,
			items:[editGrid],
			closeAction:'destroy'
		});
	 return winAdd;
}
/**
 * 获取编辑表格列配置文件
 * @returns {Array} 公用列配置
 */
function getEditTbCol(gridId){
	var tscpCombo = Ext.create('tscpCombo');
	var pubColumns = [{header:'指标序号',dataIndex:'seq_num',width:220,editor:tscpCombo},
	                   {header:'指标名称',dataIndex:'zb_name',width:160},
	                   {header:'机构指定分值',dataIndex:'org_point',width:100,editor:{}},
	                   {header:'删除',width:80,align:'center',
					        xtype: 'actioncolumn',
					        sortable: false,
					        items: [{
					            icon: 'images/ext/delete.gif',
//					        	cls:'x-btn-text-icon',
//								iconCls:'Add',
					            text: '删除',
					            handler: function(grid, rowIndex, colIndex) {
					            	grid.getStore().removeAt(rowIndex); 
					            }
					        }]
				    }];
	tscpCombo.on('expand',function(){
		tscpCombo.getStore().reload();
	});
	tscpCombo.on('select',function(combo,records,eOpts){
		var selectionModel =  Ext.getCmp(gridId).getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];
	//	console.log(bzcpCombo);
		dataModel.set('zb_name',records[0].get("zb_name"));
		dataModel.set('point',records[0].get("point"));
	});
	/*
	ctCombo.on('select',function(combo,records,eOpts){
		var selectionModel =  Ext.getCmp(gridId).getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];
		dataModel.set('ctName',records[0].get("ct_name"));
	});*/
	return pubColumns;
}


/**
 * 总行学习积分
 */
function zhxf(){
	var winAdd;
	var txCode = 30151;
	var gridId = "zhxfGridId";
	var zhjfStore = Ext.data.StoreManager.lookup('editGridStoreId');
	var pubColumns = getEditTbCol(gridId);
	var zhxfGrid = Ext.create('Ext.grid.Panel',{
		id:gridId,
		border:true,
		width:'100%',
		height:'100%',
		store:zhjfStore,
		columns:pubColumns,
		selModel:Ext.create('Ext.selection.CheckboxModel',{
			checkOnly:true
		}),
		plugins:[cellEditing],
		tbar:[{
			text:'新增',
			iconCls:'Tablerowinsert',
			handler:function(){
				var row = Ext.create('planDataModel');
				zhjfStore.insert(0,row);
			}
		},{
			text:'保存',
			iconCls:'Databasesave',
			menu:[{
				text:'保存【当前机构有效】',
				iconCls:'Tablesave',
				handler:function(){
					Ext.MessageBox.show({
						title:'提示',
						msg:'正在提交...',
						progress:true,
						closable:true
					});
					var keyArray = new Array();
					keyArray.push("seq_num");
					new comm().saveAll("gridStoreId","editGridStoreId",keyArray,txCode,winAdd,'zbfwControler.html?scope=1');
				}
			},{
				text:'保存【当前机构及下属所有子机构有效】',
				iconCls:'Pagesave',
				handler:function(){
					var keyArray = new Array();//待批量插入数据非空字段数组
					keyArray.push("seq_num");
					new comm().saveAll("gridStoreId","editGridStoreId",keyArray,txCode,winAdd,'zbfwControler.html?scope=2');
				}
			}]
		}]
	});
	var winAdd = getWinAdd("添加支行特色指标范围",zhxfGrid);
	winAdd.show();
}

//删除
function handleDel(rec,url,storeId){
//	var formTemp  = Ext.getCmp(formId).getForm();
//	formTemp.loadRecord(rec);
//	formTemp.findField('tx_code').setValue('10013');
	Ext.Msg.confirm('提示信息','确认删除吗？',function(btn){
		   if(btn == 'yes'){
			   //comm_update(url,storeId,formId);
			   var paramColl = new Ext.util.MixedCollection();//集合
			   paramColl.add("userId", rec.get("userId"));
			   paramColl.add("ctId", rec.get("ctId"));
			   paramColl.add("tx_code", "30092");
			   var paramJson = Ext.decode(Ext.encode(paramColl)).map;
			   comm_ajax(url,storeId,paramJson);
		   }   
	   });
}



	
		
		
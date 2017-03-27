
//设置编辑表格
var cellEditing = Ext.create('Ext.grid.plugin.CellEditing',{
		clicksToEdit:1
});

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
	var userCombo = Ext.create('userCombo');
	var ctCombo = Ext.create('ctCombo',{
		fieldLabel:''
	});
	var pubColumns = [{header:'用户编号',dataIndex:'userId',width:100,editor:userCombo},
	                   {header:'姓名',dataIndex:'userName',width:100},
	                   {header:'资格编号',dataIndex:'ctId',width:100,editor:ctCombo},
	                   {header:'资格名称',dataIndex:'ctName',width:100},
	                   {header:'删除',width:80,align:'center',
					        xtype: 'actioncolumn',
					        sortable: false,
					        items: [{
					            icon: 'images/ext/delete.gif',
					            tooltip: '删除',
					            handler: function(grid, rowIndex, colIndex) {
					            	grid.getStore().removeAt(rowIndex); 
					            }
					        }]
				    }];
	userCombo.on('select',function(combo,records,eOpts){
		var selectionModel =  Ext.getCmp(gridId).getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];
		dataModel.set('userName',records[0].get("userName"));
	});
	ctCombo.on('select',function(combo,records,eOpts){
		var selectionModel =  Ext.getCmp(gridId).getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];
		dataModel.set('ctName',records[0].get("ct_name"));
	});
	return pubColumns;
}


/**
 * 总行学习积分
 */
function zhxf(){
	var winAdd;
	var txCode = 30091;
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
			text:'手动录入',
			iconCls:'Penciladd',
			menu:[{
				text:'添加一行',
//				icon:'images/ext/add.gif',
				iconCls:'Tablerowinsert',
				handler:function(){
					var row = Ext.create('planDataModel');
					zhjfStore.insert(0,row);
				}
			},{
				text:'保存',
//				icon:'images/ext/save.gif',
				iconCls:'Tablesave',
				handler:function(){
//					saveAll(zhjfStore,txCode,winAdd);
					var keyArray = new Array();
					keyArray.push("userId");
					keyArray.push("ctId");
					new comm().saveAll("gridStoreId","editGridStoreId",keyArray,txCode,winAdd,'uctManage.html');
				}
			}]
		},{
			text:'批量导入',
			iconCls:'Pageexcel',
			menu:[{
				text:'上传批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30094","员工资格认证信息批量");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				html:'<iframe id="myiframe"></iframe>',
				handler:function(){
//					document.location = "downLoad.html?txCode=30062";//会导致【MessagePush.onPageLoad(userId);】已注册的后台监听失效
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30094";
				}
			}]
		}]
	});
	var winAdd = getWinAdd("添加员工资格认证信息",zhxfGrid);
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



	
		
		
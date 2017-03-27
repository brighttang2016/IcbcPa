
var keyArray = new Array();
keyArray.push("userId");
keyArray.push("userName");
keyArray.push("cTypeId");
keyArray.push("cTypeName");
keyArray.push("point");

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
/**设置查询当前积分维护类型下属子类型查询参数
 * @param creditTypeCombo 积分维护类型下拉框对象
 * @param ctypeId 积分类型id 
 * @param ctypePId 学习积分父类型id
 */
function setCreditType(creditTypeCombo,cTypeId,cTypePid){
	var ctcStore = creditTypeCombo.getStore();//积分维护类型store
	ctcStore.on("beforeLoad",function(){
		Ext.apply(ctcStore.proxy.extraParams,{
			cTypeId:cTypeId,
			cTypePid:cTypePid
		});
	});
	ctcStore.reload();
}
/**
 * 获取编辑表格列配置文件
 * @returns {Array} 公用列配置
 */
function getEditTbCol(txCode,gridId){
	var userCombo = Ext.create('userCombo');
	var creditTypeCombo = Ext.create('creditTypeCombo');
	switch(txCode){
	case 30061:
		setCreditType(creditTypeCombo,"100","000");//查询积分类型(总行基础学习积分)
		break;
	case 30063:
		setCreditType(creditTypeCombo,"000","200");//查询积分类型(市行基础学习积分)
		break;
	case 30065:
		setCreditType(creditTypeCombo,"301","300");//查询积分类型(附加学习积分)
		break;
	case 30067:
		setCreditType(creditTypeCombo,"400","000");//查询积分类型(手工调整学习积分)
		break;
	}
	var pubColumns = [{header:'用户编号',dataIndex:'userId',width:100,editor:userCombo},
	                   {header:'姓名',dataIndex:'userName',width:100},
	                   {header:'积分类型编号',dataIndex:'cTypeId',width:100,editor:creditTypeCombo},
	                   {header:'积分类型名称',dataIndex:'cTypeName',width:200},
	                   {header:'积分',dataIndex:'point',width:100,editor:{
	                	   allowBlank:false,
	                	   regex:/(^[0]{1}\.{1}[1-9]+$)|(^[-]?[1-9]{1,}\.?[0-9]*$)/,
	                	   emptyText:'请录整数或小数',
	                	   msgTarget:'side',
	                	   regexText:'数据非法,请录整数或小数'
	                   }},
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
	creditTypeCombo.on('select',function(combo,records,eOpts){
		var selectionModel =  Ext.getCmp(gridId).getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];
		dataModel.set('cTypeName',records[0].get("cTypeName"));
	});
	return pubColumns;
}

/**
 * 总行学习积分
 */
function zhxf(){
	var winAdd;
	var txCode = 30061;
	var gridId = "zhxfGridId";
	var zhjfStore = Ext.data.StoreManager.lookup('zhjfStoreId');
	var pubColumns = getEditTbCol(txCode,gridId);
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
					new comm().saveAll("gridStoreId","zhjfStoreId",keyArray,txCode,winAdd,'creditManage.html');
				}
			}]
		},{
			text:'批量导入',
			iconCls:'Pageexcel',
			menu:[{
				text:'上传批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30062","上传总行基础学分批量文件");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				html:'<iframe id="myiframe"></iframe>',
				handler:function(){
//					document.location = "downLoad.html?txCode=30062";//会导致【MessagePush.onPageLoad(userId);】已注册的后台监听失效
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30062";
				}
			},{
				text:'下载批量模版文件2',
				iconCls:'Packagedown',
				html:'<iframe id="myiframe"></iframe>',
				handler:function(){
//					document.location = "downLoad.html?txCode=30062";//会导致【MessagePush.onPageLoad(userId);】已注册的后台监听失效
					Ext.getDom('downloadIframe').src = "downLoad2.html?name=唐亮";
				}
			}]
		}]
	});
	winAdd = getWinAdd("总行学分维护",zhxfGrid);
	winAdd.show();
}
/**
 * 市行学分维护
 */
function shxf(){
	var winAdd;
	var txCode = 30063;//市行基础学分单笔
	var gridId = "shxfGridId";
	var shxfStore = Ext.data.StoreManager.lookup("shxfStoreId");
	var pubColumns = getEditTbCol(txCode,gridId);
	var shxfGrid = Ext.create('Ext.grid.Panel',{
		id:gridId,
		border:true,
		width:'100%',
		height:'100%',
		store:shxfStore,
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
					shxfStore.insert(0,row);
				}
			},{
				text:'保存',
//				icon:'images/ext/save.gif',
				iconCls:'Tablesave',
				handler:function(){
//					saveAll(shxfStore,txCode,winAdd);
					new comm().saveAll("gridStoreId","shxfStoreId",keyArray,txCode,winAdd,'creditManage.html');
				}
			}]
		},{
			text:'批量导入',
			iconCls:'Pageexcel',
			menu:[{
				text:'上传批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30064","上传市行基础学分批量文件");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				handler:function(){
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30064";
				}
			}]
		}]
	});
	winAdd = getWinAdd("市行学分维护",shxfGrid);
	winAdd.show();
}
/**
 * 附加学分维护
 */
function fjxf(){
	var winAdd;
	var txCode = 30065;//附加学分单笔
	var gridId = 'fjxfGridId';
	var fjxfStore = Ext.data.StoreManager.lookup("fjxfStoreId");
	var pubColumns = getEditTbCol(txCode,gridId);
	var fjxfGrid = Ext.create('Ext.grid.Panel',{
		id:gridId,
		border:true,
		width:'100%',
		height:'100%',
		store:fjxfStore,
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
					fjxfStore.insert(0,row);
				}
			},{
				text:'保存',
//				icon:'images/ext/save.gif',
				iconCls:'Tablesave',
				handler:function(){
//					saveAll(fjxfStore,txCode,winAdd);
					new comm().saveAll("gridStoreId","fjxfStoreId",keyArray,txCode,winAdd,'creditManage.html');
				}
			}]
		},{
			text:'批量导入',
			iconCls:'Pageexcel',
			menu:[{
				text:'上传批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30066","上传附加学分批量文件");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				handler:function(){
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30066";
				}
			}]
		}]
	});
	winAdd = getWinAdd("附加学分维护",fjxfGrid);
	winAdd.show();
}
/**
 * 学分手工调整
 */
function sgtz(){
	var winAdd;
	var txCode = 30067;//附加学分单笔
	var gridId = 'sgxfGridId';
	var fjxfStore = Ext.data.StoreManager.lookup("sgxfStoreId");
	var pubColumns = getEditTbCol(txCode,gridId);
	var sgxfGrid = Ext.create('Ext.grid.Panel',{
		id:gridId,
		border:true,
		width:'100%',
		height:'100%',
		store:fjxfStore,
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
					sgxfStore.insert(0,row);
				}
			},{
				text:'保存',
//				icon:'images/ext/save.gif',
				iconCls:'Tablesave',
				handler:function(){
//					saveAll(sgxfStore,txCode,winAdd);
					new comm().saveAll("gridStoreId","sgxfStoreId",keyArray,txCode,winAdd,'creditManage.html');
				}
			}]
		},{
			text:'批量导入',
			iconCls:'Pageexcel',
			menu:[{
				text:'上传批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30068","上传手工调整学分批量文件");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				handler:function(){
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30068";
				}
			}]
		}]
	});
	winAdd = getWinAdd("学分手工调整",sgxfGrid);
	winAdd.show();
}

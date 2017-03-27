
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
//	var tscpCombo = Ext.create('tscpCombo');
	var pubColumns = [{header:'机构号',dataIndex:'orgid',width:100,locked:true},
	                   {header:'机构名称',dataIndex:'orgname',width:150,locked:true},
	                   {header:'业务量考核收入总包',dataIndex:'zb_ywl',width:130,editor:{}},
	                   {header:'标准产品考核收入总包',dataIndex:'zb_bzcp',width:130,editor:{}},
	                   {header:'特色业务考核收入总包',dataIndex:'zb_zhts',width:130,editor:{}},
	                   {header:'定性考核收入总包',dataIndex:'zb_dx',flex:1,editor:{}}];
	return pubColumns;
}

//总包设置（方案二）
function zbsz2(){
	var winAdd;
	var txCode = 30073;
	var gridId = "zbGridId";
//	var zbStore = Ext.data.StoreManager.lookup('zbStoreId');
	Ext.regModel('zbModel',{
		fields:['orgid','orgname','zq','zb_ywl','zb_bzcp','zb_zhts','zb_dx']
	});
	var zbStore = Ext.create('Ext.data.Store',{
		storeId:'zbStoreId',
//		autoLoad:{start:0,limit:pageSize},
		autoLoad:false,
		model:'zbModel',
		width:'100%',
		height:'100%',
		pageSize:pageSize,
		proxy:{
			type:'ajax',
			url:'zbOrgControler.html',
			reader:new Ext.data.JsonReader({
				root:'rows',
				totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
			})
		}
	});
	
	zbStore.reload();
	var pubColumns = getEditTbCol(txCode,gridId);
	var zbGrid = Ext.create('Ext.grid.Panel',{
		id:gridId,
		border:true,
		width:'100%',
		height:'100%',
		buttonAlign:'center',
		store:zbStore,
		columns:pubColumns,
		/*selModel:Ext.create('Ext.selection.CheckboxModel',{
			checkOnly:true
		}),*/
		plugins:[cellEditing],
		tbar:[{
			xtype: 'textfield',
	        name: 'zq',
	        id:'zq',
	        fieldLabel: '当前考核周期:',
	        allowBlank: false 
		},'-',{
			text:'批量导入【方案一】',
			iconCls:'Pageexcel',
			menu:[{
				text:'导入批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload(txCode,"选择方案一批量文件");
				}
			},{
				text:'下载批量模版',
				iconCls:'Packagedown',
				handler:function(){
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code="+txCode;
				}
			}]
			
		}],
		buttons:[{
			text:'保存',
//			icon:'images/ext/save.gif',
			iconCls:'Tablesave',
			handler:function(){
				var zq  = Ext.getCmp('zq');
				if(zq.value == '' || zq.value==null || zq.value==undefined){
					msgAlert("请录入考核周期");
				}else{
				//	console.log(zbStore);
					records = zbStore.data;
					for(var i = 0;i < records.length;i ++){
						record = records.get(i);
						record.set('zq',zq.value);
					}
					//alert(records.get(0).get("zq"));
					var keyArray = new Array();
					keyArray.push("zb_ywl");
					keyArray.push("zb_bzcp");
					keyArray.push("zb_zhts");
					keyArray.push("zb_dx");
					new comm().saveAll("gridStoreId","zbStoreId",keyArray,txCode,winAdd,'zbControler.html');
				}
				
			}
		}]
	});
	winAdd = getWinAdd("机构总包设置",zbGrid);
	winAdd.show();
	winAdd.on('close',function(){
		zbStore.removeAll();
	});
}

//总包设置（方案一）
function zbsz(){
	var winAdd;
	var txCode = 30072;
	var gridId = "zbGridId";
//	var zbStore = Ext.data.StoreManager.lookup('zbStoreId');
	Ext.regModel('zbModel',{
		fields:['orgid','orgname','zq','zb_ywl','zb_bzcp','zb_zhts','zb_dx']
	});
	var zbStore = Ext.create('Ext.data.Store',{
		storeId:'zbStoreId',
//		autoLoad:{start:0,limit:pageSize},
		autoLoad:false,
		model:'zbModel',
		width:'100%',
		height:'100%',
		pageSize:pageSize,
		proxy:{
			type:'ajax',
			url:'zbOrgControler.html',
			reader:new Ext.data.JsonReader({
				root:'rows',
				totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
			})
		}
	});
	
	zbStore.reload();
	var pubColumns = getEditTbCol(txCode,gridId);
	var zbGrid = Ext.create('Ext.grid.Panel',{
		id:gridId,
		border:true,
		width:'100%',
		height:'100%',
		buttonAlign:'center',
		store:zbStore,
		columns:pubColumns,
		/*selModel:Ext.create('Ext.selection.CheckboxModel',{
			checkOnly:true
		}),*/
		plugins:[cellEditing],
		tbar:[{
			xtype: 'textfield',
	        name: 'zq',
	        id:'zq',
	        fieldLabel: '当前考核周期:',
	        allowBlank: false 
		},'-',{
			text:'批量导入【方案一】',
			iconCls:'Pageexcel',
			menu:[{
				text:'导入批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload(txCode,"选择方案一批量文件");
				}
			},{
				text:'下载批量模版',
				iconCls:'Packagedown',
				handler:function(){
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code="+txCode;
				}
			}]
			
		}],
		buttons:[{
			text:'保存',
//			icon:'images/ext/save.gif',
			iconCls:'Tablesave',
			handler:function(){
				var zq  = Ext.getCmp('zq');
				if(zq.value == '' || zq.value==null || zq.value==undefined){
					msgAlert("请录入考核周期");
				}else{
				//	console.log(zbStore);
					records = zbStore.data;
					for(var i = 0;i < records.length;i ++){
						record = records.get(i);
						record.set('zq',zq.value);
					}
					//alert(records.get(0).get("zq"));
					var keyArray = new Array();
					keyArray.push("zb_ywl");
					keyArray.push("zb_bzcp");
					keyArray.push("zb_zhts");
					keyArray.push("zb_dx");
					new comm().saveAll("gridStoreId","zbStoreId",keyArray,txCode,winAdd,'zbControler.html');
				}
				
			}
		}]
	});
	winAdd = getWinAdd("机构总包设置",zbGrid);
	winAdd.show();
	winAdd.on('close',function(){
		zbStore.removeAll();
	});
}


	
		
		
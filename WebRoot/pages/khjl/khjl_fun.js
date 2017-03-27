
var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    });

//删除
/*function handleDel(grid){
	var sm = grid.getSelectionModel();
	var records = sm.getSelection();
	var jsonArray = new Array();
	Ext.each(records,function(item){
		jsonArray.push(item.data);
		//alert(item.data.userId);
	});
	Ext.MessageBox.confirm('提示','确认删除?',function(btn){
		if(btn == 'yes'){
			Ext.Ajax.request({
				type:'ajax',
				url:'rpManage.html',
				params:{
					data:Ext.encode(jsonArray),
					tx_code:'30053'
				},
				success:function(response){
					Ext.data.StoreManager.lookup("gridStoreId").reload();
				},
				failure:function(){
					
				}
			});
		}
	});
}*/
function handleUpdate(rec,storeId,formId){
	var tempPanel = Ext.getCmp(formId);
	var formTemp  = tempPanel.getForm();
	formTemp.loadRecord(rec);
	formTemp.findField('tx_code').setValue('30041');
	winUpd = Ext.create('win',{
		title:'编辑',
		width:600,
		height:150,
		items:[tempPanel]});
	winUpd.show();
}
//展示新增弹窗
function showAdd(){
	var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    });
//	var sizeCombo = new sizeComboPrt();
	var rewardGridStore = Ext.data.StoreManager.lookup('rewardGridStoreId');
	var userCombo = Ext.create('userCombo');
	var yearCombo = Ext.create('yearCombo');
	userCombo.on('select',function(combo,records,eOpts){
		//新纪录数据初始化
//		var dataModel = rewardGridStore.getAt(0);//存在bug：员工考核及奖励信息录入表格，如果录入多条记录，当修改后面记录的员工编码，第一条记录的员工姓名被修改
		var selectionModel = Ext.getCmp('rewardGridId').getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];//表格每次都是单选，当前编辑行即为第一条记录
		dataModel.set("userName",records[0].get("userName"));
		date = new Date();
		dataModel.set("rpTime",date.getFullYear());
		dataModel.set("assptId","1");
		dataModel.set("rpPoint","0");
	});
	var yearCombo = Ext.create('yearCombo');
	var rewardGridColumns = [ 
//	    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'left'}),//行号
		{header:'员工编码',dataIndex:'userId',width:100,sortable:true,align:'left',
	    	editor:userCombo},
		{header:'姓名',dataIndex:'userName',width:100,sortable:false,align:'left'},
		{header:'年份',dataIndex:'rpTime',sortable:false,align:'left',width:100,
	    		editor:yearCombo},
    	{header:'考核结果',dataIndex:'assptId',flex:1,sortable:false,align:'left',
    		editor:Ext.create('assCombo2',{
    			fieldLabel:''
    		})},
		 {header:'奖励积分',dataIndex:'rpPoint',width:100,sortable:false,align:'left',
		    		editor:Ext.create('rpCombo')},
		 {header:'奖励积分原因',dataIndex:'rpMsg',width:100,sortable:false,align:'left',
			    		editor:{
			    			allowBlank:false,
			    			blankText:'请输入积分奖励原因'
			    		}},
		{header:'删除',width:80,align:'center',
            xtype: 'actioncolumn',
            sortable: false,
            items: [{
                icon: 'images/ext/delete.gif',
                tooltip: '删除',
                handler: function(grid, rowIndex, colIndex) {
                	rewardGridStore.removeAt(rowIndex); 
                }
            }]
        }
	];
	Ext.define('Plant', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'userId', type: 'string'},
            {name: 'userName', type: 'string'},
            {name: 'rpTime',type:'string'},
            {name: 'assptId',type:'string'},
            {name: 'rpPoint',type:'string'},
            {name: 'rpMsg',type:'string'}
        ]
    });
	
	var rewardGrid = Ext.create('Ext.grid.GridPanel',{
			id:'rewardGridId',
//			title:'员工考核及奖励信息录入',
			scroll:true,
			border:true,
//			layout:'fit',
			width:'100%',
			height:'100%',
			viewConfig:{
//				forceFit:true,
//				stripeRows:true
			},
			store:rewardGridStore,
			features:[{
				ftype:'summary'
			},{
				ftype:'summary'
			}],
			columns:rewardGridColumns,
			selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),
			tbar:[{
				text:'添加一行',
				cls:'x-btn-text-icon',
				icon:'images/ext/add.gif',
				handler:function(){
				/*	date = new Date();
	                var r = Ext.create('Plant', {
	                	rpTime: date.getFullYear(),
	                	assptId:'6',
	                	rpPoint: '0'
	                	
	                });
	                rewardGridStore.insert(0, r);
	                cellEditing.startEditByPosition({row: 0, column: 0});
	                */
	                var row = Ext.create('Plant');
	                rewardGridStore.insert(0,row);
				}
			},{
				text:'保存',
				cls:'x-btn-text-icon',
				icon:'images/ext/save.gif',
				handler:function(){
					var rpPointTag = 0;//奖励积分标识(1:以选择奖励积分，必须输入积分奖励原因)
					var m = rewardGridStore.getModifiedRecords().slice(0);
					var jsonArray = new Array();
					Ext.each(m,function(item){
						jsonArray.push(item.data);
						if(item.data.rpPoint != "0" && item.data.rpPoint != "" && item.data.rpMsg == ""){
							rpPointTag = 1;
						}
					});
					//alert(rpPointTag);
					if(rpPointTag == 1){
						msgAlert("奖励积分不为0,必须录入积分奖励原因!");
					}else{
						Ext.Ajax.request({
							url:'rpManage.html',
							params:{
								data:Ext.encode(jsonArray),//encodeURIComponent(Ext.encode(jsonArray)),
								tx_code:'30051'
							},
							success:function(response){
								//msgAlert("成功");
								rewardGridStore.removeAll();
								Ext.data.StoreManager.lookup('gridStoreId').reload();
								winAdd.hide();
//								winAdd.close();
							},failure:function(){
								//msgAlert("失败");
							}
						});
					}
				}
			}],
			plugins: [cellEditing]
		});
	winAdd = getWinAdd("员工奖励信息录入",rewardGrid);
	winAdd.show();
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
//		增加、修改处理结束----------------------------


/**
 * 获取编辑表格列配置文件
 * @returns {Array} 公用列配置
 */
function getEditTbCol(gridId){
	var userCombo = Ext.create('userCombo');
	/*var creditTypeCombo = Ext.create('creditTypeCombo');
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
	}*/
	var yearCombo = Ext.create('yearCombo',{
		allowBlank:false,
		emptyText:'请选择考核年份',
		msgTarget:'side',
		regexText:'不能为空,请选择考核年份'
	});
	var pubColumns = [{header:'用户编号',dataIndex:'userId',width:100,editor:userCombo},
	                   {header:'姓名',dataIndex:'userName',width:100},
	                   {header:'年份',dataIndex:'rpTime',sortable:false,align:'left',width:100,
	       	    		editor:yearCombo},
//	                   {header:'考核结果',dataIndex:'assptId',flex:1,sortable:false,align:'left',editor:{}},
	                   {header:'奖励积分',dataIndex:'rpPoint',width:100,editor:{
	                	   allowBlank:false,
	                	   regex:/(^[0]{1}\.{1}[1-9]+$)|(^[-]?[1-9]{1,}\.?[0-9]*$)/,
	                	   emptyText:'请录整数或小数',
	                	   msgTarget:'side',
	                	   regexText:'数据非法,请录整数或小数'
	                   }},
	                   {header:'奖励积分原因',dataIndex:'rpMsg',width:100,sortable:false,align:'left',
				    		editor:{
				    			allowBlank:false,
				    			blankText:'请输入积分奖励原因'
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
/*	creditTypeCombo.on('select',function(combo,records,eOpts){
		var selectionModel =  Ext.getCmp(gridId).getSelectionModel();
		var dataModel = selectionModel.getSelection()[0];
		dataModel.set('cTypeName',records[0].get("cTypeName"));
	});*/
	return pubColumns;
}

function zhxf(){
	var winAdd;
	var txCode = 30051;
	var gridId = "zhxfGridId";
	var zhjfStore = Ext.data.StoreManager.lookup('rewardGridStoreId');
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
					var keyArray = new Array();//非空键数组（json对应值非空）
					keyArray.push("userId");
					keyArray.push("rpTime");
					keyArray.push("rpPoint");
					keyArray.push("rpMsg");
					new comm().saveAll("gridStoreId","rewardGridStoreId",keyArray,txCode,winAdd,'rpManage.html');
				}
			}]
		},{
			text:'批量导入',
			iconCls:'Pageexcel',
			menu:[{
				text:'上传批量文件',
				iconCls:'Folderup',
				handler:function(){
					fileUpload("30054","上传员工奖励信息批量文件");
				}
			},{
				text:'下载批量模版文件',
				iconCls:'Packagedown',
				html:'<iframe id="myiframe"></iframe>',
				handler:function(){
//					document.location = "downLoad.html?txCode=30062";//会导致【MessagePush.onPageLoad(userId);】已注册的后台监听失效
					Ext.getDom('downloadIframe').src = "downLoad.html?tx_code=30054";
				}
			}]
		}]
	});
	winAdd = getWinAdd("添加奖励信息",zhxfGrid);
	winAdd.show();
}






	
		
		
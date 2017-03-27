//展示新增弹窗
function getGridPanel(){
	var gyZbGridStore = Ext.data.StoreManager.lookup("wdsdfpStoreId");
	setTimeout(function(){
		gyZbGridStore.reload();
	},100);
	var myColumns = [ 
	     		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
		     		    {header:'网点编号',dataIndex:'orgid',width:110,align:'left'},
		                {header:'网点名称名称',dataIndex:'orgname',width:170,align:'left',flex:1},
		                {header:'考核周期',dataIndex:'zq',width:120,align:'left'},
		                {header:'柜员可分配总包',dataIndex:'zb',width:120,align:'left'}
	                ];
	var sizeCombo = new sizeComboPrt();
	var gyZbGrid = Ext.create('Ext.grid.GridPanel',{
			id:'gyZbGridId',
			rowLines:true,
//			title:'网点手动分配绩效导入结果表',
			scroll:true,
			border:true,
//			layout:'fit',
			width:'100%',
			height:'100%',
			viewConfig:{
//				forceFit:true,
//				stripeRows:true
			},
			store:gyZbGridStore,
			/*features:[{
				ftype:'summary'
			},{
				ftype:'summary'
			}],*/
			columns:myColumns,
			bbar:[{//分页（dockedItems显示在表格顶端）
				id:'gyZbGridPBar',
				xtype:'pagingtoolbar',
				store:gyZbGridStore,
				displayInfo:true,
				border:false,
				items:['每页显示',sizeCombo,'条']
			}]
		});
	sizeCombo.on('select',function(combo){
		var pageSize = parseInt(combo.getValue());
		var gyZbGridPBar = Ext.getCmp('gyZbGridPBar');
		gyZbGridPBar.pageSize = pageSize;
		gyZbGridStore.pageSize = pageSize;
		gyZbGridStore.loadPage(1);
	});
	return gyZbGrid;
}

function showAdd(){
	var gyZbGrid = getGridPanel();
	winAdd = Ext.create('win',{
		title:'网点当期可分配柜员总包',
		width:600,
		height:300,
		items:[gyZbGrid]
	});
	winAdd.show();
}
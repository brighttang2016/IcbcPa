var pageSize = 10;
Ext.regModel('tableModel',{
	fields:['userid','name','orgid','depid','orgname','depname','zq',
	        'jx_ywl_zh','jx_bzcp_zh','jx_tscp_zh','jx_dx_zh',
	        'jx_ywl_wd','jx_bzcp_wd','jx_tscp_wd','jx_dx_wd',
	        'zsf_ywl_zh','zsf_bzcp_zh','zsf_tscp_zh','zsf_dx_zh',
	        'zsf_ywl_wd','zsf_bzcp_wd','zsf_tscp_wd','zsf_dx_wd']
});

var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
//	autoLoad:{start:0,limit:pageSize},
	autoLoad:false,
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSize,
	proxy:{
		type:'ajax',
		url:'jxQueryControler.html?tx_code=10000080',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});
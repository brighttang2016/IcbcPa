var pageSize = 10;

Ext.regModel('tableModel',{
	fields:['userid','name','orgid','depid','orgname','depname',
	        'jx_ywl','jx_bzcp','jx_zhts','jx_dx',
	        'zsf_ywl','zsf_bzcp','zsf_zhts','zsf_dx','zq']
});

var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
//	autoLoad:{start:0,limit:pageSize},
	autoLoad:true,
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSize,
	proxy:{
		type:'ajax',
		url:'calcControler.html?txCode=10000051',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

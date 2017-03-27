var pageSize = 10;
Ext.regModel('tableModel',{
	fields:['userid','name','orgid','depid','orgname','depname','zq',
	        'jx_gwlz','jx_wdzykh','jx_qyyxjl','jx_wdsdfp','jx_sum','jbjx_pname']
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
		url:'jxQueryControler.html',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});
tableStore.on('beforeload',function(){
	Ext.apply(tableStore.proxy.extraParams,{
		tranData:Ext.encode({"tx_code":"20000011","userIdQuery":"","depId":"","orgId":""})
	});
});
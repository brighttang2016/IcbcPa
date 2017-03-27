var pageSize = 10;
/*var pageSizePointInfo = 5;
var pageSizeWglHis = 5;
var pageSizeJblHis = 5;
var pageSizeXf = 50;
var pageSizeUserCt = 10;*/


Ext.regModel('zbModel',{
	fields:['orgid','orgname','zq','zb_ywl','zb_bzcp','zb_zhts','zb_dx']
});
Ext.regModel('tableModel',{
	fields:['orgid','orgname','zq','zb_gylz','zb_khjllz','zb_qyyx']
});
var zbStore = Ext.create('Ext.data.Store',{
	storeId:'zbStoreId',
//	autoLoad:{start:0,limit:pageSize},
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
		tranData:Ext.encode({"tx_code":"20000004","userIdQuery":"","depId":"","orgId":""})
	});
});
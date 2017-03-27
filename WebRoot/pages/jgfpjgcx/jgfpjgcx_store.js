var pageSize = 10;

Ext.regModel('tableModel',{
	fields:['orgid','orgname','zq','jbjx_pid','jbjx_id','rs_gw','rs_kh','rs_kh_zh','jbjx_name','jbjx_pname',
	        'zb_kh','zb_sy','zb_fp']
});

var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
	autoLoad:{start:0,limit:pageSize},
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSize,
	proxy:{
		type:'ajax',
		url:'initDataControler.html?tx_code=10000054',
        /*params:{
        	tx_code:'30069'
        },*/
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

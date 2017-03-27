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
	fields:['orgid','orgname','zq',
	        'bl_zhgy_ywl','bl_zhgy_bzcp','bl_zhgy_zhts','bl_zhgy_dx','bl_zhkhjl_bzcp','bl_zhkhjl_zhts','bl_zhkhjl_dx',
	        'bl_wdgy_ywl','bl_wdgy_bzcp','bl_wdgy_zhts','bl_wdgy_dx','bl_wdkhjl_bzcp','bl_wdkhjl_zhts','bl_wdkhjl_dx']
});


/*//岗位类别store
var jobStore = Ext.create('Ext.data.Store', {
	storeId:'jobStoreId',
	autoLoad: false,
    fields: ['jobid', 'jobname'],
    proxy:{
    	type:'ajax',
    	url:'jobQuery.html',
    	reader:new Ext.data.JsonReader({
    		root:'rows'
    	})
    },
    data : [
        {"menuId":"0", "menuName":"岗位1"},
        {"menuId":"1", "menuName":"岗位2"}
    ]
	});
*/
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
		url:'initDataControler.html?tx_code=10000069',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});
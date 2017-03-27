var pageSize = 10;

Ext.regModel('tableModel',{
	fields:[{name:'orgId',mapping:'orgid'},{name:'orgName',mapping:'orgname'},
	        {name:'zbInit',mapping:'zb_init'},
	        {name:'blMova',mapping:'bl_mova'},
	        {name:'xsGy',mapping:'xs_gy'},
	        {name:'xsKhjl',mapping:'xs_khjl'},
	        
	        {name:'xsblDg',mapping:'xsbl_dg'},
	        {name:'xsblGg',mapping:'xsbl_gg'},
	        {name:'xsblDgKhjl',mapping:'xsbl_dgkhjl'},
	        {name:'xsblXsKhjl',mapping:'xsbl_xskhjl'},
	        {name:'xsblDtKhjl',mapping:'xsbl_dtkhjl'},
	        
	        {name:'operTime',mapping:'oper_time'},
	        'zq','note1','note2','note3','note4','note5','note6']
});

var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
	autoLoad:{start:0,limit:pageSize},
//	autoLoad:false,
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSize,
	proxy:{
		type:'ajax',
		url:'initDataControler.html?tx_code=10000053',
        /*params:{
        	tx_code:'30069'
        },*/
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

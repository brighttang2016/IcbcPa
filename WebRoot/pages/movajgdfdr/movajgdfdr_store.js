var pageSize = 10;

Ext.regModel('tableModel',{
	fields:[{name:'orgId',mapping:'orgid'},{name:'orgName',mapping:'orgname'},
	        {name:'ptJyzh',mapping:'pt_jyzh'},
	        {name:'ptZdcp',mapping:'pt_zdcp'},
	        {name:'ptMova',mapping:'pt_mova'},
	        {name:'operTime',mapping:'oper_time'},
	        'zq','note1','note2','note3','note4','note5','note6']
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
		url:'initDataControler.html?tx_code=10000056',
        /*params:{
        	tx_code:'30069'
        },*/
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

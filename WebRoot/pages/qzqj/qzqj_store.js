
var pageSizeTable = 10;

Ext.regModel('zhjfModel',{
	fields:['userId','userName']
});

//总行基础学分store
var zhjfStore = Ext.create('Ext.data.Store', {
    storeId:'zhjfStoreId',
    model:zhjfModel,
    data:[]
});


Ext.regModel('tableModel',{
			fields:[{name:'orgId',mapping:'orgid'},{name:'orgName',mapping:'orgname'},
			        'jbjx_pid','khfw','ywl_xx','ywl_sx','bzcp_xx','bzcp_sx',
			        'zhts_xx','zhts_sx','dx_xx','dx_sx']
		});
var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
	autoLoad:{start:0,limit:pageSizeTable},
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSizeTable,
	proxy:{
		type:'ajax',
		url:'param_qzqj.html?tx_code=10000076',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});
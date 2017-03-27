
var pageSize = 10;

Ext.regModel('tableModel',{
			fields:[{name:'assptId',mapping:'asspt_id'},
			        {name:'assptName',mapping:'asspt_name'},
			        'point','note','cond']
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
		url:'assptManage.html?tx_code=30042',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});
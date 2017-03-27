
var pageSize = 10;

Ext.regModel('tableModel',{
			fields:[{name:'ssId',mapping:'ss_id'},
			        {name:'yearStart',mapping:'year_start'},
			        {name:'yearEnd',mapping:'year_end'},
			        'subsity','note']
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
		url:'ssManage.html?tx_code=30024',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

var pageSizeTable = 10;

Ext.regModel('tableModel',{
			fields:['bl_id','nr_id','nr_name','min','max']
		});
var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
//	autoLoad:{start:0,limit:pageSizeTable},
	autoLoad:false,
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSizeTable,
	proxy:{
		type:'ajax',
		url:'zbzbManage.html?tx_code=20000016',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

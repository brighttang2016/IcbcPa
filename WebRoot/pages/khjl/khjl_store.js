
var pageSize = 10;
Ext.regModel('assComboModel',{
	fields:[{name:'assptId',mapping:'asspt_id'},
	        {name:'assptName',mapping:'asspt_name'}]
});
/*Ext.regModel('userComboModel',{
	fields:[{name:'userId',mapping:'user_id'},
	        {name:'userName',mapping:'user_name'}]
});*/
Ext.regModel('tableModel',{
			fields:[{name:'userId',mapping:'user_id'},
			        {name:'userName',mapping:'name'},
			        {name:'rpTime',mapping:'rp_time'},
			        {name:'assptTime',mapping:'asspt_time'},
			        {name:'rpPoint',mapping:'rp_point'},
			        {name:'rpMsg',mapping:'rp_msg'}]
		});
Ext.regModel('rewardGridModel',{
	fields:[{name:'userId',mapping:'user_id'},
	        {name:'userName',mapping:'user_name'},
	        {name:'rpTime',mapping:'rp_time'},
	        {name:'assptId',mapping:'asspt_id'},
	        {name:'rpPoint',mapping:'rp_point'},
	        {name:'rpMsg',mapping:'rp_msg'}]
});

var assComboStore = Ext.create('Ext.data.Store',{
	storeId:'assComboStoreId',
	autoLoad:false,
	model:'assComboModel',
	proxy:{
		type:'ajax',
		url:'assptManage.html?tx_code=30042',
		reader:new Ext.data.JsonReader({
//			type:'json',
			root:'rows'
		})
	}
});


var rewardGridStore = Ext.create('Ext.data.Store',{
	storeId:'rewardGridStoreId',
	autoLoad:false,
	model:rewardGridModel,
	data : []
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
		url:'rpManage.html?tx_code=30053',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});
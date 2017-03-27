
var pageSizeTable = 10;

Ext.regModel('editGridModel',{
	fields:['seq_num','zb_name','point','org_point']
});

//总行基础学分store
var editGridStore = Ext.create('Ext.data.Store', {
    storeId:'editGridStoreId',
    model:editGridModel,
    data:[]
});

Ext.regModel('tableModel',{
			fields:['orgid','orgname','org_point','seq_num','zb_name','unit','point']
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
		url:'zbfwControler.html?tx_code=30154',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

/*var roleStore = Ext.create('Ext.data.ArrayStore', {
//    fields: ['value','text'],
	fields:['role_id','role_name'],
    proxy: {
        type: 'ajax',
//        url: 'ItemSeletorServlet',
//        url:'Role_roleAllQuery.action',
        url:'roleRouter.html',
//        reader: 'array'
        reader:new Ext.data.JsonReader({
        	root:'rows'
        })
    },
    autoLoad: true,
    sortInfo: {
        field: 'value',
        direction: 'ASC'
    }
});*/
/*
roleStore.on('beforeLoad',function(){
	Ext.apply(roleStore.proxy.extraParams,{
		role_name:'',
		role_desc:'',
		tx_code:'10024'
	});
});*/
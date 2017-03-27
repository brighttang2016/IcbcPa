var pageSize = 10;
var pageSizePointInfo = 5;
var pageSizeWglHis = 5;
var pageSizeJblHis = 5;
var pageSizeXf = 50;
var pageSizeUserCt = 10;


Ext.regModel('tableModel',{
	fields:[{name:'orgId',mapping:'orgid'},{name:'orgName',mapping:'orgname'},
	        {name:'deptId',mapping:'depid'},{name:'depName',mapping:'depname'},
	        {name:'userId',mapping:'userid'},'name','sex',{name:'propertyId',mapping:'property'},'mail','stat',
	        {name:'beginDate',mapping:'begindate'},'roles',{name:'jbId',mapping:'jb_id'},
	        {name:'attStat',mapping:'attstat'},{name:'jbName',mapping:'jbname'},
	        {name:'propertyName',mapping:'propertyname'}]
});


//岗位类别store
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

//人员类别store
var propertyStore = Ext.create('Ext.data.Store', {
	storeId:'propertyStoreId',
	autoLoad: false,
	fields: ['propertyid', 'propertyname'],
    proxy:{
    	type:'ajax',
    	url:'propertyQuery.html',
    	reader:new Ext.data.JsonReader({
    		root:'rows'
    	})
    },
    data : [
        {"menuId":"0", "menuName":"人员类别1"},
        {"menuId":"1", "menuName":"人员类别2"}
    ]
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
		url:'userManage.html?tx_code1111111=10014',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

var roleStore = Ext.create('Ext.data.ArrayStore', {
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
});

roleStore.on('beforeLoad',function(){
	Ext.apply(roleStore.proxy.extraParams,{
		role_name:'',
		role_desc:'',
		tx_code:'10024'
	});
});
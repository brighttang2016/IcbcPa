
var pageSizeTable = 10;

Ext.regModel('editGridModel',{
	fields:['userId','ctId']
});

/*//岗位model
Ext.regModel('jobModel',{
	fields:[
	        {name:'id',mapping:'menuid'},
	        {name:'menuId',mapping:'menuid'},
	        {name:'menuName',mapping:'menuname'},
	        {name:'pId',mapping:'pid'},
	        {name:'pName',mapping:'pname'},
	        {name:'ctId',mapping:'ct_id'},
	        {name:'dtId',mapping:'dt_id'},
	        {name:'mGrade',mapping:'m_grade'},
	        {name:'sGrade',mapping:'s_grade'},
	        {name:'sLevel',mapping:'s_level'},
	        {name:'seqNum',mapping:'seq_num'},'leaftag'
	]
});*/


//总行基础学分store
var editGridStore = Ext.create('Ext.data.Store', {
    storeId:'editGridStoreId',
    model:editGridModel,
    data:[]
});

//岗位tree store
/*var jobTreeStore = Ext.create('Ext.data.TreeStore',{
	storeId:'jobTreeStoreId',
	model:'jobModel',
	nodeParam:'menuTreeId',
	proxy:{
		type:'ajax',
		url: 'jobManage.html?tx_code=30034',
		reader:new Ext.data.JsonReader({
			root:'rows'
		})
//				reader:'json'//reader必须为json
	},
	folderSort: true,
	autoLoad:false,
	root:{
		name:'根节点',
		id:'-1',
		expanded: true//默认展开根节点
	}
});*/


//岗位类别store

/*var jobStore = Ext.create('Ext.data.Store', {
			autoLoad: true,
		    fields: ['jbid', 'jbname'],
		    proxy:{
		    	type:'ajax',
		    	url:'jobQuery.html',
		    	reader:new Ext.data.JsonReader({
		    		root:'rows'
		    	})
		    }
		    data : [
		        {"menuId":"0", "menuName":"岗位1"},
		        {"menuId":"1", "menuName":"岗位2"}
		    ]
		});*/

//人员类别store
/*var propertyStore = Ext.create('Ext.data.Store', {
			autoLoad: true,
			fields: ['propertyid', 'propertyname'],
		    proxy:{
		    	type:'ajax',
		    	url:'propertyQuery.html',
		    	reader:new Ext.data.JsonReader({
		    		root:'rows'
		    	})
		    }
		});*/


Ext.regModel('tableModel',{
			fields:[{name:'orgId',mapping:'orgid'},{name:'orgName',mapping:'orgname'},
			        {name:'deptId',mapping:'depid'},{name:'depName',mapping:'depname'},
			        {name:'userId',mapping:'userid'},'name',
			        {name:'ctId',mapping:'ct_id'},{name:'ctName',mapping:'ct_name'},
			        {name:'operTime',mapping:'oper_time'}]
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
		url:'uctManage.html?tx_code=30093',
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
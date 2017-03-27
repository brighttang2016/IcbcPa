var tranData = {"tx_code":"10014","userIdQuery":"","depId":"","orgId":""};//查询用户信息交易数据
var pageSizeTable = 10;

//岗位model
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
});


//岗位层级tree store
var jobTreeStore = Ext.create('Ext.data.TreeStore',{
	storeId:'jobTreeStoreId',
	model:'jobModel',
	nodeParam:'menuTreeId',
	proxy:{
		type:'ajax',
//		url: 'jobManage.html?tx_code=10044',
		url: 'jobQuery.html?tx_code=00000000', //2016-01-26
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
});

//岗位类别store
var jbCatStore = Ext.create('Ext.data.Store', {
	autoLoad: false,
    fields: ['jbcatid', 'jbcatname'],
    proxy:{
    	type:'ajax',
    	url:'jbCatQuery.html',
    	reader:new Ext.data.JsonReader({
    		root:'rows'
    	})
    }
});

//岗位类别store
var jobStore = Ext.create('Ext.data.Store', {
	autoLoad: true,
    fields: ['jbid', 'jbname'],
    proxy:{
    	type:'ajax',
    	url:'jobQuery.html',
    	reader:new Ext.data.JsonReader({
    		root:'rows'
    	})
    }
});

//人员性质store
var propertyStore = Ext.create('Ext.data.Store', {
			autoLoad: false,
			fields: ['propertyid', 'propertyname'],
		    proxy:{
		    	type:'ajax',
		    	url:'propertyQuery.html',
		    	reader:new Ext.data.JsonReader({
		    		root:'rows'
		    	})
		    }
		    /*data : [
		        {"menuId":"0", "menuName":"人员类别1"},
		        {"menuId":"1", "menuName":"人员类别2"}
		    ]*/
		});


Ext.regModel('tableModel',{
			fields:[{name:'orgId',mapping:'orgid'},{name:'orgName',mapping:'orgname'},
			        {name:'deptId',mapping:'depid'},{name:'depName',mapping:'depname'},
			        {name:'userId',mapping:'userid'},'name','sex',{name:'propertyId',mapping:'property'},'mail','stat',
			        {name:'beginDate',mapping:'begindate'},'roles',
			        {name:'jbId',mapping:'jb_id'},{name:'jbjxId',mapping:'jbjx_id'},{name:'jbjxPid',mapping:'jbjx_pid'},
			        {name:'jbName',mapping:'jbname'},{name:'jbjxName',mapping:'jbjx_name'},{name:'jbjxPname',mapping:'jbjx_pname'},
			        {name:'attStat',mapping:'attstat'},
			        {name:'propertyName',mapping:'propertyname'},{name:'jbCat',mapping:'jb_cat'}]
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
		url:'userManage.html',
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
		
});
tableStore.on('beforeload',function(){
	Ext.apply(tableStore.proxy.extraParams,{
		tranData:Ext.encode({"tx_code":"10014","userIdQuery":"","depId":"","orgId":""})
	});
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
    autoLoad: false,
    sortInfo: {
        field: 'value',
        direction: 'ASC'
    }
});

roleStore.on('beforeLoad',function(){
	Ext.apply(roleStore.proxy.extraParams,{
		tranData:Ext.encode({role_name:'',role_desc:'',tx_code:'10024',auth_check:'0'})
	});
});
//工资档次model
Ext.define('wlComboModel',{
	extend:'Ext.data.Model',
	fields:['wl_id','wl_name']
});
//工资等级model
Ext.define('wgComboModel',{
	extend:'Ext.data.Model',
	fields:['wg_value','wg_name']
});
//职务层级model
Ext.define('dtComboModel', {
    extend: 'Ext.data.Model',
    fields: ['dt_id', 'dt_name']
});
//认证资格model
Ext.define('ctComboModel', {
    extend: 'Ext.data.Model',
    fields: ['ct_id', 'ct_name']
});
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

//工资档次下拉框store
var wlComboStore = Ext.create('Ext.data.Store',{
	storeId:'wlComboStoreId',
	autoLoad:false,
	model:'wlComboModel',
	proxy:{
		type:'ajax',
		url:'wlQuery.html',
		reader:{
			type:'json',
			root:'rows'
		}
	}
});

//工资等级下拉框store
var wgComboStore = Ext.create('Ext.data.Store',{
	storeId:'wgComboStoreId',
	autoLoad:false,
	model:'wgComboModel',
	proxy:{
		type:'ajax',
		url:'wgQuery.html',
		reader:{
			type:'json',
			root:'rows'
		}
	}
});

//职务层级store
var dtComboStore = Ext.create('Ext.data.Store',{
	storeId:'dtComboStoreId',
	autoLoad:false,
	model:'dtComboModel',//将数据源写入到此处配置的model之中
	proxy:{
		type:'ajax',
		url:'dtQuery.html',
		reader:{
			type:'json',
			root:'rows'
		}
	}
});

//认证资格store
var ctComboStore = Ext.create('Ext.data.Store',{
	storeId:'ctComboStoreId',
	autoLoad:false,
	model:'ctComboModel',//将数据源写入到此处配置的model之中
	proxy:{
		type:'ajax',
		url:'ctQuery.html',
		reader:{
			type:'json',//不指定貌似也不会报错
			//model:'BookInfo2',//此处model可以不要，加上有和作用？？？？2014-12-13,官方api中没有此项，网上的都是坑爷的。。。。
			root:'rows'//指定根节点名，当后台返回的是json字符串是，必须指定数据的root，方能使得model获得后台数据（此乃关键）
		}
	}
});

//岗位tree store
var jobTreeStore = Ext.create('Ext.data.TreeStore',{
	storeId:'jobTreeStoreId',
	model:'jobModel',
	nodeParam:'menuTreeId',
	proxy:{
		type:'ajax',
		url: 'jobManage.html?tx_code=00000000',
		reader:new Ext.data.JsonReader({
			root:'rows'
		}),
//				reader:'json'//reader必须为json
	},
	folderSort: true,
	autoLoad:true,
	root:{
		name:'根节点',
		id:'-1',
		expanded: true//默认展开根节点
	}
});

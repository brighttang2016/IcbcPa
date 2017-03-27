
//认证资格model
Ext.define('ctComboModel', {
    extend: 'Ext.data.Model',
    fields: ['ct_id', 'ct_name']
});
Ext.regModel('orgInfo',{
	fields:[
	        {name:'id',mapping:'menuid'},
	        {name:'menuId',mapping:'menuid'},
	        {name:'menuName',mapping:'menuname'},
	        {name:'pId',mapping:'pid'},
	        {name:'pName',mapping:'pname'},
	        {name:'menuType',mapping:'menutype'},'leaftag'
	]
});

Ext.regModel('zbComboModel',{
//	fields:[{name:'seqNum',mapping:'seq_num'},{name:'zbName',mapping:'zb_name'},'point','unit']
	fields:['seq_num','zb_name','point','unit']
});

Ext.regModel('userComboModel',{
	fields:[{name:'userId',mapping:'user_id'},
	        {name:'userName',mapping:'user_name'}]
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

var tscpComboStore = Ext.create('Ext.data.Store',{
	storeId:'tscpComboStoreId',
	autoLoad:false,
	model:'zbComboModel',
	proxy:{
		type:'ajax',
		url:'zbComboControler.html?index=2',//查询所有特色产品指标
		reader:new Ext.data.JsonReader({
//			type:'json',
			root:'rows'
		})
	}
});

var bzcpComboStore = Ext.create('Ext.data.Store',{
	storeId:'bzcpComboStoreId',
	autoLoad:false,
	model:'zbComboModel',
	proxy:{
		type:'ajax',
		url:'zbComboControler.html?index=1',//查询所有标准指标
		reader:new Ext.data.JsonReader({
//			type:'json',
			root:'rows'
		})
	}
});

//用户选择
var userComboStore = Ext.create('Ext.data.Store',{
	storeId:'userComboStoreId',
	autoLoad:false,
	model:'userComboModel',
	proxy:{
		type:'ajax',
		url:'userQuery.html',
		reader:new Ext.data.JsonReader({
//			type:'json',
			root:'rows'
		})
	}
});

//机构部门store,点击上级，查询下级（jg.js使用）（查询区）
var tree_store = Ext.create('Ext.data.TreeStore',{
	storeId:'treeStoreId',
	model:'orgInfo',
	nodeParam:'menuTreeId',
	proxy:{
		type:'ajax',
//		url: 'orgManage.html?tx_code=10034',
		url: 'orgTreeQuery.html',
		
		reader:new Ext.data.JsonReader({
			root:'rows'
		})
//				reader:'json'//reader必须为json
	},
	folderSort: true,
	autoLoad:false/*,//如下root属性配置，会使得autoLoad:false失效
	root:{
		name:'根节点',
		id:'-1',
		expanded: true//默认展开根节点
	}*/
});
//机构部门store,点击上级，查询下级（jg.js使用）（用户更行弹窗）
var tree_store_upd = Ext.create('Ext.data.TreeStore',{
	storeId:'treeStoreUpdId',
	model:'orgInfo',
	nodeParam:'menuTreeId',
	proxy:{
		type:'ajax',
		url: 'orgManage.html?tx_code=10034',
		reader:new Ext.data.JsonReader({
			root:'rows'
		})
//				reader:'json'//reader必须为json
	},
	folderSort: true,
	autoLoad:false/*,
	root:{
		name:'根节点',
		id:'-1',
		expanded: true//默认展开根节点
	}*/
});

tree_store.on('beforeLoad',function(){
	//alert(menuName);
	Ext.apply(tree_store.proxy.extraParams,{
		leafTag:leafTag,
		pId:pId,
		pName:pName,
		menuId:menuId,
		menuType:menuType,
		menuName:menuName
	});
});
tree_store_upd.on('beforeLoad',function(){
	//alert(menuName);
	Ext.apply(tree_store_upd.proxy.extraParams,{
		leafTag:leafTag,
		pId:pId,
		pName:pName,
		menuId:menuId,
		menuType:menuType,
		menuName:menuName
	});
});



/*yh.js使用(查询所有机构部门信息)*/
var tree_storeall = Ext.create('Ext.data.TreeStore',{
	storeId:'tree_storeall',
	model:'orgInfo',
	nodeParam:'menuTreeId',
	proxy:{
		type:'ajax',
		url: 'getAllOrg.html',
//		url: 'orgManage.html?tx_code=10034',
		reader:new Ext.data.JsonReader({
			root:'rows'
		})
//				reader:'json'//reader必须为json
	},
	folderSort: true,
	autoLoad:false/*,
	root:{
		name:'根节点',
		id:'-1',
		expanded: true//默认展开根节点
	}*/
});
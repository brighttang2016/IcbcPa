var pageSize = 10;
var pageSizePointInfo = 5;
var pageSizeWglHis = 5;
var pageSizeJblHis = 5;
/*Ext.regModel('jbHisModel',{
	fields:[{name:'userId',mapping:'userid'},{name:'chgTime',mapping:'chg_time'},{name:'userName',mapping:'name'},{name:"jbName",mapping:"jb_name"}]
});
Ext.regModel('wglHisModel',{
	fields:[{name:'userId',mapping:'user_id'},{name:'chgTime',mapping:'chg_time'},{name:'wgName',mapping:'wg_name'},{name:'wlName',mapping:'wl_name'},{name:'userName',mapping:'name'}]
});
Ext.regModel('pointInfoModel',{
	fields:[{name:'userId',mapping:'user_id'},{name:'chgTime',mapping:'chg_time'},'point','type',{name:'userName',mapping:'user_name'}]
});*/
Ext.regModel('cTypeModel',{
	fields:[{name:'cTypeId',mapping:'ctype_id'},
	        {name:'cTypeName',mapping:'ctype_name'}]
});
Ext.regModel('zhjfModel',{
	fields:['userId','userName','cTypeId','cTypeName','point']
});
Ext.regModel('tableModel',{
	fields:['orgid','orgname',
	        'depid','depname','userid'
	        ,'username','col1','col2','col3','col4','col5','col6','col7','col8','col9','col10','col11'
	        ,'col12','col13','col14','col15','col16','col17','col18','col18','zq','point']
});
var cTypeStore = Ext.create('Ext.data.Store',{
	storeId:'cTypeStoreId',
	model:cTypeModel,
	autoLoad:false,
	proxy:{
		type:'ajax',
		url:'cTypeQuery.html',
		reader:Ext.create('Ext.data.JsonReader',{
			root:'rows'
		})
	}
});
//手工调整学分store
var sgxfStore = Ext.create('Ext.data.Store', {
    storeId:'sgxfStoreId',
    model:zhjfModel,
    data:[]
});
//附加学分store
var fjxfStore = Ext.create('Ext.data.Store', {
    storeId:'fjxfStoreId',
    model:zhjfModel,
    data:[]
});
//市行基础学分store
var shxfStore = Ext.create('Ext.data.Store', {
    storeId:'shxfStoreId',
    model:zhjfModel,
    data:[]
});
//总行基础学分store
var zhjfStore = Ext.create('Ext.data.Store', {
    storeId:'zhjfStoreId',
    model:zhjfModel,
    data:[]
});

var tableStore = Ext.create('Ext.data.Store',{
	storeId:'gridStoreId',
	autoLoad:{start:0,limit:pageSize},
//	autoLoad:false,
	model:'tableModel',
	width:'100%',
	height:'100%',
	pageSize:pageSize,
	proxy:{
		type:'ajax',
		url:'initDataControler.html?tx_code=30014',
        /*params:{
        	tx_code:'30069'
        },*/
		reader:new Ext.data.JsonReader({
			root:'rows',
			totalProperty:'count'//映射json返回报文中的results属性，作为总记录数，
		})
	}
});

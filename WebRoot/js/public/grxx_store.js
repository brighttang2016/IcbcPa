/**
 * 个人信息store
 */

var pageSizePointInfo = 10;
var pageSizeWglHis = 10;
var pageSizeJblHis = 10;
var pageSizeXf = 50;
var pageSizeUserCt = 10;
Ext.define('userCtModel',{
	extend: 'Ext.data.Model',
    idProperty: 'userCtModelId',
    fields: [
        {name: 'ct_id', type: 'string'},
        {name: 'ct_name', type: 'string'},
        {name: 'oper_time', type: 'string'}
    ]
});
Ext.define('Task', {
    extend: 'Ext.data.Model',
    idProperty: 'taskId',
    fields: [
        {name: 'group_id', type: 'int'},
        {name: 'group_name', type: 'string'},
        {name: 'ctype_id', type: 'string'},
        {name: 'ctype_name', type: 'string'},
        {name: 'col_name', type: 'float'},
        {name: 'point_year', type: 'string'},
        {name: 'oper_date', type: 'date', dateFormat:'Ymd'},
        {name: 'point', type: 'int'},
        {name:'user_id',mapping:'userid',type:'string'},
        {name:'name',type:'string'}
    ]
});

Ext.regModel('jbHisModel',{
	fields:[{name:'userId',mapping:'userid'},{name:'chgTime',mapping:'chg_time'},{name:'userName',mapping:'name'},{name:"jbName",mapping:"jb_name"}]
});
Ext.regModel('wglHisModel',{
	fields:[{name:'userId',mapping:'user_id'},{name:'chgTime',mapping:'chg_time'},{name:'wgName',mapping:'wg_name'},{name:'wlName',mapping:'wl_name'},{name:'userName',mapping:'name'}]
});
Ext.regModel('pointInfoModel',{
	fields:[{name:'userId',mapping:'user_id'},{name:'chgTime',mapping:'chg_time'},'point','type',{name:'userName',mapping:'user_name'}]
});


//用户已获得资格
var userCtStore = Ext.create('Ext.data.Store', {
    storeId:'userCtStoreId',
    model:userCtModel,
    autoLoad:false,
    pageSize:pageSizeUserCt,
    proxy: {
        type: 'ajax',
        url:'userCtQuery.html',
        reader:Ext.create('Ext.data.JsonReader',{
        	root:'rows'
        })
        /*reader:new Ext.data.JsonReader({
        	root:'rows',
        	totalProperty:'count'
        })*/
    }
});

//学分store
var xfStore = Ext.create('Ext.data.Store', {
	storeId:'xfStoreId',
	pageSize:pageSizeXf,
    model: 'Task',
    //data: data,
    autoLoad:false,
    sorters: {property: 'due', direction: 'ASC'},
    groupField:'group_name',
   proxy:{
    	type:'ajax',
    	url:'userCreditQuery.html',
    	//reader:Ext.create('Ext.data.JsonReader',{
    	 reader:new Ext.data.JsonReader({
    		root:'rows'
    	})
    }
});

//历年岗位变更信息
var jsHisStore = Ext.create('Ext.data.Store', {
    storeId:'jsHisStoreId',
    model:jbHisModel,
    autoLoad:false,
    pageSize:pageSizeJblHis,
    proxy: {
        type: 'ajax',
        url:'jbHisQuery.html',
        reader:new Ext.data.JsonReader({
        	root:'rows',
        	totalProperty:'count'
        })
    }
});
//历年工资登记档次变更信息
var wglHisStore = Ext.create('Ext.data.Store', {
    storeId:'wglHisStoreId',
    model:wglHisModel,
//    autoLoad:{start:0,limit:pageSizePointInfo},
    autoLoad:false,
    pageSize:pageSizeWglHis,
    proxy: {
        type: 'ajax',
        url:'wglHisQuery.html',
        reader:new Ext.data.JsonReader({
        	root:'rows',
        	totalProperty:'count'
        })
    }
});


//积分历史信息
var pointInfoStore = Ext.create('Ext.data.Store', {
    storeId:'pointInfoStoreId',
    model:pointInfoModel,
//    autoLoad:{start:0,limit:pageSizePointInfo},
    autoLoad:false,
    pageSize:pageSizePointInfo,
    proxy: {
        type: 'ajax',
        url:'pointInfoHisQuery.html',
        reader:new Ext.data.JsonReader({
        	root:'rows',
        	totalProperty:'count'
        })
    }
});





var states = Ext.create('Ext.data.Store', {
		    fields: ['menuId', 'menuName'],
		    data : [
		        {"menuId":"100", "menuName":"添加机构"},
		        {"menuId":"200", "menuName":"添加部门"}
		    ]
		});

Ext.define("creditTypeCombo",{
	extend:'Ext.form.ComboBox',
//	fieldLabel:'机构部门',
//    forceSelection:true,
    emptyText:'选择学分维护类型',
    name:'orgDeptComboQuery',
//    id:'orgInfoQuery',
    margin:'0 0 4 0 ',
    store: Ext.data.StoreManager.lookup('cTypeStoreId'),
    queryMode:'remote',
    displayField:'cTypeName',
    valueField:'cTypeId',
    renderTo:Ext.getBody(),
//    width:350,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'选择学分维护类型',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false
});

Ext.define('planDataModel',{
	extend:'Ext.data.Model',
	fields:[{name:'userId',type:'string'},{name:'ctId',type:'string'}]
});

/*****************查询*************/
/*Ext.define("orgDeptComboQuery",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'机构部门',
//    forceSelection:true,
    emptyText:'选择机构部门',
    name:'orgDeptComboQuery',
//    id:'orgInfoQuery',
    margin:'0 0 4 0 ',
    store: states,
    queryMode:'local',
    displayField:'menuName',
    valueField:'menuId',
    renderTo:Ext.getBody(),
    width:350,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'请选择机构部门',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false,
    tpl: "<div style='height:200px;border:1px solid #ffffff' id='treeId'></div>"
});*/




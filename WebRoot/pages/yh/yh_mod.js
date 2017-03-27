/*Ext.require([
 	'pages/yh/yh_store'
]);*/




//岗位类别comboBox
Ext.define("jobCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'岗位层级',
//    forceSelection:true,
    emptyText:'选择岗位层级',
    name:'jobId',
    margin:'0 0 4 0 ',
    store: jobStore,
    queryMode:'local',
    displayField:'jbname',
    valueField:'jbid',
    renderTo:Ext.getBody(),
    width:280,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'请选择岗位层级',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false
});


//岗位类别comboBox
Ext.define("jbCatCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'岗位类别',
//    forceSelection:true,
    emptyText:'选择岗位类别',
    name:'propertyId',
    margin:'0 0 4 0 ',
    store: jbCatStore,
    queryMode:'local',
    displayField:'jbcatname',
    valueField:'jbcatid',
    renderTo:Ext.getBody(),
    width:280,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'请选择岗位类别',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false
});

//人员性质comboBox
Ext.define("propertyCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'人员类别',
//    forceSelection:true,
    emptyText:'选择人员类别',
    name:'propertyId',
    margin:'0 0 4 0 ',
    store: propertyStore,
    queryMode:'local',
    displayField:'propertyname',
    valueField:'propertyid',
    renderTo:Ext.getBody(),
    width:280,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'请选择人员类别',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false
});


var states = Ext.create('Ext.data.Store', {
		    fields: ['menuId', 'menuName'],
		    data : [
		        {"menuId":"100", "menuName":"添加机构"},
		        {"menuId":"200", "menuName":"添加部门"}
		    ]
		});



//岗位层级（更新）
Ext.define("jobComboUpd",{
	extend:'Ext.form.ComboBox',
    fieldLabel:'岗位层级',
//    forceSelection:true,
//    id:'orgInfoUpd',
    emptyText:'选择岗位层级',
//    name:'jobComboUpd',
//    id:'orgInfoUpd',
    margin:'0 0 4 0 ',
    border:5,
//    store: states,
    queryMode:'local',
    displayField:'menuName',
    valueField:'menuId',
    renderTo:Ext.getBody(),
    width:280,
//    width:350,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:false,
    blankText:'请选择岗位层级',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false,
    tpl: "<div style='height:200px;border:1px solid #ffffff' id='jobTreeDiv'></div>",
    listConfig:{
    	minWidth:280
    }
});

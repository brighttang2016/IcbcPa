// The data store containing the list of states


Ext.define('planDataModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'userId', type: 'string'},
            {name: 'userName', type: 'string'},
            {name: 'rpTime',type:'string'},
            {name: 'assptId',type:'string'},
            {name: 'rpPoint',type:'string'},
            {name: 'rpMsg',type:'string'}
        ]
    });

//奖励积分下拉框
var pointAA = new Array();
for(var i = 0;i <= 3;i++){
	var pointA = new Array();
	pointA.push(parseInt(i));
	pointA.push(parseInt(i)+"分");
	pointAA.push(pointA);
}
Ext.define('rpCombo',{
	extend:'Ext.form.field.ComboBox',
	store:pointAA,
	editable:false,
	emptyText:'请选择奖励积分'
});

//员工考核结果下拉框
Ext.define('assCombo2',{
	extend:'Ext.form.field.ComboBox',
	name:'assCombo',
	allowBlank:false,
	blankText:'请选择考核结果',
//	fieldLabel:'选择考核结果',
	emptyText:'请选择考核结果',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'all',//queryParam=all，查询当前用户所在机构所有用户
	queryParam:'userIdQuery',
	minChars:3,
	queryDelay:300,
	store:Ext.data.StoreManager.lookup('assComboStoreId'),
	displayField:'assptName',
	valueField:'assptId',
	queryMode:'remote',
	forceSelection:true,
	editable:false
});

//*********年份下拉框开始*********
var yearAA = new Array();
var yearNow = new Date().getFullYear();
var yearIndex = 5;
for (var i = 0; i <= yearIndex; i++) {
	var yearA = new Array();
	var year = parseInt(yearNow)-parseInt(i);
	yearA.push(year);
	yearA.push(year+"年");
	yearAA.push(yearA);
}
Ext.define('yearCombo',{
	extend:'Ext.form.field.ComboBox',
    store: yearAA,
//    fieldLabel:'请选择年份',
//    labelAlign:'right',
    editable:false,
    emptyText:'请选择年份'
});
//*********年份下拉框结束*********
var states = Ext.create('Ext.data.Store', {
    fields: ['value', 'name'],
    data : [
        {"value":"优秀", "name":"优秀"},
        {"value":"良好", "name":"良好"},
        {"value":"称职(合格)", "name":"称职(合格)"},
        {"value":"基本称职(基本合格)", "name":"基本称职(基本合格)"},
        {"value":"不称职(不合格)", "name":"不称职(不合格)"},
        {"value":"未考核", "name":"未考核"}
    ]
});
//当前用户所在机构所有用户
/*Ext.define('userCombo',{
	extend:'Ext.form.field.ComboBox',
	name:'userId',
	allowBlank:false,
	blankText:'请输入员工编码',
//	fieldLabel:'选择部门',
	emptyText:'请输入员工编码',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'all',//queryParam=all，查询当前用户所在机构所有用户
	queryParam:'userIdQuery',
	minChars:3,
	queryDelay:300,
	store:Ext.data.StoreManager.lookup('userComboStoreId'),
	displayField:'userName',
	valueField:'userId',
	queryMode:'remote',
	forceSelection:true
});*/
Ext.define('assCombo',{
	extend:'Ext.form.field.ComboBox',
	name:'assptName',
	fieldLabel: '考核结果',
    store: states,
    queryMode: 'local',
    displayField: 'name',
    valueField: 'value',
    renderTo: Ext.getBody(),
    autoFitErrors:false,
    width:280,
    editable:false
});
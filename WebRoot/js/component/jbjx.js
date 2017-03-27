/**
 *绩效考核岗位下拉框组件
 */
/*****************下拉框父类*************/
/*Ext.define("jbjxCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'考核周期',
//    forceSelection:true,
    emptyText:'选择考核周期',
    name:'jbjxCombo',
    margin:'0 0 4 0 ',
//    store: jbjxStore,
    queryMode:'local',
    displayField:'jbjx_name',
    valueField:'jbjx_id',
    renderTo:Ext.getBody(),
    width:280,
    labelAlign:'right',
    allowBlank:true,
    blankText:'请选择考核周期',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false,
    listConfig:{
    	minWidth:250
    }
});*/
/*Ext.define('jbjxComboQuery',{
	extend:'jbjxCombo',
	tpl: "<div style='height:296px;border:0px solid red;overflow:hidden' id='jbjxComboId'></div>",
});*/

/*//岗位类别comboBox
Ext.define("jbjxpCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'所属岗位',
//    forceSelection:true,
    emptyText:'所属岗位',
    name:'jbjxpComboName',
    margin:'0 0 4 0 ',
    store: jbjxpStore,
    queryMode:'local',
    displayField:'jbjx_pname',
    valueField:'jbjx_pid',
    renderTo:Ext.getBody(),
    width:280,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'所属岗位',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false
});*/

function jbjxComponent(){
	//获取所属绩效考核岗位下拉框组件
	this.getJbjxCombo = function(){
		Ext.regModel('jbjxModel',{
			fields:['jbjx_id','jbjx_name']
		});
		var jbjxStore = Ext.create('Ext.data.Store',{
			storeId:'jbjxStoreId',
			model:'jbjxModel',
			proxy:{
				type:'ajax',
				params:{jbjx_pid:''},
				url: 'jbjxControler.html',
				reader:new Ext.data.JsonReader({
					root:'rows'
				})
//						reader:'json'//reader必须为json
			},
			folderSort: true,
			autoLoad:false/*,
			root:{
				name:'根节点',
				id:'-1',
				expanded: true//默认展开根节点
			}*/
		});
//		var jbjxCombo = Ext.create('jbjxCombo');
		var isLoad = false;
		/*var jbjxCombo = Ext.create('Ext.form.ComboBox', {
		    fieldLabel: '绩效考核岗位',
		    store: Ext.data.StoreManager.lookup('jbjxStoreId'),
//		    queryMode: 'local',
		    name:'jbjxQuery',
		    displayField: 'jbjx_name',
		    valueField: 'jbjx_id',
		    renderTo: Ext.getBody(),
		    editable:false
		});*/
		var jbjxCombo = Ext.create('Ext.form.ComboBox', {
		    store: Ext.data.StoreManager.lookup('jbjxStoreId'),
		    fieldLabel:'绩效考核岗位',
//		    forceSelection:true,
		    emptyText:'绩效考核岗位',
		    name:'jbjxId',
		    margin:'0 0 4 0 ',
//		    store: jbjxpStore,
		    queryMode:'local',
		    displayField:'jbjx_name',
		    valueField:'jbjx_id',
//		    renderTo:Ext.getBody(),
		    width:280,
		    //maxHeight: 200,  
		    labelAlign:'right',
		    allowBlank:true,
		    blankText:'绩效考核岗位',
		    msgTarget:'side',
		    autoFitErrors:false,
		    layout:'fit',
		    editable:false
		});
		
		jbjxCombo.on('expand',function(){
			if(!isLoad){
				jbjxCombo.getStore().reload();
				isLoad = true;
			}
		});
		return jbjxCombo;
	};
	//获取所属绩效考核岗位下拉框组件
	this.getJbjxpCombo = function(){
		Ext.regModel('jbjxpModel',{
			fields:['jbjx_pid','jbjx_pname']
		});
		var jbjxpStore = Ext.create('Ext.data.Store',{
			storeId:'jbjxpStoreId',
			model:'jbjxpModel',
//			nodeParam:'menuTreeId',
			proxy:{
				type:'ajax',
				url: 'jbjxpControler.html',
				reader:new Ext.data.JsonReader({
					root:'rows'
				})
//						reader:'json'//reader必须为json
			},
			folderSort: true,
			autoLoad:false
		});
		
		var isLoad = false;
		var jbjxpCombo = Ext.create('Ext.form.ComboBox', {
		    fieldLabel: '所属岗位',
		    store: Ext.data.StoreManager.lookup('jbjxpStoreId'),
		    fieldLabel:'所属岗位',
//		    forceSelection:true,
		    emptyText:'所属岗位',
		    name:'jbjxPid',
		    margin:'0 0 4 0 ',
//		    store: jbjxpStore,
		    queryMode:'local',
		    displayField:'jbjx_pname',
		    valueField:'jbjx_pid',
//		    renderTo:Ext.getBody(),
		    width:280,
		    //maxHeight: 200,  
		    labelAlign:'right',
		    allowBlank:true,
		    blankText:'所属岗位',
		    msgTarget:'side',
		    autoFitErrors:false,
		    layout:'fit',
		    editable:false
		});
		jbjxpCombo.on('expand',function(){
			if(!isLoad){
				jbjxpCombo.getStore().reload();
				isLoad = true;
			}
		});
		return jbjxpCombo;
	};
}


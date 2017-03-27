/**
 *周期下拉框组件
 */
/*****************下拉框父类*************/
Ext.define("zqCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'考核周期',
//    forceSelection:true,
    emptyText:'选择考核周期',
    name:'zqCombo',
    margin:'0 0 4 0 ',
//    store: zqStore,
    queryMode:'local',
    displayField:'zqId',
    valueField:'zqId',
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
});
Ext.define('zqComboQuery',{
	extend:'zqCombo',
	tpl: "<div style='height:296px;border:0px solid red;overflow:hidden' id='zqComboId'></div>",
});
function zqComponent(){
	this.getZqCombo = function(){
		Ext.regModel('zqModel',{
			fields:['zq']
		});
		var zqStore = Ext.create('Ext.data.Store',{
			storeId:'zqStoreId',
			model:'zqModel',
//			nodeParam:'menuTreeId',
			proxy:{
				type:'ajax',
				url: 'jxZqControler.html',
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
//		var zqCombo = Ext.create('zqCombo');
		var isLoad = false;
		var zqCombo = Ext.create('Ext.form.ComboBox', {
		    fieldLabel: '考核周期',
		    store: Ext.data.StoreManager.lookup('zqStoreId'),
//		    queryMode: 'local',
		    name:'zqQuery',
		    displayField: 'zq',
		    valueField: 'zq',
		    renderTo: Ext.getBody(),
		    editable:false
		});
		zqCombo.on('expand',function(){
			if(!isLoad){
				zqCombo.getStore().reload();
				isLoad = true;
			}
		});
		return zqCombo;
	};
}


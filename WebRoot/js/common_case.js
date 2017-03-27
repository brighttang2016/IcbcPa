/*公用实例*/



/*var applyNum = Ext.create('textFieldNum',{
	name:'gds_num',
	fieldLabel:'申请数量'
});*/

var orgComboUpd = Ext.create('orgComboUpd',{
	id:'orgComboUpdId',
	name:'org_id'
});
var orgComboQuery = Ext.create('orgCombo',{
	id:'orgComboQueryId',
	name:'org_id'
});

//耗材物品大类(query)
var pTypeCombo = Ext.create('pTypeCombo',{
	id:'pTypeComboId',
	name:'type_idp',
	listeners:{
		focus:function(comboNow){
			Ext.getCmp('cTypeComboId').clearValue();
		}
	}
});
//耗材物品小类(query)
var cTypeCombo = Ext.create('cTypeCombo',{
	id:'cTypeComboId',
	name:'type_id',
	listeners:{
		focus:function(comboNow){
			pTypeValue = Ext.getCmp('pTypeComboId').getValue();
			if(pTypeValue == null){
				comboNow.allQuery = "小类所属大类id";//没有选择大类id
			}else{
				comboNow.allQuery = pTypeValue;
			}
		}
	}
});

//耗材物品大类(update)
var pTypeComboUpd = Ext.create('pTypeCombo',{
	id:'pTypeComboUpdId',
	allowBlank:false,
	forceSelection:true,
	blankText:'请点击右侧下拉按钮选择物品大类',
	name:'typePID',
	listeners:{
		focus:function(){
			Ext.getCmp('cTypeComboUpdId').clearValue();
		}
	}
});

//耗材物品小类(update)
var cTypeComboUpd = Ext.create('cTypeCombo',{
	id:'cTypeComboUpdId',
	name:'typeCID',
	allowBlank:false,
	forceSelection:true,
	blankText:'请点击右侧下拉按钮选择物品小类',
	listeners:{
		focus:function(comboNow){
			pTypeValue =Ext.getCmp('pTypeComboUpdId').getValue();
			if(pTypeValue == null){
				comboNow.allQuery = "小类所属大类id";//没有选择大类id
			}else{
				comboNow.allQuery = pTypeValue;
			}
		}
	}
});
/*供应商实例*/
Ext.create('splCombo',{
	id:'splComboId'
});
Ext.create('splComboUpd',{
	id:'splComboUpdId'
});

/*询价商家实例*/
Ext.create('splnCombo',{
	id:'splnComboId'
});
Ext.create('splnComboUpd',{
	id:'splnComboUpdId'
});
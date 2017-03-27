// JavaScript Document
//*******************自定义组件区开始******************************

Ext.define('textFieldDcml',{
	extend:'Ext.form.field.Text',
	regex:/^0{1}.{1}[0-9]+$/,
	regexText:'数据非法,请录入小数(0-1之间)',
	emptyText:'请录入小数(0-1之间)',
	allowBlank:true,
	width:280,
	autoFitErrors:false,
	msgTarget:'side',
	labelAlign:'right'
});

Ext.define('textFieldInt',{
	extend:'Ext.form.field.Text',
	regex:/^[1-9]*\d*$/,
	regexText:'数据非法,请录入非零整数',
	emptyText:'请录入整数',
	allowBlank:true,
	width:280,
	autoFitErrors:false,
	msgTarget:'side',
	labelAlign:'right'
});

//添加修改默认输入框基类
Ext.define('inputTextField',{
	extend:'Ext.form.field.Text',
	name:'role_name',
	fieldLabel:'字段label',
	blankText:'请录入数据,若无数据,可录入“无”',
	width:280,
	readOnly:false,
	allowBlank:false,
	labelAlign:'right',
	msgTarget:'side',
	autoFitErrors:false,
	maxLength:20,
	maxLengthText:'数据超长,限定20字以内'
});

//表单交易码(隐藏域)
var tran_code = {
	xtype:'hidden',//交易码（隐藏域）
//	xtype:'textfield',
	name:'tx_code',
	fieldLabel:'交易码'
};
//查询区日期输入框基类
Ext.define('queryDateInput',{
	extend:'Ext.form.field.Date',
//	fieldLabel:'起始日期',
	labelAlign:'right',
	format:'Y-m-d',
	width:280,
	autoFitErrors:false,
	msgTarget:'side',
	editable:false
});
//查询区输入框输入基类
Ext.define('queryTextInput',{
	extend:'Ext.form.field.Text',
	width:245,
	labelAlign:'right'
});
//查询区输入框panel基类(存放queryTextInput)
Ext.define('queryTextField',{
	extend:'Ext.form.Panel',
	margin:'2 0 2 0',
	columnWidth:.33,
	labelWidth:100,
//	width:300,
	layout: {   
		type: 'hbox',    
//		align: 'middle ',    
		pack: 'center'
	},
	frame:true,
	baseCls : 'my-panel-no-border',
	border:true,
//	margin:'0 0 3 0',
	items:[{
		xtype:'textfield',
		fieldLabel:'名称label',
		labelAlign:'right',
		name:'role_name'
	}]
});

//弹窗框架容器类
Ext.define('win',{
	extend:'Ext.window.Window',
//	id:'winId',//父类win使用了id，会导致实例化弹窗异常
	width:350,
	height:383,
	closable:true,
	shadow:true,
//	closeAction:'close',
	closeAction:'destroy',
	resizable:false,
	modal:true,
//			border:false,
	layout:'fit'//,//updFormPanel自适应外层容器大小
//	items:[new updFormPanel1()]
});

//hbox布局输入框
Ext.define('hboxField',{
	extend:'Ext.form.Panel',
	columnWidth:.5,
	layout: {   
		type: 'hbox',    
		align: 'left '/*,   
		pack: 'center'*/
	},
	frame:true,
//			baseCls : 'my-panel-no-border',
	border:false
});



//当前pageSize下拉框组件基类
Ext.define('sizeComboPrt',{
	extend:'Ext.form.ComboBox',
	name:'pageSizeShow',
	store:new Ext.data.ArrayStore({
		fields:['text','value'],
		data:[['全部',2147483646],['50',50],['20',20],['10',10],['5',5],['3',3]]//int取值范围：-2147483648至2147483647，查询上限条数：2147483646（前端委婉解决全部查询问题,基本满足系统目前功能）
	}),
	displayField:'text',
	valueField:'value',
	emptyText:'10',
	width:50
});



//*******************自定义组件区结束******************************

function cmpResotore(){
	
}
function noticeShow(msg){
	Ext.Msg.show({title:'提示',msg:msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
}
//增、删、改提交
function handlerSave(url,storeName){
//	alert(updFormPanel);
//	alert(storeName);
	var currStore = Ext.data.StoreManager.lookup(storeName);
//	alert("currStore1111:"+currStore);
//	alert(updFormPanel.getForm().isValid());
	if(!updFormPanel.getForm().isValid()){
		msgAlert("错误:表单非法,无法提交！");
	}
	if(updFormPanel.getForm().isValid()){
		updFormPanel.getForm().submit({
			waitTitle:'提示',
			waitMsg:'正在提交数据请稍候...',
	//		url:'User_userUpdateExt.action',//提交到更新
			url:url,
			success:function(currForm,action){
	//			Ext.Msg.alert('提示',action.result.msg);
				msgAlert(action.result.errorMsg);
				//Ext.Msg.show({title:'提示',msg:action.result.errorMsg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
	//					tableStore.loadPage(1);
	//			tableStore.reload();
				currStore.reload();
			},
			failure:function(currForm,action){
				msgAlert(action.result.errorMsg);
	//			Ext.Msg.alert('提示',action.result.msg);
//				Ext.Msg.show({title:'提示',msg:action.result.errors.msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
//				Ext.Msg.show({title:'提示',msg:'通信异常',icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
				//currForm.reset();
//				setTimeout('parent.location.href = "pages/login/login.jsp";',3000);
				//sessionValidate(action.result.errors.errorMsg);
			}
		});
	//	win.hide();
		handlerCancle();
	}
}

/**
 * 组件重新渲染20150720
 */
function reDoLayout(){
	Ext.ComponentManager.each(function(cmpId,cmp,length){  
        if(cmp.hasOwnProperty("renderTo")){  
        	try{
        		cmp.doLayout(); 
        	}catch(e){
        	}
        }  
    });  
}

Ext.EventManager.onWindowResize(function(){  
	reDoLayout();
});
//页面跳转

function changePage(url){
	Ext.getDom('contendIframe').src = url+"?consbReqId="+new Date().getTime();
}

//隐藏添加修改弹窗
function handlerCancle(){
//	console.log("handlerCancle");
	try{
		winUpd.hide();
//		winUpd.close();
	}catch(e){}
	try{
		winAdd.hide();
//		winAdd.close();
	}catch(e){}
}

/*function sessionValidate(msg){
	Ext.Msg.show({title:'提示',msg:msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
//	Ext.Msg.alert("提示",msg);
	setTimeout('parent.location.href = "pages/login/login.jsp";',2000);
	return;
}*/

function logout(userId){
	//alert("logout");
//	alert(navigator.userAgent);
	/*Ext.Ajax.request({
		url:'User_userLogout.action',
		success:function(response){
			if ((navigator.userAgent.indexOf('MSIE') >= 0 || (navigator.userAgent.indexOf('.NET')) >= 0)&& (navigator.userAgent.indexOf('Opera') < 0)) {//ie,navigator.userAgent.indexOf('.NET')ie11判断
				   parent.location = '../../?userId='+userId;
			}else{
				   parent.location = '?userId='+userId;//也可
			}
			//parent.location = '?userId='+userId;//也可
		}
	});  */ 
	/*if ((navigator.userAgent.indexOf('MSIE') >= 0 || (navigator.userAgent.indexOf('.NET')) >= 0)&& (navigator.userAgent.indexOf('Opera') < 0)) {//ie,navigator.userAgent.indexOf('.NET')ie11判断
//		   parent.location = '../../?userId='+userId;
		parent.close();
	}else{
//		   parent.location = '?userId='+userId;//也可
		parent.close();
	}*/
	Ext.Ajax.request({
//		url:'rpManage.html',
		url:'logout.html',
		params:{
		},
		success:function(response){
			top.close();
		},
		failure:function(){
		}
	});
}


/**
 * 2014-12-17 brighttang
 * 下拉列表每页显示多少条记录combo
 */
/*var sizeCombo = Ext.create('Ext.form.ComboBox',{
	name:'pageSizeShow',
//	hiddenName:'pageSizeShow',
	store:new Ext.data.ArrayStore({
		fields:['text','value'],
		data:[['100',100],['50',50],['30',30],['10',10]]
	}),
	displayField:'text',
	valueField:'value',
	emptyText:'10',
	width:50
});
sizeCombo.on('select',function(combo){
	var pageSize = parseInt(combo.getValue());
	tableStore.pageSize = pageSize;//修改数据源中的pageSize
	tableStore.load({
		params:{
			start:0,
			limit:pageSize,
			//userId:'tang'
		}
	});
});*/

//资格认证mode
Ext.define("ctCombo",{  
	extend:'Ext.form.ComboBox',
//	name:'ctId',
//	id:'ctId',
	allowBlank:true,
	blankText:'请选择认证资格要求',
	fieldLabel:'认证资格要求',
	emptyText:'请选择认证资格要求',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'allbook_json_ret',
	queryParam:'searchbook',
	minChars:3,
	queryDelay:300,
	store:Ext.data.StoreManager.lookup("ctComboStoreId"),
	displayField:'ct_name',
	valueField:'ct_id',
	queryMode:'remote',
	forceSelection:true,
	editable:false
});

//特色产品下拉框
Ext.define('tscpCombo',{
	extend:'Ext.form.field.ComboBox',
	name:'tscpComboName',
	allowBlank:false,
	blankText:'请输入产品序号',
//	fieldLabel:'选择部门',
	emptyText:'请输入产品序号',
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
	queryParam:'seqNumQuery',
	minChars:1,
	queryDelay:300,
	store:Ext.data.StoreManager.lookup('tscpComboStoreId'),
	displayField:'zb_name',
	valueField:'seq_num',
	queryMode:'remote',
	forceSelection:true
});

//标准产品下拉框
Ext.define('bzcpCombo',{
	extend:'Ext.form.field.ComboBox',
	name:'bzcpComboName',
	allowBlank:false,
	blankText:'请输入产品序号',
//	fieldLabel:'选择部门',
	emptyText:'请输入产品序号',
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
	queryParam:'seqNumQuery',
	minChars:1,
	queryDelay:300,
	store:Ext.data.StoreManager.lookup('bzcpComboStoreId'),
	displayField:'zb_name',
	valueField:'seq_num',
	queryMode:'remote',
	forceSelection:true
});

//当前用户所在机构所有用户
Ext.define('userCombo',{
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
	minChars:1,
	queryDelay:300,
	store:Ext.data.StoreManager.lookup('userComboStoreId'),
	displayField:'userName',
	valueField:'userId',
	queryMode:'remote',
	forceSelection:true
});


var sexStore = Ext.create('Ext.data.Store', {
		    fields: ['menuId', 'menuName'],
		    data : [
		        {"menuId":"Y", "menuName":"男"},
		        {"menuId":"X", "menuName":"女"}
		    ]
		});
Ext.define("sexCombo",{
	extend:'Ext.form.ComboBox',
	fieldLabel:'性别',
//    forceSelection:true,
    emptyText:'选择性别',
    name:'sex',
    margin:'0 0 4 0 ',
    store: sexStore,
    queryMode:'local',
    displayField:'menuName',
    valueField:'menuId',
    renderTo:Ext.getBody(),
    width:280,
    //maxHeight: 200,  
    labelAlign:'right',
    allowBlank:true,
    blankText:'请选择性别',
    msgTarget:'side',
    autoFitErrors:false,
    layout:'fit',
    editable:false
});

//记录更新、新增面板基类
Ext.define('updFormPanelPrt',{
	extend:'Ext.form.Panel',
//	id:'updFormPanel',
//	resizable:true,
	buttonAlign:'center',
	fieldDefaults:{
		margin:'2 0 2 0',
		labelAlign:'right',
		allowBlank:false,
		blankText:'该项为必输项,若无数据请填写"无"',
		msgTarget:'side',
		maxLength:25,
		maxLengthText:'数据超长,限定20字以内'
	},
	resizable:false
});
//填写审批意见类
Ext.define('approveDesc',{
	extend:'Ext.form.field.TextArea',
	width:500,
	height:100,
	xtype:'textarea',
	maxLength:250,
	maxLengthText:'审批意见字数上限：250',
	fieldLabel:'审批意见',
	allowBlank:true,
	blankText:'请填写审批意见，如：【同意】、【不同意】、【无】及其他内容',
	msgTarget:'side',
	autoFitErrors:false
});

//耗材申请理由类
Ext.define('applyDesc',{
	extend:'Ext.form.field.TextArea',
	width:580,
	height:150,
	labelAlign:'right',
	fieldLabel:'*物品申请理由',
	name:'apply_desc',
	allowBlank:false,
	blankText:'请输入物品申请理由',
	msgTarget:'side',
	autoFitErrors:false,
	maxLength:1000,
	maxLengthText:'数据超长,长度限定1000字以内'
});

//申请数目类
Ext.define('textFieldNum',{
	extend:'Ext.form.field.Text',
//	regex:/^[0-9]+\.?\d*$/,
	regex:/(^0{1}$)|(^[-]*[0]{1}\.{1}[0-9]{0,2}$)|(^[-]*[1-9]{1,}[0-9]*\.?[0-9]{0,2}$)/,
	msgTarget:'side',
	width:280,
	allowBlank:false,
	autoFitErrors:false,
	regexText:'数据非法,限定整数或两位小数'
});

//自定义机构查询下拉框组件（用户管理界面：新增、修改、查询）开始
Ext.define('OrgInfo', {
    extend: 'Ext.data.Model',
    fields: ['org_name', 'org_id']
});
orgStore = Ext.create('Ext.data.Store',{
	storeId:'orgStoreId',
	autoLoad:false,
	model:'OrgInfo',//将数据源写入到此处配置的model之中
	proxy:{
		type:'ajax',
		url:'Branch_branchQuery.action',
		reader:{
			type:'json',//不指定貌似也不会报错
			//model:'BookInfo2',//此处model可以不要，加上有和作用？？？？2014-12-13,官方api中没有此项，网上的都是坑爷的。。。。
			root:'rows'//指定根节点名，当后台返回的是json字符串是，必须指定数据的root，方能使得model获得后台数据（此乃关键）
		}
	}
});

//查询
Ext.define("orgCombo",{  
	extend:'Ext.form.ComboBox',
	name:'org_id',
	allowBlank:true,
	blankText:'请选择部门',
	fieldLabel:'选择部门',
	emptyText:'请选择部门',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'allbook_json_ret',
	queryParam:'searchbook',
	minChars:3,
	queryDelay:300,
	store:orgStore,
	displayField:'org_name',
	valueField:'org_id',
	queryMode:'remote',
	forceSelection:true
});
//修改
Ext.define("orgComboUpd",{  
	extend:'Ext.form.ComboBox',
	name:'org_id',
	allowBlank:true,
	blankText:'请选择部门',
	fieldLabel:'选择部门',
	emptyText:'请选择部门',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'allbook_json_ret',
	queryParam:'searchbook',
	minChars:3,
	queryDelay:300,
	store:orgStore,
	displayField:'org_name',
	valueField:'org_id',
	queryMode:'remote',
	forceSelection:true
});

//耗材大类，小类下拉框组件开始
Ext.define('TypeInfo', {
    extend: 'Ext.data.Model',
    fields: ['type_name', 'type_id']
});
Ext.define('typeStore',{
	extend:'Ext.data.Store',
	model:'TypeInfo',//将数据源写入到此处配置的model之中
	proxy:{
		type:'ajax',
		url:'Consb_consbTypeQueryExt.action',
		reader:{
			type:'json',//不指定貌似也不会报错
			//model:'BookInfo2',//此处model可以不要，加上有和作用？？？？2014-12-13,官方api中没有此项，网上的都是坑爷的。。。。
			root:'rows'//指定根节点名，当后台返回的是json字符串是，必须指定数据的root，方能使得model获得后台数据（此乃关键）
		}
	}
});
var cTypeStore = Ext.create('typeStore',{
	storeId:'cTypeStoreId',
	autoLoad:false
});
var pTypeStore = Ext.create('typeStore',{
	storeId:'pTypeStoreId',
	autoLoad:false
});
//耗材大类
Ext.define("pTypeCombo",{  
	extend:'Ext.form.ComboBox',
//	id:'pTypeComboId',
	name:'typePID',
	allowBlank:true,
	fieldLabel:'物品大类',
	emptyText:'请选择物品大类',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listConfig:{
		emptyText:'未找到匹配值',
		loadingText:'正在加载信息',
		maxHeight:200
	},
	allQuery:'0',//父类id为0：查询所有物品大类
	queryParam:'typePId',
	minChars:3,
	queryDelay:300,
//	store:new typeStore(),
	store:pTypeStore,
	displayField:'type_name',
	valueField:'type_id',
	queryMode:'remote',
	forceSelection:true
});

//耗材小类
Ext.define("cTypeCombo",{  
	extend:'Ext.form.ComboBox',
//	id:'cTypeComboId',
	name:'typeCID',
	allowBlank:true,
	fieldLabel:'物品小类',
	emptyText:'请选择物品小类',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
//	maxHeight:200,
	listConfig:{
		emptyText:'未找到匹配值',
		loadingText:'正在加载信息',
		maxHeight:200
	},
	allQuery:'小类所属大类id',
	queryParam:'typePId',
	minChars:3,
	queryDelay:300,
//	store:new typeStore(),
	store:cTypeStore,
	displayField:'type_name',
	valueField:'type_id',
	queryMode:'remote',
	listeners:{
		beforeshow:function(pTypeCombo){
			pTypeValue  = pTypeCombo.getValue();
			return false;
		}
	}
});


//*********************************自定义供应商下拉框组件（物品信息管理）开始*********************************
Ext.define('SplInfo', {
    extend: 'Ext.data.Model',
    fields: ['spl_name', 'spl_id']
});
splStore = Ext.create('Ext.data.Store',{
	storeId:'splStoreId',
	autoLoad:false,
	model:'SplInfo',//将数据源写入到此处配置的model之中
	proxy:{
		type:'ajax',
		url:'Ew_ewRouter.action',
		reader:{
			type:'json',//不指定貌似也不会报错
			//model:'BookInfo2',//此处model可以不要，加上有和作用？？？？2014-12-13,官方api中没有此项，网上的都是坑爷的。。。。
			root:'rows'//指定根节点名，当后台返回的是json字符串是，必须指定数据的root，方能使得model获得后台数据（此乃关键）
		}
	}
});
splStore.on('beforeLoad',function(){
	Ext.apply(splStore.proxy.extraParams,{
		tx_code:'10434',//query supplier info 
		spl_id:'',
		spl_name:'',
		spl_addr:''//,
	});
});
//供应商（下拉框）查询
Ext.define("splCombo",{  
	extend:'Ext.form.ComboBox',
	name:'spl_id',
	/*allowBlank:false,
	blankText:'请选择供应商',*/
	emptyText:'请选择供应商',
	fieldLabel:'请选择供应商',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'queryValue',
	queryParam:'queryName',
	minChars:3,
	queryDelay:300,
	store:splStore,
	displayField:'spl_name',
	valueField:'spl_id',
	queryMode:'remote'
});

//供应商（下拉框）修改
Ext.define("splComboUpd",{  
	extend:'Ext.form.ComboBox',
//	name:'spl_id_upd',
	name:'spl_id',
	allowBlank:false,
	blankText:'请选择供应商',
	emptyText:'请选择供应商',
	fieldLabel:'请选择供应商',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'queryValue',
	queryParam:'queryName',
	minChars:3,
	queryDelay:300,
	store:splStore,
	displayField:'spl_name',
	valueField:'spl_id',
	queryMode:'remote',
	forceSelection:true,
	blankText:'请点击右侧下拉按钮选择供应商'
});

//*********************************自定义供应商下拉框组件（物品信息管理）结束*********************************



//*********************************自定义询价商家下拉框组件（物品信息管理）开始*********************************
Ext.define('SplnInfo', {
    extend: 'Ext.data.Model',
    fields: ['spln_name', 'spln_id']
});
splnStore = Ext.create('Ext.data.Store',{
	storeId:'splnStoreId',
	model:'SplnInfo',//将数据源写入到此处配置的model之中
	proxy:{
		type:'ajax',
		url:'Ew_ewRouter.action',
		reader:{
			type:'json',//不指定貌似也不会报错
			//model:'BookInfo2',//此处model可以不要，加上有和作用？？？？2014-12-13,官方api中没有此项，网上的都是坑爷的。。。。
			root:'rows'//指定根节点名，当后台返回的是json字符串是，必须指定数据的root，方能使得model获得后台数据（此乃关键）
		}
	}
});
splnStore.on('beforeLoad',function(){
	Ext.apply(splnStore.proxy.extraParams,{
		tx_code:'10454',//query supplier info 
		spln_id:'',
		spln_name:'',
		spln_addr:''//,
	});
});
//询价商家（下拉框）查询
Ext.define("splnCombo",{  
	extend:'Ext.form.ComboBox',
	name:'spln_id',
	/*allowBlank:false,
	blankText:'请选择供应商',*/
	emptyText:'请选择询价商家',
	fieldLabel:'请选择询价商家',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'queryValue',
	queryParam:'queryName',
	minChars:3,
	queryDelay:300,
	store:splnStore,
	displayField:'spln_name',
	valueField:'spln_id',
	queryMode:'remote'
});

//询价商家（下拉框）修改
Ext.define("splnComboUpd",{  
	extend:'Ext.form.ComboBox',
//	name:'spl_id_upd',
	name:'spln_id',
	allowBlank:false,
	blankText:'请点击右侧下拉按钮选择询价商家',
	emptyText:'请选择询价商家',
	fieldLabel:'选择询价商家',
	msgTarget:'side',
	autoFitErrors:false,
	labelAlign:'right',//label靠右
	width:280,
	listconfig:{
		loadingText:'正在加载信息',
		emptyText:'未找到匹配值',
		maxHeight:100
	},
	allQuery:'queryValue',
	queryParam:'queryName',
	minChars:3,
	queryDelay:300,
	store:splnStore,
	displayField:'spln_name',
	valueField:'spln_id',
	queryMode:'remote',
	forceSelection:true//,
});

//角色选择面板基类
Ext.define('selFormPanelPrt',{
	extend:'Ext.form.Panel',
    title: '选择角色',
    width: 600,
    bodyPadding: '10 0 0 10',
    height: 210,
    layout: 'column',
    draggable:false,
    border:false
});
//*********************************自定义询价商家下拉框组件结束*********************************



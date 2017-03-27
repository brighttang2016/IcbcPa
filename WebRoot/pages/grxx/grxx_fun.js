


/**
 * 查看详细信息框架窗口
 * @param baseInfo 基本信息fieldset
 * @param pointInfo 积分信息fieldset
 */
/*function showPopWin(baseInfo,pointInfo,wglHisInfo,jbHisInfo){
	winAdd = Ext.create('Ext.window.Window',{
		title:'用户详细信息',
		width:'700px',
		height:'100%',
		constrain:true,
		autoScroll:true,
		bodyStyle:'background:#ffffff',
		layout:'column',
//		maximized:true,//默认最大化窗口
		maximizable:true,
//		margin:'5 5 5 5',
		items:[{
			columnWidth:1,
			border:false,
			layout:{
				type:'hbox',
				align:'center',
				pack:'center'
			},
			items:[baseInfo]
		},{
			columnWidth:1,
			border:false,
			layout:{
				type:'hbox',
				pack:'center'
			},
			items:[pointInfo]
		},{
			columnWidth:1,
			border:false,
			layout:{
				type:'hbox',
				pack:'center'
			},
			items:[wglHisInfo]
		},{
			columnWidth:1,
			border:false,
			layout:{
				type:'hbox',
				pack:'center'
			},
			items:[jbHisInfo]
		}]
	});
	winAdd.show();
}*/

/**
 * 用户资格信息
 */
function userCtInfo(userId){
//	userCtStore.load();
	var userCtInfoGrid = Ext.create('Ext.grid.Panel', {
	    title: '已有资格',
		margin:'20 0 0 0',
	    store: userCtStore,
	    columns: [
				Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
				{ text: '资格编号',  dataIndex: 'ct_id'},
				{ text: '资格名称',  dataIndex: 'ct_name' },
		        { text: '获得时间', dataIndex: 'oper_time',flex:1,renderer:function(value){
		        	return value.substr(0,4)+"-"+value.substr(4,2)+"-"+value.substr(6,2);
		        }}],
	    height: 320,
	    width: 600
	});
	userCtStore.on('beforeLoad',function(){
		Ext.apply(userCtStore.proxy.extraParams,{
			userId:userId
		});
	});
	userCtStore.load();
//	return userCtInfo;
//	return panel;
	return userCtInfoGrid;
}

/**
 * 用户学习积分
 * @param userId
 * @returns
 */
function userxfInfo(userId){
	var xfStore = Ext.data.StoreManager.lookup('xfStoreId');
	/*var xfHisCombo = new sizeComboPrt();
	xfHisCombo.on("select",function(combo){
		var pageSize = xfHisCombo.getValue();
		xfStore.pageSize = pageSize;
		xfStore.load();
	});*/
	xfStore.on('beforeLoad',function(){
		Ext.apply(xfStore.proxy.extraParams,{
			userId:userId
		});
	});
	xfStore.load();
	var grid = Ext.create('Ext.grid.Panel', {
        width: '90%',
        height: 320,
        margin:'20 0 0 0',
        frame: false,
        title:'学分历史记录',
//        iconCls: 'icon-grid',
        store: xfStore,
        //plugins: [cellEditing],
        features: [{
//            id: 'group',
            ftype: 'groupingsummary',
            groupHeaderTpl: '{name}',
//            hideGroupedHeader: false,
//            enableGroupingMenu: false
        }],
        columns: [{
        	text:'用户号',
        	dataIndex:'user_id'
        },{
        	text:'姓名',
        	dataIndex:'name'
        },{
            text: '学分类型',
            flex: 1,
            tdCls: 'task',
            sortable: true,
            dataIndex: 'ctype_name',
            hideable: false,
            summaryType: 'count',
            summaryRenderer: function(value, summaryData, dataIndex) {
                return ((value === 0 || value > 1) ? '(' + value + ' 条记录)' : '(1 条记录)');
            }
        },{
            header: '操作日期',
            width: 80,
            sortable: true,
            dataIndex: 'oper_date',
            //summaryType: 'max',
            renderer: Ext.util.Format.dateRenderer('Y-m-d'),
            //summaryRenderer: Ext.util.Format.dateRenderer('YYYY/dd/mm'),
            field: {
                xtype: 'datefield'
            }
        }, {
            header: '学分年',
            width: 75,
            sortable: true,
            dataIndex: 'point_year',
            //summaryType: 'sum',
            renderer: function(value, metaData, record, rowIdx, colIdx, store, view){
                return value;
            }
        }, {
            header: '学分',
            width: 75,
            sortable: true,
            //renderer: Ext.util.Format.usMoney,
           // summaryRenderer: Ext.util.Format.usMoney,
            summaryRenderer: function(value, summaryData, dataIndex) {
                return value + ' 分';
            },
            dataIndex: 'point',
            summaryType: 'sum',
            field: {
                xtype: 'numberfield'
            }
        }]/*,
        bbar:[{
	    	xtype:'pagingtoolbar',
	    	store:xfStore,
	    	displayInfo:true,
	    	border:false,
	    	items:['每页显示',xfHisCombo,'条']
	    }]*/
    });
	
	var jbHisInfo = Ext.create("Ext.form.FieldSet",{
		title:'学习积分',
		//width:'100%',
		margin:'5 5 5 5',
		collapsible:true,
		items:[{
			border:false,
			layout:{
				type:'hbox',
				pack:'center',
				align:'center'
			},
			items:[grid]
		}]
	});
//	return jbHisInfo;
	return grid;
}

/**
 * 岗位层级历史变更记录
 * @param userId
 * @returns
 */
function userJbHisInfo(userId){
	var jbHisStore = Ext.data.StoreManager.lookup('jsHisStoreId');//积分历史store
	var jbHisCombo = new sizeComboPrt();
	jbHisCombo.on("select",function(combo){
		var pageSize = combo.getValue();
		jbHisStore.pageSize = pageSize;
		jbHisStore.load();
	});
	var jbHisGrid = Ext.create('Ext.grid.Panel', {
	    title: '岗位层级历史变更记录',
	    store: jbHisStore,
	    collapsible:true,
	    columns: [
				Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
				{ text: '用户号',  dataIndex: 'userId'},
				{ text: '姓名',  dataIndex: 'userName' },
	        { header: '岗位层级', dataIndex: 'jbName',width:150},
	        { text: '变更时间', dataIndex: 'chgTime',flex:1,renderer:function(value){
	        	return value.substr(0,4)+"-"+value.substr(4,2)+"-"+value.substr(6,2);
	        }}],
	    height: 190,
	    width: 600,
	    //renderTo: Ext.getBody(),
	    bbar:[{
	    	xtype:'pagingtoolbar',
	    	store:jbHisStore,
	    	displayInfo:true,
	    	border:false,
	    	items:['每页显示',jbHisCombo,'条']
	    }]
	});
	jbHisStore.on('beforeLoad',function(){
		Ext.apply(jbHisStore.proxy.extraParams,{
			userId:userId
		});
	});
	jbHisStore.load();
	var jbHisInfo = Ext.create("Ext.form.FieldSet",{
		title:'岗位层级',
		margin:'5 5 5 5',
		//width:'100%',
		collapsible:true,
		items:[{
			border:false,
			layout:{
				type:'hbox',
				pack:'center',
				align:'center'
			},
			items:[jbHisGrid]
		}]
	});
//	return jbHisInfo;
	return jbHisGrid;
}

/**
 * 用户工资等级档次历史记录
 * @param userId
 * @returns
 */
function userWglHisInfo(userId){
	var wglHisStore = Ext.data.StoreManager.lookup('wglHisStoreId');//积分历史store
	var wglHisSizeCombo = new sizeComboPrt();
	wglHisSizeCombo.on("select",function(combo){
		var pageSize = combo.getValue();
		wglHisStore.pageSize = pageSize;
		wglHisStore.load();
	});
	var wglHisGrid = Ext.create('Ext.grid.Panel', {
	    title: '工资等级档次历史记录',
	    store: wglHisStore,
	    collapsible:true,
	    columns: [
				Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
				{ text: '用户号',  dataIndex: 'userId'},
				{ text: '姓名',  dataIndex: 'userName' },
	        { header: '工资等级', dataIndex: 'wgName'},
	        { header: '工资档次', dataIndex: 'wlName'},
	        { text: '变更时间', dataIndex: 'chgTime',flex:1,renderer:function(value){
	        	return value.substr(0,4)+"-"+value.substr(4,2)+"-"+value.substr(6,2);
	        }}],
	    height: 190,
	    width: 600,
	    //renderTo: Ext.getBody(),
	    bbar:[{
	    	xtype:'pagingtoolbar',
	    	store:wglHisStore,
	    	displayInfo:true,
	    	border:false,
	    	items:['每页显示',wglHisSizeCombo,'条']
	    }]
	});
	wglHisStore.on('beforeLoad',function(){
		Ext.apply(wglHisStore.proxy.extraParams,{
			userId:userId
		});
	});
	wglHisStore.load();
	var wglHisInfo = Ext.create("Ext.form.FieldSet",{
		title:'工资等级',
		margin:'5 5 5 5',
		//width:'100%',
		collapsible:true,
		items:[{
			border:false,
			layout:{
				type:'hbox',
				pack:'center',
				align:'center'
			},
			items:[wglHisGrid]
		}]
	});
	return wglHisInfo;
}

/**
 * 用户积分历史信息
 * @param userId
 * @returns 历史积分fieldset
 */
function userPointHisInfo(userId){
	var pointInfoStore = Ext.data.StoreManager.lookup('pointInfoStoreId');//积分历史store
	var pointInfoSizeCombo = new sizeComboPrt();
	pointInfoSizeCombo.on("select",function(combo){
		var pageSize = combo.getValue();
		pointInfoStore.pageSize = pageSize;
		pointInfoStore.load();
	});
	var pointInfoGrid = Ext.create('Ext.grid.Panel', {
		id:'pointInfoGrid',
	    title: '积分更改历史记录',
	    store: pointInfoStore,
	    collapsible:true,
	    columns: [
				Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
				{ text: '用户号',  dataIndex: 'userId' },
				{ text: '姓名',  dataIndex: 'userName' },
	        { header: '积分改变时间', dataIndex: 'chgTime', flex: 1,renderer:function(value){
	        	return value.substring(0,4)+"-"+value.substr(4,2)+"-"+value.substr(6,2);
	        }},
	        { header: '积分', dataIndex: 'point'},
	        { text: '积分类型', dataIndex: 'type',renderer:function(typeValue){
//	        	msgAlert((typeValue == 0)+"|"+(typeValue == "")+"|"+(typeValue == "0"));
	        	var retValue = "";
	        	if(typeValue == "0")
	        		retValue = "基础积分";
	        	else if(typeValue == "1")
	        		retValue = "奖励积分";
	        	else retValue = "无";
	        	return retValue;
	        }}],
	    height: 190,
	    width: 600,
	    //renderTo: Ext.getBody(),
	    bbar:[{
	    	xtype:'pagingtoolbar',
	    	store:pointInfoStore,
	    	displayInfo:true,
	    	border:false,
	    	items:['每页显示',pointInfoSizeCombo,'条']
	    }]
	});
	pointInfoStore.on('beforeLoad',function(){
		Ext.apply(pointInfoStore.proxy.extraParams,{
			userId:userId
		});
	});
	pointInfoStore.load();
	var pointInfo = Ext.create("Ext.form.FieldSet",{
		title:'积分信息',
		margin:'5 5 5 5',
		//width:'100%',
		collapsible:true,
		items:[{
			border:false,
			layout:{
				type:'hbox',
				pack:'center',
				align:'center'
			},
			items:[pointInfoGrid]
		}]
	});
	return pointInfo;
}

/**
 * 用户基本信息
 * @return 用户基本信息fieldset
 */
function userBasicInfo(record){
	var basePanel = Ext.create('Ext.panel.Panel',
		{
			title:'用户基本信息',
			margin:'5 5 5 5',
			width:600,
			height:150,
			collapsible:true,
			items:[{
				xtype:'form',
				baseCls:'my-panel-no-border',
				layout:'column',
				defaults:{
//					labelWidth:50,
					labelAlign:'right',
//					msgTarget:'side',
					margin:'0 0 4 0'
				},
				items:[{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'员工号',
					value:record.userid/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'姓名',
					value:record.name,
					border:true/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'所属机构',
					value:record.orgname/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'所属部门',
					value:record.depname,
					border:true/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'性别',
					value:record.sex=="Y"?"男":"女",
					border:true/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'邮件地址',
					value:record.mail,
					border:true/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'参加工作日期',
					value:record.begindate,
					border:true/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'人员性质',
					value:record.propertyname,
					border:true/*,
					style:'border:1px solid red',*/
				},{
					columnWidth:.5,
					xtype:'displayfield',
					fieldLabel:'岗位类别',
					value:record.jbname,
					border:true/*,
					style:'border:1px solid red',*/
				}]
			}]
		
	});
	var baseInfo = Ext.create('Ext.form.FieldSet',{
		margin:'5 5 5 5',
		title:'基本信息',
		//width:'100%',
		margin:'5 5 5 5',
		collapsible:true,
		layout:{
			type:'hbox',
			align:'center',
			pack:'center'
		},
		items:[basePanel]
	});
	return baseInfo;
}




	
		
		
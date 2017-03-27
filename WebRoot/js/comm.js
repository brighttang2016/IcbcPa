
// 公共通信类（brighttang 20150618）


function comm(){
	/**
	 * 批量插入保存提交表格内容
	 * @param gridStoreId 数据展示表格store id
	 * @param editGridStoreId 已编辑store id（待批量插入数据）
	 * @param keyArray 待批量插入数据非空字段数组
	 * @param txCode 交易码
	 * @param winAdd 表格父窗口
	 * @param url 批量提交保存url
	 */
	this.saveAll = function(gridStoreId,editGridStoreId,keyArray,txCode,winAdd,url){
		var editTbStore = Ext.data.StoreManager.lookup(editGridStoreId);
		var dataModels = editTbStore.getModifiedRecords().slice(0);
//		alert(zhjfStore.getModifiedRecords().length);
		var jsonArray = new Array();
		var isValid = 1;
		Ext.each(dataModels,function(item){
			for(var i = 0;i < keyArray.length;i++){
				if(item.data[keyArray[i]] == '' || item.data[keyArray[i]] == null || item.data[keyArray[i]] == undefined){
					isValid = 0;
				}	
			}
			jsonArray.push(item.data);
		});
//		alert(Ext.encode(jsonArray));
		if(isValid == 1 && jsonArray.length > 0){
			Ext.Ajax.request({
//				url:'rpManage.html',
				url:url,
				params:{
					data:Ext.encode(jsonArray),
					tx_code:txCode//总行学习积分
				},
				success:function(response){
					editTbStore.removeAll();
//					Ext.data.StoreManager.lookup("gridStoreId").reload();
					Ext.data.StoreManager.lookup(gridStoreId).reload();
//					winAdd.hide();//第一次10-13保存成功，再次打开弹窗添加新行（添加首行没问题，第二行之后的）抛出异常（添加后表格所有记录无法显示2015-）
					winAdd.close();
				},
				failure:function(){
				}
			});
		}else{
			msgAlert("表格数据非法,无法提交!<br/><hr/>提示:表格可能存在空数据!");
		}
	};
	
	/**
	 * 批量删除
	 * @param grid 表格（显示数据）
	 * @param url 批量删除提交url
	 * @param tx_code 交易码
	 */
	this.handleDelBat = function(grid,url,tx_code){
		var sm = grid.getSelectionModel();
		var records = sm.getSelection();
		var jsonArray = new Array();
		Ext.each(records,function(item){
			jsonArray.push(item.data);
			//alert(item.data.userId);
		});
		Ext.MessageBox.confirm('提示','确认删除?',function(btn){
			if(btn == 'yes'){
				Ext.Ajax.request({
					type:'ajax',
					url:url,
					params:{
						data:Ext.encode(jsonArray),
						tx_code:tx_code
					},
					success:function(response){
						grid.getStore().reload();
//						Ext.data.StoreManager.lookup(storeId).reload();
					},
					failure:function(){
					}
				});
			}
		});
	};
}
/**
 * 批量插入保存提交表格内容
 * @param gridStoreId 数据展示表格store id
 * @param editTbStore 已编辑store（待批量插入数据）
 * @param keyArray 待批量插入数据非空字段数组
 * @param txCode 交易码
 * @param winAdd 表格父窗口
 * @param url 批量提交保存url
 */
function saveAll(gridStoreId,editTbStore,keyArray,txCode,winAdd,url){
		var dataModels = editTbStore.getModifiedRecords().slice(0);
//		alert(zhjfStore.getModifiedRecords().length);
		var jsonArray = new Array();
		var isValid = 1;
		Ext.each(dataModels,function(item){
			for(var i = 0;i < keyArray.length;i++){
				if(item.data[keyArray[i]] == ''){
					isValid = 0;
				}	
			}
			jsonArray.push(item.data);
		});
//		alert(Ext.encode(jsonArray));
		if(isValid == 1 && jsonArray.length > 0){
			Ext.Ajax.request({
//				url:'rpManage.html',
				url:url,
				params:{
					data:Ext.encode(jsonArray),
					tx_code:txCode//总行学习积分
				},
				success:function(response){
					editTbStore.removeAll();
//					Ext.data.StoreManager.lookup("gridStoreId").reload();
					Ext.data.StoreManager.lookup(gridStoreId).reload();
//					winAdd.hide();//第一次10-13保存成功，再次打开弹窗添加新行（添加首行没问题，第二行之后的）抛出异常（添加后表格所有记录无法显示2015-）
					winAdd.close();
				},
				failure:function(){
				}
			});
		}else{
			msgAlert("表格数据非法,无法提交!<br/><hr/>提示:表格可能存在空数据!");
		}
}

/**
 * 批量删除
 * @param grid 表格（显示数据）
 * @param url 批量删除提交url
 * @param tx_code 交易码
 */
function handleDelBat(grid,url,tx_code){
	var sm = grid.getSelectionModel();
	var records = sm.getSelection();
	var jsonArray = new Array();
	Ext.each(records,function(item){
		jsonArray.push(item.data);
		//alert(item.data.userId);
	});
	Ext.MessageBox.confirm('提示','确认删除?',function(btn){
		if(btn == 'yes'){
			Ext.Ajax.request({
				type:'ajax',
				url:url,
				params:{
					data:Ext.encode(jsonArray),
					tx_code:tx_code
				},
				success:function(response){
					grid.getStore().reload();
//					Ext.data.StoreManager.lookup(storeId).reload();
				},
				failure:function(){
				}
			});
		}
	});
}

/**
 * 获取表单数据
 * @param formId 目标表单id
 * @returns json对象
 */
function getParam(formId){
	var currForm = Ext.getCmp(formId).getForm();
	var fieldColl = currForm.getFields();
	var paramColl = new Ext.util.MixedCollection();//集合（表单参数）
	fieldColl.each(function(item,index,len){
		paramColl.add(item.getName(), item.getValue());
	});
	//alert(Ext.encode(paramColl).map);
	var paramJson = Ext.decode(Ext.encode(paramColl)).map;//查询参数json对象（所有查询对象）
	//alert(Ext.JSON.encode(paramJson));
	return paramJson;
//	return Ext.JSON.encode(paramJson);
}

/**
 * 通讯(查询)
 * @param storeId 表格数据仓库id
 * @param formId 查询表单id
 */
function comm_query(storeId,formId,tx_code){
	var currStore = Ext.data.StoreManager.lookup(storeId);
//	var paramJson = getParam(formId);
	currStore.on('beforeLoad',function(){
		var formParams = getParam(formId);
//		formParams.add("tx_code",tx_code);
		formParams.tx_code = tx_code;
		Ext.apply(tableStore.proxy.extraParams,{
			tranData:Ext.encode(formParams)
		});
	});
	currStore.loadPage(1);
}

/*
 * 直接提交ajax请求
 * url:ajax请求地址
 * storeId：请求后刷新store
 * param:ajax待发送报文
 */
function comm_ajax(url,storeId,params){
	Ext.Ajax.request({
		type:'ajax',
		url:url,
		params:params,
		success:function(resp,opts){
			if(storeId != ""){
				var currStore = Ext.data.StoreManager.lookup(storeId);
				try{//机构树操作不适应普遍刷新，需要特殊的局部刷新
					setTimeout(function(){
						currStore.reload();
					},1000);
				}catch(e){}
			}
		},
		failure:function(resp,opts){
		}
	});
}




/**
 * 直接表单提交通讯
 * @param url
 * @param storeId
 * @param formId
 */
function comm_dir(url,storeId,formId){
//	alert(updFormPanel);
//	alert(storeName);
	var currStore = Ext.data.StoreManager.lookup(storeId);
//	alert("currStore1111:"+currStore);
//	alert(updFormPanel.getForm().isValid());
	updFormPanel = Ext.getCmp(formId);
	if(!updFormPanel.getForm().isValid()){
		msgAlert("错误:表单非法,无法提交！");
	}
	if(updFormPanel.getForm().isValid()){
		/*updFormPanel.getForm().submit({
			waitTitle:'提示',
			waitMsg:'正在提交数据请稍候...',
	//		url:'User_userUpdateExt.action',//提交到更新
			url:url,
			success:function(currForm,action){
				Ext.Msg.show({title:'提示',msg:action.result.errorMsg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
				currStore.reload();
			},
			failure:function(currForm,action){
				//sessionValidate(action.result.errors.msg);
			}
		});*/
		var formParams = getParam(formId);
		var params = {"tranData":Ext.encode(formParams)};
		comm_ajax(url,storeId,params);
		handlerCancle();
	}
}

/**
 * 通信：所有参数放入json报文中
 * @param url 后台actin地址
 * @param storeId 待刷新表格store
 * @param json json报文
 * @param formId 表单id（用于校验表单合法性）
 */
function comm_json(url,storeId,json,formId){
	//alert("数据通讯comm_update"+url+"|"+storeId+"|"+formId);
//	var paramJson = getParam(formId);
	if(Ext.getCmp(formId).getForm().isValid()){
		Ext.Ajax.request({
			url:url,
			params:{"data":json},
			success:function(resp,opts){
				var currStore = Ext.data.StoreManager.lookup(storeId);
				try{//机构树操作不适应普遍刷新，需要特殊的局部刷新
					currStore.reload();
				}catch(e){}
			},
			failure:function(resp,opts){
				msgAlert("通信异常");
			}
		});
		handlerCancle();
	}else{
		msgAlert("表单非法,无法提交");
	}
	return;
}

/**
 * 通讯(更新)：不适合一个field多个值的情况（如：itemselector）
 * @param url 更新操作url
 * @param storeId 表格数据仓库id
 * @param formId 数据录入表单id
 * @param txCode 交易码
 */
function comm_update(url,storeId,formId){
	//alert("数据通讯comm_update"+url+"|"+storeId+"|"+formId);
	var paramJson = getParam(formId);
	if(Ext.getCmp(formId).getForm().isValid()){
		Ext.Ajax.request({
			url:url,
			params:paramJson,
			success:function(resp,opts){
				var currStore = Ext.data.StoreManager.lookup(storeId);
				try{//机构树操作不适应普遍刷新，需要特殊的局部刷新
					currStore.reload();
				}catch(e){}
			},
			failure:function(resp,opts){
				msgAlert("通信异常");
			}
		});
		handlerCancle();
	}else{
		msgAlert("表单非法,无法提交");
	}
	return;
}
/**
 * 用户登录
 * @param form
 */
function login(form){
	if(form.isValid()){
		form.submit({
			waitTitle:'提示',
			waitMsg:'正在验证用户信息，请稍候...',
			success:function(currentForm,action){
				var location;
				/*if ((navigator.userAgent.indexOf('MSIE') >= 0 || (navigator.userAgent.indexOf('.NET')) >= 0)&& (navigator.userAgent.indexOf('Opera') < 0)) {//ie
					location = 'pages/main/main.jsp';
				}
				else{
					location = 'pages/main/main.jsp';
				}*/
				location = 'pages/main/main.jsp';
				//alert(action.result.errorCode);
				if(action.result.errorCode == '00'){//验证通过
					window.location=location+'?consbReqId='+new Date().getTime();
				}else{//验证失败
//					Ext.Msg.show({title:'提示',msg:action.result.msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
					msgAlert(action.result.errorMsg);
				}
			},
			failure:function(form,action){
				//alert("tttttt");
//						Ext.Msg.alert('提示',action.result.msg+"|"+action.failureType);
//				Ext.Msg.show({title:'提示',msg:action.result.msg,icon:Ext.MessageBox.INFO,buttons:Ext.Msg.YES,buttonText:{yes:'确定'}});
				msgAlert(action.result.errorMsg);
			}
		});
	}
}
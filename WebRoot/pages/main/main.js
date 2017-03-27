// JavaScript Document

/*var tree = new Ext.tree.TreePanel({
	loader:new Ext.tree.TreeLoader({dataUrl:''}),
	title:'west',
	region:'west',
	split:true,
	border:true,
	collapsible:true,
	width:120,
	minSize:80,
	maxSize:200
});*/
function pageOnload(){
	//window.onbeforeunload = onbeforeunload_handler;  //关闭浏览器页面，清除session 2016-04-07
	window.onunload = unload_handler;
}
function unload_handler(){ 
//	if(event.clientX>document.body.clientWidth    &&    event.clientY<0 || event.altKey){
//		logout("");
//	}
	logout("");
//	var warning="如果你此时离开，所有未提交的信息将全部丢失，是否离开？";  
//   return warning;   
}

	
var userId = top.varpool.userId;
var userName = top.varpool.userName;
var orgName = top.varpool.orgName;
Ext.application({
	name:'borderRegion',
	launch:function(){
		pageOnload();
//		userValidate(userId);
		var jsonTest = {
			'name':'tang',
			'sex':'男',
			'rec':[{
				'id':'001',
				'value':'value001'
			},{
				'id':'002',
				'value':'value002'
			},{
				'id':'003',
				'value':'value003'
			},]	
		};
//		Ext.MessageBox.alert('json对象测试结果','jsonTest.rec[1].value:'+jsonTest.rec[1].value+'<br/>jsonTest.name:'+jsonTest.name,funCallBack);
		function funCallBack(id){
//			Ext.Msg.alert("点击ok按钮，获取按钮id","ok按钮id:"+id);
		}
		//Ext.create('Ext.panel.Panel',{
		var myViewPort = Ext.create('Ext.container.Viewport',{
			//width:1024,
			width:'100%',
			//height:600,
			layout:'border',
			items:[{
				region:'north',
				id:'northFrame',
				border:false,
				bodyStyle:"background-color:#638B97;background-image:url(images/ext/north.png)",//background-image:url('images/sys/north.jpg');
//				xtype:'panel',
				height:55,
				split:false,
//				html:'<span>耗材管理</span>',
				margin:'0 -1 0 -1',
				items:[
				      /* new Ext.Button({
				    	   margin:'30 0 0 0',
				    	   text:'<span north-toolbar-font>注销</span>',
			    		   cls:'x-btn-text-icon font-color',
			    		   handler:function(){
			    			   window.location = 'pages/login/login.jsp';
			    		   }
				       })*/
				       /*new Ext.toolbar.Toolbar({
				    	   id:'topTool',
//				    	   padding:'30 0 0 0',
				    	   width:200,
				    	   style: 'background-color:red; background-image:url();marginBottom:10)', 
				    	   items:['->','-',{
				    		   text:'<span north-toolbar-font>注销</span>',
				    		   cls:'x-btn-text-icon font-color',
				    		   handler:function(){
				    			   window.location = 'pages/login/login.jsp';
				    		   }
				    	   }]
				       })*/
				]/*,
				bbar:[
					new Ext.toolbar.Toolbar({
						   id:'topTool',
						   floating:true,
					//	   padding:'30 0 0 0',
						   width:200,
						   style: 'background-color:red; background-image:url();marginBottom:10)', 
						   items:['->','-',{
							   text:'<span north-toolbar-font>注销</span>',
							   cls:'x-btn-text-icon font-color',
							   handler:function(){
								   window.location = 'pages/login/login.jsp';
							   }
						   }]
					})
				]*/
			},{
				//title:'south',
				region:'south',
				xtype:'panel',
				border:true,
				height:20,
				split:false,
//				html:'<div style="width:100%;text-align:center;border:0px solid red">南天信息</div>',
				items:[
						{
							xtype:"box",
							autoEl:{
								tag:"div",
//								cls:"copyright",
								style:'border:0px solid red;width:100%;height:14px;margin:2px;background-color:#dfe8f6;text-align:center',
//								html:'<div style="width:100%;text-align:center;border:0px solid red">Copyright &copy; 2014 Nantian Information CQ All Rights Reserved</div>',
								html:'Copyright &copy; 2016 Nantian Information CQ All Rights Reserved'//,
//								html:"Copyright &copy; 2010 Nantian Information Equipment Co.,Ltd. All Rights Reserved."
							}
						}
					],
				margins:'0 -1 0 -1'
				
				
			},{
				title:'系统功能',
				border:false,
				region:'west',
				id:'west-panel',
				xtype:'panel',
				margins:'0 2 0 0',
				width:200,
				collapsible:true,
				layout:'accordion',
				split:true,
				minWidth: 175,
				maxWidth:300,
				layoutConfig:{
					titleCollapse:true,
					animate:true,
					activeOnTop:true
//					layout:'fit'
				},
				tools: [{
			        itemId: 'refresh',
			        type: 'refresh',
//			        hidden: true,
			        handler: function(){
			        	Ext.MessageBox.show({
			        		title:'提示',
			        		msg:'正在处理...',
			        		progressText:'导航初始化中...',
			        		width:300,
			        		progress:true,
			        		closable:false,
			        		animateTarget:this
			        	});
			        	Ext.Ajax.request({
			        		type:'ajax',
			        		url:'naviRefresh.html',
			        		success:function(){
			        			Ext.MessageBox.updateProgress('1','导航初始化完成','处理完毕');
			        			setTimeout(function(){Ext.MessageBox.hide();},500);
			        			setTimeout(function(){document.location.reload();},1000);
			        		},
			        		failure:function(){}
			        	});
			        	
			        }
			    }]
			},{
				title:'内容区域',
				region:'center',
				layout:'border',
				border:false,
//				margins:'3 1 0 0',
				items:[{
					region:'north',
					height:28,
					items:[{
						xtype:'box',
						autoEl:{
							tag:'div',
							style:'border:0px solid red;width:100%;height:20px;margin:3px;background-color:#dfe8f6',
							children:[
										{
											tag:"img",
											src:"images/ext/path.png",
											style:"margin:2px 0px 2px 5px;vertical-align:middle"
											
										},
										{
											tag:"span",
											id:"navi",
											style:"margin:0px 0px 2px 5px;vertical-align:middle;color:#b4b4b4"
										}
									]
						}
					}]//,
//					margins:'0 0 4 0'
				},{
					region:'center',
//					layout:'border',
					layout:'fit',
					border:false,
					xtype:'panel',
					id:'mainContent',
					contentEl:'contendIframe'//,
//					margins:'10 0 4 0'
				}]
			
				/*xtype:'panel',
				
				callapsible:true,
				id:'mainContent',
				contentEl:'contendIframe'*/
			}],
			renderTo:Ext.getBody()	
		});
		
		getSubject();
		
		
		//顶部工具条
		Ext.create('Ext.toolbar.Toolbar', {
		    renderTo: Ext.getBody()	,
		    width: '100%',
		    margin:'20 0 0 0',
		    items: [
		        '->',
		        {
		            text: '<span style="color:#9D013A;font-weight:800">退出系统</span>',
		            //cls:'x-btn-text-icon font-color',
		            iconCls:'Errordelete',
//		            icon:'images/ext/logOff.png',
		           // tooltip:'退出系统【快捷键：ctrl+q】',
		            handler:function(){
	    			   Ext.Msg.confirm('提示','确认退出系统？',function(btn){
	    				   if(btn == 'yes'){
	    					   logout(userId);
	    				   }   
	    			   });
	    		    }
		        },'-',{
		        	text : '<span style="color:#9D013A;font-weight:800">'+orgName+'</span>',
		        	iconCls:'House',
	            	margin:'0 0 0 0'
		        },
		        '-',{
	            	text : '<span style="color:#9D013A;font-weight:800">'+userName+"("+userId+')</span>',
	            	iconCls:'User',
	            	margin:'0 30 0 0'
	            }
	            /*,'-',
		        {
		            xtype: 'splitbutton',
		            text : 'Split Button'
		        }*/
		    ]
		});
//		changePage('pages/main/welcome.jsp');
	}
});

function getSubject(){
	var request = Ext.Ajax.request({
		//url:'Menu_getMenuItem.action',
		url:'getMenuItem.html',
//		url:'naviRefresh2.html',
		
		/*params:{
			user_id:userId
		},*/
		method:'GET',
		success:addSubject,
		failure:function(resp,opts){
//			Ext.Msg.alert("提示","登录失效，请重新登录");
		}
	});
}
function addSubject(response,options){
	var cmp=Ext.getCmp('west-panel');
	var strData=response.responseText;
//	var jsonObject = Ext.JSON.encode(strData);
//	alert(jsonObject.get('root'));
	var array=Ext.JSON.decode(strData);
//	alert(array.length);
	for(var i=0;i<array.length;i++){
		cmp.add({
			title:array[i].menuText,
			id:array[i].menuId,
			html:'<div id="div'+array[i].menuId+'"style="height:100%;width:100%;overflow:auto;border:0px solid red"></div>',
			iconCls:'nav',
			border:false,
			layout:'fit',
			listeners:{
	 			expand:function(p){
//	 				reDoLayout();
	 				document.getElementById("west-panel").onclick = function(){
	 					reDoLayout();
	 				};
 					var treeId='tree'+p.id;
// 					alert(treeId);
					if(Ext.getCmp(treeId)==null){
						generateTree(p.id);
					}else{
				   		Ext.getCmp(treeId).getRootNode().reload();
				   	}	
					Ext.getCmp(array[i].menuId).doLayout();
	 			}
		 	}
		});
		
		/*Ext.getCmp(array[i].menuId).on('expand',function(p){
				alert("expand");
				var treeId='tree'+p.id;
				if(Ext.getCmp(treeId)==null){
					generateTree(p.id);
				}else{
			   		Ext.getCmp(treeId).getRootNode().reload();
			   	}	
			});*/
	}
	
//	cmp.doLayout();
	generateTree(array[0].menuId);
	cmp.doLayout();
	
}


function getTreeStore(){
	
}


function generateTree(subjectId){
//alert('generateTree');
	var divID='div'+subjectId;
	//--------------------数据源远程begin------------------
	Ext.regModel('orgInfo',{
		fields:['menuId','parentId','menuText','nodeType','urlText','isLeaf',{name:'id',mapping:'menuId'}]
	});
	var tree_store = Ext.create('Ext.data.TreeStore',{
		storeId:'tree_storeid',
		model:'orgInfo',
		nodeParam:'menuTreeId',
		proxy:{
			type:'ajax',
//			url:'TreeTest',
			url: 'getMenuItem.html?menuId='+subjectId,
//			url: 'naviRefresh2.html?menuId='+subjectId,
//			url: 'Menu_getMenuItem.action',
//			reader:'json'//reader必须为json
			reader:new Ext.data.JsonReader({
				root:'root'
			})
		},
		folderSort: true,
		autoLoad:true,
		root:{
			name:'根节点',
			id:'-1',
			expanded: true//默认展开根节点
		}
	});
	
	//获得TreePanel实例
//	var tree = Ext.create('Ext.tree.Panel',{
	var tree = Ext.create('Ext.tree.TreePanel',{
		renderTo:Ext.get(divID),
//		renderTo:Ext.getBody(),
		//title:'TreeGrid',
//		width:300,
//		width:'100%',
		rootVisible:false,
		singleExpand:false,
		//height:200,
//		layout:'fit',

		store:tree_store,
		border:false,
		autoScroll:false,
		autoWidth:true,
		autoHeight:true,
		autoScroll:true,
		margin:'-1 0 0 0',
		id:'tree'+subjectId,
		hrefTarget:'mainContent',
		displayField:'menuText'//,
		//法一：面板配置项绑定事件处理函数
		/*listeners:{
			itemclick:function(view,rec,item,index,e,obj){
				if(rec.get('nodeType') == '1'){
					changePage(rec.get('urlText'));	
					alert(item);
				}	
			}
		}*/
	});
	tree.on('itemclick',function(view,rec,item,index,e,obj){
//		alert(rec.raw.id);
//		alert(rec.raw.menuText);
		/*alert(item);
		alert(index);
		alert(e);
		alert(obj);*/
//		alert(rec.isLeaf());
//		alert(rec.getPath());
		Ext.getDom('navi').innerHTML=rec.raw.menuText;
//		loadIFrame(item);
		
		if(rec.get('nodeType') == '1'){
			changePage(rec.get('urlText'));	
		}	
	});
	
//	监听左框架改变，重新渲染导航条panel
	Ext.getCmp('west-panel').on('resize',function(){
		tree.doLayout(); 
	});
	
}


/*function loadIFrame(node)
{
	alert("loadIFrame");
	//判断是否是叶子节点
     if(node.isLeaf())
     {
    	setNavigation(node);
     	var urlText=node.attributes.urlText;
     	if(urlText=="") 
     		urlText="about:blank";
     	changePage(urlText);
     }
}*/

function setNavigation(node)
{
	alert(node);
	var arrRet=[];
	var path=node.getPath();
	alert(path);
	/*var tree=node.getOwnerTree();
	var id=tree.getEl().id.substr(3);
	var base=(Ext.getCmp(id)).initialConfig.title;
	arrRet.push(base);
	var arrPath=path.split('/');
	for(var i=2;i<arrPath.length;i++)
	{
		arrRet.push(tree.getNodeById(arrPath[i]).text)
	}
	var strNavi=arrRet.join('-->');
	Ext.getDom('navi').innerHTML=strNavi;*/
	
}




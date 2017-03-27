// JavaScript Document
Ext.application({
	name:'tresTest',
	launch:function(){
		//获得TreeStore实例
		var tree_store = Ext.create('Ext.data.TreeStore',{
			root:{
				expanded:true,
				text:'根节点',
				children:[
					{text:'menu1',leaf:true},
					{text:'menu2',children:[
						{text:'menu2-1',leaf:true},
						{text:'menu2-2',children:[
							{text:'menu2-2-1',leaf:true},
							{text:'menu2-2-2',leaf:true},
							{text:'menu2-2-3',leaf:true},
						]},
						{text:'menu2-3',children:[
							{text:'menu2-3-1',leaf:true},
							{text:'menu2-3-2',leaf:true},
							{text:'menu2-3-3',leaf:true},
						]}
					]}
				]	
			}	
		});
		//获得TreePanel实例
		var tree = Ext.create('Ext.tree.Panel',{
			renderTo:Ext.get('tree'),
			//title:'TreeGrid',
			width:300,
			rootVisible:false,
			singleExpand:true,
			height:200,
			store:tree_store,
			border:true,
			//autoScroll:false
		});
		
	}	
});
/*
var tree = new Ext.tree.TreePanel({
	el:'tree'	
});
var root = new Ext.tree.TreeNode({
	test:"root节点"	
});
tree.setRootNode(root);
tree.render();
*/


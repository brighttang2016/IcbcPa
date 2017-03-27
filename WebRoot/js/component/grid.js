/**
 * 表格组件
 * 2015-11-17 brighttang
 */


function gridCmp(){
	//重新配置表格(根据后台返回，动态显示列)
	this.reconfig = function(grid){
		tableStore = grid.getStore();
		tableStore.on('load',function(store,records,successful,eOpts){
			
			//获取records值测试2015-11-06
			//方法一：
			/*records = tableStore.data;
			record = records.get(0);
			userId = record.get("userId");
			record = records[0];*/
			//方法二：
			/*record = records[0];
			alert(record.get("userId"));*/
			//console.log(records);
			if(records != null && records.length > 0)
				record = records[0];
			else 
				record = null;
			//console.log(record);
			var jsonArray = new Array();
			if(record != null){//后台有list数据返回
				for(key in record.getData()){
					var coll = new Ext.util.MixedCollection();
					var colName = key+"_colname";//列名
					var widthName = key+"_width";//宽度
					var reg = new RegExp('col');
					if(!reg.test(key)){
						//console.log(key);
						//console.log(colName);
						coll.add('locked',true);
					}else{
						coll.add('locked',false);
					}
					coll.add('header',record.raw[colName]);
					coll.add('dataIndex',key);
					coll.add('width',parseInt(record.raw[widthName]));
					coll.add('sortable',false);
					coll.add('align','center');
					//console.log(coll);
					if(record.raw[colName] != '' && record.raw[colName]!= null && record.raw[colName]!= undefined)
						jsonArray.push(coll.map);
				}
			}
			grid.reconfigure(tableStore,jsonArray);
		});
	};
}
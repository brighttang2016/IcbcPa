// The data store containing the list of states
var states = Ext.create('Ext.data.Store', {
    fields: ['value', 'name'],
    data : [
        {"value":"优秀", "name":"优秀"},
        {"value":"良好", "name":"良好"},
        {"value":"称职(合格)", "name":"称职(合格)"},
        {"value":"基本称职(基本合格)", "name":"基本称职(基本合格)"},
        {"value":"不称职(不合格)", "name":"不称职(不合格)"},
        {"value":"未考核", "name":"未考核"}
    ]
});

// Create the combo box, attached to the states data store
Ext.define('assCombo',{
	extend:'Ext.form.field.ComboBox',
	name:'assptName',
	fieldLabel: '考核结果',
    store: states,
    queryMode: 'local',
    displayField: 'name',
    valueField: 'value',
    renderTo: Ext.getBody(),
    autoFitErrors:false,
    width:280,
    editable:false
});
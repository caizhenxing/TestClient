
Ext.define('MyApp.view.AddCheckPointWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.AddCheckPointWindow',
    height: 400,
    id: 'AddCheckPointWindow',
    width: 800,
    modal:true,
    layout: {
        type: 'fit'
    },
    title: '配置检查点',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                xtype: 'gridpanel',
                id: 'AddCheckPointGrid',
                title: '',
                store: 'CheckPoint',
                columns: [
                {
                    xtype: 'actioncolumn',
                    flex:1,
                    items: [
                        {
                            handler: function(view, rowIndex, colIndex, item, e, record, row) {
                                Ext.MessageBox.confirm(
                                "confirm",
                                "确认删除？",
                                function(e){
                                    if(e=='yes'){
                                        Ext.getStore('CheckPoint').removeAt(rowIndex);
                                        Ext.getStore('CheckPoint').sync({
                                            success:function(){
                                            	Ext.getStore('CheckPoint').load();
                                            }
                                        });
                                    }
                                }); 
                            },
                            icon: 'image/delete.png',
                            tooltip: 'delete'
                        }
                    ]
                },
                {
                    xtype: 'gridcolumn',
					flex:3,
                    dataIndex: 'name',
                    text: '名称',
                    editor: {
                        xtype: 'textfield'
                    },
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                    	return value.replace(new RegExp("<","gm"),"&lt;");
                    }
                },
                {
                    xtype: 'gridcolumn',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {

                        if(value==null || value==""){
                            return "请选择";
                        }else{
                            return value;
                        }
                    },
                    flex: 2,
                    dataIndex: 'type',
                    text: '类型',
                    editor: {
                        xtype: 'combobox',
                        editable:false,
                        id:'CheckPointType',
                        store: ['contain','pattern','sql'],
                        listeners: {
                        	'change': function(that, newValue, oldValue, eOpts){
                        		if(newValue=='sql'){
                        			Ext.getCmp('Base').timeStamp=new Date().getTime();
                        			Ext.widget('SqlVerificationSettingWindow').show();
                        		}
                        	}
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'checkInfo',
                    text: '检查字符串',
                    flex: 8,
                    editor: {
                        xtype: 'textarea',
                        id:'CheckInfoTextArea',
 						height:100
                    },
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                    	return value.replace(new RegExp("<","gm"),"&lt;");
                    }
                }],
                dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                                var store = Ext.getStore('CheckPoint');
                                store.insert(0,{});
                                var rowEditing = Ext.getCmp('AddCheckPointGrid').getPlugin("RowEditPlugin");
                                rowEditing.startEdit(0,1); 
                            },
                            icon: 'image/add.png',
                            tooltip: '新增'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                                Ext.getStore('CheckPoint').load();
                            },
                            icon: 'image/refresh.png',
                            tooltip: '刷新'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                            	Ext.widget('GenerateCheckpointsWindow').show();
                            },
                            icon: 'image/generate.png',
                            tooltip: '批量导入检查点'
                        }
                    ]
                }],
            	plugins: [
                Ext.create('Ext.grid.plugin.RowEditing', {
                    pluginId: 'RowEditPlugin',
                    listeners: {
                        edit: {
                            fn: me.onRowEditingEdit,
                            scope: me
                        }
                    }
                })
                ]}
            ],
            listeners: {
                activate: {
                    fn: me.onSuiteManagementActivate,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onRowEditingEdit: function(editor, context, eOpts) {
        Ext.getStore('CheckPoint').sync({
            success:function(){
                if(Ext.getCmp('CheckPointType').getValue()=='sql'){
                	Ext.getCmp('AddCheckPointWindow').close();
                }else
                	Ext.getStore('CheckPoint').load();
            }
        });

    },
    
    onSuiteManagementActivate: function(window, eOpts) {
    	console.log('check point window is actived');
		Ext.getStore('CheckPoint').proxy.extraParams.folderName=Ext.getCmp('Base').folderName;
		console.log(Ext.getStore('CheckPoint').proxy.extraParams.folderName);
		Ext.getStore('CheckPoint').load();
    }    
    
});
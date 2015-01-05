Ext.define('MyApp.view.AddFolderWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.AddFolderWindow',
    height: 100,
    id: 'AddFolderWindow',
    width: 300,
    layout: {
        type: 'fit'
    },
    title: '添加目录',
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	items:[
        		{
        			xtype:'form',
        			id:"AddFolderForm",
        			items:[
        				{
        					xtype:'textfield',
        					anchor: '100%',
        					fieldLabel: '目录名',
        					name: 'folderName',
        					regex:/^((?![\/:*?"<>|@']).)*$/,
        			    	regexText:"禁止包含字符\/:*?\"<>|@'",
        				},
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                            	var thestore=Ext.getStore('TreeStore');
                            	var form=Ext.getCmp('AddFolderForm').getForm();
                            	if(form.isValid()){
                            		var folder=form.findField('folderName').getValue();
                                	var path=Ext.getCmp('AddFolderForm').theNode.raw.folderName+"/"+folder+"-dir";
                                	thestore.proxy.extraParams={
                                		folderName:path
                                	};
                                	
                                	var childnode={
                                		    text:folder,
                                		    leaf:false,
                                		    folderName:path
                                	};
                                	Ext.getCmp('AddFolderForm').theNode.insertChild(0,childnode);
                                	Ext.getCmp('AddFolderForm').theNode.expand();
                                	me.close();
                                	
                                	thestore.sync({
                                		success:function(betch,options){
                                		},
                                		failure:function(betch,options){
                                			Ext.Msg.alert("错误",thestore.getProxy().getReader().rawData.msg);
                                			thestore.load();
                                		}
                                	});
                            	}
                            },
                            text: '新建'
                        }        				
        			]
        		}
        	]
        });
        me.callParent(arguments);
    }
});
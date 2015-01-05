Ext.define('MyApp.view.AddItemWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.AddItemWindow',
    height: 100,
    id: 'AddItemWindow',
    width: 300,
    layout: {
        type: 'fit'
    },
    title: '添加测试',
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	items:[
        		{
        			xtype:'form',
        			id:"AddItemForm",
        			items:[
        				{
        					xtype:'textfield',
        					anchor: '100%',
        					fieldLabel: '测试名',
        					name: 'folderName',
        					regex:/^((?![\/:*?"<>|@']).)*$/,
        			    	regexText:"禁止包含字符\/:*?\"<>|@'",
        				},
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                            	var thestore=Ext.getStore('TreeStore');
                            	var form=Ext.getCmp('AddItemForm').getForm();
                            	var folder=form.findField('folderName').getValue();
                            	if(form.isValid()){
                            		var path=Ext.getCmp('AddItemForm').theNode.raw.folderName+"/"+folder+"-leaf";
                                	thestore.proxy.extraParams={
                                		folderName:path
                                	};
                                	
                                	var childnode={
                                		    text:folder,
                                		    leaf:true,
                                		    folderName:path
                                	};
                                	Ext.getCmp('AddItemForm').theNode.insertChild(0,childnode);
                                	Ext.getCmp('AddItemForm').theNode.expand();
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
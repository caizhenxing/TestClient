Ext.define('MyApp.view.PasswordConfirmationForHistoryFileClean', {
	extend: 'Ext.window.Window',
    alias: 'widget.PasswordConfirmationForHistoryFileClean',
    title:"密码",
	modal:true,
	width:250,
	height:115,
	collapsible:false,
	resizable:false,
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            items: [
            {
            	xtype: 'form',
            	//autoWidth:true,
            	layout:"form",
            	frame:true,
            	labelAlign:"right",
		 		items:[
 		        {
 		        	xtype:"label",
 		        	height : 20,
 		        	text :"请输入密码:"
 		        },
 		        {
 		        	xtype : "textfield",
 		        	inputType : 'password',
 		        	allowBlank:false,
 		        	maxLength:255,
 		        	height : 20,
 		        	width:228,
 		        	id: 'pwd4historyDeletion',
 		        }
 		        ],
 		        buttons : [
 		        {
 		        	text : '确定',
 		        	handler : function(){
 		        		var pwd=Ext.getCmp("pwd4historyDeletion").getValue();
 		        		if(pwd==='153348196'){
 		        			Ext.widget('DeleteExpiredHistoryFilesWindow').show();
 		        			me.close();
 		        		}
 		        		else
 		        			Ext.Msg.alert("密码错误","密码错了啦，请联系管理员：\r\nliang.chen@ctrip.com");
 		        	}
 		        },
 		        {    
 		        	text : '取消',
 		        	handler : function(){
 		        		me.destroy();
 		        	}
 		        }]
            }]
        });
        me.callParent(arguments);
    }
});
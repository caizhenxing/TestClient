function loadAnchor (url) {
	console.log(url);
	//if(!url && typeof(url)!="undefined" && url!=0){
	if(url){
		//console.log(url);
		var queryobject=Ext.Object.fromQueryString(url);
		//console.log(queryobject.name);
		
		if(queryobject.name=='Ribao'){
			Ext.getCmp('RibaoButton').fireEvent("click");
			var projectid=queryobject.projectid;

			Ext.getStore('TestProject').load({
			    scope: this,
			    callback: function(records, operation, success) {
			        //Ext.getCmp('ProjectCombo1').select(Ext.getStore('TestProject').findRecord('id',projectid).get('value'));
			    	Ext.getCmp('ProjectCombo1').select(Ext.getStore('TestProject').findRecord('id',projectid));
			    }
			});
			
			
			//console.log(queryobject.RibaoDay);
			if(queryobject.RibaoDay=='RibaoDay1'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=0;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDay5'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=2;
			    Ext.getStore('DailyBugData').proxy.extraParams.start=4;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDay10'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=2;
			    Ext.getStore('DailyBugData').proxy.extraParams.start=9;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDay20'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=2;
			    Ext.getStore('DailyBugData').proxy.extraParams.start=19;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDay30'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=2;
			    Ext.getStore('DailyBugData').proxy.extraParams.start=29;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDay60'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=2;
			    Ext.getStore('DailyBugData').proxy.extraParams.start=59;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDay120'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=2;
			    Ext.getStore('DailyBugData').proxy.extraParams.start=119;
			    Ext.getStore('DailyBugData').load();
			}else if(queryobject.RibaoDay=='RibaoDayAll'){
			    Ext.getStore('DailyBugData').proxy.extraParams.projectId=projectid;
			    Ext.getStore('DailyBugData').proxy.extraParams.type=1;
			    Ext.getStore('DailyBugData').load();
			}
		}else if(queryobject.name=='Zongbao'){
			Ext.getCmp('ZongbaoButton').fireEvent("click");
			//Ext.getCmp('ProjectCombo2').select(queryobject.projectid);
			Ext.getStore('TestProject').load({
			    scope: this,
			    callback: function(records, operation, success) {
			        //Ext.getCmp('ProjectCombo2').select(Ext.getStore('TestProject').findRecord('id',queryobject.projectid).get('value'));
			    	Ext.getCmp('ProjectCombo2').select(Ext.getStore('TestProject').findRecord('id',queryobject.projectid));
			    }
			});
			Ext.getStore('FenMokuaiHaoshi').proxy.extraParams.projectId=queryobject.projectid;
			Ext.getStore('FenMokuaiHaoshi').load();
			Ext.getStore('FenMokuaiShuliang').proxy.extraParams.projectId=queryobject.projectid;
			Ext.getStore('FenMokuaiShuliang').load();
			Ext.getStore('FentuanduiHaoshi').proxy.extraParams.projectId=queryobject.projectid;
			Ext.getStore('FentuanduiHaoshi').load();
			Ext.getStore('FensitHaoshi').proxy.extraParams.projectId=queryobject.projectid;
			Ext.getStore('FensitHaoshi').load();
			
		}
	
	}
	
}


Ext.onReady (function () {
    var url = document.location.hash.split('#!')[1];
    loadAnchor (url);
});

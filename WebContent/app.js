/*
 * File: app.js
 *
 * This file was generated by Sencha Architect version 2.2.2.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

//@require @packageOverrides
Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
    models: [
	    'CheckPoint',
	    'TestHistory',
	    'TestItem',
	    'BatchRun',
	    'ScheduledJob',
	    'PreConfig',
	    'QueryBoundDataItem',
	    'ServiceBoundDataItem',
	    'SqlVerificationDataItem',
	    'TestCaseResult',
	    'TestStatusDistribution',
	    'TestPassedRate',
	    'RunningSet',
	    'ScheduledRunningSet'
    ],
    stores: [
   	 	'CheckPoint',
   	 	'CheckPointResult',
   	 	'TestHistory',
   	 	'TestItem',
   	 	'BatchRun',
   	 	'ScheduledJob',
   	 	'PreConfig',
   	 	'QueryBoundDataItem',
   	 	'ServiceBoundDataItem',
   	 	'SqlVerificationDataItem',
   	 	'TestCaseResult',
   	 	'TestStatusDistribution',
   	 	'TestPassedRate',
   	 	'OutputParameterDataItem',
   	 	'RunningSet',
   	 	'SelectedTreeStore',
   	 	'ScheduledRunningSet'
    ],
    views: [
        'Base',
        'AddFolderWindow',
        'AddItemWindow',
        "ModifyNodeWindow",
        "MainPanel",
        "TestConfigWindow",
        "FolderPanel",
        "EnvEditWindow",
        "AddCheckPointWindow",
        "GenerateParametersWindow",
        "TestHistoryWindow",
        'BatchExecutionWindow',
        'BatchTestHistoryWindow',
        'PasswordConfirmationWindow',
        'RootPanel',
        'ScheduledJobWindow',
        'FolderSelectionWindow',
        'CronExpressionSettingWindow',
        'TestSelectionWindow',
        'PreConfigurationWindow',
        'PreDBQuerySettingWindow',
        'PreServiceSettingWindow',
        'SqlActionSettingWindow',
        'SqlVerificationSettingWindow',
        'FormatSoa1XmlWindow',
        'ShrinkResponseStringWindow',
        'ServiceActionWindow',
        'CopyTestAtSameDirWindow',
        'GenerateCheckpointsWindow',
        'DeleteExpiredHistoryFilesWindow',
        'PasswordConfirmationForHistoryFileClean',
        'OutputParameterSettingWindow',
        'DeletedModeWindow',
        'RunningSetSettingWindow',
        'RunningSetUpdateWindow',
        'RunningSetPanel',
        'TestCaseResultGrid',
        'TestStatusDistributionPieChart',
        'TestPassedRateTrendChart',
        'ScheduledRunningSetWindow'
    ],
    autoCreateViewport: true,
    name: 'MyApp'
});
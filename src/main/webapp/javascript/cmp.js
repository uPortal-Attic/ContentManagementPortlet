/*
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 **/
 
var contentManagementPortlet = contentManagementPortlet || {};

(function($, cmpInstance) {
    
    if (cmpInstance.loaded) {
        return;
    }

    cmpInstance.CMP = function(callerSettings) {
        var settings = $.extend( {
            formUrl: null, 
			successFunction: null,
			beforeFormSubmit: null
        }, callerSettings || {});

        this.addFormHandler = function(formId) {
						
            $(formId).ajaxForm({
                url: settings.formUrl,
                type: 'POST',
                dataType: 'json',
                clearForm: true,
                beforeSubmit: settings.beforeFormSubmit,
                success: settings.successFunction
            });
        };
        return this;
    };
    
    cmpInstance.loaded = true;
})(jQuery, contentManagementPortlet);

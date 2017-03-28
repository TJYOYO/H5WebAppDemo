/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();

//给H5页面中的扫描按钮调用
            function addToCal() {
                var title = "PhoneGap Day";
                //js回调
                var success = function(message) {
                    //alert("Success");
                    //改变内容
                    var result = document.getElementById("result");
                    result.innerHTML = message;
                };
                var error = function(message) {
                    alert("Oopsie! " + message);
                };
                calendarPlugin.createEvent(title, success, error);
            }
            //java直接加载js方法的调用
            function javaCalljs(lastmessage){
                //alert("java调用js"+lastmessage);
                var result = document.getElementById("result");
                result.innerHTML = lastmessage;
            }

var calendarPlugin = {
    createEvent: function(title, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'MyPlugin', // mapped to our native Java class called "CalendarPlugin"
            'addAnroidEntry', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "title": title
            }]
        );
     }
}
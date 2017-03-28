# H5WebAppDemo
Android studio导入项目Platforms中的Android项目既可以运行了！！！

**1: 本机混合开发Phonegap的配置，如下：**
phonegap 版本: 6.4.6
Cordova 版本: 6.5.0

![图片.png](http://upload-images.jianshu.io/upload_images/909565-deae8275001f370e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**2: 创建项目**
命令: 
```
$ cordova create hello com.example.hello HelloWorld  (后两个分拨是项目名称和包名)
$ cd hello 
$ cordova platform add android --save
```

![图片.png](http://upload-images.jianshu.io/upload_images/909565-a21c0777ec4ccfd9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**生成的android 项目目录：**

![图片.png](http://upload-images.jianshu.io/upload_images/909565-0fbb12b258caf2b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

然后导入Platforms文件夹里面的android项目，结构如下：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/909565-88c7b637714f9c31.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

下面开始实现H5和android的数据传递:

####一：以H5的js为起点，实现js调用android，android返回数据js
**1: index.html**

加入内容：
```
<div class="title">H5demo</div>
        <br/><br/>
        <div class="lastparent">
            <p class="tip">输入: </p>
            <br/>
            <textarea rows="10" cols="25" id="result" class="content">在这里输入内容...</textarea>
            <br/><br/><br/>
            <button class="btn" onclick="addToCal();">点击获取数据</button>
            <br/><br/>
        </div>
```

![device-2017-03-28-135937.png](http://upload-images.jianshu.io/upload_images/909565-6d5bf946a3c3e809.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**2: index.css**

加入内容：
```
.title{
    font-size:20px;
    background-color:#3F51B5;
    color:white;
    padding:15px 10px 15px 10px;//上，右，下，左
}

.btn{
    width:120px;
    height:40px;
    font-size:16px;
}

.content{
    font-size:20px;
}

.tip{
    font-size:20px;
}

.lastparent{
    margin: auto;
    text-align: center;
}
```

![device-2017-03-28-135609.png](http://upload-images.jianshu.io/upload_images/909565-280c92032a1005d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**3.index.js**

加入内容:

```
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

```

说明：点击按钮addToCAL , 调用插件calendarPlugin的方法createEvent, 然后触发 cordova.exec, 然后调用插件Myplugin的方法exec, 这样就实现了js调用android，android返回数据js。

**4: 插件Myplugin**

该类是自定义的插件，来实现js调用android的功能，最终调用exec()方法

```
@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        try {
            if (ACTION_ADD_ANDROID_ENTRY.equals(action)) {
                //调用android的dialog
                startDialog(callbackContext);
                return true;
            }
            callbackContext.error("Invalid action");
            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }
    }


```
```
/**
     * 通过js调用java，实现js触发android功能
     * @param callbackContext
     */
    private void startDialog(final CallbackContext callbackContext) {
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("接收到js的触发")
                .setPositiveButton("确定", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbackToJs(callbackContext);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadJsFunction();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
```
点击确定按钮：**由js触发插件的方法execute，然后由callbackContext回调函数，给数据js**

```
/**
     * 由js触发插件的方法execute，然后由callbackContext回调函数，给数据js
     *
     * @param callbackContext
     */
    private void callbackToJs(final CallbackContext callbackContext){
        callbackContext.success("确定按钮回复: 收到了，谢谢!");
    }
```

####二：以android的java为起点，实现java代码调用js

上面dialog，点击了取消按钮 : **webview.loadurl方法可以直接调用js的功能**

```
/**
     * 不通过方法execute, java自己调用js的方法
     *
     * webview.loadurl需要工作的在UI主线程
     */
    private void loadJsFunction(){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mWebView != null){
                    mWebView.loadUrl("javascript:"+ "javaCalljs(\'"+ "取消按钮回复: java自己调用js方法" +"\')");
                }else{
                    Log.d(TAG, "webViewCallJavascript: mwebview is null !");
                }
            }
        });
    }
```

![device-2017-03-28-141311.png](http://upload-images.jianshu.io/upload_images/909565-c0c2bc4ff5e7d0cc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

到这里基于phonegap(cordova)的混合开发demo，实现js和andorid之间的触发数据传递就完成了！

######坑1：webview，退出时，报错了E/webview: java.lang.Throwable: Error: WebView.destroy() called while still attached!

解决:
```
 @Override
    public void onDestroy() {

        //解除webview绑定在父布局的情况，不用时，需要释放
        ViewGroup viewGroup = (ViewGroup)mActivity.findViewById(android.R.id.content);
        SystemWebView webView = (SystemWebView) viewGroup.getChildAt(0);
        viewGroup.removeView(webView);
        webView.removeAllViews();

        mWebView = null;
        super.onDestroy();
    }
```

######坑2：SystemWebChromeClient: file:///android_asset/www/index.html: Line 54 : Refused to execute inline event handler because it violates the following Content Security Policy directive: "default-src 'self' data: gap: https://ssl.gstatic.com 'unsafe-eval'". Either the 'unsafe-inli.........

解决: index.html中, 以<meta http-equiv="Content-Security-Policy"修改如下:
```
<meta http-equiv="Content-Security-Policy" content="default-src * 'unsafe-inline'; style-src 'self' 'unsafe-inline'; media-src *" />
```

demo地址: https://github.com/George-Soros/H5WebAppDemo

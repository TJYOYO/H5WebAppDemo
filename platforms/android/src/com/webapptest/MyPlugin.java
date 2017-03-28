package com.webapptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ViewGroup;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.engine.SystemWebView;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class MyPlugin extends CordovaPlugin {

    private static final String TAG = "MyPlugin";
    public static final String ACTION_ADD_ANDROID_ENTRY = "addAnroidEntry";
    private CallbackContext mCallbackContext;
    private Activity mActivity;
    private CordovaWebView mWebView;

    /**
     * 初始化上下文和webview
     * @param cordova
     * @param webView
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.mActivity = cordova.getActivity();
        this.mWebView = webView;
    }

    /**
     * js默认调用该方法
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return
     * @throws JSONException
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        try {
            if (ACTION_ADD_ANDROID_ENTRY.equals(action)) {
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

    /**
     * 由js触发插件的方法execute，然后由callbackContext回调函数，给数据js
     *
     * @param callbackContext
     */
    private void callbackToJs(final CallbackContext callbackContext){
        callbackContext.success("确定按钮回复: 收到了，谢谢!");
    }

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
}

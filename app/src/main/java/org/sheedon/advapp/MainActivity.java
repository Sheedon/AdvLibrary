package org.sheedon.advapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.sheedon.advlibrary.AdvFactory;
import org.sheedon.advlibrary.AdvParentView;
import org.sheedon.advlibrary.Constant;
import org.sheedon.advlibrary.model.AdvModel;
import org.sheedon.advlibrary.model.SourceModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTitle;
    private AdvParentView mAdvParent;

    int REQUESTCODE_FROM_ACTIVITY = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = findViewById(R.id.title);
        mAdvParent = findViewById(R.id.view_adv);

        AdvFactory.init(Constant.TYPE_ADV_WEB);

        SourceModel sourceModel = new SourceModel();
        sourceModel.setImagePaths(new String[]{"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1603365312,3218205429&fm=26&gp=0.jpg"});
        AdvFactory.download(sourceModel);

        // 绑定和配置广告内容
        AdvModel model = new AdvModel();
        model.setImagePath("/storage/emulated/0/DCIM");
        mAdvParent.initConfig(model);
    }


    public void onSelectVideoClick(View view) {
//        new LFilePicker()
//                .withActivity(MainActivity.this)
//                .withTheme(R.style.AdvFileTheme)
//                .withBackIcon(Constant.BACKICON_STYLETWO)
//                .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
//                .withStartPath("/storage/emulated/0/Movies")
//                .withTitle("视频文件夹选择")
//                .withChooseMode(false)
//                .withIsGreater(false)
//                .withFileSize(500 * 1024)
//                .start();

        mAdvParent.onResume();
    }

    public void onSelectImageClick(View view) {
//        new LFilePicker()
//                .withActivity(MainActivity.this)
//                .withTheme(R.style.AdvFileTheme)
//                .withBackIcon(Constant.BACKICON_STYLETWO)
//                .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
//                .withStartPath("/storage/emulated/0/DCIM")
//                .withTitle("视频文件夹选择")
//                .withChooseMode(false)
//                .withIsGreater(false)
//                .withFileSize(500 * 1024)
//                .start();

        mAdvParent.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdvParent.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdvParent.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdvParent.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //If it is a file selection mode, you need to get the path collection of all the files selected
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                Toast.makeText(getApplicationContext(), "selected " + list.size(), Toast.LENGTH_SHORT).show();
                //If it is a folder selection mode, you need to get the folder path of your choice
                String path = data.getStringExtra("path");
                Toast.makeText(getApplicationContext(), "The selected path is:" + path, Toast.LENGTH_SHORT).show();
                mTitle.setText(path);
            }
        }
    }
}
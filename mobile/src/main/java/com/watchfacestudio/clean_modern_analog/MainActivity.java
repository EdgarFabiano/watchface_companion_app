package com.watchfacestudio.clean_modern_analog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.remote.interactions.RemoteActivityHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button textView = findViewById(R.id.openOnMyWatch);
        ImageView imageView = findViewById(R.id.watchImage);
        textView.setOnClickListener(view -> launchPlayStoreOnWear());
        imageView.setOnClickListener(view -> launchPlayStoreOnWear());
    }

    private Node getConnectedNode() {
        Node returnNode = null;
        Task<List<Node>> wearableList = Wearable.getNodeClient(this).getConnectedNodes();
        try {
            List<Node> nodes = Tasks.await(wearableList);
            for (Node node : nodes) {
                if (node.isNearby()) {
                    returnNode = node;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return returnNode;
    }

    private void launchPlayStoreOnWear() {
        new Thread(() -> {
            Looper.prepare();
            if (getConnectedNode() != null) {
                RemoteActivityHelper remoteActivityHelper = new RemoteActivityHelper(this, Executors.newSingleThreadExecutor());
                remoteActivityHelper.startRemoteActivity(
                        new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse("market://details?id=" + getPackageName()))
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                );

                Toast.makeText(this, getResources().getString(R.string.check_your_watch), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_watch_connected), Toast.LENGTH_LONG).show();
            }
        }).start();

    }
}
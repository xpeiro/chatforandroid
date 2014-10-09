package xpeiro.chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by xpeiro on 9/10/14.
 */
public class NotificationBroadcastRecv extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "";
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        Bundle extras = intent.getExtras();
        if (extras != null){
            if (!extras.getString("message").isEmpty()) {
                message = extras.getString("message");
            }
        }

        ClipData clip = ClipData.newPlainText("message", message);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to Clipboard",
                Toast.LENGTH_LONG).show();
        NotificationManager manager=
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(001);
    }
}

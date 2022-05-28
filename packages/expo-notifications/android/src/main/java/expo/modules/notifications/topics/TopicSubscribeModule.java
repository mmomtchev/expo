package expo.modules.notifications.topics;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import expo.modules.core.ExportedModule;
import expo.modules.core.ModuleRegistry;
import expo.modules.core.Promise;
import expo.modules.core.interfaces.ExpoMethod;

import androidx.annotation.NonNull;

public class TopicSubscribeModule extends ExportedModule {
  private final static String EXPORTED_NAME = "ExpoTopicSubscribeModule";

  private final static String TOPIC_SUBSCRIBE_FAIL_CODE = "E_TOPIC_SUBSCRIBE_FAILED";

  public TopicSubscribeModule(Context context) {
    super(context);
  }

  @Override
  public String getName() {
    return EXPORTED_NAME;
  }

  /**
   * Subscribe to a broadcast topic
   *
   * @param topic Broadcast topic to subscribe to
   * @param promise Promise to be resolved when the operation completes
   */
  @ExpoMethod
  public void topicSubscribeAsync(final String topic, final Promise promise) {
    FirebaseMessaging.getInstance().subscribeToTopic(topic)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (!task.isSuccessful()) {
              if (task.getException() == null) {
                promise.reject(TOPIC_SUBSCRIBE_FAIL_CODE, "Subscribing to the topic failed.");
              } else {
                promise.reject(TOPIC_SUBSCRIBE_FAIL_CODE, "Subscribing to the topic failed: " + task.getException().getMessage(), task.getException());
              }
              return;
            }
            promise.resolve(null);
          }
        });
  }

  /**
   * Unsubscribe from a previously subscribed broadcast topic
   *
   * @param topic Broadcast topic to unsubscribe from
   * @param promise Promise to be resolved when the operation completes
   */
  @ExpoMethod
  public void topicUnsubscribeAsync(final String topic, final Promise promise) {
    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (!task.isSuccessful()) {
              if (task.getException() == null) {
                promise.reject(TOPIC_SUBSCRIBE_FAIL_CODE, "Unsubscribing from the topic failed.");
              } else {
                promise.reject(TOPIC_SUBSCRIBE_FAIL_CODE, "Unsubscribing from the topic failed: " + task.getException().getMessage(), task.getException());
              }
              return;
            }
            promise.resolve(null);
          }
        });
  }
}

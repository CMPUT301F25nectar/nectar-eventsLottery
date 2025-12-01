from firebase_functions import https_fn
from firebase_functions.options import set_global_options
from firebase_admin import initialize_app, firestore, messaging
from firebase_functions import firestore_fn
app = initialize_app()
set_global_options(max_instances=10)
@firestore_fn.on_document_created(document="notifications/{docId}")
def send_push_notifications_to_android(event):
    """
    Triggered when a new notification document is created
    Checks user preferences before sending notification
    """
    data = event.data.to_dict()
    deviceId = data.get("deviceId")
    notification_type = data.get("type") # "winning", "losing", "cancelled", "admin"
    db = firestore.client()
    user_doc = db.collection("users").document(deviceId).get()
    if not user_doc.exists:
        print(f"User not found: {deviceId}")
        return
    user_data = user_doc.to_dict()
    if notification_type == "winning":
        if not user_data.get("receiveWinningNotifs", True):
            print(f"User {deviceId} has disabled winning notifications")
            return
    elif notification_type == "losing":
        if not user_data.get("receiveLosingNotifs", True):
            print(f"User {deviceId} has disabled losing notifications")
            return
    elif notification_type == "cancelled":
        if not user_data.get("receiveCancelledNotifs", True):
            print(f"User {deviceId} has disabled cancelled notifications")
            return
    elif notification_type == "admin":
        if not user_data.get("receiveAdminNotifs", True):
            print(f"User {deviceId} has disabled admin notifications")
            return
    fcm_token = user_data.get("fcmToken")
    if not fcm_token:
        print(f"No FCM token found for user {deviceId}")
        return
    title = data.get("title", "BeeTree Notification")
    message_body = data.get("message", "")
    msg = messaging.Message(
        token=fcm_token,
        notification=messaging.Notification(
            title=title,
            body=message_body
        ),
        data={
            "type": data.get("type", ""),
            "eventId": data.get("eventId", "")
        }
    )
    try:
        response = messaging.send(msg)
        print(f"Successfully sent notification to {deviceId}: {response}")

        log_notification(db, deviceId, notification_type, title, message_body, data.get("eventId"))

    except Exception as e:
        print(f"Error sending notification to {deviceId}: {e}")


def log_notification(db, device_id, notification_type, title, message, event_id):

    try:
        db.collection("notification_logs").add({
            "deviceId": device_id,
            "type": notification_type,
            "title": title,
            "message": message,
            "eventId": event_id,
            "timestamp": firestore.SERVER_TIMESTAMP,
            "status": "sent"
        })
        print(f"Notification logged for device {device_id}")
    except Exception as e:
        print(f"Error logging notification: {e}")

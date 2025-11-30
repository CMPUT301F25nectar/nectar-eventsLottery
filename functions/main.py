from firebase_functions import https_fn
from firebase_functions.options import set_global_options
from firebase_admin import initialize_app, firestore, messaging
from firebase_functions import firestore_fn

app = initialize_app()
set_global_options(max_instances=10)

@firestore_fn.on_document_created(document="notifications/{docId}")
def send_push_notifications_to_android(event):
    # Fixed: event.data.to_dict() not event.data_to_dict()
    data = event.data.to_dict()
    deviceId = data.get("deviceId")
    db = firestore.client()

    user_doc = db.collection("users").document(deviceId).get()

    if not user_doc.exists:
        print("user not found")
        return

    fcm_token = user_doc.to_dict().get("fcmToken")

    if not fcm_token:
        print(f"couldn't find FCM for user {deviceId}")
        return

    msg = messaging.Message(
        token=fcm_token,
        notification=messaging.Notification(
            title=data.get("title", "Notification"),
            body=data.get("message", "")  # Fixed: 'body' not 'message'
        ),
        data={
            "type": data.get("type", ""),
            "eventId": data.get("eventId", "")
        }
    )
    response = messaging.send(msg)

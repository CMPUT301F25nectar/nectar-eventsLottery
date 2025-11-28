# Welcome to Cloud Functions for Firebase for Python!
# To get started, simply uncomment the below code or create your own.
# Deploy with `firebase deploy`

from firebase_functions import https_fn
from firebase_functions.options import set_global_options
from firebase_admin import initialize_app, firestore, messages 
from firebase_functions import firestore_fn

app = initialize_app()

# For cost control, you can set the maximum number of containers that can be
# running at the same time. This helps mitigate the impact of unexpected
# traffic spikes by instead downgrading performance. This limit is a per-function
# limit. You can override the limit for each function using the max_instances
# parameter in the decorator, e.g. @https_fn.on_request(max_instances=5).
set_global_options(max_instances=10)

@firestore_fn.on_document_created(path="notifications")
def send_push_notifications_to_android(event):
    notifications = event.data_to_dict()
    deviceId = data.get("deviceId")
    db = firestore.client()

    user_doc = db.collection("users").document(deviceId).get()

    if not user_doc.exists:
        print("user not found")
        return

    fcm_token = user_doc.to_dict().get("fcmToken")
    
    if not fcm_token:
        print(f"couldn't find FCM for user {user_id}")
        return
    
    msg = messaging.Message(
        token = fcm_token,
        notification = message.Notification(
            title = data.get("title", "Notification"),
            message = data.get("message","")
        ),
        data = {
            "type" : data.get("type", ""),
            "eventId" : data.get("eventId", "")
        }
    )
    response = messaging.send(msg)
# initialize_app()
#
#
# @https_fn.on_request()
# def on_request_example(req: https_fn.Request) -> https_fn.Response:
#     return https_fn.Response("Hello world!")
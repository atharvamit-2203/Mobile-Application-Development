"""
Update all pending events to approved status so they become visible
"""
import requests
import json

# Path to google-services.json
google_services_path = r'D:\MyApplication16\app\google-services.json'

try:
    # Read google-services.json
    with open(google_services_path, 'r') as f:
        config = json.load(f)
    
    project_id = config['project_info']['project_id']
    api_key = config['client'][0]['api_key'][0]['current_key']
    
    print("=" * 60)
    print("APPROVING ALL PENDING EVENTS")
    print("=" * 60)
    print(f"Project: {project_id}")
    print()
    
    # Get all events from Firestore
    firestore_url = f"https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/club_events"
    
    response = requests.get(firestore_url)
    
    if response.status_code == 200:
        data = response.json()
        documents = data.get('documents', [])
        
        if not documents:
            print("No events found in database")
        else:
            updated_count = 0
            
            for doc in documents:
                fields = doc.get('fields', {})
                
                # Extract values
                title = fields.get('title', {}).get('stringValue', 'N/A')
                status = fields.get('status', {}).get('stringValue', 'N/A')
                club_name = fields.get('club_name', {}).get('stringValue', 'N/A')
                event_date = fields.get('event_date', {}).get('stringValue', 'N/A')
                
                # Get document ID
                doc_path = doc.get('name', '')
                event_id = doc_path.split('/')[-1]
                
                print(f"Event: {title}")
                print(f"  Club: {club_name}")
                print(f"  Date: {event_date}")
                print(f"  Current Status: {status}")
                print(f"  ID: {event_id}")
                
                if status == 'pending':
                    print(f"  ⚠️  Updating status to 'approved'...")
                    
                    # Update the status using PATCH
                    update_url = f"{firestore_url}/{event_id}?updateMask.fieldPaths=status"
                    
                    update_payload = {
                        "fields": {
                            "status": {
                                "stringValue": "approved"
                            }
                        }
                    }
                    
                    update_response = requests.patch(update_url, json=update_payload)
                    
                    if update_response.status_code == 200:
                        print(f"  ✅ Successfully updated to 'approved'!")
                        updated_count += 1
                    else:
                        print(f"  ❌ Error updating: {update_response.status_code}")
                        print(f"  {update_response.text}")
                elif status == 'approved':
                    print(f"  ✅ Already approved")
                else:
                    print(f"  ℹ️  Status is '{status}' - not updating")
                
                print()
            
            print("=" * 60)
            print(f"✅ DONE! Updated {updated_count} events to approved status")
            print("=" * 60)
            print("\nAll approved events are now visible to all users!")
    
    elif response.status_code == 403:
        print("❌ Permission denied. Firebase security rules may be blocking access.")
        print("\nNote: Events created from now on will be auto-approved.")
    else:
        print(f"❌ Error: {response.status_code}")
        print(response.text)

except FileNotFoundError:
    print(f"❌ google-services.json not found at: {google_services_path}")
except Exception as e:
    print(f"❌ Error: {str(e)}")
    import traceback
    traceback.print_exc()

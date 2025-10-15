

from fastapi import FastAPI,Header, HTTPException

import httpx
from recommandation1 import test
from recommandation2 import user_test
from combined import recommandation
import py_eureka_client.eureka_client as eureka_client

app = FastAPI()

@app.on_event("startup")
async def startup_event():
    await eureka_client.init_async(
        eureka_server="http://discovery-service:8761/eureka/",
        app_name="fastapi-service",
        instance_port=8000,
        instance_host="fastapi-service"
    )

@app.on_event("shutdown")
async def shutdown_event():
    await eureka_client.stop_async()



@app.get("/events_recommanded/{client_id}")
async def user(
    client_id: int, 
    authorization: str = Header(None)  # Get JWT token from frontend request header
):
    if not authorization:
        raise HTTPException(status_code=401, detail="Missing Authorization header")

    # 🔹 Extract token from "Bearer <token>"
    token = authorization.replace("Bearer ", "")

    # 🔹 Fetch events
    url_events = "http://event-service:9092/events"
    async with httpx.AsyncClient() as client:
        response_events = await client.get(url_events)
        response_events.raise_for_status()
        events = response_events.json()

    # 🔹 Fetch reservations for this client
    url_reservations_user = f"http://reservation-service:9093/reservations_client/{client_id}"
    headers = {"Authorization": f"Bearer {token}"}
    async with httpx.AsyncClient() as client:
        response_reservations_user = await client.get(url_reservations_user, headers=headers)
        response_reservations_user.raise_for_status()
        reservations_user = response_reservations_user.json()

    # 🔹 Fetch all reservations
    url_all_reservations = "http://reservation-service:9093/reservations"
    async with httpx.AsyncClient() as client:
        response_all_reservations = await client.get(url_all_reservations, headers=headers)
        response_all_reservations.raise_for_status()
        all_reservations = response_all_reservations.json()

    # 🔹 Run your recommendation logic
    events_df, cosine_sim = test(reservations=reservations_user, events=events)
    user_event_matrix, user_similarity_df = user_test(all_reservations, events)
    events_recommended = recommandation(client_id, events_df, cosine_sim, user_event_matrix, user_similarity_df)

    return events_recommended


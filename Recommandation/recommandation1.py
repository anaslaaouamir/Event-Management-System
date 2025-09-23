import pandas as pd
from sentence_transformers import SentenceTransformer
import numpy as np


def test(reservations,events):
    print("***********************  loading data ***********************")
    events_df = pd.DataFrame(events)
    print("Events DataFrame:")
    print(events_df.head())

    print("***********************")

    # Convert reservations to DataFrame
    reservations_df = pd.DataFrame(reservations)
    print("Reservations DataFrame:")
    print(reservations_df.head())


#################################################################################################""


    print("***********************  text field ***********************")
    # Combine useful text info into one field
    events_df["text"] = (
        events_df["title"].fillna('') + " " +
        events_df["description"].fillna('') + " " +
        events_df["location"].fillna('')+ " " +
        events_df["tags"].fillna('')
    )
    #pd.set_option('display.max_colwidth', None)
    print(events_df[["title", "text"]])


##################################################################################################""



    # print("***********************  Build user Profile ***********************")
    # #🔹 Step 4. Build user profile (from reservations)

    # # Filter reservations for a given user (e.g. user idClient = 2)
    # user_id =2
    # user_reservations = [r["idEvent"] for r in reservations if r["idClient"] == user_id]
    # print("User reserved:", user_reservations)



    ############################################### Semantic embeddings instead of TF-IDF   ##################################################

    print("***********************  Semantic embeddings ***********************")
    # Load a pre-trained embedding model
    model = SentenceTransformer("all-MiniLM-L6-v2")

    # Encode event descriptions
    event_embeddings = model.encode(events_df["text"].tolist(), convert_to_tensor=True)

    # Compute cosine similarity
    from sklearn.metrics.pairwise import cosine_similarity
    cosine_sim = cosine_similarity(event_embeddings)

    print("Embedding-based similarity matrix:", cosine_sim.shape)


    ########################################    Step 5. Recommend function  #########################################################""

    print("***********************  recommandation ***********************")
    def recommend_events(user_id, reservations, events_df, cosine_sim, top_n=5):
        # Find event indices the user reserved
        reserved_event_ids = [r["idEvent"] for r in reservations if r["idClient"] == user_id]
        indices = events_df[events_df["idEvent"].isin(reserved_event_ids)].index.tolist()

        if not indices:
            return []

        # Aggregate similarity scores
        sim_scores = np.sum(cosine_sim[indices], axis=0)

        # Exclude already reserved events
        for idx in indices:
            sim_scores[idx] = -1

        # Get top recommendations
        top_indices = sim_scores.argsort()[-top_n:][::-1]
        return events_df.iloc[top_indices]

    # Example
    #recommendations = recommend_events(id_user, reservations, events_df, cosine_sim, top_n=5)

    return  events_df, cosine_sim



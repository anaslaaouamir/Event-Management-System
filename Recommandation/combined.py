import numpy as np
import pandas as pd
from datetime import datetime

def recommandation(user_id,events_df,cosine_sim,user_event_matrix,user_similarity_df):

    def hybrid_recommend(user_id, events_df, cosine_sim_events, user_event_matrix, user_similarity_df, alpha=0.5, top_n=5):
        # --- Content-based score (event similarity) ---
        reserved_event_ids = user_event_matrix.loc[user_id][user_event_matrix.loc[user_id] > 0].index
        indices = events_df[events_df["idEvent"].isin(reserved_event_ids)].index.tolist()

        if indices:
            content_scores = np.sum(cosine_sim_events[indices], axis=0)
        else:
            content_scores = np.zeros(len(events_df))

        # --- Collaborative score (user similarity) ---
        sim_scores = user_similarity_df.loc[user_id]
        collaborative_scores = np.dot(sim_scores.values, user_event_matrix.values)

        if content_scores.shape != collaborative_scores.shape:
            raise ValueError(f"Shapes mismatch: content {content_scores.shape}, collaborative {collaborative_scores.shape}")


        # --- Combine both ---
        final_scores = alpha * content_scores + (1 - alpha) * collaborative_scores

        # Remove events already reserved
        reserved = user_event_matrix.loc[user_id].values
        final_scores[reserved > 0] = -1

        # Make sure eventDateTime is datetime
        events_df["eventDateTime"] = pd.to_datetime(events_df["eventDateTime"], errors="coerce")

        # Filter out past events
        today = pd.Timestamp(datetime.now())
        future_mask = events_df["eventDateTime"] > today
        final_scores = np.where(future_mask, final_scores, -1)

        # Top-N
        top_indices = final_scores.argsort()[-top_n:][::-1]
        return events_df.iloc[top_indices]




    # Call hybrid_recommend for user_id = 2
    recommended_hybrid = hybrid_recommend(
        user_id=user_id,
        events_df=events_df,
        cosine_sim_events=cosine_sim,
        user_event_matrix=user_event_matrix,
        user_similarity_df=user_similarity_df,
        alpha=0.8,  # you can adjust the weight between content and collaborative
        top_n=5     # number of recommendations
    )

    print("Hybrid recommendations for user 2:")
    print(recommended_hybrid)
    return recommended_hybrid.to_dict(orient="records")

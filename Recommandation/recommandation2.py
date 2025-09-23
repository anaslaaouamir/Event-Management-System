import pandas as pd
import numpy as np


def user_test(reservations,events):
    reservations_df = pd.DataFrame([
        {
            "user_id": r["idClient"],
            "event_id": r["idEvent"]
        }
        for r in reservations
    ])

    print(reservations_df)



    #############################################################

    # Suppose reservations_df already exists
    # Create binary matrix: 1 if user reserved event, 0 otherwise
    user_event_matrix = reservations_df.pivot_table(
        index="user_id",
        columns="event_id",
        aggfunc=len,
        fill_value=0
    )

    # 🔹 Fix: align with all events
    all_event_ids = [e["idEvent"] for e in events]
    user_event_matrix = user_event_matrix.reindex(columns=all_event_ids, fill_value=0)

    print(user_event_matrix)



    #############################################################

    from sklearn.metrics.pairwise import cosine_similarity

    # Each row is a user, each column is an event
    user_similarity = cosine_similarity(user_event_matrix)
    user_similarity_df = pd.DataFrame(
        user_similarity,
        index=user_event_matrix.index,
        columns=user_event_matrix.index
    )

    print(user_similarity_df)






    def recommend_events_with_scores(user_id, user_event_matrix, top_n=5):
    # Similarities of this user with others
        sim_scores = user_similarity_df.loc[user_id]

        # Weighted sum of other users' reservations
        weighted_events = np.dot(sim_scores.values, user_event_matrix.values)

        # Convert to pandas Series with event IDs
        weighted_events = pd.Series(weighted_events, index=user_event_matrix.columns)

        # Remove events already reserved
        reserved_events = user_event_matrix.loc[user_id]
        weighted_events = weighted_events[reserved_events == 0]

        # Top recommendations
        top_events = weighted_events.sort_values(ascending=False).head(top_n)

        # Return event IDs and their scores
        return top_events

    # Example
    #recommended_events = recommend_events_with_scores(user_id=user_id, user_event_matrix=user_event_matrix, top_n=5)
    #print("Recommended events and scores for user 5:\n", recommended_events)

    return user_event_matrix, user_similarity_df


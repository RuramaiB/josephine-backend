import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest

def detect_anomalies(data: pd.DataFrame, contamination: float = 0.05):
    """
    Detects anomalies in price data using Isolation Forest.
    Returns anomaly scores and binary labels (-1 for anomaly, 1 for normal).
    """
    if data.empty or 'price' not in data.columns:
        return []

    model = IsolationForest(contamination=contamination, random_state=42)
    prices = data[['price']].values
    model.fit(prices)
    
    # 1 for normal, -1 for anomaly
    predictions = model.predict(prices)
    # The lower, the more abnormal. 
    scores = model.decision_function(prices)
    
    results = []
    for i, (pred, score) in enumerate(zip(predictions, scores)):
        results.append({
            "index": i,
            "is_anomaly": bool(pred == -1),
            "anomaly_probability": float(1 - (score + 1) / 2) # Normalized index-like probability
        })
    return results

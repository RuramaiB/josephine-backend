import pandas as pd
import numpy as np
from model_training import load_model, MODEL_PATH
import datetime

def predict_next(history_df: pd.DataFrame):
    """
    Predicts the price for the next day.
    """
    model = load_model()
    if not model:
        # Fallback to simple moving average if no model trained
        return history_df['price'].mean(), 0.5
    
    history_df['timestamp'] = pd.to_datetime(history_df['timestamp'])
    last_date = history_df['timestamp'].max()
    next_date = last_date + datetime.timedelta(days=1)
    next_date_ord = np.array([[next_date.toordinal()]])
    
    prediction = model.predict(next_date_ord)[0]
    
    # Confidence could be calculated based on variance, here we return a constant for demonstration
    return prediction, 0.85

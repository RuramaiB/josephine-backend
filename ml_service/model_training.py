import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
import joblib
import os

# Using joblib via jobpy for model persistence (or just pickle if preferred)
import pickle

MODEL_PATH = "price_model.pkl"

def train_and_save(df: pd.DataFrame):
    """
    Trains a simple linear regression model based on time-based indexing.
    In a real scenario, this would involve feature engineering (lagged prices, seasonality).
    """
    # Simple conversion of timestamp to numerical ordinal for regression
    df['timestamp'] = pd.to_datetime(df['timestamp'])
    df['time_ord'] = df['timestamp'].apply(lambda x: x.toordinal())
    
    X = df[['time_ord']]
    y = df['price']
    
    model = LinearRegression()
    model.fit(X, y)
    
    # Save model
    with open(MODEL_PATH, 'wb') as f:
        pickle.dump(model, f)
        
    # In-sample evaluation for metrics
    y_pred = model.predict(X)
    from evaluation import evaluate_model
    return evaluate_model(y, y_pred)

def load_model():
    if os.path.exists(MODEL_PATH):
        with open(MODEL_PATH, 'rb') as f:
            return pickle.load(f)
    return None

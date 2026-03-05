from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import pandas as pd
import numpy as np
import model_training
import prediction
import anomaly
import evaluation

app = FastAPI(title="Price Prediction ML Service", version="1.0.0")

class PriceData(BaseModel):
    productId: str
    price: float
    timestamp: str

class TrainingRequest(BaseModel):
    data: List[PriceData]

class PredictionRequest(BaseModel):
    productId: str
    history: List[PriceData]

@app.get("/")
def read_root():
    return {"status": "ML Service is running"}

@app.post("/ml/train")
def train_model(request: TrainingRequest):
    df = pd.DataFrame([d.dict() for d in request.data])
    if df.empty:
        raise HTTPException(status_code=400, detail="Empty data provided")
    
    metrics = model_training.train_and_save(df)
    return {"message": "Model trained successfully", "metrics": metrics}

@app.post("/ml/predict")
def predict_price(request: PredictionRequest):
    df = pd.DataFrame([d.dict() for d in request.history])
    if df.empty:
        raise HTTPException(status_code=400, detail="Empty history provided")
    
    # Run prediction
    predicted_price, confidence = prediction.predict_next(df)
    
    # Run anomaly detection on history
    anomaly_results = anomaly.detect_anomalies(df)
    latest_anomaly = anomaly_results[-1] if anomaly_results else {"is_anomaly": False, "anomaly_probability": 0.0}
    
    return {
        "productId": request.productId,
        "predictedPrice": float(predicted_price),
        "confidence": float(confidence),
        "is_anomaly": latest_anomaly["is_anomaly"],
        "anomaly_probability": latest_anomaly["anomaly_probability"]
    }

@app.post("/ml/anomaly-detection")
def detect_anomalies_endpoint(request: TrainingRequest):
    df = pd.DataFrame([d.dict() for d in request.data])
    results = anomaly.detect_anomalies(df)
    return {"results": results}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

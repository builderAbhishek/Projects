import os

model_path = './model/model.pth'

if os.path.exists(model_path) and os.path.getsize(model_path) > 0:
    print("Model file exists and is not empty.")
else:
    print("Model file is empty or does not exist.")

from flask import Flask, request, send_file
from werkzeug.utils import secure_filename
from PIL import Image
import os
import torch
from torch.utils.data import DataLoader, Dataset
from torchvision import transforms
import io

# Define constants
UPLOAD_FOLDER = 'uploads/'
OUTPUT_FOLDER = 'outputs/'
MODEL_PATH = 'model.pth'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

app = Flask(__name__)

# Ensure the output folder exists
os.makedirs(OUTPUT_FOLDER, exist_ok=True)

# Check if the file extension is allowed
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# Route for uploading images
@app.route('/upload', methods=['POST'])
def upload_image():
    if 'lr_image' not in request.files or 'hr_image' not in request.files:
        return 'No image files found', 400

    lr_file = request.files['lr_image']
    hr_file = request.files['hr_image']

    # Check if files have valid extensions
    if not (allowed_file(lr_file.filename) and allowed_file(hr_file.filename)):
        return 'Invalid file type. Only PNG, JPG, and JPEG are allowed.', 400

    # Save the images with a secure filename
    lr_filename = secure_filename(lr_file.filename)
    hr_filename = secure_filename(hr_file.filename)

    lr_image_path = os.path.join(UPLOAD_FOLDER, 'lr_' + lr_filename)
    hr_image_path = os.path.join(UPLOAD_FOLDER, 'hr_' + hr_filename)

    lr_file.save(lr_image_path)
    hr_file.save(hr_image_path)

    # Check if the files are saved correctly
    if not os.path.exists(lr_image_path) or not os.path.exists(hr_image_path):
        return 'Failed to save images', 400

    # Log the image paths
    print(f"LR Image Path: {lr_image_path}")
    print(f"HR Image Path: {hr_image_path}")

    # Train model with the uploaded images
    train_model(lr_image_path, hr_image_path)

    return 'Images uploaded and model training started!'

# Model Training Function
def train_model(lr_image_path, hr_image_path):
    # Check if images exist
    if not os.path.exists(lr_image_path) or not os.path.exists(hr_image_path):
        print("Error: One or both images not found.")
        return
    
    # Load images
    try:
        lr_image = Image.open(lr_image_path).convert('RGB')
        hr_image = Image.open(hr_image_path).convert('RGB')
    except Exception as e:
        print(f"Error loading images: {e}")
        return

    # Transform images to tensor
    transform = transforms.Compose([transforms.ToTensor()])
    lr_image_tensor = transform(lr_image).unsqueeze(0)  # Add batch dimension
    hr_image_tensor = transform(hr_image).unsqueeze(0)

    # Ensure images are non-empty tensors
    if lr_image_tensor.numel() == 0 or hr_image_tensor.numel() == 0:
        print("Error: Empty tensor for images.")
        return

    # Initialize and train model
    model = SimpleEnhanceModel()
    if os.path.exists(MODEL_PATH) and os.path.getsize(MODEL_PATH) > 0:
        model.load_state_dict(torch.load(MODEL_PATH))
        model.eval()

    loss_fn = torch.nn.MSELoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=1e-4)

    # Forward pass
    enhanced_image = model(lr_image_tensor)
    loss = loss_fn(enhanced_image, hr_image_tensor)

    # Backpropagation
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()

    print(f'Model training loss: {loss.item()}')

    # Save the updated model after training
    save_model(model)

# Save Model Function
def save_model(model):
    torch.save(model.state_dict(), MODEL_PATH)
    print("Model saved successfully.")

# A simple model for training (replace with your own model structure)
class SimpleEnhanceModel(torch.nn.Module):
    def __init__(self):
        super(SimpleEnhanceModel, self).__init__()
        self.conv1 = torch.nn.Conv2d(3, 64, kernel_size=3, padding=1)
        self.relu = torch.nn.ReLU()
        self.conv2 = torch.nn.Conv2d(64, 3, kernel_size=3, padding=1)

    def forward(self, x):
        x = self.relu(self.conv1(x))
        x = self.conv2(x)
        return x

if __name__ == '__main__':
    app.run(debug=True)

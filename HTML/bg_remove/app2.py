import os
from flask import Flask, request, send_file, render_template
import torch
from torch.utils.data import DataLoader
from torchvision import transforms
from PIL import Image
from werkzeug.utils import secure_filename

app = Flask(__name__)

# Define directories for saving images
UPLOAD_FOLDER = 'static/uploads/'
MODEL_PATH = 'model/model.pth'

# Ensure the upload folder exists
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# Allowable file extensions for image uploads
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

# Check if file is an allowed image type
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# Simple model (You can use your trained model)
class SimpleEnhanceModel(torch.nn.Module):
    def __init__(self):
        super(SimpleEnhanceModel, self).__init__()
        self.conv1 = torch.nn.Conv2d(3, 64, kernel_size=3, padding=1)
        self.conv2 = torch.nn.Conv2d(64, 3, kernel_size=3, padding=1)
        self.relu = torch.nn.ReLU()

    def forward(self, x):
        x = self.relu(self.conv1(x))
        x = self.conv2(x)
        return x

# Route for index page (HTML)
@app.route('/')
def index():
    return render_template('index.html')

# Route to handle image uploads
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

    # Train model with the uploaded images
    train_model(lr_image_path, hr_image_path)

    return 'Images uploaded and model training started!'

# Function to train the model (you can adjust this based on your training loop)
def train_model(lr_image_path, hr_image_path):
    # Simple transform to tensor
    transform = transforms.Compose([transforms.ToTensor()])

    # Load LR and HR images
    lr_image = Image.open(lr_image_path).convert('RGB')
    hr_image = Image.open(hr_image_path).convert('RGB')

    # Apply transformations
    lr_image_tensor = transform(lr_image).unsqueeze(0)  # Add batch dimension
    hr_image_tensor = transform(hr_image).unsqueeze(0)

    # Initialize model, loss, and optimizer
    model = SimpleEnhanceModel()
    model.load_state_dict(torch.load(MODEL_PATH))
    model.eval()  # Set to evaluation mode

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
    torch.save(model.state_dict(), MODEL_PATH)
    print('Model saved!')

if __name__ == '__main__':
    app.run(debug=True)

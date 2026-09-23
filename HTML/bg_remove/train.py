import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader
from torchvision import transforms
from PIL import Image
import os

# Hyperparameters
BATCH_SIZE = 1
EPOCHS = 5
LR_DIR = 'static/train_data/LR_image.png'
HR_DIR = 'static/train_data/HR_image.png'

# Define a simple model (e.g., a convolutional network)
class SimpleEnhanceModel(nn.Module):
    def __init__(self):
        super(SimpleEnhanceModel, self).__init__()
        self.conv1 = nn.Conv2d(3, 64, kernel_size=3, padding=1)
        self.conv2 = nn.Conv2d(64, 3, kernel_size=3, padding=1)
        self.relu = nn.ReLU()
    
    def forward(self, x):
        x = self.relu(self.conv1(x))
        x = self.conv2(x)
        return x

# Dataset class
class ImageEnhanceDataset(Dataset):
    def __init__(self, lr_image_path, hr_image_path, transform=None):
        self.lr_image_path = lr_image_path
        self.hr_image_path = hr_image_path
        self.transform = transform

    def __len__(self):
        return 1  # Single image for testing

    def __getitem__(self, idx):
        lr_image = Image.open(self.lr_image_path).convert('RGB')
        hr_image = Image.open(self.hr_image_path).convert('RGB')

        if self.transform:
            lr_image = self.transform(lr_image)
            hr_image = self.transform(hr_image)

        return lr_image, hr_image

# Transforms
transform = transforms.Compose([
    transforms.ToTensor(),
])

# Load dataset
dataset = ImageEnhanceDataset(LR_DIR, HR_DIR, transform=transform)
train_loader = DataLoader(dataset, batch_size=BATCH_SIZE, shuffle=False)

# Initialize model
model = SimpleEnhanceModel()
loss_fn = nn.MSELoss()
optimizer = torch.optim.Adam(model.parameters(), lr=1e-4)

# Training loop
for epoch in range(EPOCHS):
    for lr_image, hr_image in train_loader:
        optimizer.zero_grad()

        # Forward pass
        enhanced_image = model(lr_image)

        # Compute loss
        loss = loss_fn(enhanced_image, hr_image)
        loss.backward()
        
        # Update weights
        optimizer.step()

    print(f'Epoch [{epoch+1}/{EPOCHS}], Loss: {loss.item():.4f}')

# Save the trained model
torch.save(model.state_dict(), 'model/enhance_model.pth')

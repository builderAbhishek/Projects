import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, Dataset
import os

# Example model (replace with your actual model class)
class SimpleEnhanceModel(nn.Module):
    def __init__(self):
        super(SimpleEnhanceModel, self).__init__()
        self.conv1 = nn.Conv2d(3, 64, kernel_size=3, stride=1, padding=1)
        self.conv2 = nn.Conv2d(64, 64, kernel_size=3, stride=1, padding=1)
        self.conv3 = nn.Conv2d(64, 3, kernel_size=3, stride=1, padding=1)

    def forward(self, x):
        x = torch.relu(self.conv1(x))
        x = torch.relu(self.conv2(x))
        x = self.conv3(x)
        return x

# Example custom dataset (Replace with your own dataset of LR and HR image pairs)
class ImagePairDataset(Dataset):
    def __init__(self, lr_images, hr_images):
        self.lr_images = lr_images
        self.hr_images = hr_images

    def __len__(self):
        return len(self.lr_images)

    def __getitem__(self, idx):
        lr_image = self.lr_images[idx]
        hr_image = self.hr_images[idx]
        return lr_image, hr_image

# Create the model
model = SimpleEnhanceModel()

# Define loss and optimizer
loss_fn = nn.MSELoss()
optimizer = optim.Adam(model.parameters(), lr=1e-4)

# Example: Create dummy LR and HR images (Replace with your real images)
# Note: Replace this with actual images or load images from disk
dummy_lr_images = [torch.randn(3, 256, 256) for _ in range(100)]  # 100 dummy LR images
dummy_hr_images = [torch.randn(3, 256, 256) for _ in range(100)]  # 100 dummy HR images

# Create a dataset and data loader
dataset = ImagePairDataset(dummy_lr_images, dummy_hr_images)
train_loader = DataLoader(dataset, batch_size=4, shuffle=True)

# Train the model
epochs = 5  # Set the number of epochs
for epoch in range(epochs):
    model.train()
    running_loss = 0.0

    for lr_images, hr_images in train_loader:
        optimizer.zero_grad()
        
        # Forward pass
        output = model(lr_images)

        # Compute loss
        loss = loss_fn(output, hr_images)
        running_loss += loss.item()

        # Backpropagation
        loss.backward()
        optimizer.step()

    print(f'Epoch [{epoch + 1}/{epochs}], Loss: {running_loss/len(train_loader)}')

# Save the model after training
torch.save(model.state_dict(), 'model.pth')
print("Model saved as model.pth")

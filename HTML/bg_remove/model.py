from flask import Flask, request, send_file
from rembg import remove
from PIL import Image
import io
import os

app = Flask(__name__)

# Define the folder for uploaded images
UPLOAD_FOLDER = 'uploads/'
OUTPUT_FOLDER = 'outputs/'

# Ensure the output folder exists
os.makedirs(OUTPUT_FOLDER, exist_ok=True)

@app.route('/')
def index():
    return send_file('index.html')

@app.route('/upload', methods=['POST'])
def upload_image():
    if 'image' not in request.files:
        return 'No image file part', 400
    
    file = request.files['image']
    
    if file.filename == '':
        return 'No selected file', 400
    
    # Save the uploaded image
    input_image_path = os.path.join(UPLOAD_FOLDER, file.filename)
    file.save(input_image_path)

    # Remove the background from the image
    with open(input_image_path, "rb") as input_file:
        input_data = input_file.read()
    
    output_data = remove(input_data)
    output_image_path = os.path.join(OUTPUT_FOLDER, 'output_image.png')
    background_removed_image = Image.open(io.BytesIO(output_data))
    background_removed_image.save(output_image_path)

    # Send the output image as a response
    return send_file(output_image_path, as_attachment=True)

if __name__ == '__main__':
    app.run(debug=True)

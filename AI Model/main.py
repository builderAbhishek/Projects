import fitz  # PyMuPDF
from transformers import pipeline
import os

# 1. PDF ya .txt file ko read karna
def read_pdf(file_path):
    """
    Function to extract text from a PDF file
    """
    doc = fitz.open(file_path)
    text = ""
    for page in doc:
        text += page.get_text()
    return text

def read_txt(file_path):
    """
    Function to read text from a .txt file
    """
    with open(file_path, 'r', encoding='utf-8') as file:
        text = file.read()
    return text

# 2. Model Load Karna (Question Answering pipeline)
qa_pipeline = pipeline("question-answering", model="distilbert-base-cased-distilled-squad")

# 3. Question Answering Function
def answer_question(question, context):
    """
    Function to get the answer for a question from the given context
    """
    result = qa_pipeline({
        'question': question,
        'context': context
    })
    return result['answer']

# 4. Main Function for PDF or .txt Processing
def main(file_path, question):
    # Check file extension and read accordingly
    if file_path.endswith('.pdf'):
        context = read_pdf(file_path)
    elif file_path.endswith('.txt'):
        context = read_txt(file_path)
    else:
        print("Unsupported file format. Please provide a .pdf or .txt file.")
        return
    
    # Get answer for the question
    answer = answer_question(question, context)
    print("Answer: ", answer)

if __name__ == "__main__":
    # Example usage
    file_path = "ic.txt"  # Change this to your file path
    question = "What does RAM stand for?"  # Example question
    main(file_path, question)

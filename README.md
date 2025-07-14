# MNIST Character Recognition Android App

This project is a practical blend of my Android development experience and machine learning studies from the course [The Data Science Course: Complete Data Science Bootcamp 2025](https://www.udemy.com/course/the-data-science-course-complete-data-science-bootcamp), specifically **Module 52**, where we build a character recognition model using TensorFlow 2.0 and the MNIST dataset.

The app captures grayscale camera frames, processes a central cropped region, resizes it to 28x28, and feeds it into a TensorFlow Lite model to predict the handwritten digit. It's designed to demonstrate how a classic deep learning model can be embedded into a real-time mobile application.

## Jupyter Notebook

The project also includes a Jupyter notebook containing all the steps for:

- Dataset extraction and loading
- Preprocessing
- Model creation using TensorFlow 2.0
- Training and evaluation

You can find it here: [TensorFlow_MNIST_Part1_with_comments.ipynb](jupyter/TensorFlow_MNIST_Part1_with_comments.ipynb)

## Model Architecture

The model used in this project is a simple Sequential Neural Network built using TensorFlow 2.0. It is designed to classify handwritten digits from the MNIST dataset, which contains grayscale images of 28x28 pixels.

## Architecture Details

* Input Layer: Each 28x28 image is flattened into a 1D array of 784 elements.
* Dense Layer 1: 50 neurons with ReLU activation.
* Dense Layer 2: 50 neurons with ReLU activation.
* Output Layer: 10 neurons with Softmax activation, representing the probability of each digit (0 through 9).

The use of ReLU activation in the hidden layers enables the model to learn complex patterns, while the Softmax activation in the final layer ensures that the output values represent probabilities summing to 1.

![model_rep](screenshots/model-rep.png)

## Features

- Real-time camera input using CameraX
- Grayscale processing and cropping logic
- Histogram-based filtering to reduce redundant predictions
- Embedded TensorFlow Lite model (trained on MNIST)
- ViewModel and coroutine-based architecture for clean separation of UI and processing

## Screenshots

![pred1](screenshots/Screenshot_20250711-151303.png)

## Planned Improvements

- [x] ~~Add FPS counter or inference time metrics~~
- [x] ~~Show the confidence of the prediction~~
- [x] ~~Improve the ui of prediction show with percentage and icon~~
- [x] ~~Update screenshots on readme~~
- [x] ~~Integrate confidence score in the UI~~
- [x] ~~Improve the histogram comparison with Chi-squared distance~~
- [ ] Add the bitmap preview on the left size of the prediction result
- [ ] Add option to toggle histogram difference filtering
- [ ] Improve modularization (e.g., inject `FrameManager` using DI like Hilt)
- [ ] Write unit tests for core logic

## How to Run

1. Clone the repository
2. Open in Android Studio
3. Build and run on a device (Camera is required)
4. The model will predict digits from the central area of the frame in real time

## License

MIT – feel free to use this as a base for your own Android + ML experiments.

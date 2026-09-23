import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'sign_up_screen.dart';
import 'home_screen.dart'; // Import your HomeScreen

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final FirebaseAuth _auth = FirebaseAuth.instance;
  String? _errorMessage;
  bool _isLoading = false;
  bool _loginSuccess = false;

  // Function to handle Firebase error messages
 // Function to handle Firebase error messages
String _getErrorMessage(FirebaseAuthException e) {
  switch (e.code) {
    case 'user-not-found':
      return 'No user found for that email.';
    case 'wrong-password':
      return 'Incorrect password. Please try again.';
    case 'invalid-email':
      return 'The email address is badly formatted.';
    case 'email-already-in-use':
      return 'The email address is already in use by another account.';
    case 'too-many-requests':
      return 'Too many requests. Please try again later.';
    case 'reCAPTCHA-invalid':
      return 'ReCAPTCHA verification failed. Please try again.';
    default:
      return 'Incorrect email/password. Please try again.';
  }
}


  Future<void> _login() async {
    setState(() {
      _isLoading = true;
      _loginSuccess = false; // Reset success message before login attempt
      _errorMessage = null; // Reset error message
    });

    try {
      UserCredential userCredential = await _auth.signInWithEmailAndPassword(
        email: _emailController.text,
        password: _passwordController.text,
      );

      if (userCredential.user != null) {
        setState(() {
          _loginSuccess = true;
        });

        // After login success, navigate to the home screen after a brief delay
        await Future.delayed(Duration(seconds: 1)); // Delay to show success message
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()), // Replace HomeScreen with your desired screen
        );
      }
    } on FirebaseAuthException catch (e) {
      setState(() {
        _errorMessage = _getErrorMessage(e); // Map Firebase error to user-friendly message
      });
    } finally {
      setState(() {
        _isLoading = false; // Hide loading indicator
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        title: Text('Smartage = स्मार्ट घर', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 30, color: Color.fromARGB(255, 255, 255, 255))),
        centerTitle: true,
        backgroundColor: const Color(0xFF007BFF),
        elevation: 1,
      ),
      body: Stack(
        children: [
          // Background Gradient
          Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [const Color.fromARGB(255, 251, 252, 253), const Color(0xFFF8F9FA)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          // Content
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Center(
              child: SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    // Logo or App Title
                    Image.asset(
                      'assets/images/logo.png', // Add a logo for branding
                      height: 120,
                    ),
                    SizedBox(height: 20),
                    Text(
                      'Welcome Back!',
                      style: TextStyle(
                        fontSize: 30,
                        fontWeight: FontWeight.bold,
                        color: const Color(0xFF212529),
                      ),
                    ),
                    SizedBox(height: 20),
                    // Email Input Field
                    TextField(
                      controller: _emailController,
                      keyboardType: TextInputType.emailAddress,
                      decoration: InputDecoration(
                        filled: true,
                        fillColor: const Color(0xFFFFFFFF).withOpacity(0.9),
                        labelText: 'Email or Phone',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(color: Colors.blue),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(color: Colors.blue),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(color: Colors.blue.shade700, width: 2),
                        ),
                        prefixIcon: Icon(Icons.email, color: Colors.blue),
                      ),
                    ),
                    SizedBox(height: 20),
                    // Password Input Field
                    TextField(
                      controller: _passwordController,
                      obscureText: true,
                      decoration: InputDecoration(
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.9),
                        labelText: 'Password',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(color: Colors.blue),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(color: Colors.blue),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(color: Colors.blue.shade700, width: 2),
                        ),
                        prefixIcon: Icon(Icons.lock, color: Colors.blue),
                      ),
                    ),
                    SizedBox(height: 20),
                    // Show error message if any
                    if (_errorMessage != null)
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          _errorMessage!,
                          style: TextStyle(color: Colors.red),
                        ),
                      ),
                    // Show success message if login is successful
                    if (_loginSuccess)
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          'Login Successful!',
                          style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold),
                        ),
                      ),
                    // Show loading spinner if login is in progress
                    if (_isLoading)
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: CircularProgressIndicator(),
                      ),
                    // Login Button
                    ElevatedButton(
                      onPressed: _login,
                      style: ElevatedButton.styleFrom(
                        padding: EdgeInsets.symmetric(vertical: 16.0, horizontal: 40.0),
                        backgroundColor: const Color(0xFF00C49A),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30),
                        ),
                      ),
                      child: Text(
                        'Login',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: const Color.fromARGB(255, 255, 255, 255)),
                      ),
                    ),
                    SizedBox(height: 10),
                    // Sign Up Link
                    TextButton(
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (context) => SignUpScreen()), // Navigate to SignUp screen
                        );
                      },
                      child: Text(
                        'Don\'t have an account? Sign Up',
                        style: TextStyle(color: const Color.fromARGB(255, 0, 0, 0), fontSize: 16),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:smartage/screens/home_screen.dart'; // Import Home Screen
import 'package:smartage/screens/login_screen.dart'; // Import Login Screen if needed

class SignUpScreen extends StatefulWidget {
  @override
  _SignUpScreenState createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController = TextEditingController();
  final FirebaseAuth _auth = FirebaseAuth.instance;
  String? _errorMessage;
  bool _isLoading = false;

  Future<void> _signUp() async {
  if (_passwordController.text != _confirmPasswordController.text) {
    setState(() {
      _errorMessage = "Passwords do not match!";
    });
    return;
  }

  setState(() {
    _isLoading = true;
  });

  try {
    UserCredential userCredential = await _auth.createUserWithEmailAndPassword(
      email: _emailController.text,
      password: _passwordController.text,
    );
    if (userCredential.user != null) {
      setState(() {
        _isLoading = false; // Stop loading
      });

      // Redirect to Home Screen after successful signup
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => HomeScreen()),
      );
    }
  } on FirebaseAuthException catch (e) {
    setState(() {
      _isLoading = false;
      _errorMessage = "Error: ${e.message}";
    });
    print("Firebase Error: ${e.message}");
  }
}


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Sign Up'),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Create an Account',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 20),
            // Email Input Field
            TextField(
              controller: _emailController,
              decoration: InputDecoration(
                labelText: 'Email',
                border: OutlineInputBorder(),
              ),
            ),
            SizedBox(height: 20),
            // Password Input Field
            TextField(
              controller: _passwordController,
              obscureText: true,
              decoration: InputDecoration(
                labelText: 'Password',
                border: OutlineInputBorder(),
              ),
            ),
            SizedBox(height: 20),
            // Confirm Password Input Field
            TextField(
              controller: _confirmPasswordController,
              obscureText: true,
              decoration: InputDecoration(
                labelText: 'Confirm Password',
                border: OutlineInputBorder(),
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
            // Show loading spinner when sign-up is in progress
            if (_isLoading)
              Center(child: CircularProgressIndicator()),
            // Sign Up Button
            if (!_isLoading)
              Center(
                child: ElevatedButton(
                  onPressed: _signUp,
                  child: Text('Sign Up'),
                ),
              ),
            SizedBox(height: 10),
            Center(
              child: TextButton(
                onPressed: () {
                  // Navigate to Login screen if the user already has an account
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(builder: (context) => LoginScreen()),
                  );
                },
                child: Text('Already have an account? Login'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

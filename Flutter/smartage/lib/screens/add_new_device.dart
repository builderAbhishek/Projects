import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart'; // For handling permissions
import 'package:location/location.dart' as location; // For location services
import 'package:wifi_iot/wifi_iot.dart'; // For Wifi handling
import 'package:url_launcher/url_launcher.dart'; // For opening settings page

class AddDeviceScreen extends StatefulWidget {
  @override
  _AddDeviceScreenState createState() => _AddDeviceScreenState();
}

class _AddDeviceScreenState extends State<AddDeviceScreen> {
  bool _isLocationEnabled = false;
  bool _locationPermissionGranted = false;
  bool _isWifiEnabled = false;
  List<String> availableNetworks = []; // List to store Wi-Fi networks

  @override
  void initState() {
    super.initState();
    _checkLocationPermission();  // Start by checking for location permission
  }

  // Function to check location permission
  Future<void> _checkLocationPermission() async {
    PermissionStatus locationPermission = await Permission.location.request();

    if (locationPermission.isGranted) {
      // If permission is granted
      setState(() {
        _locationPermissionGranted = true;
      });
      _checkLocationServices(); // Proceed to check if location services are enabled
    } else if (locationPermission.isDenied) {
      // If permission is denied
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Location permission is required to continue.")),
      );
    } else if (locationPermission.isPermanentlyDenied) {
      // If permission is permanently denied
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Location permission is permanently denied.")),
      );
      await openAppSettings();  // Open app settings for manual permission granting
    }
  }

  // Function to check if location services are enabled
  Future<void> _checkLocationServices() async {
    var locationService = location.Location();
    bool serviceEnabled;

    serviceEnabled = await locationService.serviceEnabled();
    if (!serviceEnabled) {
      // If location services are not enabled, request to enable them
      serviceEnabled = await locationService.requestService();
      if (serviceEnabled) {
        setState(() {
          _isLocationEnabled = true;
        });
        _checkWifiStatus(); // Check Wi-Fi status after enabling location
      } else {
        setState(() {
          _isLocationEnabled = false;
        });
      }
    } else {
      setState(() {
        _isLocationEnabled = true;
      });
      _checkWifiStatus(); // Check Wi-Fi status if location services are already enabled
    }
  }

  // Function to check if Wi-Fi is enabled
  Future<void> _checkWifiStatus() async {
    bool wifiEnabled = await WiFiForIoTPlugin.isEnabled();  // Use correct method to check if Wi-Fi is enabled
    setState(() {
      _isWifiEnabled = wifiEnabled;
    });
    if (_isWifiEnabled) {
      _loadNetworks();  // Load Wi-Fi networks if Wi-Fi is enabled
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Please turn on Wi-Fi to proceed.")),
      );
    }
  }

  // Function to load Wi-Fi networks and filter the ones starting with "Smart"
  Future<void> _loadNetworks() async {
    try {
      List<WifiNetwork> networks = await WiFiForIoTPlugin.loadWifiList(); // List of WifiNetwork objects
      List<String> smartageNetworks = networks
          .where((network) => network.ssid != null && network.ssid!.startsWith("Smart")) // Access ssid properly
          .map((network) => network.ssid!) // Extract ssid string from WifiNetwork
          .toList();
      setState(() {
        availableNetworks = smartageNetworks;
      });
    } catch (e) {
      print("Error loading Wi-Fi networks: $e");
    }
  }

  // Function to open Wi-Fi settings page
  Future<void> _openWifiSettings() async {
    // This opens the Wi-Fi settings page on Android/iOS devices
    const url = 'wifi://'; // Android and iOS will use the Wi-Fi settings
    if (await canLaunch(url)) {
      await launch(url); // Open Wi-Fi settings if possible
    } else {
      throw 'Could not open Wi-Fi settings';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Add New Device"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            if (!_locationPermissionGranted)
              Text(
                'Location permission is not granted. Please enable it to continue.',
                style: TextStyle(color: Colors.red),
              ),
            if (!_isLocationEnabled)
              ElevatedButton(
                onPressed: _checkLocationServices,  // Request to enable location services
                child: Text('Enable Location Services'),
              ),
            if (!_isWifiEnabled)
              ElevatedButton(
                onPressed: _openWifiSettings,  // Open Wi-Fi settings to enable Wi-Fi
                child: Text('Enable Wi-Fi'),
              ),
            if (_isWifiEnabled)
              ElevatedButton(
                onPressed: _loadNetworks,  // Load networks once Wi-Fi is enabled
                child: Text('Load Smart Devices'),
              ),
            SizedBox(height: 20),
            availableNetworks.isNotEmpty
                ? ListView.builder(
                    shrinkWrap: true,
                    itemCount: availableNetworks.length,
                    itemBuilder: (context, index) {
                      return Card(
                        child: ListTile(
                          title: Text(availableNetworks[index]),
                          onTap: () {
                            // Handle network selection
                            ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('Selected: ${availableNetworks[index]}')));
                          },
                        ),
                      );
                    },
                  )
                : Center(child: Text('No devices found')),
          ],
        ),
      ),
    );
  }
}

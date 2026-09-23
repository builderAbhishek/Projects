import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';
import 'package:permission_handler/permission_handler.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final FirebaseAuth _auth = FirebaseAuth.instance;
  Map<String, List<Map<String, dynamic>>> rooms = {};
  bool isLoading = false;
  TextEditingController _roomNameController = TextEditingController();
  TextEditingController _applianceNameController = TextEditingController();

  Database? database;

  // Dummy data for initial load
  final defaultRooms = {
    "Living Room": [
      {"name": "Appliance 1", "status": true},
      {"name": "Appliance 2", "status": false},
      {"name": "Appliance 3", "status": true},
      {"name": "Appliance 4", "status": false},
      {"name": "Appliance 5", "status": true},
      {"name": "Appliance 6", "status": false},
    ]
  };

  @override
  void initState() {
    super.initState();
    _requestPermissions();  // Request permissions on app start
  }

  // Request storage permissions
  Future<void> _requestPermissions() async {
    // Check if storage permissions are granted
    PermissionStatus status = await Permission.storage.request();

    if (status.isGranted) {
      _openDatabase();  // Open the database if permission granted
      _fetchDevices();  // Fetch devices from database
    } else if (status.isDenied) {
      // If permission is denied, show a dialog to ask user to enable permission
      _showPermissionDialog();
    } else if (status.isPermanentlyDenied) {
      // If permission is permanently denied, open app settings for the user to enable it
      _openAppSettings();
    }
  }

  // Show dialog to request permission
  void _showPermissionDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Permission Required'),
          content: Text('We need storage permission to proceed. Please allow it in the settings.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                _requestPermissions(); // Retry requesting permission
              },
              child: Text('Retry'),
            ),
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                _openAppSettings(); // Open app settings to grant permission
              },
              child: Text('Open Settings'),
            ),
          ],
        );
      },
    );
  }

  // Open app settings if permission is permanently denied
  void _openAppSettings() async {
    await openAppSettings();
  }

  // In _openDatabase method
  Future<void> _openDatabase() async {
    database = await openDatabase(
      p.join(await getDatabasesPath(), 'rooms_database.db'),
      onCreate: (db, version) {
        return db.execute(
          'CREATE TABLE rooms(id INTEGER PRIMARY KEY, roomName TEXT, applianceName TEXT, status INTEGER)',
        );
      },
      version: 1,
    );

    // Check if the data is already inserted
    List<Map> result = await database!.query('rooms');
    print("Fetched data: $result");

    if (result.isEmpty) {
      print('Inserting default data...');
      _insertDefaultData();
    } else {
      print("Data already exists in DB");
    }
  }

  Future<void> _insertDefaultData() async {
    final batch = database!.batch();

    // Insert default rooms and appliances
    defaultRooms.forEach((roomName, appliances) {
      appliances.forEach((appliance) {
        batch.insert('rooms', {
          'roomName': roomName,
          'applianceName': appliance['name'],
          'status': appliance['status'] == true ? 1 : 0,  // Store status as 1 or 0
        });
      });
    });

    await batch.commit();
    print("Default data inserted!");
  }

  Future<void> _fetchDevices() async {
    setState(() {
      isLoading = true;
    });

    await Future.delayed(Duration(seconds: 2)); // Simulate fetching delay

    List<Map> result = await database!.query('rooms');
    Map<String, List<Map<String, dynamic>>> fetchedRooms = {};

    print("Fetched data from DB: $result");

    for (var row in result) {
      String roomName = row['roomName'];
      if (fetchedRooms[roomName] == null) {
        fetchedRooms[roomName] = [];
      }

      // Convert the 'status' to boolean (1 -> true, 0 -> false)
      fetchedRooms[roomName]!.add({
        'name': row['applianceName'],
        'status': (row['status'] as int) == 1,  // Cast to int and check for 1 (true) or 0 (false)
      });
    }

    setState(() {
      rooms = fetchedRooms;
      isLoading = false;
    });

    print("Rooms after fetching: $rooms");
  }

  // Update the appliance name and status in the database
  Future<void> _updateApplianceData(String roomName, List<Map<String, dynamic>> appliances) async {
    final batch = database!.batch();

    appliances.forEach((appliance) {
      batch.update(
        'rooms',
        {
          'applianceName': appliance['name'],
          'status': appliance['status'] ? 1 : 0,  // Convert boolean to 1 or 0
        },
        where: 'roomName = ? AND applianceName = ?',
        whereArgs: [roomName, appliance['name']],
      );
    });

    await batch.commit();
    print("Data updated for $roomName appliances.");
  }

  // Function to show the edit dialog for room and appliances
  void _editRoomAndApplianceNamesDialog(String roomName) {
    _roomNameController.text = roomName;

    // Get appliances for the selected room
    List<Map<String, dynamic>> appliances = rooms[roomName]!;

    List<TextEditingController> applianceControllers = appliances
        .map((appliance) => TextEditingController(text: appliance['name']))
        .toList();

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Edit Room and Appliance Names'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: _roomNameController,
                decoration: InputDecoration(labelText: 'Room Name'),
              ),
              SizedBox(height: 20),
              // Create TextFields for appliance names
              ...appliances.asMap().entries.map((entry) {
                int idx = entry.key;
                var appliance = entry.value;
                return TextField(
                  controller: applianceControllers[idx],
                  decoration: InputDecoration(labelText: 'Appliance ${idx + 1}'),
                );
              }).toList(),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () {
                setState(() {
                  String newRoomName = _roomNameController.text;
                  List<Map<String, dynamic>> updatedAppliances = [];
                  for (int i = 0; i < applianceControllers.length; i++) {
                    updatedAppliances.add({
                      'name': applianceControllers[i].text,
                      'status': appliances[i]['status'],
                    });
                  }

                  // Remove old room and add the updated room
                  if (rooms.containsKey(roomName)) {
                    rooms.remove(roomName);
                    rooms[newRoomName] = updatedAppliances;
                  }

                  // Save updated data to the database
                  _updateApplianceData(newRoomName, updatedAppliances);
                });

                // Close the dialog after saving
                Navigator.pop(context);

                // Refresh data from the database after the update
                _fetchDevices(); // Fetch updated data
              },
              child: Text('Save'),
            ),
            TextButton(
              onPressed: () {
                Navigator.pop(context);
              },
              child: Text('Cancel'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        title: Text(
          'Smartage = स्मार्ट घर',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 24, color: Colors.white),
        ),
        centerTitle: true,
        backgroundColor: Color(0xFF007BFF),
        elevation: 1,
      ),
      body: Stack(
        children: [
          // Background color
          Container(
            decoration: BoxDecoration(
              color: Color(0xFFF8F9FA),
            ),
          ),
          // Main content
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                isLoading
                    ? Center(child: CircularProgressIndicator())
                    : Expanded(
                        child: ListView.builder(
                          itemCount: rooms.keys.length,
                          itemBuilder: (context, roomIndex) {
                            String roomName = rooms.keys.elementAt(roomIndex);
                            List<Map<String, dynamic>> roomDevices = rooms[roomName]!;

                            return Card(
                              margin: EdgeInsets.symmetric(vertical: 12),
                              elevation: 5,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(10),
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Padding(
                                    padding: const EdgeInsets.all(12.0),
                                    child: Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        Text(
                                          roomName,
                                          style: TextStyle(
                                            fontSize: 22,
                                            fontWeight: FontWeight.bold,
                                          ),
                                        ),
                                        IconButton(
                                          icon: Icon(Icons.edit, color: Colors.blue),
                                          onPressed: () {
                                            _editRoomAndApplianceNamesDialog(roomName);
                                          },
                                        ),
                                      ],
                                    ),
                                  ),
                                  Column(
                                    children: roomDevices.map((device) {
                                      return ListTile(
                                        leading: Icon(
                                          device['status'] ? Icons.power : Icons.power_off,
                                          color: device['status'] ? Colors.green : Colors.red,
                                        ),
                                        title: Text(device['name']),
                                        trailing: Switch(
                                          value: device['status'],
                                          onChanged: (value) {
                                            setState(() {
                                              device['status'] = value;
                                            });

                                            // Update the database with the new status
                                            _updateApplianceData(roomName, roomDevices);
                                          },
                                        ),
                                      );
                                    }).toList(),
                                  ),
                                  Padding(
                                    padding: const EdgeInsets.all(12.0),
                                    child: Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                                      children: [
                                        ElevatedButton(
                                          onPressed: () {
                                            // Turn off all devices in the room
                                            setState(() {
                                              for (var device in roomDevices) {
                                                device['status'] = false;
                                              }
                                            });
                                            _updateApplianceData(roomName, roomDevices);
                                          },
                                          style: ElevatedButton.styleFrom(
                                            backgroundColor: Colors.red,
                                            shape: RoundedRectangleBorder(
                                              borderRadius: BorderRadius.circular(20),
                                            ),
                                          ),
                                          child: Text('Turn All Off'),
                                        ),
                                        ElevatedButton(
                                          onPressed: () {
                                            // Turn on all devices in the room
                                            setState(() {
                                              for (var device in roomDevices) {
                                                device['status'] = true;
                                              }
                                            });
                                            _updateApplianceData(roomName, roomDevices);
                                          },
                                          style: ElevatedButton.styleFrom(
                                            backgroundColor: Colors.green,
                                            shape: RoundedRectangleBorder(
                                              borderRadius: BorderRadius.circular(20),
                                            ),
                                          ),
                                          child: Text('Turn All On'),
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                            );
                          },
                        ),
                      ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

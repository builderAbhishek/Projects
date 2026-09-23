import 'package:flutter/material.dart';

class DeviceControlScreen extends StatefulWidget {
  final String deviceName;
  final String deviceType;
  final IconData deviceIcon;

  const DeviceControlScreen({
    Key? key,
    required this.deviceName,
    required this.deviceType,
    required this.deviceIcon,
  }) : super(key: key);

  @override
  _DeviceControlScreenState createState() => _DeviceControlScreenState();
}

class _DeviceControlScreenState extends State<DeviceControlScreen> {
  bool _isDeviceOn = false;
  double _brightness = 50;
  String _timer = "00:00";
  String _statusMessage = "Device is Off";

  void _toggleDevicePower() {
    setState(() {
      _isDeviceOn = !_isDeviceOn;
      _statusMessage = _isDeviceOn ? "Device is On" : "Device is Off";
    });
  }

  void _setTimer(String duration) {
    setState(() {
      _timer = duration;
      _statusMessage = "Timer set for $duration";
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.deviceName),
        centerTitle: true,
        backgroundColor: Colors.teal,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Device Name and Icon
            Center(
              child: Column(
                children: [
                  Icon(
                    widget.deviceIcon,
                    size: 80,
                    color: _isDeviceOn ? Colors.teal : Colors.grey,
                  ),
                  const SizedBox(height: 10),
                  Text(
                    widget.deviceName,
                    style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                  ),
                  Text(
                    widget.deviceType,
                    style: TextStyle(fontSize: 16, color: Colors.grey),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),
            // On/Off Toggle
            Center(
              child: ElevatedButton(
                onPressed: _toggleDevicePower,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 15),
                  backgroundColor: _isDeviceOn ? Colors.teal : Colors.grey,
                ),
                child: Text(
                  _isDeviceOn ? "Turn Off" : "Turn On",
                  style: const TextStyle(fontSize: 18),
                ),
              ),
            ),
            const SizedBox(height: 30),
            // Custom Controls
            widget.deviceType == "Light"
                ? Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Brightness",
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                      Slider(
                        value: _brightness,
                        min: 0,
                        max: 100,
                        divisions: 10,
                        label: "${_brightness.toInt()}%",
                        activeColor: Colors.teal,
                        inactiveColor: Colors.grey,
                        onChanged: _isDeviceOn
                            ? (value) {
                                setState(() {
                                  _brightness = value;
                                  _statusMessage = "Brightness set to ${_brightness.toInt()}%";
                                });
                              }
                            : null,
                      ),
                    ],
                  )
                : Container(),
            const SizedBox(height: 20),
            Text(
              "Timer",
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                ElevatedButton(
                  onPressed: () {
                    if (_isDeviceOn) _setTimer("00:30");
                  },
                  child: const Text("30 Min"),
                ),
                ElevatedButton(
                  onPressed: () {
                    if (_isDeviceOn) _setTimer("01:00");
                  },
                  child: const Text("1 Hour"),
                ),
                ElevatedButton(
                  onPressed: () {
                    if (_isDeviceOn) _setTimer("02:00");
                  },
                  child: const Text("2 Hours"),
                ),
              ],
            ),
            const SizedBox(height: 20),
            // Live Status
            Text(
              "Live Status",
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Container(
              margin: const EdgeInsets.only(top: 10),
              padding: const EdgeInsets.all(16.0),
              decoration: BoxDecoration(
                color: Colors.teal.withOpacity(0.1),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: Colors.teal),
              ),
              child: Text(
                _statusMessage,
                style: const TextStyle(fontSize: 16, color: Colors.black),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

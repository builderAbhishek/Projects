import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

class DatabaseHelper {
  static final DatabaseHelper instance = DatabaseHelper._init();
  static Database? _database;

  DatabaseHelper._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('smartage.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);
    return await openDatabase(path, version: 1, onCreate: _createDB);
  }

  Future _createDB(Database db, int version) async {
    await db.execute(''' 
      CREATE TABLE rooms (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        roomName TEXT
      );
    ''');

    await db.execute('''
      CREATE TABLE appliances (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        roomId INTEGER,
        applianceName TEXT,
        status INTEGER,
        FOREIGN KEY (roomId) REFERENCES rooms(id)
      );
    ''');
  }

  Future<int> insertRoom(String roomName) async {
    final db = await instance.database;
    return await db.insert('rooms', {'roomName': roomName});
  }

  Future<int> insertAppliance(int roomId, String applianceName, int status) async {
    final db = await instance.database;
    return await db.insert('appliances', {
      'roomId': roomId,
      'applianceName': applianceName,
      'status': status
    });
  }

  Future<List<Map<String, dynamic>>> getRooms() async {
    final db = await instance.database;
    return await db.query('rooms');
  }

  Future<List<Map<String, dynamic>>> getAppliances(int roomId) async {
    final db = await instance.database;
    return await db.query('appliances', where: 'roomId = ?', whereArgs: [roomId]);
  }

  Future<int> updateRoom(int roomId, String newRoomName) async {
    final db = await instance.database;
    return await db.update('rooms', {'roomName': newRoomName},
        where: 'id = ?', whereArgs: [roomId]);
  }

  Future<int> updateAppliance(int applianceId, String newApplianceName) async {
    final db = await instance.database;
    return await db.update('appliances', {'applianceName': newApplianceName},
        where: 'id = ?', whereArgs: [applianceId]);
  }

  Future<void> deleteRoom(int roomId) async {
    final db = await instance.database;
    await db.delete('rooms', where: 'id = ?', whereArgs: [roomId]);
    await db.delete('appliances', where: 'roomId = ?', whereArgs: [roomId]);
  }
}

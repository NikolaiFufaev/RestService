package com.example;

import com.example.db.ConnectionManager;
import com.example.db.ConnectionManagerImpl;
import com.example.util.InitSqlScheme;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
        InitSqlScheme.initSqlScheme(connectionManager);
        InitSqlScheme.initSqlData(connectionManager);

    }
}
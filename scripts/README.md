To initialize database:(default username is your username, port is 5432)

```
$PG_DIR/initdb -D database
```

To start server:
```
$PG_DIR/pg_ctl -D database -l logfile start
```

To stop server:
```
$PG_DIR/pg_ctl -D database stop
```

To run an sql script:
```
psql -h localhost -p 5432 -d postgres -f script_file_name
```

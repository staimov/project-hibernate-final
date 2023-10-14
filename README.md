[![My Skills](https://skillicons.dev/icons?i=java,maven,hibernate,mysql,redis,docker&theme=light)](https://skillicons.dev)
# project-hibernate-final

JavaRush hibernate final project:

https://javarush.com/quests/lectures/jru.module4.lecture08

## Steps to run the program

1. Build the project:
```
mvn clean install
```
2. Run MySQL and Redis Stack docker containers:
```
docker compose up -d
```
3. Apply [dump-hibernate-final.sql](./dump-hibernate-final.sql) script to MySQL.
4. Run the program. For example:
```
java -jar target/project-hibernate-final-1.0.jar
```
5. Look at the results in the console.
6. Open Redis Stack UI http://localhost:8001/ .
7. To invalidate cache restart redis and mysql containers before each restart of the program:
```
docker compose up --force-recreate --no-deps -d my-redis-stack
docker compose up --force-recreate --no-deps -d my-mysql      
```

## Results

### With p6spy

![screenshot](./src/main/resources/result_w_p6spy.jpg?raw=true)

### Without p6spy

![screenshot](./src/main/resources/result_wo_p6spy.jpg?raw=true)

### Redis

![screenshot](./src/main/resources/redis.jpg?raw=true)

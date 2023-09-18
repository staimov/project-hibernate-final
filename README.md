[![My Skills](https://skillicons.dev/icons?i=java,hibernate,mysql,redis,docker&theme=light)](https://skillicons.dev)
# project-hibernate-final

JavaRush hibernate final project:

https://javarush.com/quests/lectures/jru.module4.lecture08

## Steps to run the program

1. Build the project:
```
mvn clean install
```
2. Run docker containers:
```
docker compose up -d
```
3. Apply [dump-hibernate-final.sql](./dump-hibernate-final.sql) script to MySQL.
4. Run the program. For example:
```
java -jar target/project-hibernate-final-1.0.jar
```
5. Look at the results in the console.
6. Restart the redis container before each restart of the program:
```
docker compose up --force-recreate --no-deps -d my-redis-stack
```

## Results

![screenshot](./src/main/resources/result.jpg?raw=true)

![screenshot](./src/main/resources/redis.jpg?raw=true)

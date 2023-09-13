# project-hibernate-final

JavaRush hibernate final project:

https://javarush.com/quests/lectures/jru.module4.lecture08

## Steps to run the program

1. Run docker containers:
```
docker pull mysql

docker run --name my-mysql -d -p 63306:3306 -e MYSQL_ROOT_PASSWORD=root --restart unless-stopped -v my-mysql:/var/lib/mysql mysql:latest

docker pull redis/redis-stack

docker run -d --name my-redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest

```


2. Apply [dump-hibernate-final.sql](./dump-hibernate-final.sql) script to MySQL.


3. Run the program.


4. Restart the redis container before each restart of the program:
```
docker stop my-redis-stack

docker rm my-redis-stack

docker run -d --name my-redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest

```


## Result

![screenshot](./src/main/resources/result.jpg?raw=true)
